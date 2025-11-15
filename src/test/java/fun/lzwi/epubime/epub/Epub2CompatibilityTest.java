package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.epub.EpubBook;
import fun.lzwi.epubime.epub.EpubChapter;
import fun.lzwi.epubime.epub.EpubResource;
import fun.lzwi.epubime.epub.Metadata;
import fun.lzwi.epubime.parser.MetadataParser;
import fun.lzwi.epubime.parser.NavigationParser;
import fun.lzwi.epubime.parser.ResourceParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EPUB2兼容性测试类
 * 专门测试EPUB2格式的兼容性，包括元数据解析、导航解析、资源解析等
 */
public class Epub2CompatibilityTest {

    @Test
    public void testEpub2MetadataParsingWithoutNamespaces() {
        // 测试EPUB2格式的元数据解析（完全不带命名空间）
        String sampleOpfContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<package version=\"2.0\">" +
                "  <metadata>" +
                "    <title>EPUB2 Book Without Namespaces</title>" +
                "    <creator>Author Name</creator>" +
                "    <language>en-US</language>" +
                "    <identifier>urn:uuid:test-123</identifier>" +
                "    <publisher>Test Publisher</publisher>" +
                "    <date>2023-01-01</date>" +
                "    <description>A test book for EPUB2 compatibility</description>" +
                "    <subject>Fiction</subject>" +
                "    <rights>Copyright 2023</rights>" +
                "    <contributor>Editor Name</contributor>" +
                "    <type>Text</type>" +
                "    <format>application/epub+zip</format>" +
                "    <source>Original Source</source>" +
                "    <meta name=\"cover\" content=\"cover.jpg\"/>" +
                "  </metadata>" +
                "</package>";

        MetadataParser metadataParser = new MetadataParser();
        Metadata metadata = metadataParser.parseMetadata(sampleOpfContent, "2.0");

        assertNotNull(metadata);
        assertEquals("EPUB2 Book Without Namespaces", metadata.getTitle());
        assertEquals("Author Name", metadata.getCreator());
        assertEquals("en-US", metadata.getLanguage());
        assertEquals("urn:uuid:test-123", metadata.getIdentifier());
        assertEquals("Test Publisher", metadata.getPublisher());
        assertEquals("2023-01-01", metadata.getDate());
        assertEquals("A test book for EPUB2 compatibility", metadata.getDescription());
        assertEquals("Fiction", metadata.getSubject());
        assertEquals("Copyright 2023", metadata.getRights());
        assertEquals("Editor Name", metadata.getContributor());
        assertEquals("Text", metadata.getType());
        assertEquals("application/epub+zip", metadata.getFormat());
        assertEquals("Original Source", metadata.getSource());
        assertEquals("cover.jpg", metadata.getCover());
    }

    @Test
    public void testEpub2MetadataParsingWithMixedNamespaces() {
        // 测试EPUB2格式的元数据解析（混合命名空间）
        String sampleOpfContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<package version=\"2.0\">" +
                "  <metadata>" +
                "    <dc:title xmlns:dc=\"http://purl.org/dc/elements/1.1/\">EPUB2 Book With Mixed Namespaces</dc:title>" +
                "    <creator>Author Without Namespace</creator>" +
                "    <dc:language>zh-CN</dc:language>" +
                "    <identifier>Mixed Identifier</identifier>" +
                "    <dc:publisher>Mixed Publisher</dc:publisher>" +
                "    <date>2023-06-15</date>" +
                "  </metadata>" +
                "</package>";

        MetadataParser metadataParser = new MetadataParser();
        Metadata metadata = metadataParser.parseMetadata(sampleOpfContent, "2.0");

        assertNotNull(metadata);
        assertEquals("EPUB2 Book With Mixed Namespaces", metadata.getTitle());
        assertEquals("Author Without Namespace", metadata.getCreator());
        assertEquals("zh-CN", metadata.getLanguage());
        assertEquals("Mixed Identifier", metadata.getIdentifier());
        assertEquals("Mixed Publisher", metadata.getPublisher());
        assertEquals("2023-06-15", metadata.getDate());
    }

    @Test
    public void testEpub2NcxNavigationParsing() {
        // 测试EPUB2 NCX导航解析 - 先测试简单版本
        String ncxContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<!DOCTYPE ncx PUBLIC \"-//NISO//DTD ncx 2005-1//EN\" \"http://www.daisy.org/z3986/2005/ncx-2005-1.dtd\">" +
                "<ncx version=\"2005-1\">" +
                "  <head>" +
                "    <meta name=\"dtb:uid\" content=\"urn:uuid:test-ncx\"/>" +
                "  </head>" +
                "  <docTitle>" +
                "    <text>EPUB2 Test Book</text>" +
                "  </docTitle>" +
                "  <navMap>" +
                "    <navPoint id=\"navpoint-1\" playOrder=\"1\">" +
                "      <navLabel>" +
                "        <text>Chapter 1</text>" +
                "      </navLabel>" +
                "      <content src=\"chapter1.html\"/>" +
                "    </navPoint>" +
                "    <navPoint id=\"navpoint-2\" playOrder=\"2\">" +
                "      <navLabel>" +
                "        <text>Chapter 2</text>" +
                "      </navLabel>" +
                "      <content src=\"chapter2.html\"/>" +
                "    </navPoint>" +
                "  </navMap>" +
                "</ncx>";

        NavigationParser navigationParser = new NavigationParser();
        List<EpubChapter> chapters = navigationParser.parseNcx(ncxContent);

        assertNotNull(chapters);
        assertEquals(2, chapters.size());

        // 检查第一章
        EpubChapter chapter1 = chapters.get(0);
        assertEquals("Chapter 1", chapter1.getTitle());
        assertEquals("chapter1.html", chapter1.getContent());

        // 检查第二章
        EpubChapter chapter2 = chapters.get(1);
        assertEquals("Chapter 2", chapter2.getTitle());
        assertEquals("chapter2.html", chapter2.getContent());
    }

    @Test
    public void testEpub2NcxNavigationParsingWithNested() {
        // 测试EPUB2 NCX导航解析 - 嵌套版本
        String ncxContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<ncx version=\"2005-1\">" +
                "  <navMap>" +
                "    <navPoint id=\"navpoint-1\" playOrder=\"1\">" +
                "      <navLabel>" +
                "        <text>Chapter 1</text>" +
                "      </navLabel>" +
                "      <content src=\"chapter1.html\"/>" +
                "    </navPoint>" +
                "    <navPoint id=\"navpoint-2\" playOrder=\"2\">" +
                "      <navLabel>" +
                "        <text>Chapter 2</text>" +
                "      </navLabel>" +
                "      <content src=\"chapter2.html\"/>" +
                "      <navPoint id=\"navpoint-2-1\" playOrder=\"3\">" +
                "        <navLabel>" +
                "          <text>Section 2.1</text>" +
                "        </navLabel>" +
                "        <content src=\"chapter2.html#section1\"/>" +
                "      </navPoint>" +
                "    </navPoint>" +
                "  </navMap>" +
                "</ncx>";

        NavigationParser navigationParser = new NavigationParser();
        List<EpubChapter> chapters = navigationParser.parseNcx(ncxContent);

        assertNotNull(chapters);
        assertEquals(2, chapters.size());

        // 检查第一章
        EpubChapter chapter1 = chapters.get(0);
        assertEquals("Chapter 1", chapter1.getTitle());
        assertEquals("chapter1.html", chapter1.getContent());

        // 检查第二章及其子章节
        EpubChapter chapter2 = chapters.get(1);
        assertEquals("Chapter 2", chapter2.getTitle());
        assertEquals("chapter2.html", chapter2.getContent());
        assertNotNull(chapter2.getChildren());
        assertEquals(1, chapter2.getChildren().size());

        EpubChapter section21 = chapter2.getChildren().get(0);
        assertEquals("Section 2.1", section21.getTitle());
        assertEquals("chapter2.html#section1", section21.getContent());
    }

    @Test
    public void testEpub2ResourceParsing() {
        // 测试EPUB2资源解析
        String opfContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<package version=\"2.0\">" +
                "  <metadata>" +
                "    <title>Test Book</title>" +
                "    <identifier>test-id</identifier>" +
                "  </metadata>" +
                "  <manifest>" +
                "    <item id=\"chapter1\" href=\"chapter1.html\" media-type=\"application/xhtml+xml\"/>" +
                "    <item id=\"chapter2\" href=\"chapter2.html\" media-type=\"application/xhtml+xml\"/>" +
                "    <item id=\"cover\" href=\"cover.jpg\" media-type=\"image/jpeg\"/>" +
                "    <item id=\"stylesheet\" href=\"style.css\" media-type=\"text/css\"/>" +
                "    <item id=\"ncx\" href=\"toc.ncx\" media-type=\"application/x-dtbncx+xml\"/>" +
                "  </manifest>" +
                "  <spine toc=\"ncx\">" +
                "    <itemref idref=\"chapter1\"/>" +
                "    <itemref idref=\"chapter2\"/>" +
                "  </spine>" +
                "</package>";

        ResourceParser resourceParser = new ResourceParser(null);
        List<EpubResource> resources = resourceParser.parseResources(opfContent, "");

        assertNotNull(resources);
        assertEquals(5, resources.size());

        // 验证资源是否正确解析
        boolean hasChapter1 = resources.stream().anyMatch(r -> "chapter1".equals(r.getId()) && "chapter1.html".equals(r.getHref()));
        boolean hasChapter2 = resources.stream().anyMatch(r -> "chapter2".equals(r.getId()) && "chapter2.html".equals(r.getHref()));
        boolean hasCover = resources.stream().anyMatch(r -> "cover".equals(r.getId()) && "cover.jpg".equals(r.getHref()));
        boolean hasStylesheet = resources.stream().anyMatch(r -> "stylesheet".equals(r.getId()) && "style.css".equals(r.getHref()));
        boolean hasNcx = resources.stream().anyMatch(r -> "ncx".equals(r.getId()) && "toc.ncx".equals(r.getHref()));

        assertTrue(hasChapter1, "Chapter1 resource not found");
        assertTrue(hasChapter2, "Chapter2 resource not found");
        assertTrue(hasCover, "Cover resource not found");
        assertTrue(hasStylesheet, "Stylesheet resource not found");
        assertTrue(hasNcx, "NCX resource not found");
    }

    @Test
    public void testEpub2NcxPathExtraction() {
        // 测试EPUB2 NCX路径提取
        String opfContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<package version=\"2.0\">" +
                "  <metadata>" +
                "    <title>Test Book</title>" +
                "  </metadata>" +
                "  <manifest>" +
                "    <item id=\"ncx\" href=\"toc.ncx\" media-type=\"application/x-dtbncx+xml\"/>" +
                "    <item id=\"chapter1\" href=\"chapter1.html\" media-type=\"application/xhtml+xml\"/>" +
                "  </manifest>" +
                "  <spine toc=\"ncx\">" +
                "    <itemref idref=\"chapter1\"/>" +
                "  </spine>" +
                "</package>";

        ResourceParser resourceParser = new ResourceParser(null);
        String ncxPath = resourceParser.getNcxPath(opfContent, "");

        assertNotNull(ncxPath);
        assertEquals("toc.ncx", ncxPath);
    }

    @Test
    public void testEpub2CoverDetection() {
        // 测试EPUB2封面检测
        String opfContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<package version=\"2.0\">" +
                "  <metadata>" +
                "    <title>Test Book</title>" +
                "    <meta name=\"cover\" content=\"cover-image\"/>" +
                "  </metadata>" +
                "  <manifest>" +
                "    <item id=\"cover-image\" href=\"cover.jpg\" media-type=\"image/jpeg\"/>" +
                "  </manifest>" +
                "</package>";

        ResourceParser resourceParser = new ResourceParser(null);
        String coverId = resourceParser.getCoverResourceId(opfContent);

        assertNotNull(coverId);
        assertEquals("cover-image", coverId);
    }

    @Test
    public void testEpubVersionDetectionEdgeCases() {
        // 测试版本检测的边界情况
        MetadataParser metadataParser = new MetadataParser();

        // 测试没有version属性的情况
        String opfWithoutVersion = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<package>" +
                "  <metadata>" +
                "    <title>Test Book</title>" +
                "  </metadata>" +
                "</package>";

        Metadata metadata1 = metadataParser.parseMetadata(opfWithoutVersion, "3.0");
        assertNotNull(metadata1);

        // 测试空version属性的情况
        String opfWithEmptyVersion = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<package version=\"\">" +
                "  <metadata>" +
                "    <title>Test Book</title>" +
                "  </metadata>" +
                "</package>";

        Metadata metadata2 = metadataParser.parseMetadata(opfWithEmptyVersion, "3.0");
        assertNotNull(metadata2);

        // 测试EPUB2.0版本
        String opfWith20Version = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<package version=\"2.0\">" +
                "  <metadata>" +
                "    <title>Test Book</title>" +
                "  </metadata>" +
                "</package>";

        Metadata metadata3 = metadataParser.parseMetadata(opfWith20Version, "2.0");
        assertNotNull(metadata3);
    }

    @Test
    public void testEpub2MultipleMetadataValues() {
        // 测试EPUB2多值元数据
        String sampleOpfContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<package version=\"2.0\">" +
                "  <metadata>" +
                "    <title>Main Title</title>" +
                "    <title>Subtitle</title>" +
                "    <creator>Author 1</creator>" +
                "    <creator>Author 2</creator>" +
                "    <subject>Science Fiction</subject>" +
                "    <subject>Adventure</subject>" +
                "    <language>en</language>" +
                "    <language>fr</language>" +
                "  </metadata>" +
                "</package>";

        MetadataParser metadataParser = new MetadataParser();
        Metadata metadata = metadataParser.parseMetadata(sampleOpfContent, "2.0");

        assertNotNull(metadata);
        assertEquals(2, metadata.getTitles().size());
        assertEquals(2, metadata.getCreators().size());
        assertEquals(2, metadata.getSubjects().size());
        assertEquals(2, metadata.getLanguages().size());

        // 验证第一个值
        assertEquals("Main Title", metadata.getTitle());
        assertEquals("Author 1", metadata.getCreator());
        assertEquals("Science Fiction", metadata.getSubject());
        assertEquals("en", metadata.getLanguage());
    }
}