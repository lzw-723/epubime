package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.zip.ZipFileManager;
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
     * Get root file path
     *
     * @param containerContent Container file content
     * @return Root file path
     */
    protected static String getRootFilePath(String containerContent) {
        // Try to get from cache - static method cannot directly get epubFile, so temporarily not cached
        int start = containerContent.indexOf("full-path=\"");
        int end = containerContent.indexOf("\"", start + 11);
        return containerContent.substring(start + 11, end);
    }

    /**
     * Get root file directory
     *
     * @param rootFilePath Root file path
     * @return Root file directory
     */
    protected static String getRootFileDir(String rootFilePath) {
        // Try to get from cache - static method cannot directly get epubFile, so temporarily not cached
        int start = rootFilePath.lastIndexOf("/");
        return rootFilePath.substring(0, start + 1);
    }

    protected static Metadata parseMetadata(String opfContent) {
        // Try to get from cache - static method cannot directly get epubFile, so temporarily not cached
        Objects.requireNonNull(opfContent);
        Metadata metadata = new Metadata();
        Jsoup.parse(opfContent, Parser.xmlParser()).select("metadata").forEach(meta -> {
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
        
        // Try to get from cache
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
            // Set EPUB file reference for on-demand streaming loading of resources
            res.setEpubFile(epubFile);
            // Do not load data immediately, only set file reference, provide on-demand loading capability
            resources.add(res);
        }
        
        // Cache result
        cache.getParsedResultCache().put(cacheKey, new ArrayList<>(resources));
        return resources;
    }

    // Parse EPUB file and return EpubBook object
    public EpubBook parse() throws EpubParseException {
        EpubBook book = new EpubBook();
        
        // Get cache for current EPUB file
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(epubFile);
        String cacheKey = "fullParse:" + epubFile.getAbsolutePath();
        
        // Try to get complete parsing result from cache
        EpubBook cachedBook = (EpubBook) cache.getParsedResultCache().get(cacheKey);
        if (cachedBook != null) {
            return new EpubBook(cachedBook);
        }
        
        try {
            // First ZIP access: read container.xml and OPF file
            List<String> firstBatchPaths = new ArrayList<>();
            firstBatchPaths.add(CONTAINER_FILE_PATH);
            
            // First read container.xml to get OPF file path
            java.util.Map<String, String> firstBatchContents = ZipUtils.getMultipleZipFileContents(epubFile, firstBatchPaths);
            String container = firstBatchContents.get(CONTAINER_FILE_PATH);
            Objects.requireNonNull(container);
            String opfPath = getRootFilePath(container);
            String opfDir = getRootFileDir(opfPath);
            
            // Reset file path list to include OPF file
            firstBatchPaths.add(opfPath);
            firstBatchContents = ZipUtils.getMultipleZipFileContents(epubFile, firstBatchPaths);
            container = firstBatchContents.get(CONTAINER_FILE_PATH);
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
            
            java.util.Map<String, String> secondBatchContents = ZipUtils.getMultipleZipFileContents(epubFile, secondBatchPaths);
            String ncxContent = secondBatchContents.get(ncxPath);
            String navContent = navPath != null ? secondBatchContents.get(navPath) : null;
            
            // Parse chapter files to get chapter content
            List<EpubChapter> ncx = parseNcx(ncxContent);
            book.setNcx(ncx);
            
            // Parse nav
            if (navContent != null) {
                List<EpubChapter> nav = parseNav(navContent);
                book.setNav(nav);
            }

            // Parse resource files to get resource data - now only set references, do not load data
            List<EpubResource> resources = parseResources(opfContent, opfDir, epubFile);
            book.setResources(resources);
            
            // Cache complete parsing result
            cache.getParsedResultCache().put(cacheKey, new EpubBook(book));
        } catch (IOException e) {
            throw new EpubParseException("Failed to read EPUB file", e);
        } finally {
            // Clean up ZIP file handle after parsing
            ZipFileManager.getInstance().closeCurrentZipFile();
        }
        
        return book;
    }
    
    /**
     * Parse EPUB file and return EpubBook object, but do not use cache
     * @return Parsed EpubBook object
     * @throws EpubParseException
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
     * Stream process HTML chapter content to avoid loading entire file into memory
     * @param epubFile EPUB file
     * @param htmlFileName HTML file name
     * @param processor Consumer function to process HTML content
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
     * Stream process multiple HTML chapter contents
     * @param epubFile EPUB file
     * @param htmlFileNames HTML file name list
     * @param processor Consumer function to process each HTML content
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