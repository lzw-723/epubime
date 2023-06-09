package fun.lzwi.epubime.document;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import fun.lzwi.epubime.util.XmlUtils;

public class XhtmlDocUtils {
    protected static String getTitle(Document doc) {
        Node head = XmlUtils.getChildNodeByTagName(doc.getDocumentElement(), "head");
        Node title = XmlUtils.getChildNodeByTagName(head, "title");
        return title.getTextContent();
    }

    protected static String getBody(Document doc) {
        Node body = XmlUtils.getChildNodeByTagName(doc.getDocumentElement(), "body");
        return XmlUtils.getNodeContent(body);
    }

    // TODO: plainText
    // private static String getPlainText(Document doc){
    // Node body = XmlUtils.getChildNodeByTagName(doc, "body");
    // StringBuilder sb = new StringBuilder();
    // XmlUtils.foreachNodeList(body.getChildNodes(), n -> {

    // });
    // }
    // private static String getPlainText(Node node) {
    // XmlUtils.foreachNodeList(node.get.getChildNodes(), n -> {

    // });
    // }
}
