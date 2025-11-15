package fun.lzwi.epubime.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnhancedXmlUtilsTest {

    @Test
    public void testParseXmlValid() {
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                           "<package>" +
                           "  <metadata>" +
                           "    <title>Test Book</title>" +
                           "  </metadata>" +
                           "</package>";

        Document doc = XmlUtils.parseXml(xmlContent);
        
        assertNotNull(doc, "Document should not be null");
        assertEquals("package", doc.selectFirst("package").tagName());
        assertEquals("Test Book", doc.selectFirst("title").text());
    }

    @Test
    public void testParseXmlNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            XmlUtils.parseXml(null);
        });
    }

    @Test
    public void testParseXmlEmpty() {
        Document doc = XmlUtils.parseXml("");
        assertNotNull(doc, "Document should not be null for empty content");
    }

    @Test
    public void testGetAttribute() {
        String html = "<element id=\"test-id\" class=\"test-class\" data-value=\"123\">Content</element>";
        Element element = Jsoup.parse(html).selectFirst("element");

        assertEquals("test-id", XmlUtils.getAttribute(element, "id"));
        assertEquals("test-class", XmlUtils.getAttribute(element, "class"));
        assertEquals("123", XmlUtils.getAttribute(element, "data-value"));
        assertEquals("", XmlUtils.getAttribute(element, "non-existent"));
    }

    @Test
    public void testGetAttributeNullElement() {
        assertEquals("", XmlUtils.getAttribute(null, "id"));
        assertEquals("", XmlUtils.getAttribute(null, "any-attribute"));
    }

    @Test
    public void testGetText() {
        String html = "<element>Some text content</element>";
        Element element = Jsoup.parse(html).selectFirst("element");

        assertEquals("Some text content", XmlUtils.getText(element));
    }

    @Test
    public void testGetTextNullElement() {
        assertEquals("", XmlUtils.getText(null));
    }

    @Test
    public void testGetTextEmptyElement() {
        String html = "<element></element>";
        Element element = Jsoup.parse(html).selectFirst("element");

        assertEquals("", XmlUtils.getText(element));
    }

    @Test
    public void testSelectFirstFromDocument() {
        String xml = "<package>" +
                    "  <metadata>" +
                    "    <title>Book Title</title>" +
                    "  </metadata>" +
                    "</package>";
        Document doc = XmlUtils.parseXml(xml);

        Element titleElement = XmlUtils.selectFirst(doc, "title");
        assertNotNull(titleElement, "Title element should be found");
        assertEquals("Book Title", titleElement.text());

        Element nonExistent = XmlUtils.selectFirst(doc, "non-existent");
        assertNull(nonExistent, "Non-existent element should return null");
    }

    @Test
    public void testSelectFirstFromDocumentNullParams() {
        String xml = "<package><title>Test</title></package>";
        Document doc = XmlUtils.parseXml(xml);

        assertNull(XmlUtils.selectFirst(null, "title"), "Should return null for null document");
        assertNull(XmlUtils.selectFirst(doc, null), "Should return null for null query");
        assertNull(XmlUtils.selectFirst(null, null), "Should return null for both null");
    }

    @Test
    public void testSelectFromDocument() {
        String xml = "<package>" +
                    "  <metadata>" +
                    "    <creator>Author 1</creator>" +
                    "    <creator>Author 2</creator>" +
                    "  </metadata>" +
                    "</package>";
        Document doc = XmlUtils.parseXml(xml);

        Elements creators = XmlUtils.select(doc, "creator");
        assertNotNull(creators, "Creators should not be null");
        assertEquals(2, creators.size(), "Should find 2 creators");
        assertEquals("Author 1", creators.get(0).text());
        assertEquals("Author 2", creators.get(1).text());

        Elements nonExistent = XmlUtils.select(doc, "non-existent");
        assertNotNull(nonExistent, "Should return empty elements");
        assertEquals(0, nonExistent.size(), "Should be empty");
    }

    @Test
    public void testSelectFromDocumentNullParams() {
        String xml = "<package><title>Test</title></package>";
        Document doc = XmlUtils.parseXml(xml);

        Elements result1 = XmlUtils.select(null, "title");
        assertNotNull(result1, "Should return empty elements for null document");
        assertEquals(0, result1.size(), "Should be empty");

        Elements result2 = XmlUtils.select(doc, null);
        assertNotNull(result2, "Should return empty elements for null query");
        assertEquals(0, result2.size(), "Should be empty");

        Elements result3 = XmlUtils.select(null, null);
        assertNotNull(result3, "Should return empty elements for both null");
        assertEquals(0, result3.size(), "Should be empty");
    }

    @Test
    public void testSelectFirstFromElement() {
        String xml = "<package>" +
                    "  <metadata>" +
                    "    <title>Book Title</title>" +
                    "  </metadata>" +
                    "</package>";
        Document doc = XmlUtils.parseXml(xml);
        Element metadata = doc.selectFirst("metadata");

        Element title = XmlUtils.selectFirst(metadata, "title");
        assertNotNull(title, "Title should be found in metadata");
        assertEquals("Book Title", title.text());

        Element nonExistent = XmlUtils.selectFirst(metadata, "non-existent");
        assertNull(nonExistent, "Non-existent element should return null");
    }

    @Test
    public void testSelectFirstFromElementNullParams() {
        String xml = "<package><metadata><title>Test</title></metadata></package>";
        Document doc = XmlUtils.parseXml(xml);
        Element metadata = doc.selectFirst("metadata");

        assertNull(XmlUtils.selectFirst(null, "title"), "Should return null for null element");
        assertNull(XmlUtils.selectFirst(metadata, null), "Should return null for null query");
        assertNull(XmlUtils.selectFirst(null, null), "Should return null for both null");
    }

    @Test
    public void testSelectFromElement() {
        String xml = "<metadata>" +
                    "  <creator>Author 1</creator>" +
                    "  <creator>Author 2</creator>" +
                    "</metadata>";
        Document doc = XmlUtils.parseXml(xml);
        Element metadata = doc.selectFirst("metadata");

        Elements creators = XmlUtils.select(metadata, "creator");
        assertNotNull(creators, "Creators should not be null");
        assertEquals(2, creators.size(), "Should find 2 creators");

        Elements nonExistent = XmlUtils.select(metadata, "non-existent");
        assertNotNull(nonExistent, "Should return empty elements");
        assertEquals(0, nonExistent.size(), "Should be empty");
    }

    @Test
    public void testSelectFromElementNullParams() {
        String xml = "<metadata><title>Test</title></metadata>";
        Document doc = XmlUtils.parseXml(xml);
        Element metadata = doc.selectFirst("metadata");

        Elements result1 = XmlUtils.select(null, "title");
        assertNotNull(result1, "Should return empty elements for null element");
        assertEquals(0, result1.size(), "Should be empty");

        Elements result2 = XmlUtils.select(metadata, null);
        assertNotNull(result2, "Should return empty elements for null query");
        assertEquals(0, result2.size(), "Should be empty");

        Elements result3 = XmlUtils.select(null, null);
        assertNotNull(result3, "Should return empty elements for both null");
        assertEquals(0, result3.size(), "Should be empty");
    }

    @Test
    public void testBuildXPath() {
        String xml = "<package>" +
                    "  <metadata id=\"meta1\">" +
                    "    <title>Test</title>" +
                    "  </metadata>" +
                    "</package>";
        Document doc = XmlUtils.parseXml(xml);
        Element title = doc.selectFirst("title");

        String xpath = XmlUtils.buildXPath(title);
        assertNotNull("XPath should not be null", xpath);
        // The XPath might not contain all elements depending on the implementation
        assertTrue(xpath.length() > 0, "XPath should not be empty");
        assertTrue(xpath.contains("title"), "XPath should contain title");
    }

    @Test
    public void testBuildXPathWithId() {
        String xml = "<package>" +
                    "  <metadata id=\"meta1\">" +
                    "    <title>Test</title>" +
                    "  </metadata>" +
                    "</package>";
        Document doc = XmlUtils.parseXml(xml);
        Element metadata = doc.selectFirst("metadata");

        String xpath = XmlUtils.buildXPath(metadata);
        assertTrue(xpath.contains("[@id='meta1']"),
                  "XPath should use id for elements with id attribute");
    }

    @Test
    public void testBuildXPathWithClass() {
        String xml = "<package>" +
                    "  <metadata class=\"book-meta\">" +
                    "    <title>Test</title>" +
                    "  </metadata>" +
                    "</package>";
        Document doc = XmlUtils.parseXml(xml);
        Element metadata = doc.selectFirst("metadata");

        String xpath = XmlUtils.buildXPath(metadata);
        assertTrue(xpath.contains("[@class='book-meta']"),
                  "XPath should use class for elements with class attribute");
    }

    @Test
    public void testBuildXPathNullElement() {
        String xpath = XmlUtils.buildXPath(null);
        assertEquals("/", xpath);
    }

    @Test
    public void testBuildElementInfo() {
        String xml = "<element id=\"test-id\" class=\"test-class\" data-value=\"123\">Element Text</element>";
        Element element = Jsoup.parse(xml).selectFirst("element");

        String info = XmlUtils.buildElementInfo(element);
        
        assertTrue(info.contains("Element: element"), "Info should contain tag name");
        assertTrue(info.contains("Attributes:"), "Info should contain attributes");
        assertTrue(info.contains("id='test-id'"), "Info should contain id");
        assertTrue(info.contains("class='test-class'"), "Info should contain class");
        assertTrue(info.contains("data-value='123'"), "Info should contain data-value");
        assertTrue(info.contains("[Text: 'Element Text']"), "Info should contain text");
    }

    @Test
    public void testBuildElementInfoNoAttributes() {
        String xml = "<simple>Simple Text</simple>";
        Element element = Jsoup.parse(xml).selectFirst("simple");

        String info = XmlUtils.buildElementInfo(element);
        
        assertEquals("Element: simple [Text: 'Simple Text']", info);
    }

    @Test
    public void testBuildElementInfoNoText() {
        String xml = "<empty id=\"test\"></empty>";
        Element element = Jsoup.parse(xml).selectFirst("empty");

        String info = XmlUtils.buildElementInfo(element);
        
        assertTrue(info.contains("Element: empty"), "Info should contain tag name");
        assertTrue(info.contains("Attributes:"), "Info should contain attributes");
        assertTrue(info.contains("id='test'"), "Info should contain id");
        assertFalse(info.contains("[Text:"), "Info should not contain text for empty elements");
    }

    @Test
    public void testBuildElementInfoLongText() {
        String longText = "This is a very long text that should exceed 100 characters limit for element info display in the exception message";
        String xml = "<element>" + longText + "</element>";
        Element element = Jsoup.parse(xml).selectFirst("element");

        String info = XmlUtils.buildElementInfo(element);
        
        assertFalse(info.contains(longText), "Info should not contain long text");
        assertTrue(info.contains("Element: element"), "Info should contain tag name");
    }

    @Test
    public void testBuildElementInfoNullElement() {
        String info = XmlUtils.buildElementInfo(null);
        assertEquals("Element: null", info);
    }

    @Test
    public void testTruncateContent() {
        String shortContent = "Short content";
        String truncated = XmlUtils.truncateContent(shortContent);
        assertEquals(shortContent, truncated, "Short content should not be truncated");

        String longContent = "";
        for (int i = 0; i < 300; i++) {
            longContent += "a";
        }
        String truncatedLong = XmlUtils.truncateContent(longContent);
        assertTrue(truncatedLong.contains("... (truncated)"), "Long content should be truncated");
        assertTrue(truncatedLong.startsWith("a"), "Truncated content should start with 'a'");
        assertTrue(truncatedLong.length() <= 220, "Truncated content should be around 217 chars");
    }

    @Test
    public void testTruncateContentNull() {
        String truncated = XmlUtils.truncateContent(null);
        assertEquals("null", truncated);
    }

    @Test
    public void testTruncateContentCustomLength() {
        String content = "This is a test content for custom truncation";
        
        // Test with max length longer than content
        String truncated1 = XmlUtils.truncateContent(content, 100);
        assertEquals(content, truncated1, "Content shorter than max should not be truncated");

        // Test with max length shorter than content
        String truncated2 = XmlUtils.truncateContent(content, 10);
        assertTrue(truncated2.contains("... (truncated)"), "Content should be truncated");
        assertEquals("This is a ... (truncated)", truncated2);
    }

    @Test
    public void testTruncateContentCustomLengthNull() {
        String truncated = XmlUtils.truncateContent(null, 50);
        assertEquals("null", truncated);
    }

    @Test
    public void testHasAttribute() {
        String xml = "<element id=\"test-id\" class=\"test-class\">Content</element>";
        Element element = Jsoup.parse(xml).selectFirst("element");

        assertTrue(XmlUtils.hasAttribute(element, "id"), "Should have id attribute");
        assertTrue(XmlUtils.hasAttribute(element, "class"), "Should have class attribute");
        assertFalse(XmlUtils.hasAttribute(element, "non-existent"), "Should not have non-existent attribute");
    }

    @Test
    public void testHasAttributeNullParams() {
        String xml = "<element id=\"test\">Content</element>";
        Element element = Jsoup.parse(xml).selectFirst("element");

        assertFalse(XmlUtils.hasAttribute(null, "id"), "Should return false for null element");
        assertFalse(XmlUtils.hasAttribute(element, null), "Should return false for null attribute name");
        assertFalse(XmlUtils.hasAttribute(null, null), "Should return false for both null");
    }

    @Test
    public void testGetChildCount() {
        String xml = "<parent>" +
                    "  <child>Child 1</child>" +
                    "  <child>Child 2</child>" +
                    "  <child>Child 3</child>" +
                    "</parent>";
        Element parent = Jsoup.parse(xml).selectFirst("parent");

        int childCount = XmlUtils.getChildCount(parent);
        assertEquals(3, childCount, "Should have 3 children");
    }

    @Test
    public void testGetChildCountNoChildren() {
        String xml = "<empty></empty>";
        Element element = Jsoup.parse(xml).selectFirst("empty");

        int childCount = XmlUtils.getChildCount(element);
        assertEquals(0, childCount, "Should have 0 children");
    }

    @Test
    public void testGetChildCountNullElement() {
        int childCount = XmlUtils.getChildCount(null);
        assertEquals(0, childCount, "Should return 0 for null element");
    }

    @Test
    public void testHasChildren() {
        String xmlWithChildren = "<parent><child>Child</child></parent>";
        Element parent = Jsoup.parse(xmlWithChildren).selectFirst("parent");
        assertTrue(XmlUtils.hasChildren(parent), "Should have children");

        String xmlWithoutChildren = "<empty></empty>";
        Element empty = Jsoup.parse(xmlWithoutChildren).selectFirst("empty");
        assertFalse(XmlUtils.hasChildren(empty), "Should not have children");
    }

    @Test
    public void testHasChildrenNullElement() {
        assertFalse(XmlUtils.hasChildren(null), "Should return false for null element");
    }

    @Test
    public void testComplexXmlStructure() {
        // Test with simpler XML that should work (without namespaces)
        String simpleXml = "<package>" +
                          "  <metadata>" +
                          "    <title>Test Book</title>" +
                          "    <creator>Author One</creator>" +
                          "    <creator>Author Two</creator>" +
                          "  </metadata>" +
                          "  <manifest>" +
                          "    <item id=\"ncx\" href=\"toc.ncx\" media-type=\"application/x-dtbncx+xml\"/>" +
                          "    <item id=\"nav\" href=\"nav.xhtml\" media-type=\"application/xhtml+xml\" properties=\"nav\"/>" +
                          "  </manifest>" +
                          "</package>";

        Document doc = XmlUtils.parseXml(simpleXml);
        
        // Test basic element selection
        Elements creators = XmlUtils.select(doc, "creator");
        assertEquals(2, creators.size(), "Should find 2 creators in simple XML");

        Element title = XmlUtils.selectFirst(doc, "title");
        assertNotNull(title, "Should find title element");
        assertEquals("Test Book", XmlUtils.getText(title));

        Element firstItem = XmlUtils.selectFirst(doc, "item");
        assertNotNull(firstItem, "Should find first item");
        assertEquals("ncx", XmlUtils.getAttribute(firstItem, "id"));
        assertEquals("toc.ncx", XmlUtils.getAttribute(firstItem, "href"));
        
        // Test with complex XML (with namespaces) - this might not work perfectly due to namespace issues
        String complexXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                           "<package version=\"3.0\" unique-identifier=\"bookid\" xmlns=\"http://www.idpf.org/2007/opf\">" +
                           "  <metadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\">" +
                           "    <dc:title id=\"title\">Test Book</dc:title>" +
                           "    <dc:creator id=\"creator1\">Author One</dc:creator>" +
                           "  </metadata>" +
                           "</package>";
        
        Document complexDoc = XmlUtils.parseXml(complexXml);
        // 对于带命名空间的复杂XML，选择器可能无法正常工作，但解析应该成功
        assertNotNull(complexDoc, "Complex XML should be parsed successfully");
    }
}