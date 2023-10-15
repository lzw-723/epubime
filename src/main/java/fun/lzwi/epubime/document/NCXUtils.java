package fun.lzwi.epubime.document;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fun.lzwi.epubime.document.section.NavPoint;
import fun.lzwi.epubime.util.XmlUtils;

public class NCXUtils {
    public static Node getNcxElement(InputStream in) throws ParserConfigurationException, SAXException, IOException {
        return XmlUtils.getElementsByTagName(in, "ncx").item(0);
    }

    public static Map<String, String> getHead(Node ncx) {
        Node headNode = XmlUtils.getChildNodeByTagName(ncx, "head");
        NodeList ncxMeta = headNode.getChildNodes();
        Map<String, String> head = new HashMap<>();
        XmlUtils.foreachNodeList(ncxMeta, m -> {
            if ("meta".equals(m.getNodeName())) {
                String name = XmlUtils.getNodeAttribute(m, "name");
                String content = XmlUtils.getNodeAttribute(m, "content");
                head.put(name, content);
            }
        });
        return head;
    }

    private static String getText(Node node) {
        if (node == null) {
            return null;
        }
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node n = childNodes.item(i);
            if ("text".equals(n.getNodeName())) {
                return n.getTextContent();
            }
        }
        return null;
    }

    public static String getDocTitle(Node ncx) {
        Node docTitleNode = XmlUtils.getChildNodeByTagName(ncx, "docTitle");
        return getText(docTitleNode);
    }

    public static String getDocAuthor(Node ncx) {
        Node docAuthorNode = XmlUtils.getChildNodeByTagName(ncx, "docAuthor");
        return getText(docAuthorNode);
    }

    private static NavPoint getNavPoint(Node navPointNode) {
        NavPoint navPoint = new NavPoint();
        navPoint.setId(XmlUtils.getNodeAttribute(navPointNode, "id"));
        navPoint.setPlayOrder(Integer.parseInt(XmlUtils.getNodeAttribute(navPointNode, "playOrder")));
        XmlUtils.foreachNodeList(navPointNode.getChildNodes(), n -> {
            if ("navLabel".equals(n.getNodeName())) {
                navPoint.setNavLabel(getText(n));
            } else if ("content".equals(n.getNodeName())) {
                navPoint.setContent(XmlUtils.getNodeAttribute(n, "src"));
            }
        });
        return navPoint;
    }

    public static List<NavPoint> getNavMap(Node ncx) {
        List<NavPoint> navPoints = new ArrayList<>();
        Node navMapNode = XmlUtils.getChildNodeByTagName(ncx, "navMap");
        XmlUtils.foreachNodeList(navMapNode.getChildNodes(), p -> {
            if ("navPoint".equals(p.getNodeName())) {
                navPoints.add(getNavPoint(p));
            }
        });
        return navPoints;
    }
}
