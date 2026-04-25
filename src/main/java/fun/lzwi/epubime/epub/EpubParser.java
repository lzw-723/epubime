package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.exception.EpubFormatException;
import fun.lzwi.epubime.exception.BaseEpubException;
import fun.lzwi.epubime.exception.EpubPathValidationException;
import fun.lzwi.epubime.parser.MetadataParser;
import fun.lzwi.epubime.parser.NavigationParser;
import fun.lzwi.epubime.parser.ResourceParser;
import fun.lzwi.epubime.parser.XmlUtils;
import fun.lzwi.epubime.zip.ZipBombProtection;
import fun.lzwi.epubime.zip.ZipManagedInputStream;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jsoup.nodes.Document;

/**
 * EPUB解析器类
 * 负责解析EPUB文件内容并提取元数据、章节和资源信息，遵循单一职责原则
 */
public class EpubParser {
    /** 容器文件路径 */
    public static final String CONTAINER_FILE_PATH = "META-INF/container.xml";

    private final File epubFile;
    private final EpubFileReader fileReader;
    private final MetadataParser metadataParser;
    private final NavigationParser navigationParser;
    private final ResourceParser resourceParser;

    private static class OpfContext {
        final Document opfDocument;
        final String opfContent;
        final String opfDir;
        final String epubVersion;

        OpfContext(Document opfDocument, String opfContent, String opfDir, String epubVersion) {
            this.opfDocument = opfDocument;
            this.opfContent = opfContent;
            this.opfDir = opfDir;
            this.epubVersion = epubVersion;
        }
    }

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

    public EpubFileReader getFileReader() {
        return fileReader;
    }

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

    private String extractRootFileDir(String rootFilePath) {
        int lastSlashIndex = rootFilePath.lastIndexOf("/");
        return lastSlashIndex == -1 ? "" : rootFilePath.substring(0, lastSlashIndex + 1);
    }

    private String detectEpubVersion(Document opfDocument) {
        org.jsoup.nodes.Element packageElement = opfDocument.selectFirst("package");
        if (packageElement != null) {
            String version = packageElement.attr("version");
            if (!version.isEmpty()) {
                return version;
            }
        }
        return "3.0";
    }

    private OpfContext prepareOpfContext() throws BaseEpubException, EpubPathValidationException, IOException {
        try (java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(epubFile)) {
            ZipBombProtection.validateZipFileSafety(zipFile);
        }

        String container = fileReader.readContent(CONTAINER_FILE_PATH);
        if (container == null) {
            throw new EpubFormatException("Container file not found", epubFile, CONTAINER_FILE_PATH);
        }

        String opfPath = extractRootFilePath(container);
        String opfDir = extractRootFileDir(opfPath);

        String opfContent = fileReader.readContent(opfPath);
        if (opfContent == null) {
            throw new EpubFormatException("OPF file not found", epubFile, opfPath);
        }

        Document opfDocument = org.jsoup.Jsoup.parse(opfContent, "", org.jsoup.parser.Parser.xmlParser());
        String epubVersion = detectEpubVersion(opfDocument);

        return new OpfContext(opfDocument, opfContent, opfDir, epubVersion);
    }

    public EpubBook parse() throws BaseEpubException, IOException, EpubPathValidationException {
        return parseInternal(true);
    }

    public EpubBook parseWithoutCache() throws BaseEpubException, IOException, EpubPathValidationException {
        return parseInternal(false);
    }

    private EpubBook parseInternal(boolean useCache) throws BaseEpubException, IOException, EpubPathValidationException {
        EpubBook book = new EpubBook();

        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(epubFile);
        String cacheKey = "fullParse:" + epubFile.getAbsolutePath();

        if (useCache) {
            EpubBook cachedBook = (EpubBook) cache.getParsedResult(cacheKey);
            if (cachedBook != null) {
                return new EpubBook(cachedBook);
            }
        }

        OpfContext ctx = prepareOpfContext();
        book.setVersion(ctx.epubVersion);
        book.setMetadata(metadataParser.parseMetadata(ctx.opfDocument, ctx.opfContent, ctx.epubVersion));

        List<EpubResource> resources = resourceParser.parseResources(ctx.opfDocument, ctx.opfContent, ctx.opfDir);
        book.setResources(resources);

        String ncxPath = null;
        String navPath = null;
        try {
            ncxPath = resourceParser.getNcxPath(ctx.opfDocument, ctx.opfContent, ctx.opfDir);
        } catch (IllegalArgumentException e) {
            // NCX路径可选
        }
        navPath = resourceParser.getNavPath(ctx.opfDocument, ctx.opfContent, ctx.opfDir);

        if (ncxPath != null) {
            try (ZipManagedInputStream ncxStream = ZipManagedInputStream.open(fileReader.epubFile, ncxPath)) {
                if (ncxStream != null) {
                    book.setNcx(navigationParser.parseNcx(ncxStream));
                }
            }
        }

        if (navPath != null) {
            String navContent = null;
            try (ZipManagedInputStream navStream = ZipManagedInputStream.open(fileReader.epubFile, navPath)) {
                if (navStream != null) {
                    navContent = XmlUtils.readStreamToString(navStream);
                }
            }
            if (navContent != null) {
                book.setNav(navigationParser.parseNav(navContent));
                book.setLandmarks(navigationParser.parseNavByType(navContent, "landmarks"));
                book.setPageList(navigationParser.parseNavByType(navContent, "page-list"));
            }
        }

        if (useCache) {
            cache.setParsedResult(cacheKey, new EpubBook(book));
        }

        return book;
    }

    public Metadata parseMetadataOnly() throws BaseEpubException, IOException, EpubPathValidationException {
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(epubFile);
        String cacheKey = "metadataOnly:" + epubFile.getAbsolutePath();

        Metadata cachedMetadata = (Metadata) cache.getParsedResult(cacheKey);
        if (cachedMetadata != null) {
            return new Metadata(cachedMetadata);
        }

        OpfContext ctx = prepareOpfContext();
        Metadata metadata = metadataParser.parseMetadata(ctx.opfDocument, ctx.opfContent, ctx.epubVersion);

        cache.setParsedResult(cacheKey, new Metadata(metadata));
        return metadata;
    }

    public List<EpubChapter> parseTableOfContentsOnly() throws BaseEpubException, IOException, EpubPathValidationException {
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(epubFile);
        String cacheKey = "tocOnly:" + epubFile.getAbsolutePath();

        @SuppressWarnings("unchecked")
        List<EpubChapter> cachedToc = (List<EpubChapter>) cache.getParsedResult(cacheKey);
        if (cachedToc != null) {
            return new ArrayList<>(cachedToc);
        }

        OpfContext ctx = prepareOpfContext();

        String ncxPath = null;
        String navPath = null;
        try {
            ncxPath = resourceParser.getNcxPath(ctx.opfDocument, ctx.opfContent, ctx.opfDir);
        } catch (IllegalArgumentException e) {
            // NCX路径可选
        }
        navPath = resourceParser.getNavPath(ctx.opfDocument, ctx.opfContent, ctx.opfDir);

        List<EpubChapter> toc = null;

        if (navPath != null) {
            try (ZipManagedInputStream navStream = ZipManagedInputStream.open(fileReader.epubFile, navPath)) {
                if (navStream != null) {
                    String navContent = XmlUtils.readStreamToString(navStream);
                    toc = navigationParser.parseNav(navContent);
                }
            }
        }

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

        cache.setParsedResult(cacheKey, new ArrayList<>(toc));
        return toc;
    }

    public List<EpubResource> parseResourcesOnly() throws BaseEpubException, IOException, EpubPathValidationException {
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(epubFile);
        String cacheKey = "resourcesOnly:" + epubFile.getAbsolutePath();

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

        OpfContext ctx = prepareOpfContext();

        List<EpubResource> resources = resourceParser.parseResources(ctx.opfDocument, ctx.opfContent, ctx.opfDir);

        for (EpubResource resource : resources) {
            if (resource.getEpubFile() == null) {
                resource.setEpubFile(epubFile);
            }
        }

        cache.setParsedResult(cacheKey, new ArrayList<>(resources));
        return resources;
    }

}
