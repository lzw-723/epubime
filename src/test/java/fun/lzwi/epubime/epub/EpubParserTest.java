package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
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
        String rootFilePath = EpubParser.getRootFilePath(containerContent);
        assertEquals("OEBPS/content.opf", rootFilePath);
    }

    @Test
    public void readEpubContent() throws EpubParseException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        String content = EpubParser.readEpubContent(epubFile, "mimetype");
        assertNotNull(content);
        assertEquals("application/epub+zip", content);
    }

    @Test

    public void parseMetadata() throws EpubParseException {

        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");

        String optContent = EpubParser.readEpubContent(epubFile, "OEBPS/book.opf");

        Metadata metadata = EpubParser.parseMetadata(optContent);

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

        //        assertEquals("text", metadata.getType());

        //        assertEquals("application/epub+zip", metadata.getFormat());

        assertEquals("https://www.7sbook.com/", metadata.getSource());


        assertEquals("2022-12-06T13:14:44Z", metadata.getModified());

    }


    @Test

    public void parseMetadataMultipleValues() throws EpubParseException {

        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");

        String optContent = EpubParser.readEpubContent(epubFile, "OEBPS/book.opf");

        Metadata metadata = EpubParser.parseMetadata(optContent);

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
    public void getNcxPath() throws EpubParseException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        String optContent = EpubParser.readEpubContent(epubFile, "OEBPS/book.opf");
        String tocPath = EpubParser.getNcxPath(optContent, "");
        assertEquals("book.ncx", tocPath);
    }

    @Test
    public void getRootFileDir() {
        String rootFileDir = EpubParser.getRootFileDir("OEBPS/content.opf");
        assertEquals("OEBPS/", rootFileDir);
    }

    @Test
    public void parseNcx() throws EpubParseException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        String ncxContent = EpubParser.readEpubContent(epubFile, "OEBPS/book.ncx");
        List<EpubChapter> ncx = EpubParser.parseNcx(ncxContent);
        assertNotNull(ncx);
        assertEquals(28, ncx.size());
    }

    @Test
    public void parseResources() throws EpubParseException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        String opfContent = EpubParser.readEpubContent(epubFile, "OEBPS/book.opf");
        List<EpubResource> resources = EpubParser.parseResources(opfContent, "OEBPS/", epubFile);
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
        List<EpubResource> resources = EpubParser.parseResources(sampleOpfContent, "", epubFile);
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
    public void parse() throws EpubParseException {
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
        String navPath = EpubParser.getNavPath(opfContent, opfDir);
        assertEquals("nav.xhtml", navPath);
    }

    @Test
    public void parseNav() {
        String navContent = "<nav><ol><li><a href=\"chapter1.xhtml\">Chapter 1</a></li><li><a href=\"chapter2" +
                ".xhtml\">Chapter 2</a></li></ol></nav>";
        List<EpubChapter> chapters = EpubParser.parseNav(navContent);
        assertNotNull(chapters);
        assertEquals(2, chapters.size());
    }

    @Test
    public void processHtmlChapterContent() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        StringBuilder content = new StringBuilder();
        boolean[] processed = {false};

        EpubParser.processHtmlChapterContent(epubFile, "mimetype", new Consumer<InputStream>() {
            @Override
            public void accept(InputStream inputStream) {
                try {
                    java.io.BufferedReader reader =
                            new java.io.BufferedReader(new java.io.InputStreamReader(inputStream, "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    processed[0] = true;
                } catch (java.io.IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        assertTrue("HTML chapter content should be processed", processed[0]);
        assertEquals("application/epub+zip", content.toString());
    }

    @Test
    public void processMultipleHtmlChapters() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        java.util.List<String> filePaths = java.util.Arrays.asList("mimetype");
        java.util.Map<String, String> contents = new java.util.HashMap<>();
        int[] processedCount = {0};

        EpubParser.processMultipleHtmlChapters(epubFile, filePaths, new BiConsumer<String, InputStream>() {
            @Override
            public void accept(String fileName, InputStream inputStream) {
                try {
                    java.io.BufferedReader reader =
                            new java.io.BufferedReader(new java.io.InputStreamReader(inputStream, "UTF-8"));
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    contents.put(fileName, content.toString());
                    processedCount[0]++;
                } catch (java.io.IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        assertEquals("Should process 1 file", 1, processedCount[0]);
        assertTrue(contents.containsKey("mimetype"));
        assertEquals("application/epub+zip", contents.get("mimetype"));
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

        Metadata metadata = EpubParser.parseMetadata(sampleOpfContent);
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

        Metadata metadata = EpubParser.parseMetadata(sampleOpfContent);
        assertNotNull(metadata);
        assertEquals("urn:uuid:123456789", metadata.getUniqueIdentifier());
        assertEquals("urn:isbn:123456789", metadata.getIdentifier()); // 第一个identifier
        assertEquals(2, metadata.getIdentifiers().size());
    }
}
