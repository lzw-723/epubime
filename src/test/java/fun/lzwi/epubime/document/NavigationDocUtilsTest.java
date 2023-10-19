package fun.lzwi.epubime.document;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fun.lzwi.Utils;
import fun.lzwi.epubime.document.section.Nav;
import fun.lzwi.epubime.document.section.element.NavItem;
import fun.lzwi.epubime.util.XmlUtils;

public class NavigationDocUtilsTest {
    private Document doc;
    private Node nav;
    private Node nav2;

    @Before
    public void setUp() throws UnsupportedEncodingException, IOException, SAXException, ParserConfigurationException {
        InputStream xhtml = Files.newInputStream(Utils.getFile("nav.xhtml").toPath());
        doc = XmlUtils.getDocument(xhtml);
        nav = doc.getElementsByTagName("nav").item(0);
        nav2 = doc.getElementsByTagName("nav").item(1);
    }

    @Test
    public void testGetNavs() throws SAXException, IOException, ParserConfigurationException {
        List<Nav> navs = NavigationDocUtils.getNavs(doc);
        assertEquals(2, navs.size());
    }

    @Test
    public void testGetNavsNested() throws SAXException, IOException, ParserConfigurationException {
        List<Nav> navs = NavigationDocUtils.getNavs(doc);
        assertEquals(3, navs.get(0).getItems().get(0).getChildren().size());
    }

    @Test
    public void testGetNavItems() throws UnsupportedEncodingException, IOException {
        Node ol = XmlUtils.getChildNodeByTagName(nav2, "ol");
        List<NavItem> navItems = NavigationDocUtils.getNavItems(ol);
        assertEquals(1, navItems.size());
    }

    @Test
    public void testGetNavItemsNested() throws UnsupportedEncodingException, IOException {
        Node ol = XmlUtils.getChildNodeByTagName(nav, "ol");
        List<NavItem> navItems = NavigationDocUtils.getNavItems(ol);
        assertEquals(1, navItems.size());
        assertEquals(3, navItems.get(0).getChildren().size());
    }

    @Test
    public void testGetNavItem() throws UnsupportedEncodingException, IOException {
        Node ol = XmlUtils.getChildNodeByTagName(nav2, "ol");
        Node li = XmlUtils.getChildNodeByTagName(ol, "li");
        NavItem item = NavigationDocUtils.getNavItem(li);
        assertEquals("目录", item.getTitle());
        assertEquals("#toc", item.getHref());
    }

    @Test
    public void testGetNavItemNested() throws UnsupportedEncodingException, IOException {
        Node ol = XmlUtils.getChildNodeByTagName(nav, "ol");
        Node li = XmlUtils.getChildNodeByTagName(ol, "li");
        NavItem item = NavigationDocUtils.getNavItem(li);
        assertEquals("开始", item.getTitle());
        assertEquals("Section0001.xhtml", item.getHref());
        assertEquals("单层嵌套节点", 3, item.getChildren().size());
    }
}
