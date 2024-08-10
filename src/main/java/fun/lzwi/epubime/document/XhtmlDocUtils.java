package fun.lzwi.epubime.document;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fun.lzwi.epubime.util.XmlUtils;

public class XhtmlDocUtils {
    protected static String getTitle(Document doc) {
        Node head = XmlUtils.getChildNodeByTagName(doc.getDocumentElement(), "head");
        assert head != null;
        Node title = XmlUtils.getChildNodeByTagName(head, "title");
        assert title != null;
        return title.getTextContent();
    }

    protected static String getBody(Document doc) {
        Node body = XmlUtils.getChildNodeByTagName(doc.getDocumentElement(), "body");
        assert body != null;
        return body.getTextContent();
    }

    public static Document addStyle(Document doc, String style) {
        Node head = XmlUtils.getChildNodeByTagName(doc.getDocumentElement(), "head");
        assert head != null;
        Element styleNode = doc.createElement("style");
        styleNode.setAttribute("type", "text/css");
        styleNode.setTextContent(style);
        head.appendChild(styleNode);
        return doc;
    }

    public static Document addLink(Document doc, String href) {
        Node head = XmlUtils.getChildNodeByTagName(doc.getDocumentElement(), "head");
        assert head != null;
        Element link = doc.createElement("link");
        link.setAttribute("rel", "stylesheet");
        link.setAttribute("type", "text/css");
        link.setAttribute("href", href);
        head.appendChild(link);
        return doc;
    }

    public static Document addScript(Document doc, String script) {
        Node body = XmlUtils.getChildNodeByTagName(doc.getDocumentElement(), "body");
        assert body != null;
        Element scriptNode = doc.createElement("script");
        scriptNode.setAttribute("type", "text/javascript");
        scriptNode.setTextContent(script);
        body.appendChild(scriptNode);
        return doc;
    }

    public static Document addScriptHref(Document doc, String href) {
        Node body = XmlUtils.getChildNodeByTagName(doc.getDocumentElement(), "body");
        assert body != null;
        Element scriptNode = doc.createElement("script");
        scriptNode.setAttribute("type", "text/javascript");
        scriptNode.setAttribute("src", href);
        body.appendChild(scriptNode);
        return doc;
    }
}
