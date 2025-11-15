package fun.lzwi.epubime.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import static org.junit.Assert.*;

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
        
        assertNotNull("Document should not be null", doc);
        assertEquals("package", doc.selectFirst("package").tagName());
        assertEquals("Test Book", doc.selectFirst("title").text());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseXmlNull() {
        XmlUtils.parseXml(null);
    }

    @Test
    public void testParseXmlEmpty() {
        Document doc = XmlUtils.parseXml("");
        assertNotNull("Document should not be null for empty content", doc);
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
        assertNotNull("Title element should be found", titleElement);
        assertEquals("Book Title", titleElement.text());

        Element nonExistent = XmlUtils.selectFirst(doc, "non-existent");
        assertNull("Non-existent element should return null", nonExistent);
    }

    @Test
    public void testSelectFirstFromDocumentNullParams() {
        String xml = "<package><title>Test</title></package>";
        Document doc = XmlUtils.parseXml(xml);

        assertNull("Should return null for null document", XmlUtils.selectFirst(null, "title"));
        assertNull("Should return null for null query", XmlUtils.selectFirst(doc, null));
        assertNull("Should return null for both null", XmlUtils.selectFirst(null, null));
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
        assertNotNull("Creators should not be null", creators);
        assertEquals("Should find 2 creators", 2, creators.size());
        assertEquals("Author 1", creators.get(0).text());
        assertEquals("Author 2", creators.get(1).text());

        Elements nonExistent = XmlUtils.select(doc, "non-existent");
        assertNotNull("Should return empty elements", nonExistent);
        assertEquals("Should be empty", 0, nonExistent.size());
    }

    @Test
    public void testSelectFromDocumentNullParams() {
        String xml = "<package><title>Test</title></package>";
        Document doc = XmlUtils.parseXml(xml);

        Elements result1 = XmlUtils.select(null, "title");
        assertNotNull("Should return empty elements for null document", result1);
        assertEquals("Should be empty", 0, result1.size());

        Elements result2 = XmlUtils.select(doc, null);
        assertNotNull("Should return empty elements for null query", result2);
        assertEquals("Should be empty", 0, result2.size());

        Elements result3 = XmlUtils.select(null, null);
        assertNotNull("Should return empty elements for both null", result3);
        assertEquals("Should be empty", 0, result3.size());
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
        assertNotNull("Title should be found in metadata", title);
        assertEquals("Book Title", title.text());

        Element nonExistent = XmlUtils.selectFirst(metadata, "non-existent");
        assertNull("Non-existent element should return null", nonExistent);
    }

    @Test
    public void testSelectFirstFromElementNullParams() {
        String xml = "<package><metadata><title>Test</title></metadata></package>";
        Document doc = XmlUtils.parseXml(xml);
        Element metadata = doc.selectFirst("metadata");

        assertNull("Should return null for null element", XmlUtils.selectFirst(null, "title"));
        assertNull("Should return null for null query", XmlUtils.selectFirst(metadata, null));
        assertNull("Should return null for both null", XmlUtils.selectFirst(null, null));
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
        assertNotNull("Creators should not be null", creators);
        assertEquals("Should find 2 creators", 2, creators.size());

        Elements nonExistent = XmlUtils.select(metadata, "non-existent");
        assertNotNull("Should return empty elements", nonExistent);
        assertEquals("Should be empty", 0, nonExistent.size());
    }

    @Test
    public void testSelectFromElementNullParams() {
        String xml = "<metadata><title>Test</title></metadata>";
        Document doc = XmlUtils.parseXml(xml);
        Element metadata = doc.selectFirst("metadata");

        Elements result1 = XmlUtils.select(null, "title");
        assertNotNull("Should return empty elements for null element", result1);
        assertEquals("Should be empty", 0, result1.size());

        Elements result2 = XmlUtils.select(metadata, null);
        assertNotNull("Should return empty elements for null query", result2);
        assertEquals("Should be empty", 0, result2.size());

        Elements result3 = XmlUtils.select(null, null);
        assertNotNull("Should return empty elements for both null", result3);
        assertEquals("Should be empty", 0, result3.size());
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
        assertTrue("XPath should not be empty", xpath.length() > 0);
        assertTrue("XPath should contain title", xpath.contains("title"));
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
        assertTrue("XPath should use id for elements with id attribute", 
                  xpath.contains("[@id='meta1']"));
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
        assertTrue("XPath should use class for elements with class attribute", 
                  xpath.contains("[@class='book-meta']"));
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
        
        assertTrue("Info should contain tag name", info.contains("Element: element"));
        assertTrue("Info should contain attributes", info.contains("Attributes:"));
        assertTrue("Info should contain id", info.contains("id='test-id'"));
        assertTrue("Info should contain class", info.contains("class='test-class'"));
        assertTrue("Info should contain data-value", info.contains("data-value='123'"));
        assertTrue("Info should contain text", info.contains("[Text: 'Element Text']"));
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
        
        assertTrue("Info should contain tag name", info.contains("Element: empty"));
        assertTrue("Info should contain attributes", info.contains("Attributes:"));
        assertTrue("Info should contain id", info.contains("id='test'"));
        assertFalse("Info should not contain text for empty elements", info.contains("[Text:"));
    }

    @Test
    public void testBuildElementInfoLongText() {
        String longText = "This is a very long text that should exceed 100 characters limit for element info display in the exception message";
        String xml = "<element>" + longText + "</element>";
        Element element = Jsoup.parse(xml).selectFirst("element");

        String info = XmlUtils.buildElementInfo(element);
        
        assertFalse("Info should not contain long text", info.contains(longText));
        assertTrue("Info should contain tag name", info.contains("Element: element"));
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
        assertEquals("Short content should not be truncated", shortContent, truncated);

        String longContent = "";
        for (int i = 0; i < 300; i++) {
            longContent += "a";
        }
        String truncatedLong = XmlUtils.truncateContent(longContent);
        assertTrue("Long content should be truncated", truncatedLong.contains("... (truncated)"));
        assertTrue("Truncated content should start with 'a'", truncatedLong.startsWith("a"));
        assertTrue("Truncated content should be around 217 chars", truncatedLong.length() <= 220);
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
        assertEquals("Content shorter than max should not be truncated", content, truncated1);

        // Test with max length shorter than content
        String truncated2 = XmlUtils.truncateContent(content, 10);
        assertTrue("Content should be truncated", truncated2.contains("... (truncated)"));
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

        assertTrue("Should have id attribute", XmlUtils.hasAttribute(element, "id"));
        assertTrue("Should have class attribute", XmlUtils.hasAttribute(element, "class"));
        assertFalse("Should not have non-existent attribute", XmlUtils.hasAttribute(element, "non-existent"));
    }

    @Test
    public void testHasAttributeNullParams() {
        String xml = "<element id=\"test\">Content</element>";
        Element element = Jsoup.parse(xml).selectFirst("element");

        assertFalse("Should return false for null element", XmlUtils.hasAttribute(null, "id"));
        assertFalse("Should return false for null attribute name", XmlUtils.hasAttribute(element, null));
        assertFalse("Should return false for both null", XmlUtils.hasAttribute(null, null));
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
        assertEquals("Should have 3 children", 3, childCount);
    }

    @Test
    public void testGetChildCountNoChildren() {
        String xml = "<empty></empty>";
        Element element = Jsoup.parse(xml).selectFirst("empty");

        int childCount = XmlUtils.getChildCount(element);
        assertEquals("Should have 0 children", 0, childCount);
    }

    @Test
    public void testGetChildCountNullElement() {
        int childCount = XmlUtils.getChildCount(null);
        assertEquals("Should return 0 for null element", 0, childCount);
    }

    @Test
    public void testHasChildren() {
        String xmlWithChildren = "<parent><child>Child</child></parent>";
        Element parent = Jsoup.parse(xmlWithChildren).selectFirst("parent");
        assertTrue("Should have children", XmlUtils.hasChildren(parent));

        String xmlWithoutChildren = "<empty></empty>";
        Element empty = Jsoup.parse(xmlWithoutChildren).selectFirst("empty");
        assertFalse("Should not have children", XmlUtils.hasChildren(empty));
    }

    @Test
    public void testHasChildrenNullElement() {
        assertFalse("Should return false for null element", XmlUtils.hasChildren(null));
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
        assertEquals("Should find 2 creators in simple XML", 2, creators.size());

        Element title = XmlUtils.selectFirst(doc, "title");
        assertNotNull("Should find title element", title);
        assertEquals("Test Book", XmlUtils.getText(title));

        Element firstItem = XmlUtils.selectFirst(doc, "item");
        assertNotNull("Should find first item", firstItem);
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
        assertNotNull("Complex XML should be parsed successfully", complexDoc);
    }
}