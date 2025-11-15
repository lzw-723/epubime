package fun.lzwi.epubime.exception;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.*;

public class EpubXmlParseExceptionTest {

    @Test
    public void testConstructorWithXPath() {
        String message = "Invalid XML structure";
        String fileName = "content.opf";
        String filePath = "OEBPS/content.opf";
        String xpath = "/package/metadata/title";
        int lineNumber = 15;
        int columnNumber = 8;

        EpubXmlParseException exception = new EpubXmlParseException(
            message, fileName, filePath, xpath, lineNumber, columnNumber);

        assertTrue("Message should contain the expected text", 
                  exception.getMessage().contains(message));
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        assertEquals(xpath, exception.getXPath());
        assertEquals(lineNumber, exception.getLineNumber());
        assertEquals(columnNumber, exception.getColumnNumber());
        assertEquals("xmlParsing", exception.getOperation());
        assertEquals(EpubParseException.ErrorCode.XML_PARSE_ERROR, exception.getErrorCode());
    }

    @Test
    public void testConstructorWithElement() {
        String message = "Missing required attribute";
        String fileName = "toc.ncx";
        String filePath = "OEBPS/toc.ncx";
        String expectedStructure = "<navPoint> with <navLabel> and <content>";
        
        String html = "<navPoint id=\"nav1\">" +
                     "  <content src=\"chapter1.html\"/>" +
                     "</navPoint>";
        Element element = Jsoup.parse(html).select("navPoint").first();

        EpubXmlParseException exception = new EpubXmlParseException(
            message, fileName, filePath, element, expectedStructure);

        assertTrue("Message should contain the expected text", 
                  exception.getMessage().contains(message));
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        assertEquals(expectedStructure, exception.getExpectedStructure());
        assertNotNull(exception.getXPath());
        assertNotNull(exception.getElementInfo());
        assertNotNull(exception.getActualContent());
        assertEquals(EpubParseException.ErrorCode.XML_INVALID_STRUCTURE, exception.getErrorCode());
    }

    @Test
    public void testConstructorWithElementNull() {
        String message = "Element is null";
        String fileName = "test.xml";
        String filePath = "test/test.xml";
        String expectedStructure = "<root> element";

        EpubXmlParseException exception = new EpubXmlParseException(
            message, fileName, filePath, (Element) null, expectedStructure);

        assertTrue("Message should contain the expected text", 
                  exception.getMessage().contains(message));
        assertEquals("/", exception.getXPath());
        assertEquals("Element: null", exception.getElementInfo());
        assertEquals("null", exception.getActualContent());
    }

    @Test
    public void testConstructorWithExpectedAndActual() {
        String message = "Structure mismatch";
        String fileName = "metadata.xml";
        String filePath = "OEBPS/metadata.xml";
        String xpath = "/package/metadata/creator";
        String expectedStructure = "<creator role=\"aut\">Author Name</creator>";
        String actualContent = "<creator>Author Name</creator>";

        EpubXmlParseException exception = new EpubXmlParseException(
            message, fileName, filePath, xpath, expectedStructure, actualContent);

        assertTrue("Message should contain the expected text", 
                  exception.getMessage().contains(message));
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        assertEquals(xpath, exception.getXPath());
        assertEquals(expectedStructure, exception.getExpectedStructure());
        assertEquals(actualContent, exception.getActualContent());
        assertEquals(EpubParseException.ErrorCode.XML_INVALID_STRUCTURE, exception.getErrorCode());
    }

    @Test
    public void testConstructorWithLongContent() {
        String message = "Content too long";
        String fileName = "large.xml";
        String filePath = "OEBPS/large.xml";
        String xpath = "/package";
        String expectedStructure = "<package> with multiple metadata elements</package>";
        String longContent = "";
        for (int i = 0; i < 300; i++) {
            longContent += "a";
        } // 超过200字符的内容

        EpubXmlParseException exception = new EpubXmlParseException(
            message, fileName, filePath, xpath, expectedStructure, longContent);

        String actualContent = exception.getActualContent();
        assertTrue("Content should be truncated", actualContent.contains("... (truncated)"));
        assertTrue("Content should start with 'a'", actualContent.startsWith("a"));
        assertTrue("Content length should be around 217", 
                  actualContent.length() <= 220);
    }

    @Test
    public void testConstructorMissingRequiredElement() {
        String fileName = "content.opf";
        String filePath = "OEBPS/content.opf";
        String missingElement = "title";
        
        String html = "<metadata>" +
                     "  <creator>Author</creator>" +
                     "  <language>en</language>" +
                     "</metadata>";
        Element parentElement = Jsoup.parse(html).select("metadata").first();

        EpubXmlParseException exception = new EpubXmlParseException(
            fileName, filePath, missingElement, parentElement);

        assertTrue("Message should contain missing element text", 
                  exception.getMessage().contains("Missing required XML element: title"));
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        // XPath可能包含完整的HTML路径，因为JSoup解析HTML时会添加html和body元素
        String xpath = exception.getXPath();
        assertTrue("XPath should contain title element", xpath.contains("title"));
        assertTrue("XPath should show title is child of metadata", 
                  xpath.contains("metadata") && xpath.contains("title"));
        assertEquals("Expected element: title", exception.getExpectedStructure());
        assertEquals(EpubParseException.ErrorCode.XML_MISSING_REQUIRED_ELEMENT, exception.getErrorCode());
    }

    @Test
    public void testConstructorMissingRequiredElementNoParent() {
        String fileName = "root.xml";
        String filePath = "OEBPS/root.xml";
        String missingElement = "package";

        EpubXmlParseException exception = new EpubXmlParseException(
            fileName, filePath, missingElement, (Element) null);

        assertTrue("Message should contain missing element text", 
                  exception.getMessage().contains("Missing required XML element: package"));
        // XPath可能包含完整的HTML路径或只是根路径
        String xpath = exception.getXPath();
        assertTrue("XPath should contain package element", xpath.contains("package"));
        assertEquals("Expected element: package", exception.getExpectedStructure());
        assertEquals("null", exception.getActualContent());
    }

    @Test
    public void testConstructorInvalidAttribute() {
        String fileName = "toc.ncx";
        String filePath = "OEBPS/toc.ncx";
        String attributeName = "src";
        String expectedValue = "chapter1.html";
        String actualValue = "";
        
        String html = "<content></content>";
        Element element = Jsoup.parse(html).select("content").first();

        EpubXmlParseException exception = new EpubXmlParseException(
            fileName, filePath, element, attributeName, expectedValue, actualValue);

        assertTrue("Message should contain invalid attribute text", 
                  exception.getMessage().contains("Invalid XML attribute: src"));
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        assertNotNull(exception.getXPath());
        assertNotNull(exception.getElementInfo());
        assertEquals("Expected value: chapter1.html", exception.getExpectedStructure());
        assertEquals("Actual value: ", exception.getActualContent());
        assertEquals(EpubParseException.ErrorCode.XML_INVALID_ATTRIBUTE, exception.getErrorCode());
    }

    @Test
    public void testXPathBuildingWithId() {
        String html = "<package>" +
                     "  <metadata id=\"meta1\">" +
                     "    <title>Test Book</title>" +
                     "  </metadata>" +
                     "</package>";
        Element element = Jsoup.parse(html).select("metadata").first();

        EpubXmlParseException exception = new EpubXmlParseException(
            "Test", "test.xml", "test/test.xml", element, "Expected structure");

        String xpath = exception.getXPath();
        assertTrue("XPath should contain id attribute", xpath.contains("[@id='meta1']"));
    }

    @Test
    public void testXPathBuildingWithClass() {
        String html = "<package>" +
                     "  <metadata class=\"book-meta\">" +
                     "    <title>Test Book</title>" +
                     "  </metadata>" +
                     "</package>";
        Element element = Jsoup.parse(html).select("metadata").first();

        EpubXmlParseException exception = new EpubXmlParseException(
            "Test", "test.xml", "test/test.xml", element, "Expected structure");

        String xpath = exception.getXPath();
        assertTrue("XPath should contain class attribute", xpath.contains("[@class='book-meta']"));
    }

    @Test
    public void testElementInfoWithAttributes() {
        String html = "<content src=\"chapter1.html\" id=\"content1\" class=\"chapter-content\">Chapter 1</content>";
        Element element = Jsoup.parse(html).select("content").first();

        EpubXmlParseException exception = new EpubXmlParseException(
            "Test", "test.xml", "test/test.xml", element, "Expected structure");

        String elementInfo = exception.getElementInfo();
        assertTrue("Element info should contain tag name", elementInfo.contains("Element: content"));
        assertTrue("Element info should contain attributes", elementInfo.contains("Attributes:"));
        assertTrue("Element info should contain src attribute", elementInfo.contains("src='chapter1.html'"));
        assertTrue("Element info should contain text", elementInfo.contains("[Text: 'Chapter 1']"));
    }

    @Test
    public void testElementInfoWithoutText() {
        String html = "<empty></empty>";
        Element element = Jsoup.parse(html).select("empty").first();

        EpubXmlParseException exception = new EpubXmlParseException(
            "Test", "test.xml", "test/test.xml", element, "Expected structure");

        String elementInfo = exception.getElementInfo();
        assertEquals("Element: empty", elementInfo.trim());
    }

    @Test
    public void testElementInfoWithLongText() {
        String longText = "This is a very long text that should exceed 100 characters limit for element info display in the exception message";
        String html = "<content>" + longText + "</content>";
        Element element = Jsoup.parse(html).select("content").first();

        EpubXmlParseException exception = new EpubXmlParseException(
            "Test", "test.xml", "test/test.xml", element, "Expected structure");

        String elementInfo = exception.getElementInfo();
        assertFalse("Element info should not contain long text", elementInfo.contains(longText));
    }

    @Test
    public void testNullContentHandling() {
        EpubXmlParseException exception = new EpubXmlParseException(
            "Test", "test.xml", "test/test.xml", "/root", "expected", null);

        assertEquals("null", exception.getActualContent());
    }
}