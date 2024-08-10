package fun.lzwi.epubime.easy;

import fun.lzwi.Utils;
import fun.lzwi.epubime.util.XmlUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class EasyHTMLTest {

    private Document doc;

    @Before
    public void setUp() throws Exception {
        doc = XmlUtils.getDocument(Files.newInputStream(Utils.getFile("section2.xhtml").toPath()));
    }


    @Test
    public void testConstructorWithHtml() {
        // 定义一个测试用的 HTML 字符串
        String html = "<html><head><title>Test Title</title></head><body>Test Body</body></html>";

        // 创建 EasyHTML 对象并传入 HTML 字符串
        EasyHTML easyHTML = new EasyHTML(html);

        // 验证 Document 对象的属性是否正确设置
        assertEquals("Test Title", easyHTML.getTitle());
        assertEquals("Test Body", easyHTML.getBody());
    }

    @Test
    public void testGetTitle() {
        // 创建 EasyHTML 对象并传入模拟的 Document
        EasyHTML easyHTML = new EasyHTML(doc);

        // 获取标题并验证是否正确
        assertEquals("Title", easyHTML.getTitle());
    }

    @Test
    public void testGetBody() {
        // 创建 EasyHTML 对象并传入模拟的 Document
        EasyHTML easyHTML = new EasyHTML(doc);

        // 获取正文内容并验证是否正确
        assertEquals("content", easyHTML.getBody().trim());
    }

    @Test
    public void testAddStyle() {

        EasyHTML easyHTML = new EasyHTML(doc);

        // 调用 addStyle 方法并验证 XhtmlDocUtils.addStyle 方法是否被正确调用
        easyHTML.addStyle("body { color: red; }");
        assertTrue(easyHTML.getString().contains("<style type=\"text/css\">body { color: red; }</style>"));
    }

    @Test
    public void testAddLink() {
        // 创建 EasyHTML 对象并传入模拟的 Document
        EasyHTML easyHTML = new EasyHTML(doc);

        // 调用 addLink 方法并验证 XhtmlDocUtils.addLink 方法是否被正确调用
        easyHTML.addLink("https://example.com");
        assertTrue(easyHTML.getString().contains("<link href=\"https://example.com\" rel=\"stylesheet\" type=\"text/css\">"));

    }

    @Test
    public void testAddScript() {

        // 创建 EasyHTML 对象并传入模拟的 Document
        EasyHTML easyHTML = new EasyHTML(doc);

        // 调用 addScript 方法并验证 XhtmlDocUtils.addScript 方法是否被正确调用
        easyHTML.addScript("console.log('Hello, World!');");
        assertTrue(easyHTML.getString().contains("<script type=\"text/javascript\">console.log('Hello, World!');</script>"));
    }

    @Test
    public void testAddScriptHref() {
        // 创建 EasyHTML 对象并传入模拟的 Document
        EasyHTML easyHTML = new EasyHTML(doc);

        // 调用 addScriptHref 方法并验证 XhtmlDocUtils.addScriptHref 方法是否被正确调用
        easyHTML.addScriptHref("https://example.com/script.js");
        assertTrue(easyHTML.getString().contains("<script src=\"https://example.com/script.js\" type=\"text/javascript\"></script>"));

    }

    @Test
    public void testGetString() {
        // 创建 EasyHTML 对象并传入模拟的 Document
        EasyHTML easyHTML = new EasyHTML(doc);

        // 调用 getString 方法获取转换后的字符串
        String result = easyHTML.getString();

        // 验证转换后的字符串是否正确
        assertNotNull(result);
        assertTrue(result.contains("<html"));
        assertTrue(result.contains("<head"));
        assertTrue(result.contains("<title>Title</title>"));
        assertTrue(result.contains("<body>"));
        assertTrue(result.contains("</body>"));
        assertTrue(result.contains("</html>"));
    }
}