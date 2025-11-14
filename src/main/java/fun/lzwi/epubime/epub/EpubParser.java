package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.zip.ZipUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EpubParser {
    public static final String CONTAINER_FILE_PATH = "META-INF/container.xml";
    private File epubFile;

    public EpubParser(File epubFile) {
        this.epubFile = epubFile;
    }

    protected static String readEpubContent(File epubFile, String path) throws EpubParseException {
        try {
            return ZipUtils.getZipFileContent(epubFile, path);
        } catch (IOException e) {
            throw new EpubParseException("Failed to read EPUB file", e);
        }
    }


    /**
     * 获取根文件路径
     *
     * @param containerContent 容器文件内容
     * @return 根文件路径
     */
    protected static String getRootFilePath(String containerContent) {
        // 尝试从缓存获取 - 使用静态方法无法直接获取epubFile，所以暂时不缓存
        int start = containerContent.indexOf("full-path=\"");
        int end = containerContent.indexOf("\"", start + 11);
        return containerContent.substring(start + 11, end);
    }

    /**
     * 获取根文件目录
     *
     * @param rootFilePath 根文件路径
     * @return 根文件目录
     */
    protected static String getRootFileDir(String rootFilePath) {
        // 尝试从缓存获取 - 使用静态方法无法直接获取epubFile，所以暂时不缓存
        int start = rootFilePath.lastIndexOf("/");
        return rootFilePath.substring(0, start + 1);
    }

    protected static Metadata parseMetadata(String opfContent) {
        // 尝试从缓存获取 - 使用静态方法无法直接获取epubFile，所以暂时不缓存
        Objects.requireNonNull(opfContent);
        Metadata metadata = new Metadata();        Jsoup.parse(opfContent, Parser.xmlParser()).select("metadata").forEach(meta -> {
            meta.children().forEach(child -> {
                switch (child.tagName()) {
                    case "dc:title":
                        metadata.setTitle(child.text());
                        break;
                    case "dc:creator":
                        metadata.setCreator(child.text());
                        break;
                    case "dc:language":
                        metadata.setLanguage(child.text());
                        break;
                    case "dc:identifier":
                        metadata.setIdentifier(child.text());
                        break;
                    case "dc:publisher":
                        metadata.setPublisher(child.text());
                        break;
                    case "dc:date":
                        metadata.setDate(child.text());
                        break;
                    case "dc:description":
                        metadata.setDescription(child.text());
                        break;
                    case "dc:subject":
                        metadata.addSubject(child.text());
                        break;
                    case "dc:type":
                        metadata.setType(child.text());
                        break;
                    case "dc:format":
                        metadata.setFormat(child.text());
                        break;
                    case "dc:source":
                        metadata.setSource(child.text());
                        break;
                    //                    case "dc:relation":

                    //                        metadata.addRelation(child.text());

                    //                        break;

                    //                    case "dc:coverage":

                    //                        metadata.addCoverage(child.text());

                    //                        break;
                    case "dc:rights":
                        metadata.setRights(child.text());
                        break;
                    case "dc:contributor":
                        metadata.addContributor(child.text());
                        break;
                    case "meta":
                        if (child.attr("name").equals("cover")) {
                            metadata.setCover(child.attr("content"));
                        } else if (child.attr("property").equals("dcterms:rightsHolder")) {
                            metadata.setRightsHolder(child.text());
                        } else if (child.attr("property").equals("dcterms:modified")) {
                            metadata.setModified(child.text());
                        }
                        break;
                    default:
                        break;
                }
            });
        });
        return metadata;
    }

    protected static String getNcxPath(String opfContent, String opfDir) {
        Objects.requireNonNull(opfContent);
        Document document = Jsoup.parse(opfContent);
        String id = document.select("spine").attr("toc");
        String selector = String.format("manifest>item[id=\"%s\"]", id);
        Element ncxItem = document.select(selector).first();
        return opfDir + ncxItem.attr("href");
    }

    protected static List<EpubChapter> parseNcx(String tocContent) {
        Objects.requireNonNull(tocContent);
        return Jsoup.parse(tocContent).select("navMap>navPoint").stream().map(navPoint -> {
            EpubChapter chapter = new EpubChapter();
            chapter.setTitle(navPoint.select("navLabel>text").text());
            chapter.setContent(navPoint.select("content").attr("src"));
            return chapter;
        }).collect(java.util.stream.Collectors.toList());
    }

    protected static String getNavPath(String opfContent, String opfDir) {
        Objects.requireNonNull(opfContent);
        Element navItem = Jsoup.parse(opfContent).select("manifest>item[properties=\"nav\"]").first();
        if (navItem != null) {
            return opfDir + navItem.attr("href");
        }
        return null;
    }

    protected static List<EpubChapter> parseNav(String navContent) {
        Objects.requireNonNull(navContent);
        return Jsoup.parse(navContent).select("nav>ol>li>a, nav>ul>li>a").stream().map(a -> {
            EpubChapter chapter = new EpubChapter();
            chapter.setTitle(a.text());
            chapter.setContent(a.attr("href"));
            return chapter;
        }).collect(java.util.stream.Collectors.toList());
    }

    protected static List<EpubResource> parseResources(String opfContent, String opfDir, File epubFile) throws EpubParseException {
        Objects.requireNonNull(opfContent);
        
        // 尝试从缓存获取
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(epubFile);
        String cacheKey = "resources:" + opfContent.hashCode() + ":" + opfDir;
        @SuppressWarnings("unchecked")
        List<EpubResource> cachedResult = (List<EpubResource>) cache.getParsedResultCache().get(cacheKey);
        if (cachedResult != null) {
            return new ArrayList<>(cachedResult);
        }
        
        Document document = Jsoup.parse(opfContent);
        List<EpubResource> resources = new ArrayList<>();
        
        for (Element item : document.select("manifest>item")) {
            EpubResource res = new EpubResource();
            res.setId(item.attr("id"));
            res.setHref(opfDir + item.attr("href"));
            res.setType(item.attr("media-type"));
            // 设置EPUB文件引用，以便按需流式加载资源
            res.setEpubFile(epubFile);
            // 不立即加载数据，仅设置文件引用，提供按需加载的能力
            resources.add(res);
        }
        
        // 缓存结果
        cache.getParsedResultCache().put(cacheKey, new ArrayList<>(resources));
        return resources;
    }


    // 解析EPUB文件并返回EpubBook对象
    public EpubBook parse() throws EpubParseException {
        EpubBook book = new EpubBook();
        
        // 获取当前EPUB文件的缓存
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(epubFile);
        String cacheKey = "fullParse:" + epubFile.getAbsolutePath();
        
        // 尝试从缓存获取完整解析结果
        EpubBook cachedBook = (EpubBook) cache.getParsedResultCache().get(cacheKey);
        if (cachedBook != null) {
            return new EpubBook(cachedBook);
        }
        
        try {
            // 第一次ZIP访问：读取container.xml和OPF文件
            List<String> firstBatchPaths = new ArrayList<>();
            firstBatchPaths.add(CONTAINER_FILE_PATH);
            
            // 先读取container.xml以获取OPF文件路径
            java.util.Map<String, String> firstBatchContents = ZipUtils.getMultipleZipFileContents(epubFile, firstBatchPaths);
            String container = firstBatchContents.get(CONTAINER_FILE_PATH);
            Objects.requireNonNull(container);
            String opfPath = getRootFilePath(container);
            String opfDir = getRootFileDir(opfPath);
            
            // 重新设置文件路径列表，包含OPF文件
            firstBatchPaths.add(opfPath);
            firstBatchContents = ZipUtils.getMultipleZipFileContents(epubFile, firstBatchPaths);
            container = firstBatchContents.get(CONTAINER_FILE_PATH);
            String opfContent = firstBatchContents.get(opfPath);
            
            // 元数据
            Metadata metadata = parseMetadata(opfContent);
            book.setMetadata(metadata);

            // 获取章节文件路径
            String ncxPath = getNcxPath(opfContent, opfDir);
            
            String navPath = getNavPath(opfContent, opfDir);
            
            // 第二次ZIP访问：读取章节文件
            List<String> secondBatchPaths = new ArrayList<>();
            secondBatchPaths.add(ncxPath);
            if (navPath != null) {
                secondBatchPaths.add(navPath);
            }
            
            java.util.Map<String, String> secondBatchContents = ZipUtils.getMultipleZipFileContents(epubFile, secondBatchPaths);
            String ncxContent = secondBatchContents.get(ncxPath);
            String navContent = navPath != null ? secondBatchContents.get(navPath) : null;
            
            // 解析章节文件，获取章节内容
            List<EpubChapter> ncx = parseNcx(ncxContent);
            book.setNcx(ncx);
            
            // 解析 nav
            if (navContent != null) {
                List<EpubChapter> nav = parseNav(navContent);
                book.setNav(nav);
            }

            // 解析资源文件，获取资源数据 - 现在只设置引用，不加载数据
            List<EpubResource> resources = parseResources(opfContent, opfDir, epubFile);
            book.setResources(resources);
            
            // 缓存完整解析结果
            cache.getParsedResultCache().put(cacheKey, new EpubBook(book));
        } catch (IOException e) {
            throw new EpubParseException("Failed to read EPUB file", e);
        }
        
        return book;
    }
    
    /**
     * 流式处理HTML章节内容，避免将整个文件加载到内存中
     * @param epubFile EPUB文件
     * @param htmlFileName HTML文件名
     * @param processor 处理HTML内容的消费者函数
     * @throws EpubParseException
     */
    public static void processHtmlChapterContent(File epubFile, String htmlFileName, 
                                                 Consumer<InputStream> processor) throws EpubParseException {
        try {
            ZipUtils.processHtmlContent(epubFile, htmlFileName, processor);
        } catch (IOException e) {
            throw new EpubParseException("Failed to process HTML chapter content", e);
        }
    }
    
    /**
     * 流式处理多个HTML章节内容
     * @param epubFile EPUB文件
     * @param htmlFileNames HTML文件名列表
     * @param processor 处理每个HTML内容的消费者函数
     * @throws EpubParseException
     */
    public static void processMultipleHtmlChapters(File epubFile, List<String> htmlFileNames,
                                                   BiConsumer<String, InputStream> processor) throws EpubParseException {
        try {
            ZipUtils.processMultipleHtmlContents(epubFile, htmlFileNames, processor);
        } catch (IOException e) {
            throw new EpubParseException("Failed to process multiple HTML chapters", e);
        }
    }
}
