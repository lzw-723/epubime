package fun.lzwi.epubime.document;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import fun.lzwi.epubime.document.section.Nav;
import fun.lzwi.epubime.document.section.element.A;
import fun.lzwi.epubime.document.section.element.H;
import fun.lzwi.epubime.document.section.element.HtmlTag;
import fun.lzwi.epubime.document.section.element.Li;
import fun.lzwi.epubime.document.section.element.Ol;
import fun.lzwi.epubime.util.XmlUtils;

public class NavigationDocUtils {
    // protected static String getBody(Document doc) {
    //     Node body = getBodyNode(doc);
    //     return XmlUtils.getNodeContent(body);
    // }

    private static Node getBodyNode(Document doc) {
        return XmlUtils.getChildNodeByTagName(doc.getDocumentElement(), "body");
    }

    // public static NavigationDocument getDocument(Node body) {
    // NavigationDocument navigationDocument = new NavigationDocument();
    // navigationDocument.setNavs(getNavs(body));
    // return navigationDocument;
    // }

    protected static List<Nav> getNavs(Document doc) {
        List<Nav> navs = new ArrayList<>();

        XmlUtils.foreachNodeList(getBodyNode(doc).getChildNodes(), n -> {
            if (n.getNodeName().equals("nav")) {
                Nav nav = new Nav();
                nav.setEpubType(XmlUtils.getNodeAttribute(n, "epub:type"));
                nav.setChildren(getHtmlTags(n));
                navs.add(nav);
            }
        });
        return navs;
    }

    private static List<HtmlTag> getHtmlTags(Node node) {
        List<HtmlTag> tags = new ArrayList<>();
        XmlUtils.foreachNodeList(node.getChildNodes(), n -> {
            HtmlTag tag = null;
            String nodeName = n.getNodeName();
            switch (nodeName) {
                case "ol":
                    Ol ol = new Ol();
                    ol.setChildren(getHtmlTags(n));
                    tag = ol;
                    break;
                case "li":
                    Li li = new Li();
                    li.setChildren(getHtmlTags(n));
                    tag = li;
                    break;
                case "a":
                    A a = new A();
                    a.setHref(XmlUtils.getNodeAttribute(n, "href"));
                    tag = a;
                    break;

                default:
                    if (nodeName.startsWith("h")) {
                        H h = new H();
                        h.setName(nodeName);
                        tag = h;
                    }
                    break;
            }
            if (tags != null) {
                tags.add(tag);
            }
        });
        return tags;
    }
}
