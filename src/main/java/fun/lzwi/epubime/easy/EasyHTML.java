package fun.lzwi.epubime.easy;

import fun.lzwi.epubime.document.XhtmlDocUtils;
import fun.lzwi.epubime.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EasyHTML {
    private Document doc;

    public EasyHTML(Document doc) {
        this.doc = (Document) doc.cloneNode(true);
    }

    public EasyHTML(String html) {
        try {
            this.doc = XmlUtils.getDocument(new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)));
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTitle() {
        Node head = XmlUtils.getChildNodeByTagName(doc.getDocumentElement(), "head");
        return XmlUtils.getChildNodeByTagName(head, "title").getTextContent();
    }

    public String getBody() {
        return doc.getDocumentElement().getElementsByTagName("body").item(0).getTextContent();
    }

    public EasyHTML addStyle(String style) {
        XhtmlDocUtils.addStyle(doc, style);
        return this;
    }

    public EasyHTML addLink(String href) {
        XhtmlDocUtils.addLink(doc, href);
        return this;
    }

    public EasyHTML addScript(String script) {
        XhtmlDocUtils.addScript(doc, script);
        return this;
    }

    public EasyHTML addScriptHref(String href) {
        XhtmlDocUtils.addScriptHref(doc, href);
        return this;
    }

    public Document getDocument() {
        return (Document) doc.cloneNode(true);
    }

    public String getString() {
        return XmlUtils.document2String(doc);
    }
}
