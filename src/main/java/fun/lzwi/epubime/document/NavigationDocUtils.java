package fun.lzwi.epubime.document;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fun.lzwi.epubime.document.section.Nav;
import fun.lzwi.epubime.document.section.element.NavItem;
import fun.lzwi.epubime.util.XmlUtils;

public class NavigationDocUtils {

    private static Node getBodyNode(Document doc) {
        return XmlUtils.getChildNodeByTagName(doc.getDocumentElement(), "body");
    }

    protected static List<Nav> getNavs(Document doc) {
        List<Nav> navs = new ArrayList<>();

        XmlUtils.foreachNodeList(getBodyNode(doc).getChildNodes(), n -> {
            if (n.getNodeName().equals("nav")) {
                Nav nav = new Nav();
                nav.setEpubType(XmlUtils.getNodeAttribute(n, "epub:type"));
                Node l = XmlUtils.getChildNodeByTagName(n, "ol");
                if (l != null) {
                    nav.setItems(getNavItems(l));
                } else if ((l = XmlUtils.getChildNodeByTagName(n, "ul")) != null) {
                    nav.setItems(getNavItems(l));
                }
                navs.add(nav);
            }
        });
        return navs;
    }

    protected static List<NavItem> getNavItems(NodeList list) {
        List<NavItem> items = new ArrayList<>();
        XmlUtils.foreachNodeList(list, n -> {
            String nodeName = n.getNodeName();
            if (nodeName.equals("li")) {
                NavItem item = getNavItem(n);
                items.add(item);
            }
        });
        return items;
    }

    protected static List<NavItem> getNavItems(Node node) {
        return getNavItems(node.getChildNodes());
    }

    /**
     * 
     * @param node 一个包含a的li
     * @return NavItem
     */
    protected static NavItem getNavItem(Node node) {
        NavItem item = new NavItem();
        Node a = XmlUtils.getChildNodeByTagName(node, "a");
        if (a != null) {
            item.setHref(XmlUtils.getNodeAttribute(a, "href"));
            item.setTitle(a.getTextContent());
        }
        Node ol = XmlUtils.getChildNodeByTagName(node, "ol");
        if (ol != null) {
            item.setChildren(getNavItems(ol));
        }
        return item;
    }
}
