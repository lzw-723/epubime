package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.exception.EpubFormatException;
import fun.lzwi.epubime.exception.EpubZipException;
import fun.lzwi.epubime.exception.BaseEpubException;
import fun.lzwi.epubime.exception.EpubPathValidationException;
import fun.lzwi.epubime.parser.MetadataParser;
import fun.lzwi.epubime.parser.NavigationParser;
import fun.lzwi.epubime.parser.ResourceParser;
import fun.lzwi.epubime.parser.XmlUtils;
import fun.lzwi.epubime.zip.ZipBombProtection;
import fun.lzwi.epubime.zip.ZipFileManager;
import fun.lzwi.epubime.zip.ZipManagedInputStream;
import fun.lzwi.epubime.zip.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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
    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", 
                       justification = "Parameter validation is safe - all fields are final and immutable after construction")
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
     * 检测EPUB版本
     *
     * @param opfDocument 已解析的OPF Document对象
     * @return EPUB版本字符串
     */
    private String detectEpubVersion(org.jsoup.nodes.Document opfDocument) {
        // 使用已解析的Document，避免重复解析
        org.jsoup.nodes.Element packageElement = opfDocument.selectFirst("package");
        if (packageElement != null) {
            String version = packageElement.attr("version");
            if (!version.isEmpty()) {
                return version;
            }
        }
        // 默认返回3.0，如果无法检测
        return "3.0";
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
            // 使用浅拷贝，共享不可变数据，减少内存占用
            return new EpubBook(cachedBook);
        }

        // ZIP Bomb 防护：验证 EPUB 文件安全性
        try (java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(epubFile)) {
            ZipBombProtection.validateZipFileSafety(zipFile);
        }

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

        // 只解析一次OPF Document，后续所有解析器共享
        org.jsoup.nodes.Document opfDocument = org.jsoup.Jsoup.parse(opfContent, "", org.jsoup.parser.Parser.xmlParser());

        // 检测EPUB版本
        String epubVersion = detectEpubVersion(opfDocument);
        book.setVersion(epubVersion);

        // 解析元数据 - 传递已解析的Document
        book.setMetadata(metadataParser.parseMetadata(opfDocument, opfContent, epubVersion));

        // 解析资源文件 - 传递已解析的Document，避免重复解析
        List<EpubResource> resources = resourceParser.parseResources(opfDocument, opfContent, opfDir);
        book.setResources(resources);

        String ncxPath = null;
        String navPath = null;

        try {
            ncxPath = resourceParser.getNcxPath(opfDocument, opfContent, opfDir);
        } catch (IllegalArgumentException e) {
            // NCX路径可选，不抛出异常
        }

        navPath = resourceParser.getNavPath(opfDocument, opfContent, opfDir);

        // 流式解析导航文件，避免重复打开同一文件
        // 解析NCX
        if (ncxPath != null) {
            try (ZipManagedInputStream ncxStream = ZipManagedInputStream.open(fileReader.epubFile, ncxPath)) {
                if (ncxStream != null) {
                    List<EpubChapter> ncx = navigationParser.parseNcx(ncxStream);
                    book.setNcx(ncx);
                }
            }
        }

        // 解析NAV - 优化：一次性读取NAV文件内容到内存，避免重复打开流
        // 因为NAV文件通常很小（<100KB），内存开销可以忽略不计
        if (navPath != null) {
            String navContent = null;
            try (ZipManagedInputStream navStream = ZipManagedInputStream.open(fileReader.epubFile, navPath)) {
                if (navStream != null) {
                    navContent = XmlUtils.readStreamToString(navStream);
                }
            }

            // 使用已读取的内容解析不同类型的导航，无需重复打开文件
            if (navContent != null) {
                // 解析TOC导航 - 传递已解析的Document
                List<EpubChapter> nav = navigationParser.parseNav(navContent);
                book.setNav(nav);

                // 解析地标导航
                List<EpubChapter> landmarks = navigationParser.parseNavByType(navContent, "landmarks");
                book.setLandmarks(landmarks);

                // 解析页面列表导航
                List<EpubChapter> pageList = navigationParser.parseNavByType(navContent, "page-list");
                book.setPageList(pageList);
            }
        }

        // 缓存完整解析结果（使用浅拷贝，减少内存占用）
        cache.setParsedResult(cacheKey, new EpubBook(book));

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

    /**
     * 只解析元数据（按需加载）
     * 性能优化：避免解析整个EPUB文件，只读取元数据部分
     * 
     * @return 元数据对象
     * @throws BaseEpubException 解析异常
     */
    public Metadata parseMetadataOnly() throws BaseEpubException, java.io.IOException, EpubPathValidationException {
        // 获取缓存
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(epubFile);
        String cacheKey = "metadataOnly:" + epubFile.getAbsolutePath();

        // 尝试从缓存获取
        Metadata cachedMetadata = (Metadata) cache.getParsedResult(cacheKey);
        if (cachedMetadata != null) {
            return new Metadata(cachedMetadata);
        }

        // ZIP Bomb 防护
        try (java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(epubFile)) {
            ZipBombProtection.validateZipFileSafety(zipFile);
        }

        // 读取container.xml获取OPF文件路径
        String container = fileReader.readContent(CONTAINER_FILE_PATH);
        if (container == null) {
            throw new EpubFormatException("Container file not found", epubFile, CONTAINER_FILE_PATH);
        }

        String opfPath = extractRootFilePath(container);

        // 读取OPF内容
        String opfContent = fileReader.readContent(opfPath);
        if (opfContent == null) {
            throw new EpubFormatException("OPF file not found", epubFile, opfPath);
        }

        // 只解析一次OPF Document
        org.jsoup.nodes.Document opfDocument = org.jsoup.Jsoup.parse(opfContent, "", org.jsoup.parser.Parser.xmlParser());

        // 检测EPUB版本
        String epubVersion = detectEpubVersion(opfDocument);

        // 只解析元数据
        Metadata metadata = metadataParser.parseMetadata(opfDocument, opfContent, epubVersion);

        // 缓存结果
        cache.setParsedResult(cacheKey, new Metadata(metadata));

        return metadata;
    }

    /**
     * 只解析目录（按需加载）
     * 性能优化：避免解析整个EPUB文件，只读取导航部分
     * 
     * @return 章节列表（优先NAV，其次NCX）
     * @throws BaseEpubException 解析异常
     */
    public List<EpubChapter> parseTableOfContentsOnly() throws BaseEpubException, java.io.IOException, EpubPathValidationException {
        // 获取缓存
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(epubFile);
        String cacheKey = "tocOnly:" + epubFile.getAbsolutePath();

        // 尝试从缓存获取
        @SuppressWarnings("unchecked")
        List<EpubChapter> cachedToc = (List<EpubChapter>) cache.getParsedResult(cacheKey);
        if (cachedToc != null) {
            return new ArrayList<>(cachedToc);
        }

        // ZIP Bomb 防护
        try (java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(epubFile)) {
            ZipBombProtection.validateZipFileSafety(zipFile);
        }

        // 读取container.xml获取OPF文件路径
        String container = fileReader.readContent(CONTAINER_FILE_PATH);
        if (container == null) {
            throw new EpubFormatException("Container file not found", epubFile, CONTAINER_FILE_PATH);
        }

        String opfPath = extractRootFilePath(container);
        String opfDir = extractRootFileDir(opfPath);

        // 读取OPF内容
        String opfContent = fileReader.readContent(opfPath);
        if (opfContent == null) {
            throw new EpubFormatException("OPF file not found", epubFile, opfPath);
        }

        // 只解析一次OPF Document
        org.jsoup.nodes.Document opfDocument = org.jsoup.Jsoup.parse(opfContent, "", org.jsoup.parser.Parser.xmlParser());

        // 查找NAV和NCX路径
        String ncxPath = null;
        String navPath = null;

        try {
            ncxPath = resourceParser.getNcxPath(opfDocument, opfContent, opfDir);
        } catch (IllegalArgumentException e) {
            // NCX路径可选
        }

        navPath = resourceParser.getNavPath(opfDocument, opfContent, opfDir);

        List<EpubChapter> toc = null;

        // 优先使用NAV（EPUB3标准）
        if (navPath != null) {
            try (ZipManagedInputStream navStream = ZipManagedInputStream.open(fileReader.epubFile, navPath)) {
                if (navStream != null) {
                    String navContent = XmlUtils.readStreamToString(navStream);
                    toc = navigationParser.parseNav(navContent);
                }
            }
        }

        // 如果NAV不存在，使用NCX（向后兼容）
        if ((toc == null || toc.isEmpty()) && ncxPath != null) {
            try (ZipManagedInputStream ncxStream = ZipManagedInputStream.open(fileReader.epubFile, ncxPath)) {
                if (ncxStream != null) {
                    toc = navigationParser.parseNcx(ncxStream);
                }
            }
        }

        if (toc == null) {
            toc = new ArrayList<>();
        }

        // 缓存结果
        cache.setParsedResult(cacheKey, new ArrayList<>(toc));

        return toc;
    }

    /**
     * 只解析资源列表（按需加载）
     * 性能优化：避免解析整个EPUB文件，只读取资源部分
     * 
     * @return 资源列表
     * @throws BaseEpubException 解析异常
     */
    public List<EpubResource> parseResourcesOnly() throws BaseEpubException, java.io.IOException, EpubPathValidationException {
        // 获取缓存
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(epubFile);
        String cacheKey = "resourcesOnly:" + epubFile.getAbsolutePath();

        // 尝试从缓存获取
        @SuppressWarnings("unchecked")
        List<EpubResource> cachedResources = (List<EpubResource>) cache.getParsedResult(cacheKey);
        if (cachedResources != null) {
            List<EpubResource> result = new ArrayList<>(cachedResources.size());
            for (EpubResource resource : cachedResources) {
                EpubResource copy = new EpubResource(resource);
                if (copy.getEpubFile() == null) {
                    copy.setEpubFile(epubFile);
                }
                result.add(copy);
            }
            return result;
        }

        // ZIP Bomb 防护
        try (java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(epubFile)) {
            ZipBombProtection.validateZipFileSafety(zipFile);
        }

        // 读取container.xml获取OPF文件路径
        String container = fileReader.readContent(CONTAINER_FILE_PATH);
        if (container == null) {
            throw new EpubFormatException("Container file not found", epubFile, CONTAINER_FILE_PATH);
        }

        String opfPath = extractRootFilePath(container);
        String opfDir = extractRootFileDir(opfPath);

        // 读取OPF内容
        String opfContent = fileReader.readContent(opfPath);
        if (opfContent == null) {
            throw new EpubFormatException("OPF file not found", epubFile, opfPath);
        }

        // 只解析一次OPF Document
        org.jsoup.nodes.Document opfDocument = org.jsoup.Jsoup.parse(opfContent, "", org.jsoup.parser.Parser.xmlParser());

        // 只解析资源列表
        List<EpubResource> resources = resourceParser.parseResources(opfDocument, opfContent, opfDir);

        // 设置EPUB文件引用
        for (EpubResource resource : resources) {
            if (resource.getEpubFile() == null) {
                resource.setEpubFile(epubFile);
            }
        }

        // 缓存结果
        cache.setParsedResult(cacheKey, new ArrayList<>(resources));

        return resources;
    }



}
