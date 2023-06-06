package fun.lzwi.epubime.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fun.lzwi.epubime.document.NavigationDocument;
import fun.lzwi.epubime.document.section.Nav;
import fun.lzwi.epubime.document.section.element.A;
import fun.lzwi.epubime.document.section.element.H;
import fun.lzwi.epubime.document.section.element.HtmlTag;
import fun.lzwi.epubime.document.section.element.Li;
import fun.lzwi.epubime.document.section.element.Ol;

public class NavigationDocumentUtils {
    public static Node getBody(InputStream doc) {
        try {
            return XmlUtils.getElementsByTagName(doc, "body").item(0);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static NavigationDocument getDocument(Node body) {
        NavigationDocument navigationDocument = new NavigationDocument();
        navigationDocument.setNavs(getNavs(body));
        return navigationDocument;
    }

    private static List<Nav> getNavs(Node body) {
        List<Nav> navs = new ArrayList<>();

        XmlUtils.foreachNodeList(body.getChildNodes(), n -> {
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
