package fun.lzwi.epubime.document;


import fun.lzwi.Utils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import fun.lzwi.epubime.util.XmlUtils;
import org.w3c.dom.Node;

import java.nio.file.Files;

import static org.junit.Assert.*;

public class XhtmlDocUtilsTest {

    private Document doc;

    @Before
    public void setUp() throws Exception {
        doc = XmlUtils.getDocument(Files.newInputStream(Utils.getFile("section1.xhtml").toPath()));
    }

    @Test
    public void testGetTitle() {
        // 调用获取标题的方法
        String title = XhtmlDocUtils.getTitle(doc);

        // 断言获取到的标题与设置的标题一致
        assertEquals("第一章", title);
    }

    @Test
    public void testGetBody() {
        // 调用获取正文的方法
        String body = XhtmlDocUtils.getBody(doc);

        // 断言获取到的正文与设置的正文一致
        assertEquals(340, body.trim().length());
    }

    @Test
    public void testAddStyle() {

        // 添加样式
        XhtmlDocUtils.addStyle(doc, "body { color: red; }");

        // 获取样式节点
        Node head = XmlUtils.getChildNodeByTagName(doc.getDocumentElement(), "head");
        Node styleNode = XmlUtils.getChildNodeByTagName(head, "style");

        // 断言样式节点存在且内容正确
        assertNotNull(styleNode);
        assertEquals("body { color: red; }", styleNode.getTextContent());
    }

    @Test
    public void testAddLink() {
        // 添加样式链接
        XhtmlDocUtils.addLink(doc, "styles.css");

        // 获取样式链接节点
        Node head = XmlUtils.getChildNodeByTagName(doc.getDocumentElement(), "head");
        Node link = XmlUtils.getChildNodeByTagName(head, "link");

        // 断言样式链接节点存在且属性正确
        assertNotNull(link);
        assertEquals("stylesheet", link.getAttributes().getNamedItem("rel").getTextContent());
        assertEquals("text/css", link.getAttributes().getNamedItem("type").getTextContent());
        assertEquals("styles.css", link.getAttributes().getNamedItem("href").getTextContent());
    }

    @Test
    public void testAddScript() {

        // 添加脚本
        XhtmlDocUtils.addScript(doc, "console.log('Hello, World!');");

        // 获取脚本节点
        Node body = XmlUtils.getChildNodeByTagName(doc.getDocumentElement(), "body");
        Node scriptNode = XmlUtils.getChildNodeByTagName(body, "script");

        // 断言脚本节点存在且内容正确
        assertNotNull(scriptNode);
        assertEquals("text/javascript", scriptNode.getAttributes().getNamedItem("type").getTextContent());
        assertEquals("console.log('Hello, World!');", scriptNode.getTextContent());
    }

    @Test
    public void testAddScriptHref() {

        // 添加脚本链接
        XhtmlDocUtils.addScriptHref(doc, "script.js");

        // 获取脚本链接节点
        Node body = XmlUtils.getChildNodeByTagName(doc.getDocumentElement(), "body");
        Node scriptNode = XmlUtils.getChildNodeByTagName(body, "script");

        // 断言脚本链接节点存在且属性正确
        assertNotNull(scriptNode);
        assertEquals("text/javascript", scriptNode.getAttributes().getNamedItem("type").getTextContent());
        assertEquals("script.js", scriptNode.getAttributes().getNamedItem("src").getTextContent());
    }
}
