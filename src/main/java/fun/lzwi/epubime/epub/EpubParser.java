package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.exception.EpubFormatException;
import fun.lzwi.epubime.exception.EpubZipException;
import fun.lzwi.epubime.exception.BaseEpubException;
import fun.lzwi.epubime.exception.EpubPathValidationException;
import fun.lzwi.epubime.parser.MetadataParser;
import fun.lzwi.epubime.parser.NavigationParser;
import fun.lzwi.epubime.parser.ResourceParser;
import fun.lzwi.epubime.zip.ZipFileManager;
import fun.lzwi.epubime.zip.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * EPUB解析器类
 * 负责解析EPUB文件内容并提取元数据、章节和资源信息，遵循单一职责原则
 */
public class EpubParser {
    /**
     * 容器文件路径
     */
    public static final String CONTAINER_FILE_PATH = "META-INF/container.xml";

    private final File epubFile;
    private final EpubFileReader fileReader;
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
        this.fileReader = new EpubFileReader(epubFile);
        this.metadataParser = new MetadataParser();
        this.navigationParser = new NavigationParser();
        this.resourceParser = new ResourceParser(epubFile);
    }

    /**
     * 获取文件读取器
     * @return 文件读取器实例
     */
    public EpubFileReader getFileReader() {
        return fileReader;
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
     * @throws BaseEpubException 解析异常
     */
    public EpubBook parse() throws BaseEpubException, java.io.IOException, EpubPathValidationException {
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
            // 首先读取container.xml获取OPF文件路径
            String container = fileReader.readContent(CONTAINER_FILE_PATH);
            if (container == null) {
                throw new EpubFormatException("Container file not found", epubFile, CONTAINER_FILE_PATH);
            }

            String opfPath = extractRootFilePath(container);
            String opfDir = extractRootFileDir(opfPath);

            // 读取OPF内容用于提取路径信息（OPF文件通常很小）
            String opfContent = fileReader.readContent(opfPath);
            if (opfContent == null) {
                throw new EpubFormatException("OPF file not found", epubFile, opfPath);
            }

            // 解析元数据
            book.setMetadata(metadataParser.parseMetadata(opfContent));

            // 解析资源文件 - 现在只设置引用，不加载数据
            List<EpubResource> resources = resourceParser.parseResources(opfContent, opfDir);
            book.setResources(resources);

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

            // 流式解析导航文件，避免加载整个文件到内存
            // 解析NCX
            if (ncxPath != null) {
                try (java.io.InputStream ncxStream = ZipUtils.getZipFileInputStream(fileReader.epubFile, ncxPath)) {
                    if (ncxStream != null) {
                        List<EpubChapter> ncx = navigationParser.parseNcx(ncxStream);
                        book.setNcx(ncx);
                    }
                } catch (Exception e) {
                    // NCX解析失败，记录但不抛出异常
                }
            }

            // 解析NAV
            if (navPath != null) {
                try (java.io.InputStream navStream = ZipUtils.getZipFileInputStream(fileReader.epubFile, navPath)) {
                    if (navStream != null) {
                        List<EpubChapter> nav = navigationParser.parseNav(navStream);
                        book.setNav(nav);

                        // 重新打开流来解析其他类型的导航（landmarks、page-list等）
                        // 注意：这里需要重新打开流，因为InputStream不能重置
                        try (java.io.InputStream navStream2 = ZipUtils.getZipFileInputStream(fileReader.epubFile, navPath)) {
                            List<EpubChapter> landmarks = navigationParser.parseNavByType(navStream2, "landmarks");
                            book.setLandmarks(landmarks);
                        }

                        try (java.io.InputStream navStream3 = ZipUtils.getZipFileInputStream(fileReader.epubFile, navPath)) {
                            List<EpubChapter> pageList = navigationParser.parseNavByType(navStream3, "page-list");
                            book.setPageList(pageList);
                        }
                    }
                } catch (Exception e) {
                    // NAV解析失败，记录但不抛出异常
                }
            }

            // 缓存完整解析结果
            cache.setParsedResult(cacheKey, new EpubBook(book));

        } catch (Exception e) {
            if (e instanceof IOException) {
                throw new EpubZipException("Failed to read EPUB file during parsing", fileReader.epubFile, "multiple files", e);
            } else {
                throw new EpubZipException("Failed to parse EPUB file", fileReader.epubFile, "unknown", e);
            }
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
     * @throws BaseEpubException 解析异常
     */
    public EpubBook parseWithoutCache() throws BaseEpubException, java.io.IOException, EpubPathValidationException {
        // 清理当前线程的ZIP文件句柄
        ZipFileManager.getInstance().cleanup();

        try {
            return parse();
        } finally {
            // 确保清理
            ZipFileManager.getInstance().cleanup();
        }
    }




}