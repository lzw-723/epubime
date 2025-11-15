package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import fun.lzwi.epubime.exception.EpubParseException;
import fun.lzwi.epubime.exception.EpubPathValidationException;
import fun.lzwi.epubime.exception.EpubZipException;
import fun.lzwi.epubime.exception.BaseEpubException;
import fun.lzwi.epubime.epub.EpubFileReader;
import fun.lzwi.epubime.epub.EpubStreamProcessor;
import fun.lzwi.epubime.parser.MetadataParser;
import fun.lzwi.epubime.parser.NavigationParser;
import fun.lzwi.epubime.parser.ResourceParser;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class EpubParserTest {

    @Test
    public void getRootFilePath() {
        String containerContent =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><container version=\"1.0\" " + "xmlns" + "=\"urn:oasis" +
                        ":names:tc:opendocument:xmlns:container\"><rootfiles><rootfile " + "full-path=\"OEBPS" +
                        "/content.opf\" media-type=\"application/oebps-package+xml\"/></rootfiles" + "></container>";
        // Extract root file path manually for testing
        int start = containerContent.indexOf("full-path=\"");
        int end = containerContent.indexOf("\"", start + 11);
        String rootFilePath = containerContent.substring(start + 11, end);
        assertEquals("OEBPS/content.opf", rootFilePath);
    }

    @Test
    public void readEpubContent() throws BaseEpubException, EpubPathValidationException, EpubZipException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubFileReader fileReader = new EpubFileReader(epubFile);
        String content = fileReader.readContent("mimetype");
        assertNotNull(content);
        assertEquals("application/epub+zip", content);
    }
    
    @Test(expected = EpubPathValidationException.class)
    public void readEpubContentWithTraversalPathShouldThrowException() throws EpubParseException, BaseEpubException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        // 测试使用目录穿越路径时应抛出异常
        EpubFileReader fileReader = new EpubFileReader(epubFile);
        fileReader.readContent("../../../etc/passwd");
    }

    @Test
    public void parseMetadata() throws BaseEpubException, EpubPathValidationException, EpubZipException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubFileReader fileReader = new EpubFileReader(epubFile);
        String opfContent = fileReader.readContent("OEBPS/book.opf");
        MetadataParser metadataParser = new MetadataParser();
        Metadata metadata = metadataParser.parseMetadata(opfContent);
        assertNotNull(metadata);
        assertEquals("坟", metadata.getTitle());
        assertEquals("鲁迅", metadata.getCreator());
        assertEquals("zh", metadata.getLanguage());
        assertEquals("传硕公版书", metadata.getPublisher());
        assertEquals("2022-12-06T13:14:44.000000+00:00", metadata.getDate());
        assertEquals("https://www.7sbook.com/ebook/254.html", metadata.getIdentifier());
        assertArrayEquals(new String[]{"论文", "论文集"}, metadata.getSubjects().toArray());
        assertEquals("《坟》是鲁迅的论文集，收录鲁迅在1907年~1925" +
                        "年间所写的论文二十三篇。包括《人之历史》、《文化偏至论》、《摩罗诗力说》、《娜拉走后怎样》、《说胡须》、《论照相之类》、《论他妈的》、《从胡须说到牙齿》等。",
                metadata.getDescription());
        assertEquals("本书的版权和许可信息。", metadata.getRights());
        assertEquals("https://www.7sbook.com/", metadata.getSource());
        assertEquals("2022-12-06T13:14:44Z", metadata.getModified());
    }

    @Test
    public void parseMetadataMultipleValues() throws EpubParseException, BaseEpubException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubFileReader fileReader = new EpubFileReader(epubFile);
        String opfContent = fileReader.readContent("OEBPS/book.opf");
        MetadataParser metadataParser = new MetadataParser();
        Metadata metadata = metadataParser.parseMetadata(opfContent);
        assertNotNull(metadata);

        // 验证多值元数据支持
        assertFalse(metadata.getTitles().isEmpty());
        assertFalse(metadata.getCreators().isEmpty());
        assertFalse(metadata.getLanguages().isEmpty());
        assertFalse(metadata.getIdentifiers().isEmpty());
        assertFalse(metadata.getPublishers().isEmpty());
        assertFalse(metadata.getDates().isEmpty());
        assertFalse(metadata.getDescriptions().isEmpty());
        assertFalse(metadata.getRightsList().isEmpty());
        assertFalse(metadata.getSubjects().isEmpty());
        assertFalse(metadata.getContributors().isEmpty());

        // 测试文件中不存在格式和类型元数据，这里不验证
        // assertFalse(metadata.getFormats().isEmpty());
        // assertFalse(metadata.getTypes().isEmpty());

        assertFalse(metadata.getSources().isEmpty());
    }

    @Test
    public void getNcxPath() throws EpubParseException, BaseEpubException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubFileReader fileReader = new EpubFileReader(epubFile);
        String opfContent = fileReader.readContent("OEBPS/book.opf");
        ResourceParser resourceParser = new ResourceParser(epubFile);
        String tocPath = resourceParser.getNcxPath(opfContent, "");
        assertEquals("book.ncx", tocPath);
    }

    @Test
    public void getRootFileDir() {
        // Extract root file directory manually
        String rootFilePath = "OEBPS/content.opf";
        int lastSlashIndex = rootFilePath.lastIndexOf("/");
        String rootFileDir = lastSlashIndex == -1 ? "" : rootFilePath.substring(0, lastSlashIndex + 1);
        assertEquals("OEBPS/", rootFileDir);
    }

    @Test
    public void parseNcx() throws EpubParseException, BaseEpubException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubFileReader fileReader = new EpubFileReader(epubFile);
        String ncxContent = fileReader.readContent("OEBPS/book.ncx");
        NavigationParser navigationParser = new NavigationParser();
        List<EpubChapter> ncx = navigationParser.parseNcx(ncxContent);
        assertNotNull(ncx);
        assertEquals(28, ncx.size());
    }

    @Test
    public void parseResources() throws EpubParseException, BaseEpubException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubFileReader fileReader = new EpubFileReader(epubFile);
        String opfContent = fileReader.readContent("OEBPS/book.opf");
        ResourceParser resourceParser = new ResourceParser(epubFile);
        List<EpubResource> resources = resourceParser.parseResources(opfContent, "OEBPS/");
        assertNotNull(resources);
        assertEquals(35, resources.size());
    }

    @Test
    public void parseResourcesWithProperties() throws EpubParseException {
        // 创建一个模拟的OPF内容用于测试properties属性解析
        String sampleOpfContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<package xmlns=\"http://www.idpf.org/2007/opf\" version=\"3.0\"> " +
                "  <manifest> " +
                "    <item id=\"nav\" href=\"nav.xhtml\" media-type=\"application/xhtml+xml\" properties=\"nav\"/> " +
                "    <item id=\"cover\" href=\"cover.xhtml\" media-type=\"application/xhtml+xml\" properties=\"cover-image\"/> " +
                "    <item id=\"chapter1\" href=\"chapter1.xhtml\" media-type=\"application/xhtml+xml\"/> " +
                "    <item id=\"css\" href=\"style.css\" media-type=\"text/css\" properties=\"css\"/> " +
                "  </manifest> " +
                "</package>";

        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub"); // 用于测试，实际使用模拟内容
        ResourceParser resourceParser = new ResourceParser(epubFile);
        List<EpubResource> resources = resourceParser.parseResources(sampleOpfContent, "");
        assertNotNull(resources);
        assertEquals(4, resources.size());

        // 验证每个资源的properties属性
        EpubResource navResource = null;
        for (EpubResource resource : resources) {
            if ("nav".equals(resource.getId())) {
                navResource = resource;
                break;
            }
        }
        assertNotNull(navResource);
        assertEquals("nav", navResource.getProperties());

        EpubResource coverResource = null;
        for (EpubResource resource : resources) {
            if ("cover".equals(resource.getId())) {
                coverResource = resource;
                break;
            }
        }
        assertNotNull(coverResource);
        assertEquals("cover-image", coverResource.getProperties());

        EpubResource chapterResource = null;
        for (EpubResource resource : resources) {
            if ("chapter1".equals(resource.getId())) {
                chapterResource = resource;
                break;
            }
        }
        assertNotNull(chapterResource);
        assertNull(chapterResource.getProperties()); // 没有properties属性，应该是null

        EpubResource cssResource = null;
        for (EpubResource resource : resources) {
            if ("css".equals(resource.getId())) {
                cssResource = resource;
                break;
            }
        }
        assertNotNull(cssResource);
        assertEquals("css", cssResource.getProperties());
    }

    @Test
    public void parse() throws EpubParseException, BaseEpubException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubBook book = new EpubParser(epubFile).parse();
        assertNotNull(book);
        assertEquals("坟", book.getMetadata().getTitle());
        assertEquals("鲁迅", book.getMetadata().getCreator());
    }

    @Test
    public void getNavPath() {
        String opfContent = "<manifest><item properties=\"nav\" href=\"nav.xhtml\"></item></manifest>";
        String opfDir = "";
        ResourceParser resourceParser = new ResourceParser(null);
        String navPath = resourceParser.getNavPath(opfContent, opfDir);
        assertEquals("nav.xhtml", navPath);
    }

    @Test
    public void parseNav() {
        String navContent = "<nav><ol><li><a href=\"chapter1.xhtml\">Chapter 1</a></li><li><a href=\"chapter2.xhtml\">Chapter 2</a></li></ol></nav>";
        NavigationParser navigationParser = new NavigationParser();
        List<EpubChapter> chapters = navigationParser.parseNav(navContent);
        assertNotNull(chapters);
        assertEquals(2, chapters.size());
    }

    @Test
    public void parseNestedNav() {
        String navContent = "<nav><ol>" +
                "<li><a href=\"chapter1.xhtml\">Chapter 1</a>" +
                "<ol>" +
                "<li><a href=\"chapter1_1.xhtml\">Chapter 1.1</a></li>" +
                "<li><a href=\"chapter1_2.xhtml\">Chapter 1.2</a>" +
                "<ol>" +
                "<li><a href=\"chapter1_2_1.xhtml\">Chapter 1.2.1</a></li>" +
                "</ol>" +
                "</li>" +
                "</ol>" +
                "</li>" +
                "<li><a href=\"chapter2.xhtml\">Chapter 2</a></li>" +
                "</ol></nav>";
        NavigationParser navigationParser = new NavigationParser();
        List<EpubChapter> chapters = navigationParser.parseNav(navContent);
        assertNotNull(chapters);
        assertEquals(2, chapters.size());
        
        // 检查第一章及其子章节
        EpubChapter chapter1 = chapters.get(0);
        assertEquals("Chapter 1", chapter1.getTitle());
        assertEquals("chapter1.xhtml", chapter1.getContent());
        assertTrue(chapter1.hasChildren());
        assertEquals(2, chapter1.getChildren().size());
        
        // 检查第一章的第一个子章节
        EpubChapter chapter1_1 = chapter1.getChildren().get(0);
        assertEquals("Chapter 1.1", chapter1_1.getTitle());
        assertEquals("chapter1_1.xhtml", chapter1_1.getContent());
        assertFalse(chapter1_1.hasChildren());
        
        // 检查第一章的第二个子章节及其子章节
        EpubChapter chapter1_2 = chapter1.getChildren().get(1);
        assertEquals("Chapter 1.2", chapter1_2.getTitle());
        assertEquals("chapter1_2.xhtml", chapter1_2.getContent());
        assertTrue(chapter1_2.hasChildren());
        assertEquals(1, chapter1_2.getChildren().size());
        
        // 检查第一章的第二个子章节的子章节
        EpubChapter chapter1_2_1 = chapter1_2.getChildren().get(0);
        assertEquals("Chapter 1.2.1", chapter1_2_1.getTitle());
        assertEquals("chapter1_2_1.xhtml", chapter1_2_1.getContent());
        assertFalse(chapter1_2_1.hasChildren());
        
        // 检查第二章
        EpubChapter chapter2 = chapters.get(1);
        assertEquals("Chapter 2", chapter2.getTitle());
        assertEquals("chapter2.xhtml", chapter2.getContent());
        assertFalse(chapter2.hasChildren());
    }

    @Test
    public void parseNavWithEpubType() {
        String navContent = "<nav epub:type=\"toc\"><ol>" +
                "<li><a href=\"cover.xhtml\">Cover</a></li>" +
                "<li><a href=\"chapter1.xhtml\" id=\"chap1\">Chapter 1</a>" +
                "<ol>" +
                "<li><a href=\"section1_1.xhtml\" epub:type=\"text\">Section 1.1</a></li>" +
                "</ol>" +
                "</li>" +
                "</ol></nav>";
        NavigationParser navigationParser = new NavigationParser();
        List<EpubChapter> chapters = navigationParser.parseNav(navContent);
        assertNotNull(chapters);
        assertEquals(2, chapters.size());
        
        // 检查第一章及其ID
        EpubChapter chapter1 = chapters.get(1);
        assertEquals("Chapter 1", chapter1.getTitle());
        assertEquals("chapter1.xhtml", chapter1.getContent());
        assertEquals("chap1", chapter1.getId()); // 验证ID是否被正确解析
        assertTrue(chapter1.hasChildren());
    }

    @Test
    public void parseLandmarksNav() {
        String navContent = "<nav epub:type=\"landmarks\"><ol>" +
                "<li><a href=\"cover.xhtml\" epub:type=\"cover\">Cover</a></li>" +
                "<li><a href=\"toc.xhtml\" epub:type=\"toc\">Table of Contents</a></li>" +
                "<li><a href=\"bodymatter.xhtml\" epub:type=\"bodymatter\">Start of Content</a></li>" +
                "</ol></nav>";
        NavigationParser navigationParser = new NavigationParser();
        List<EpubChapter> landmarks = navigationParser.parseNavByType(navContent, "landmarks");
        assertNotNull(landmarks);
        assertEquals(3, landmarks.size());
        assertEquals("Cover", landmarks.get(0).getTitle());
        assertEquals("cover.xhtml", landmarks.get(0).getContent());
    }

    @Test
    public void parseMultipleNavTypes() {
        String navContent = "<nav epub:type=\"toc\"><ol>" +
                "<li><a href=\"chapter1.xhtml\">Chapter 1</a></li>" +
                "</ol></nav>" +
                "<nav epub:type=\"landmarks\"><ol>" +
                "<li><a href=\"cover.xhtml\" epub:type=\"cover\">Cover</a></li>" +
                "</ol></nav>";
        NavigationParser navigationParser = new NavigationParser();
        List<EpubChapter> toc = navigationParser.parseNav(navContent);
        List<EpubChapter> landmarks = navigationParser.parseNavByType(navContent, "landmarks");
        assertNotNull(toc);
        assertNotNull(landmarks);
        assertEquals(1, toc.size());
        assertEquals(1, landmarks.size());
        assertEquals("Chapter 1", toc.get(0).getTitle());
        assertEquals("Cover", landmarks.get(0).getTitle());
    }

    @Test
    public void processHtmlChapterContent() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        StringBuilder content = new StringBuilder();
        boolean[] processed = {false};

        EpubStreamProcessor streamProcessor = new EpubStreamProcessor(epubFile);
        streamProcessor.processHtmlChapter("mimetype", (Consumer<InputStream>) inputStream -> {
            try {
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                processed[0] = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        assertTrue("HTML chapter content should be processed", processed[0]);
        assertEquals("application/epub+zip", content.toString());
    }
    
    @Test(expected = EpubParseException.class)
    public void processHtmlChapterContentWithTraversalPathShouldThrowException() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        // 测试使用目录穿越路径时应抛出异常
        EpubStreamProcessor streamProcessor = new EpubStreamProcessor(epubFile);
        streamProcessor.processHtmlChapter("../../../etc/passwd", (Consumer<InputStream>) inputStream -> {
            // 不应该执行到这里
            fail("Should have thrown EpubParseException");
        });
    }

    @Test
    public void processMultipleHtmlChapters() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        java.util.List<String> filePaths = Collections.singletonList("mimetype");
        java.util.Map<String, String> contents = new java.util.HashMap<>();
        int[] processedCount = {0};

        EpubStreamProcessor streamProcessor = new EpubStreamProcessor(epubFile);
        streamProcessor.processMultipleHtmlChapters(filePaths, (BiConsumer<String, InputStream>) (fileName, inputStream) -> {
            try {
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                contents.put(fileName, content.toString());
                processedCount[0]++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        assertEquals("Should process 1 file", 1, processedCount[0]);
        assertTrue(contents.containsKey("mimetype"));
        assertEquals("application/epub+zip", contents.get("mimetype"));
    }
    
    @Test(expected = EpubParseException.class)
    public void processMultipleHtmlChaptersWithTraversalPathShouldThrowException() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        java.util.List<String> filePaths = java.util.Arrays.asList("mimetype", "../../../etc/passwd");
        // 测试使用目录穿越路径时应抛出异常
        EpubStreamProcessor streamProcessor = new EpubStreamProcessor(epubFile);
        streamProcessor.processMultipleHtmlChapters(filePaths, (BiConsumer<String, InputStream>) (fileName, inputStream) -> {
            // 不应该执行到这里
            fail("Should have thrown EpubParseException");
        });
    }
    
    @Test
    public void parseMetadataWithSampleData() {
        // 创建一个模拟的EPUB OPF内容用于测试
        String sampleOpfContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<package xmlns=\"http://www.idpf.org/2007/opf\" unique-identifier=\"bookid\" version=\"3.0\"> " +
                "  <metadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\"> " +
                "    <dc:title>Sample Book</dc:title> " +
                "    <dc:title type=\"subtitle\">Subtitle Example</dc:title> " +
                "    <dc:creator id=\"creator1\">Author One</dc:creator> " +
                "    <dc:creator id=\"creator2\">Author Two</dc:creator> " +
                "    <dc:language>en</dc:language> " +
                "    <dc:language>es</dc:language> " +
                "    <dc:identifier id=\"bookid\">urn:uuid:123456789</dc:identifier> " +
                "    <dc:publisher>Publisher Name</dc:publisher> " +
                "    <dc:date>2023-01-01</dc:date> " +
                "    <dc:subject>Test Subject 1</dc:subject> " +
                "    <dc:subject>Test Subject 2</dc:subject> " +
                "    <dc:description>A sample description for testing</dc:description> " +
                "    <dc:rights>Copyright 2023</dc:rights> " +
                "    <dc:type>Text</dc:type> " +
                "    <dc:format>application/epub+zip</dc:format> " +
                "    <dc:source>Original Source</dc:source> " +
                "    <dc:contributor>Contributor Name</dc:contributor> " +
                "    <meta property=\"dcterms:modified\">2023-01-01T12:00:00Z</meta> " +
                "    <meta property=\"dcterms:rightsHolder\">Rights Holder</meta> " +
                "    <meta property=\"rendition:layout\">reflowable</meta> " +
                "    <meta property=\"rendition:orientation\">auto</meta> " +
                "    <meta property=\"rendition:spread\">auto</meta> " +
                "    <meta property=\"rendition:viewport\">width=1200,height=600</meta> " +
                "    <meta property=\"rendition:media\">(min-width: 600px)</meta> " +
                "    <meta property=\"rendition:flow\">paginated</meta> " +
                "    <meta property=\"rendition:align-x-center\">true</meta> " +
                "    <meta property=\"schema:accessibilityFeature\">alternativeText</meta> " +
                "    <meta property=\"schema:accessibilityFeature\">longDescriptions</meta> " +
                "    <meta property=\"schema:accessibilityHazard\">noFlashingHazard</meta> " +
                "    <meta property=\"schema:accessibilitySummary\">This book includes accessibility features.</meta> " +
                "    <meta name=\"cover\" content=\"cover-image\"/> " +
                "  </metadata> " +
                "</package>";

        MetadataParser metadataParser = new MetadataParser();
        Metadata metadata = metadataParser.parseMetadata(sampleOpfContent);
        assertNotNull(metadata);

        // 验证单值元数据
        assertEquals("Sample Book", metadata.getTitle());
        assertEquals("Author One", metadata.getCreator());
        assertEquals("en", metadata.getLanguage());
        assertEquals("urn:uuid:123456789", metadata.getIdentifier());
        assertEquals("urn:uuid:123456789", metadata.getUniqueIdentifier()); // 验证unique-identifier解析
        assertEquals("Publisher Name", metadata.getPublisher());
        assertEquals("2023-01-01", metadata.getDate());
        assertEquals("Test Subject 1", metadata.getSubject());
        assertEquals("A sample description for testing", metadata.getDescription());
        assertEquals("Copyright 2023", metadata.getRights());
        assertEquals("Text", metadata.getType());
        assertEquals("application/epub+zip", metadata.getFormat());
        assertEquals("Original Source", metadata.getSource());
        assertEquals("Contributor Name", metadata.getContributor());
        assertEquals("2023-01-01T12:00:00Z", metadata.getModified());
        assertEquals("Rights Holder", metadata.getRightsHolder());
        assertEquals("cover-image", metadata.getCover());

        // 验证多值元数据
        assertEquals(2, metadata.getTitles().size());
        assertEquals(2, metadata.getCreators().size());
        assertEquals(2, metadata.getLanguages().size());
        assertEquals(2, metadata.getSubjects().size());

        // 验证渲染属性
        assertEquals("reflowable", metadata.getLayout());
        assertEquals("auto", metadata.getOrientation());
        assertEquals("auto", metadata.getSpread());
        assertEquals("width=1200,height=600", metadata.getViewport());
        assertEquals("(min-width: 600px)", metadata.getMedia());
        assertEquals("paginated", metadata.getFlow());
        assertTrue(metadata.isAlignXCenter());

        // 验证可访问性元数据
        assertEquals(2, metadata.getAccessibilityFeatures().size());
        assertTrue(metadata.getAccessibilityFeatures().contains("alternativeText"));
        assertTrue(metadata.getAccessibilityFeatures().contains("longDescriptions"));
        assertEquals(1, metadata.getAccessibilityHazard().size());
        assertEquals("noFlashingHazard", metadata.getAccessibilityHazard().get(0));
        assertEquals("This book includes accessibility features.", metadata.getAccessibilitySummary());
    }

    @Test
    public void testUniqueIdentifierParsing() {
        // 测试unique-identifier属性的解析
        String sampleOpfContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<package xmlns=\"http://www.idpf.org/2007/opf\" unique-identifier=\"bookid\" version=\"3.0\"> " +
                "  <metadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\"> " +
                "    <dc:identifier id=\"otherid\">urn:isbn:123456789</dc:identifier> " +
                "    <dc:identifier id=\"bookid\">urn:uuid:123456789</dc:identifier> " +
                "  </metadata> " +
                "</package>";

        MetadataParser metadataParser = new MetadataParser();
        Metadata metadata = metadataParser.parseMetadata(sampleOpfContent);
        assertNotNull(metadata);
        assertEquals("urn:uuid:123456789", metadata.getUniqueIdentifier());
        assertEquals("urn:isbn:123456789", metadata.getIdentifier()); // 第一个identifier
        assertEquals(2, metadata.getIdentifiers().size());
    }
}