package fun.lzwi.epubime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import fun.lzwi.Util;

public class OpfCheckerTest {
    // opf文件
    static File FILE;

    @BeforeClass
    public static void beforeClass() {
        FILE = Util.getFile("content.opf");
    }

    @Test
    public void testCheckIdentifier() {
        assertTrue("存在Id", OpfChecker.checkIdentifier(FILE));
    }

    @Test
    public void testGetIdentifier() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("正确读取Id", "urn:uuid:d249e6e6-810d-43bb-91a4-9b8533daebe0", OpfChecker.getIdentifier(FILE));
    }

    @Test
    public void testCheckLanguage() {
        assertTrue("存在language", OpfChecker.checkLanguage(FILE));
    }

    @Test
    public void testGetLanguage() throws DOMException, ParserConfigurationException, SAXException, IOException {
        assertEquals("正确读取language", "en", OpfChecker.getLanguage(FILE));
    }

    @Test
    public void testCheckTitle() {
        assertTrue("存在title", OpfChecker.checkTitle(FILE));
    }

    @Test
    public void testGetTitle() throws DOMException, ParserConfigurationException, SAXException, IOException {
        assertEquals("正确读取title", "[此处填写主标题]", OpfChecker.getTitle(FILE));
    }

    @Test
    public void testGetManifest() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("manifest子节点数目正确", 3, XmlUtils.countChildNodes(OpfChecker.getManifest(FILE)));
    }

    @Test
    public void testGetSpine() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("spine子节点数目正确", 2, XmlUtils.countChildNodes(OpfChecker.getSpine(FILE)));
    }

    @Test
    public void testGetManifestItems() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("正确获取manifest的item", 3, OpfChecker.getManifestItems(FILE).size());
    }

    @Test
    public void testGetSpineItemRefs() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("正确获取spine的itemref", 2, OpfChecker.getSpineItemRefs(FILE).size());
    }
}
