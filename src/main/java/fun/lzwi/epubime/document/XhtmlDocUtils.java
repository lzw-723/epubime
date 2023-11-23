package fun.lzwi.epubime.document;

import org.w3c.dom.Document;
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
}
