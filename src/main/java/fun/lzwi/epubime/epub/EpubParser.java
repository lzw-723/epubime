package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.exception.EpubParseException;
import fun.lzwi.epubime.exception.EpubPathValidationException;
import fun.lzwi.epubime.exception.EpubZipException;
import fun.lzwi.epubime.parser.MetadataParser;
import fun.lzwi.epubime.parser.NavigationParser;
import fun.lzwi.epubime.parser.ResourceParser;
import fun.lzwi.epubime.zip.PathValidator;
import fun.lzwi.epubime.zip.ZipFileManager;
import fun.lzwi.epubime.zip.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * EPUB解析器类
 * 负责解析EPUB文件并提取元数据、章节和资源信息
 */
public class EpubParser {
    /**
     * 容器文件路径
     */
    public static final String CONTAINER_FILE_PATH = "META-INF/container.xml";
    
    private final File epubFile;
    private final MetadataParser metadataParser;
    private final NavigationParser navigationParser;
    private final ResourceParser resourceParser;

    /**
     * 构造函数
     *
     * @param epubFile EPUB文件
     */
    public EpubParser(File epubFile) {
        if (epubFile == null) {
            throw new IllegalArgumentException("EPUB file cannot be null");
        }
        this.epubFile = epubFile;
        this.metadataParser = new MetadataParser();
        this.navigationParser = new NavigationParser();
        this.resourceParser = new ResourceParser(epubFile);
    }

    /**
     * 读取EPUB文件中指定路径的内容
     *
     * @param path 文件路径
     * @return 文件内容
     * @throws EpubParseException 解析异常
     */
    protected String readEpubContent(String path) throws EpubParseException {
        // 防止目录遍历攻击
        if (!PathValidator.isPathSafe("", path)) {
            throw new EpubPathValidationException("Invalid file path: " + path, epubFile.getName(), path);
        }

        try {
            return ZipUtils.getZipFileContent(epubFile, path);
        } catch (IOException e) {
            throw new EpubZipException("Failed to read EPUB file content", epubFile.getName(), path, e);
        }
    }

    /**
     * 从容器文件内容中获取根文件路径
     *
     * @param containerContent 容器文件内容
     * @return 根文件路径
     */
    private String extractRootFilePath(String containerContent) {
        int start = containerContent.indexOf("full-path=\"");
        if (start == -1) {
            throw new IllegalArgumentException("No root file path found in container.xml");
        }
        
        int end = containerContent.indexOf("\"", start + 11);
        if (end == -1) {
            throw new IllegalArgumentException("Invalid root file path format in container.xml");
        }
        
        return containerContent.substring(start + 11, end);
    }

    /**
     * 获取根文件目录
     *
     * @param rootFilePath 根文件路径
     * @return 根文件目录
     */
    private String extractRootFileDir(String rootFilePath) {
        int lastSlashIndex = rootFilePath.lastIndexOf("/");
        if (lastSlashIndex == -1) {
            return "";
        }
        return rootFilePath.substring(0, lastSlashIndex + 1);
    }

    /**
     * 解析EPUB文件并返回EpubBook对象
     *
     * @return 解析后的EpubBook对象
     * @throws EpubParseException 解析异常
     */
    public EpubBook parse() throws EpubParseException {
        EpubBook book = new EpubBook();

        // 获取当前EPUB文件的缓存
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(epubFile);
        String cacheKey = "fullParse:" + epubFile.getAbsolutePath();

        // 尝试从缓存获取完整解析结果
        EpubBook cachedBook = (EpubBook) cache.getParsedResult(cacheKey);
        if (cachedBook != null) {
            return new EpubBook(cachedBook);
        }

        try {
            // 第一次ZIP访问：读取container.xml和OPF文件
            List<String> firstBatchPaths = new ArrayList<>();
            firstBatchPaths.add(CONTAINER_FILE_PATH);

            // 首先读取container.xml获取OPF文件路径
            java.util.Map<String, String> firstBatchContents = ZipUtils.getMultipleZipFileContents(
                    epubFile, firstBatchPaths);
            
            String container = firstBatchContents.get(CONTAINER_FILE_PATH);
            if (container == null) {
                throw new EpubParseException("Container file not found", epubFile.getName(), 
                        CONTAINER_FILE_PATH, "parse");
            }

            String opfPath = extractRootFilePath(container);
            String opfDir = extractRootFileDir(opfPath);

            // 重置文件路径列表，包含OPF文件
            firstBatchPaths.add(opfPath);
            firstBatchContents = ZipUtils.getMultipleZipFileContents(epubFile, firstBatchPaths);
            
            String opfContent = firstBatchContents.get(opfPath);
            if (opfContent == null) {
                throw new EpubParseException("OPF file not found", epubFile.getName(), opfPath, "parse");
            }

            // 解析元数据
            book.setMetadata(metadataParser.parseMetadata(opfContent));

            // 获取章节文件路径
            String ncxPath = null;
            String navPath = null;
            
            try {
                ncxPath = resourceParser.getNcxPath(opfContent, opfDir);
            } catch (Exception e) {
                // NCX路径可选，不抛出异常
            }
            
            try {
                navPath = resourceParser.getNavPath(opfContent, opfDir);
            } catch (Exception e) {
                // NAV路径可选，不抛出异常
            }

            // 第二次ZIP访问：读取章节文件
            List<String> secondBatchPaths = new ArrayList<>();
            if (ncxPath != null) {
                secondBatchPaths.add(ncxPath);
            }
            if (navPath != null) {
                secondBatchPaths.add(navPath);
            }

            if (!secondBatchPaths.isEmpty()) {
                java.util.Map<String, String> secondBatchContents = 
                        ZipUtils.getMultipleZipFileContents(epubFile, secondBatchPaths);
                
                // 解析NCX
                if (ncxPath != null) {
                    String ncxContent = secondBatchContents.get(ncxPath);
                    if (ncxContent != null) {
                        List<EpubChapter> ncx = navigationParser.parseNcx(ncxContent);
                        book.setNcx(ncx);
                    }
                }
                
                // 解析NAV
                if (navPath != null) {
                    String navContent = secondBatchContents.get(navPath);
                    if (navContent != null) {
                        List<EpubChapter> nav = navigationParser.parseNav(navContent);
                        book.setNav(nav);
                        
                        // 解析其他类型的导航（landmarks、page-list等）
                        List<EpubChapter> landmarks = navigationParser.parseNavByType(navContent, "landmarks");
                        book.setLandmarks(landmarks);
                        
                        List<EpubChapter> pageList = navigationParser.parseNavByType(navContent, "page-list");
                        book.setPageList(pageList);
                    }
                }
            }

            // 解析资源文件 - 现在只设置引用，不加载数据
            List<EpubResource> resources = resourceParser.parseResources(opfContent, opfDir);
            book.setResources(resources);

            // 缓存完整解析结果
            cache.setParsedResult(cacheKey, new EpubBook(book));
            
        } catch (IOException e) {
            throw new EpubZipException("Failed to read EPUB file during parsing", epubFile.getName(),
                    "multiple files", e);
        } finally {
            // 解析完成后清理ZIP文件句柄
            ZipFileManager.getInstance().closeCurrentZipFile();
        }

        return book;
    }

    /**
     * 解析EPUB文件并返回EpubBook对象，但不使用缓存
     *
     * @return 解析后的EpubBook对象
     * @throws EpubParseException 解析异常
     */
    public EpubBook parseWithoutCache() throws EpubParseException {
        // 清理当前线程的ZIP文件句柄
        ZipFileManager.getInstance().cleanup();

        try {
            return parse();
        } finally {
            // 确保清理
            ZipFileManager.getInstance().cleanup();
        }
    }

    /**
     * 流式处理HTML章节内容，避免将整个文件加载到内存中
     *
     * @param htmlFileName HTML文件名
     * @param processor 处理HTML内容的消费者函数
     * @throws EpubParseException 解析异常
     */
    public void processHtmlChapterContent(String htmlFileName, Consumer<InputStream> processor) 
            throws EpubParseException {
        processHtmlChapterContent(epubFile, htmlFileName, processor);
    }

    /**
     * 流式处理多个HTML章节内容
     *
     * @param htmlFileNames HTML文件名列表
     * @param processor 处理每个HTML内容的消费者函数
     * @throws EpubParseException 解析异常
     */
    public void processMultipleHtmlChapters(List<String> htmlFileNames, 
                                           BiConsumer<String, InputStream> processor) 
            throws EpubParseException {
        processMultipleHtmlChapters(epubFile, htmlFileNames, processor);
    }

    // ========== 向后兼容的静态方法 ==========
    
    /**
     * 静态方法：读取EPUB文件中指定路径的内容
     * 保持为静态方法以提供工具功能并向后兼容
     *
     * @param epubFile EPUB文件
     * @param path 文件路径
     * @return 文件内容
     * @throws EpubParseException 解析异常
     * @deprecated 使用EpubParser实例方法代替
     */
    @Deprecated
    protected static String readEpubContent(File epubFile, String path) throws EpubParseException {
        // 防止目录遍历攻击
        if (!PathValidator.isPathSafe("", path)) {
            throw new EpubPathValidationException("Invalid file path: " + path, epubFile.getName(), path);
        }

        try {
            return ZipUtils.getZipFileContent(epubFile, path);
        } catch (IOException e) {
            throw new EpubZipException("Failed to read EPUB file content", epubFile.getName(), path, e);
        }
    }

    /**
     * 静态方法：从容器文件内容中获取根文件路径
     * 保持为静态方法以提供工具功能并向后兼容
     *
     * @param containerContent 容器文件内容
     * @return 根文件路径
     * @deprecated 使用EpubParser实例方法代替
     */
    @Deprecated
    protected static String getRootFilePath(String containerContent) {
        int start = containerContent.indexOf("full-path=\"");
        if (start == -1) {
            throw new IllegalArgumentException("No root file path found in container.xml");
        }
        
        int end = containerContent.indexOf("\"", start + 11);
        if (end == -1) {
            throw new IllegalArgumentException("Invalid root file path format in container.xml");
        }
        
        return containerContent.substring(start + 11, end);
    }

    /**
     * 静态方法：获取根文件目录
     * 保持为静态方法以提供工具功能并向后兼容
     *
     * @param rootFilePath 根文件路径
     * @return 根文件目录
     * @deprecated 使用EpubParser实例方法代替
     */
    @Deprecated
    protected static String getRootFileDir(String rootFilePath) {
        int lastSlashIndex = rootFilePath.lastIndexOf("/");
        if (lastSlashIndex == -1) {
            return "";
        }
        return rootFilePath.substring(0, lastSlashIndex + 1);
    }

    /**
     * 静态方法：解析OPF内容中的元数据
     * 保持为静态方法以提供工具功能并向后兼容
     *
     * @param opfContent OPF文件内容
     * @return 元数据对象
     * @deprecated 使用MetadataParser类代替
     */
    @Deprecated
    protected static Metadata parseMetadata(String opfContent) {
        MetadataParser parser = new MetadataParser();
        return parser.parseMetadata(opfContent);
    }

    /**
     * 静态方法：从OPF内容中获取NCX文件路径
     * 保持为静态方法以提供工具功能并向后兼容
     *
     * @param opfContent OPF文件内容
     * @param opfDir OPF文件目录
     * @return NCX文件路径
     * @deprecated 使用ResourceParser类代替
     */
    @Deprecated
    protected static String getNcxPath(String opfContent, String opfDir) {
        ResourceParser parser = new ResourceParser(null);
        return parser.getNcxPath(opfContent, opfDir);
    }

    /**
     * 静态方法：解析NCX目录内容
     * 保持为静态方法以提供工具功能并向后兼容
     *
     * @param tocContent NCX目录内容
     * @return 章节列表
     * @deprecated 使用NavigationParser类代替
     */
    @Deprecated
    protected static List<EpubChapter> parseNcx(String tocContent) {
        NavigationParser parser = new NavigationParser();
        return parser.parseNcx(tocContent);
    }

    /**
     * 静态方法：从OPF内容中获取NAV文件路径
     * 保持为静态方法以提供工具功能并向后兼容
     *
     * @param opfContent OPF文件内容
     * @param opfDir OPF文件目录
     * @return NAV文件路径，如果不存在则返回null
     * @deprecated 使用ResourceParser类代替
     */
    @Deprecated
    protected static String getNavPath(String opfContent, String opfDir) {
        ResourceParser parser = new ResourceParser(null);
        return parser.getNavPath(opfContent, opfDir);
    }

    /**
     * 静态方法：解析NAV目录内容
     * 保持为静态方法以提供工具功能并向后兼容
     *
     * @param navContent NAV目录内容
     * @return 章节列表
     * @deprecated 使用NavigationParser类代替
     */
    @Deprecated
    protected static List<EpubChapter> parseNav(String navContent) {
        NavigationParser parser = new NavigationParser();
        return parser.parseNav(navContent);
    }

    /**
     * 静态方法：解析OPF内容中的资源文件列表
     * 保持为静态方法以提供工具功能并向后兼容
     *
     * @param opfContent OPF文件内容
     * @param opfDir OPF文件目录
     * @param epubFile EPUB文件
     * @return 资源文件列表
     * @deprecated 使用ResourceParser类代替
     */
    @Deprecated
    protected static List<EpubResource> parseResources(String opfContent, String opfDir, File epubFile) {
        ResourceParser parser = new ResourceParser(epubFile);
        return parser.parseResources(opfContent, opfDir);
    }

    /**
     * 静态方法：按导航类型解析NAV内容
     * 保持为静态方法以提供工具功能并向后兼容
     *
     * @param navContent NAV目录内容
     * @param navType 导航类型（如toc, landmarks, page-list等）
     * @return 章节列表
     * @deprecated 使用NavigationParser类代替
     */
    @Deprecated
    protected static List<EpubChapter> parseNavByType(String navContent, String navType) {
        NavigationParser parser = new NavigationParser();
        return parser.parseNavByType(navContent, navType);
    }

    /**
     * 静态方法：流式处理HTML章节内容，避免将整个文件加载到内存中
     * 保持为静态方法以提供工具功能
     *
     * @param epubFile EPUB文件
     * @param htmlFileName HTML文件名
     * @param processor 处理HTML内容的消费者函数
     * @throws EpubParseException 解析异常
     */
    public static void processHtmlChapterContent(File epubFile, String htmlFileName,
                                                 Consumer<InputStream> processor) throws EpubParseException {
        // 防止目录遍历攻击
        if (!PathValidator.isPathSafe("", htmlFileName)) {
            throw new EpubPathValidationException("Invalid file path: " + htmlFileName, epubFile.getName(), htmlFileName);
        }
        
        try {
            ZipUtils.processHtmlContent(epubFile, htmlFileName, processor);
        } catch (IOException e) {
            throw new EpubZipException("Failed to process HTML chapter content", epubFile.getName(), htmlFileName, e);
        }
    }

    /**
     * 静态方法：流式处理多个HTML章节内容
     * 保持为静态方法以提供工具功能
     *
     * @param epubFile EPUB文件
     * @param htmlFileNames HTML文件名列表
     * @param processor 处理每个HTML内容的消费者函数
     * @throws EpubParseException 解析异常
     */
    public static void processMultipleHtmlChapters(File epubFile, List<String> htmlFileNames, BiConsumer<String,
            InputStream> processor) throws EpubParseException {
        // 防止目录遍历攻击
        for (String fileName : htmlFileNames) {
            if (!PathValidator.isPathSafe("", fileName)) {
                throw new EpubPathValidationException("Invalid file path: " + fileName, epubFile.getName(), fileName);
            }
        }
        
        try {
            ZipUtils.processMultipleHtmlContents(epubFile, htmlFileNames, processor);
        } catch (IOException e) {
            throw new EpubZipException("Failed to process multiple HTML chapters", epubFile.getName(),
                    "multiple files", e);
        }
    }
}