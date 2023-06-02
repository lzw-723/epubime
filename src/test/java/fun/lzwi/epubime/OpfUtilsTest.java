package fun.lzwi.epubime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import fun.lzwi.Utils;

public class OpfUtilsTest {
    // opf文件
    static File FILE;

    @BeforeClass
    public static void beforeClass() {
        FILE = Utils.getFile("content.opf");
    }

    @Test
    public void testExistIdentifier() throws FileNotFoundException {
        assertTrue("存在Id", OpfUtils.existIdentifier(FILE));
    }

    @Test
    public void testGetIdentifier() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("正确读取Id", "urn:uuid:d249e6e6-810d-43bb-91a4-9b8533daebe0", OpfUtils.getIdentifier(FILE));
    }

    @Test
    public void testExistLanguage() throws FileNotFoundException {
        assertTrue("存在language", OpfUtils.existLanguage(FILE));
    }

    @Test
    public void testGetLanguage() throws DOMException, ParserConfigurationException, SAXException, IOException {
        assertEquals("正确读取language", "en", OpfUtils.getLanguage(FILE));
    }

    @Test
    public void testExistTitle() throws FileNotFoundException {
        assertTrue("存在title", OpfUtils.existTitle(FILE));
    }

    @Test
    public void testGetTitle() throws DOMException, ParserConfigurationException, SAXException, IOException {
        assertEquals("正确读取title", "[此处填写主标题]", OpfUtils.getTitle(FILE));
    }

    @Test
    public void testGetManifest() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("manifest子节点数目正确", 3, XmlUtils.countChildNodes(OpfUtils.getManifest(FILE).getChildNodes()));
    }

    @Test
    public void testGetSpine() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("spine子节点数目正确", 2, XmlUtils.countChildNodes(OpfUtils.getSpine(FILE).getChildNodes()));
    }

    @Test
    public void testGetManifestItems() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("正确获取manifest的item", 3, OpfUtils.getManifestItems(FILE).size());
    }

    @Test
    public void testGetSpineItemRefs() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("正确获取spine的itemref", 2, OpfUtils.getSpineItemRefs(FILE).size());
    }

    @Test
    public void testGetMetaData()
            throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        assertEquals("metadata子节点数目正确", 5, XmlUtils.countChildNodes(OpfUtils.getMetaData(FILE).getChildNodes()));
    }

    @Test
    public void testGetMetaDataItems()
            throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        assertEquals("正确获取metadata的meta", 5, OpfUtils.getMetaDataItems(FILE).size());
    }

    @Test
    public void testGetMetaDCs() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("正确获取metadata的dc", 3, OpfUtils.getMetaDCs(FILE).size());
    }

    @Test
    public void testGetUniqueIdentifier()
            throws DOMException, FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        assertEquals("能获取正确unique-identifier", "BookId", OpfUtils.getUniqueIdentifier(FILE));
    }

    @Test
    public void testGetVersion()
            throws DOMException, FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        assertEquals("能正确获取version", "3.0", OpfUtils.getVersion(FILE));
    }
}
