package fun.lzwi.epubime.epub;



import fun.lzwi.epubime.cache.EpubCacheManager;

import fun.lzwi.epubime.exception.EpubParseException;
import fun.lzwi.epubime.exception.EpubPathValidationException;
import fun.lzwi.epubime.zip.ZipFileManager;

import fun.lzwi.epubime.zip.ZipUtils;

import fun.lzwi.epubime.zip.PathValidator;

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


import fun.lzwi.epubime.exception.EpubZipException;



/**

 * EPUB parser class

 * Responsible for parsing EPUB files and extracting metadata, chapters and resource information

 */

public class EpubParser {
    /**
     * Container file path
     */
    public static final String CONTAINER_FILE_PATH = "META-INF/container.xml";
    private File epubFile;

    /**
     * Constructor
     *
     * @param epubFile EPUB file
     */
    public EpubParser(File epubFile) {
        this.epubFile = epubFile;
    }

    /**
     * Read content at specified path in EPUB file
     *
     * @param epubFile EPUB file
     * @param path     file path
     * @return file content
     * @throws EpubParseException parsing exception
     */
    protected static String readEpubContent(File epubFile, String path) throws EpubParseException {
        // Prevent directory traversal attacks
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
     * Get root file path from container file content
     *
     * @param containerContent container file content
     * @return root file path
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
     * @param rootFilePath root file path
     * @return root file directory
     */
    protected static String getRootFileDir(String rootFilePath) {
        // Try to get from cache - static method cannot directly get epubFile, so temporarily not cached
        int start = rootFilePath.lastIndexOf("/");
        return rootFilePath.substring(0, start + 1);
    }

    /**
     * Parse metadata in OPF content
     *
     * @param opfContent OPF file content
     * @return metadata object
     */
    protected static Metadata parseMetadata(String opfContent) {
        // Try to get from cache - static method cannot directly get epubFile, so temporarily not cached
        Objects.requireNonNull(opfContent);
        Metadata metadata = new Metadata();
        Document opfDocument = Jsoup.parse(opfContent, Parser.xmlParser());

        // Parse package element to get unique-identifier attribute
        Element packageElement = opfDocument.selectFirst("package");
        if (packageElement != null) {
            String uniqueIdentifierId = packageElement.attr("unique-identifier");
            if (!uniqueIdentifierId.isEmpty()) {
                // Find corresponding dc:identifier element and set as uniqueIdentifier
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

                            // Maintain support for old meta name="cover" method, but with lower priority

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
     * Get NCX file path from OPF content
     *
     * @param opfContent OPF file content
     * @param opfDir     OPF file directory
     * @return NCX file path
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
     * Parse NCX table of contents content
     *
     * @param tocContent NCX table of contents content
     * @return list of chapters
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
     * Get NAV file path from OPF content
     *
     * @param opfContent OPF file content
     * @param opfDir     OPF file directory
     * @return NAV file path, returns null if it does not exist
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
     * Parse NAV table of contents content
     *
     * @param navContent NAV table of contents content
     * @return list of chapters
     */

    protected static List<EpubChapter> parseNav(String navContent) {

        Objects.requireNonNull(navContent);


        Document doc = Jsoup.parse(navContent);


        List<EpubChapter> chapters = new ArrayList<>();


        // First find nav element with type toc (this is the main navigation)

        Element navElement = doc.selectFirst("nav[epub:type= toc]");

        if (navElement == null) {

            // Try to find other expressions of toc type

            navElement = doc.selectFirst("nav[epub:type='toc']");

        }

        if (navElement == null) {

            navElement = doc.selectFirst("nav[epub:type=\"toc\"]");

        }

        if (navElement == null) {

            // If no nav element with toc type is found, use the first nav element

            navElement = doc.selectFirst("nav");

        }


        if (navElement != null) {


            // Find top-level ol or ul elements


            Elements topLists = navElement.select("> ol, > ul");


            for (Element list : topLists) {


                chapters.addAll(parseNavList(list));


            }


        }


        return chapters;


    }


    /**
     * Recursively parse navigation list
     *
     * @param listElement ol or ul element
     * @return list of chapters
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


                // Set chapter ID (if it exists)

                String id = link.attr("id");

                if (id != null && !id.isEmpty()) {

                    chapter.setId(id);

                }


                chapter.setTitle(link.text());

                chapter.setContent(link.attr("href"));


                // Process epub:type attribute

                String epubType = link.attr("epub:type");

                if (epubType != null && !epubType.isEmpty()) {

                    // Can store epub:type information as needed

                    // Not processed here, but keep possibility for extension

                }


            }


            // Find nested lists (sub-chapters)

            Elements nestedLists = li.select("> ol, > ul");


            for (Element nestedList : nestedLists) {


                if (chapter != null) {


                    List<EpubChapter> children = parseNavList(nestedList);


                    for (EpubChapter child : children) {


                        chapter.addChild(child);


                    }


                } else {


                    // If li tag has no a tag but has nested list, parse these nested lists


                    List<EpubChapter> children = parseNavList(nestedList);


                    chapters.addAll(children);


                }


            }


            // If current li tag contains link, add to chapter list


            if (chapter != null) {


                chapters.add(chapter);


            }


        }


        return chapters;


    }

    /**
     * Parse resource file list in OPF content
     *
     * @param opfContent OPF file content
     * @param opfDir     OPF file directory
     * @param epubFile   EPUB file
     * @return list of resource files
     * @throws EpubParseException parsing exception
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
     * Parse EPUB file and return EpubBook object
     *
     * @return parsed EpubBook object
     * @throws EpubParseException parsing exception
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


                // Parse other types of navigation (landmarks, page list, etc.)

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

            throw new EpubZipException("Failed to read EPUB file during parsing", epubFile.getName(),
                    "multiple " + "files", e);

        } finally {

            // Clean up ZIP file handle after parsing

            ZipFileManager.getInstance().closeCurrentZipFile();

        }


        return book;

    }

    /**
     * Parse navigation content by nav type
     *
     * @param navContent NAV table of contents content
     * @param navType    navigation type (e.g. toc, landmarks, page-list, etc.)
     * @return list of chapters
     */
    protected static List<EpubChapter> parseNavByType(String navContent, String navType) {
        Objects.requireNonNull(navContent);
        Objects.requireNonNull(navType);

        Document doc = Jsoup.parse(navContent);

        List<EpubChapter> chapters = new ArrayList<>();

        // Find specific type nav element
        Element navElement = doc.selectFirst("nav[epub:type= " + navType + "]");
        if (navElement == null) {
            navElement = doc.selectFirst("nav[epub:type='" + navType + "']");
        }
        if (navElement == null) {
            navElement = doc.selectFirst("nav[epub:type=\"" + navType + "\"]");
        }

        if (navElement != null) {
            // Find top-level ol or ul elements
            Elements topLists = navElement.select("> ol, > ul");

            for (Element list : topLists) {
                chapters.addAll(parseNavList(list));
            }
        }

        return chapters;
    }

    /**
     * Parse EPUB file and return EpubBook object, but without using cache
     *
     * @return parsed EpubBook object
     * @throws EpubParseException parsing exception
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
     * Stream processing HTML chapter content to avoid loading entire file into memory
     *
     * @param epubFile     EPUB file
     * @param htmlFileName HTML file name
     * @param processor    consumer function to process HTML content
     * @throws EpubParseException parsing exception
     */
    public static void processHtmlChapterContent(File epubFile, String htmlFileName,
                                                 Consumer<InputStream> processor) throws EpubParseException {
        // Prevent directory traversal attacks
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
     * Stream processing multiple HTML chapter contents
     *
     * @param epubFile      EPUB file
     * @param htmlFileNames HTML file name list
     * @param processor     consumer function to process each HTML content
     * @throws EpubParseException parsing exception
     */
    public static void processMultipleHtmlChapters(File epubFile, List<String> htmlFileNames, BiConsumer<String,
            InputStream> processor) throws EpubParseException {
        // Prevent directory traversal attacks
        for (String fileName : htmlFileNames) {
            if (!PathValidator.isPathSafe("", fileName)) {
                throw new EpubPathValidationException("Invalid file path: " + fileName, epubFile.getName(), fileName);
            }
        }
        
        try {

            ZipUtils.processMultipleHtmlContents(epubFile, htmlFileNames, processor);

        } catch (IOException e) {

            throw new EpubZipException("Failed to process multiple HTML chapters", epubFile.getName(),
                    "multiple " + "files", e);

        }

    }
}
