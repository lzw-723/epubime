package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.zip.ZipFileManager;
import fun.lzwi.epubime.zip.ZipUtils;
import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;

import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private File epubFile;

    /**
     * 构造函数
     *
     * @param epubFile EPUB文件
     */
    public EpubParser(File epubFile) {
        this.epubFile = epubFile;
    }

    /**
     * 读取EPUB文件中的指定路径内容
     *
     * @param epubFile EPUB文件
     * @param path     文件路径
     * @return 文件内容
     * @throws EpubParseException 解析异常
     */
    protected static String readEpubContent(File epubFile, String path) throws EpubParseException {
        try {
            return ZipUtils.getZipFileContent(epubFile, path);
        } catch (IOException e) {
            throw new EpubParseException("Failed to read EPUB file", e);
        }
    }

    /**
     * 从容器文件内容中获取根文件路径
     *
     * @param containerContent 容器文件内容
     * @return 根文件路径
     */
    protected static String getRootFilePath(String containerContent) {
        // Try to get from cache - static method cannot directly get epubFile, so temporarily not cached
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
        // Try to get from cache - static method cannot directly get epubFile, so temporarily not cached
        int start = rootFilePath.lastIndexOf("/");
        return rootFilePath.substring(0, start + 1);
    }

    /**
     * 解析OPF内容中的元数据
     *
     * @param opfContent OPF文件内容
     * @return 元数据对象
     */
    protected static Metadata parseMetadata(String opfContent) {
        // Try to get from cache - static method cannot directly get epubFile, so temporarily not cached
        Objects.requireNonNull(opfContent);
        Metadata metadata = new Metadata();
        Document opfDocument = Jsoup.parse(opfContent, Parser.xmlParser());

        // 解析package元素获取unique-identifier属性
        Element packageElement = opfDocument.selectFirst("package");
        if (packageElement != null) {
            String uniqueIdentifierId = packageElement.attr("unique-identifier");
            if (!uniqueIdentifierId.isEmpty()) {
                // 查找对应的dc:identifier元素并设置为uniqueIdentifier
                opfDocument.select("metadata > dc\\:identifier").forEach(identifier -> {
                    String id = identifier.attr("id");
                    if (uniqueIdentifierId.equals(id)) {
                        metadata.setUniqueIdentifier(identifier.text());
                    }
                });
            }
        }

        opfDocument.select("metadata").forEach(meta -> {
            meta.children().forEach(child -> {
                switch (child.tagName()) {
                    case "dc:title":
                        metadata.addTitle(child.text());
                        break;
                    case "dc:creator":
                        metadata.addCreator(child.text());
                        break;
                    case "dc:language":
                        metadata.addLanguage(child.text());
                        break;
                    case "dc:identifier":
                        metadata.addIdentifier(child.text());
                        break;
                    case "dc:publisher":
                        metadata.addPublisher(child.text());
                        break;
                    case "dc:date":
                        metadata.addDate(child.text());
                        break;
                    case "dc:description":
                        metadata.addDescription(child.text());
                        break;
                    case "dc:subject":
                        metadata.addSubject(child.text());
                        break;
                    case "dc:type":
                        metadata.addType(child.text());
                        break;
                    case "dc:format":
                        metadata.addFormat(child.text());
                        break;
                    case "dc:source":
                        metadata.addSource(child.text());
                        break;
                    //                    case "dc:relation":

                    //                        metadata.addRelation(child.text());

                    //                        break;

                    //                    case "dc:coverage":

                    //                        metadata.addCoverage(child.text());

                    //                        break;
                    case "dc:rights":
                        metadata.addRights(child.text());
                        break;
                    case "dc:contributor":
                        metadata.addContributor(child.text());
                        break;
                    case "meta":
                        String property = child.attr("property");
                        String name = child.attr("name");
                        String content = child.text(); // Use text() for meta elements with property attributes

                        if (name.equals("cover")) {

                            // 保留对旧的meta name="cover"方式的支持，但优先级较低

                            if (metadata.getCover() == null || metadata.getCover().isEmpty()) {

                                metadata.setCover(child.attr("content"));

                            }

                        } else if (property.equals("dcterms:rightsHolder")) {
                            metadata.setRightsHolder(content);
                        } else if (property.equals("dcterms:modified")) {
                            metadata.setModified(content);
                        } else if (property.equals("rendition:layout")) {
                            metadata.setLayout(content);
                        } else if (property.equals("rendition:orientation")) {
                            metadata.setOrientation(content);
                        } else if (property.equals("rendition:spread")) {
                            metadata.setSpread(content);
                        } else if (property.equals("rendition:viewport")) {

                            metadata.setViewport(content);

                        } else if (property.equals("rendition:media")) {

                            metadata.setMedia(content);

                        } else if (property.equals("rendition:flow")) {

                            metadata.setFlow(content);

                        } else if (property.equals("rendition:align-x-center")) {

                            metadata.setAlignXCenter("true".equalsIgnoreCase(content) || "yes".equalsIgnoreCase(content) || "1".equals(content));

                        } else if (property.equals("schema:accessibilityFeature")) {

                            metadata.addAccessibilityFeature(content);

                        } else if (property.equals("schema:accessibilityHazard")) {

                            metadata.addAccessibilityHazard(content);

                        } else if (property.equals("schema:accessibilitySummary")) {

                            metadata.addAccessibilitySummary(content);

                        }
                        break;
                    default:
                        break;
                }
            });
        });
        return metadata;
    }

    /**
     * 从OPF内容中获取NCX文件路径
     *
     * @param opfContent OPF文件内容
     * @param opfDir     OPF文件目录
     * @return NCX文件路径
     */
    protected static String getNcxPath(String opfContent, String opfDir) {
        Objects.requireNonNull(opfContent);
        Document document = Jsoup.parse(opfContent);
        String id = document.select("spine").attr("toc");
        String selector = String.format("manifest>item[id=\"%s\"]", id);
        Element ncxItem = document.select(selector).first();
        return opfDir + ncxItem.attr("href");
    }

    /**
     * 解析NCX目录内容
     *
     * @param tocContent NCX目录内容
     * @return 章节列表
     */
    protected static List<EpubChapter> parseNcx(String tocContent) {
        Objects.requireNonNull(tocContent);
        return Jsoup.parse(tocContent).select("navMap>navPoint").stream().map(navPoint -> {
            EpubChapter chapter = new EpubChapter();
            chapter.setTitle(navPoint.select("navLabel>text").text());
            chapter.setContent(navPoint.select("content").attr("src"));
            return chapter;
        }).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 从OPF内容中获取NAV文件路径
     *
     * @param opfContent OPF文件内容
     * @param opfDir     OPF文件目录
     * @return NAV文件路径，如果不存在则返回null
     */
    protected static String getNavPath(String opfContent, String opfDir) {
        Objects.requireNonNull(opfContent);
        Element navItem = Jsoup.parse(opfContent).select("manifest>item[properties=\"nav\"]").first();
        if (navItem != null) {
            return opfDir + navItem.attr("href");
        }
        return null;
    }

    /**

     * 解析NAV目录内容

     *

     * @param navContent NAV目录内容

     * @return 章节列表

     */

    protected static List<EpubChapter> parseNav(String navContent) {

        Objects.requireNonNull(navContent);



        Document doc = Jsoup.parse(navContent);



        List<EpubChapter> chapters = new ArrayList<>();



        // 首先查找toc类型的nav元素（这是主要内容导航）

        Element navElement = doc.selectFirst("nav[epub:type= toc]");

        if (navElement == null) {

            // 尝试查找其他toc类型的表达方式

            navElement = doc.selectFirst("nav[epub:type='toc']");

        }

        if (navElement == null) {

            navElement = doc.selectFirst("nav[epub:type=\"toc\"]");

        }

        if (navElement == null) {

            // 如果没有找到toc类型的nav，则使用第一个nav元素

            navElement = doc.selectFirst("nav");

        }



        if (navElement != null) {



            // 查找顶级的ol或ul元素



            Elements topLists = navElement.select("> ol, > ul");



            for (Element list : topLists) {



                chapters.addAll(parseNavList(list));



            }



        }



        return chapters;



    }



    /**

     * 递归解析导航列表

     *

     * @param listElement ol或ul元素

     * @return 章节列表

     */

    private static List<EpubChapter> parseNavList(Element listElement) {



        List<EpubChapter> chapters = new ArrayList<>();



        for (Element li : listElement.children()) {



            if (!"li".equalsIgnoreCase(li.tagName())) {



                continue;



            }



            EpubChapter chapter = null;

            Element link = li.selectFirst("a");



            if (link != null) {



                chapter = new EpubChapter();



                // 设置章节ID（如果存在）

                String id = link.attr("id");

                if (id != null && !id.isEmpty()) {

                    chapter.setId(id);

                }



                chapter.setTitle(link.text());

                chapter.setContent(link.attr("href"));



                // 处理epub:type属性

                String epubType = link.attr("epub:type");

                if (epubType != null && !epubType.isEmpty()) {

                    // 可以根据需要存储epub:type信息

                    // 这里暂时不处理，但保留扩展的可能性

                }



            }



            // 查找嵌套的列表（子章节）

            Elements nestedLists = li.select("> ol, > ul");



            for (Element nestedList : nestedLists) {



                if (chapter != null) {



                    List<EpubChapter> children = parseNavList(nestedList);



                    for (EpubChapter child : children) {



                        chapter.addChild(child);



                    }



                } else {



                    // 如果li标签中没有a标签但有嵌套列表，解析这些嵌套列表



                    List<EpubChapter> children = parseNavList(nestedList);



                    chapters.addAll(children);



                }



            }



            // 如果当前li标签包含链接，则添加到章节列表中



            if (chapter != null) {



                chapters.add(chapter);



            }



        }



        return chapters;



    }

    /**
     * 解析OPF内容中的资源文件列表
     * @param opfContent OPF文件内容
     * @param opfDir OPF文件目录
     * @param epubFile EPUB文件
     * @return 资源文件列表
     * @throws EpubParseException 解析异常
     */
    protected static List<EpubResource> parseResources(String opfContent, String opfDir, File epubFile) throws EpubParseException {
        Objects.requireNonNull(opfContent);
        
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(epubFile);
        String cacheKey = "resources:" + opfContent.hashCode() + ":" + opfDir;
        @SuppressWarnings("unchecked")
        List<EpubResource> cachedResult = (List<EpubResource>) cache.getParsedResult(cacheKey);
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
            res.setProperties(item.attr("properties").isEmpty() ? null : item.attr("properties"));
            res.setFallback(item.attr("fallback").isEmpty() ? null : item.attr("fallback"));
            // Set EPUB file reference for on-demand streaming loading of resources
            res.setEpubFile(epubFile);
            // Do not load data immediately, only set file reference, provide on-demand loading capability
            resources.add(res);
        }
        
        // Cache result
        cache.setParsedResult(cacheKey, new ArrayList<>(resources));
        return resources;
    }

    /**
     * 解析EPUB文件并返回EpubBook对象
     *
     * @return 解析后的EpubBook对象
     * @throws EpubParseException 解析异常
     */
    public EpubBook parse() throws EpubParseException {
        EpubBook book = new EpubBook();

        // Get cache for current EPUB file
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(epubFile);
        String cacheKey = "fullParse:" + epubFile.getAbsolutePath();

        // Try to get complete parsing result from cache

        EpubBook cachedBook = (EpubBook) cache.getParsedResult(cacheKey);

        if (cachedBook != null) {

            return new EpubBook(cachedBook);

        }

        try {
            // First ZIP access: read container.xml and OPF file
            List<String> firstBatchPaths = new ArrayList<>();
            firstBatchPaths.add(CONTAINER_FILE_PATH);

            // First read container.xml to get OPF file path
            java.util.Map<String, String> firstBatchContents = ZipUtils.getMultipleZipFileContents(epubFile,
                    firstBatchPaths);
            String container = firstBatchContents.get(CONTAINER_FILE_PATH);
            Objects.requireNonNull(container);
            String opfPath = getRootFilePath(container);
            String opfDir = getRootFileDir(opfPath);

            // Reset file path list to include OPF file

            firstBatchPaths.add(opfPath);

            firstBatchContents = ZipUtils.getMultipleZipFileContents(epubFile, firstBatchPaths);

            // No need to reassign container as it's not used after this point

            String opfContent = firstBatchContents.get(opfPath);

            // Metadata
            Metadata metadata = parseMetadata(opfContent);
            book.setMetadata(metadata);

            // Get chapter file paths
            String ncxPath = getNcxPath(opfContent, opfDir);

            String navPath = getNavPath(opfContent, opfDir);

            // Second ZIP access: read chapter files
            List<String> secondBatchPaths = new ArrayList<>();
            secondBatchPaths.add(ncxPath);
            if (navPath != null) {
                secondBatchPaths.add(navPath);
            }

            java.util.Map<String, String> secondBatchContents = ZipUtils.getMultipleZipFileContents(epubFile,
                    secondBatchPaths);
            String ncxContent = secondBatchContents.get(ncxPath);
            String navContent = navPath != null ? secondBatchContents.get(navPath) : null;

            // Parse chapter files to get chapter content
            List<EpubChapter> ncx = parseNcx(ncxContent);
            book.setNcx(ncx);

            // Parse nav
            if (navContent != null) {
                List<EpubChapter> nav = parseNav(navContent);
                book.setNav(nav);
                
                // 解析其他类型的导航（地标、页面列表等）
                List<EpubChapter> landmarks = parseNavByType(navContent, "landmarks");
                book.setLandmarks(landmarks);
                
                List<EpubChapter> pageList = parseNavByType(navContent, "page-list");
                book.setPageList(pageList);
            }

            // Parse resource files to get resource data - now only set references, do not load data
            List<EpubResource> resources = parseResources(opfContent, opfDir, epubFile);
            book.setResources(resources);

            // Cache complete parsing result

            cache.setParsedResult(cacheKey, new EpubBook(book));
        } catch (IOException e) {
            throw new EpubParseException("Failed to read EPUB file", e);
        } finally {
            // Clean up ZIP file handle after parsing
            ZipFileManager.getInstance().closeCurrentZipFile();
        }

        return book;
    }

    /**
     * 根据nav类型解析导航内容
     *
     * @param navContent NAV目录内容
     * @param navType 导航类型（如toc、landmarks、page-list等）
     * @return 章节列表
     */
    protected static List<EpubChapter> parseNavByType(String navContent, String navType) {
        Objects.requireNonNull(navContent);
        Objects.requireNonNull(navType);

        Document doc = Jsoup.parse(navContent);

        List<EpubChapter> chapters = new ArrayList<>();

        // 查找特定类型的nav元素
        Element navElement = doc.selectFirst("nav[epub:type= " + navType + "]");
        if (navElement == null) {
            navElement = doc.selectFirst("nav[epub:type='" + navType + "']");
        }
        if (navElement == null) {
            navElement = doc.selectFirst("nav[epub:type=\"" + navType + "\"]");
        }

        if (navElement != null) {
            // 查找顶级的ol或ul元素
            Elements topLists = navElement.select("> ol, > ul");

            for (Element list : topLists) {
                chapters.addAll(parseNavList(list));
            }
        }

        return chapters;
    }

    /**
     * 解析EPUB文件并返回EpubBook对象，但不使用缓存
     *
     * @return 解析后的EpubBook对象
     * @throws EpubParseException 解析异常
     */
    public EpubBook parseWithoutCache() throws EpubParseException {
        // Clean up current thread's ZIP file handle
        ZipFileManager.getInstance().cleanup();

        try {
            return parse();
        } finally {
            // Ensure cleanup
            ZipFileManager.getInstance().cleanup();
        }
    }

    /**
     * 流式处理HTML章节内容以避免将整个文件加载到内存中
     *
     * @param epubFile     EPUB文件
     * @param htmlFileName HTML文件名
     * @param processor    消费者函数，用于处理HTML内容
     * @throws EpubParseException 解析异常
     */
    public static void processHtmlChapterContent(File epubFile, String htmlFileName, Consumer<InputStream> processor) throws EpubParseException {
        try {
            ZipUtils.processHtmlContent(epubFile, htmlFileName, processor);
        } catch (IOException e) {
            throw new EpubParseException("Failed to process HTML chapter content", e);
        }
    }

    /**
     * 流式处理多个HTML章节内容
     *
     * @param epubFile      EPUB文件
     * @param htmlFileNames HTML文件名列表
     * @param processor     消费者函数，用于处理每个HTML内容
     * @throws EpubParseException 解析异常
     */
    public static void processMultipleHtmlChapters(File epubFile, List<String> htmlFileNames, BiConsumer<String,
            InputStream> processor) throws EpubParseException {
        try {
            ZipUtils.processMultipleHtmlContents(epubFile, htmlFileNames, processor);
        } catch (IOException e) {
            throw new EpubParseException("Failed to process multiple HTML chapters", e);
        }
    }
}