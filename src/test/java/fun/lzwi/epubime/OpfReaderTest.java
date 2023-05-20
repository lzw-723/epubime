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

import fun.lzwi.Util;

public class OpfReaderTest {
    // opf文件
    static File FILE;

    @BeforeClass
    public static void beforeClass() {
        FILE = Util.getFile("content.opf");
    }

    @Test
    public void testExistIdentifier() throws FileNotFoundException {
        assertTrue("存在Id", OpfReader.existIdentifier(FILE));
    }

    @Test
    public void testGetIdentifier() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("正确读取Id", "urn:uuid:d249e6e6-810d-43bb-91a4-9b8533daebe0", OpfReader.getIdentifier(FILE));
    }

    @Test
    public void testExistLanguage() throws FileNotFoundException {
        assertTrue("存在language", OpfReader.existLanguage(FILE));
    }

    @Test
    public void testGetLanguage() throws DOMException, ParserConfigurationException, SAXException, IOException {
        assertEquals("正确读取language", "en", OpfReader.getLanguage(FILE));
    }

    @Test
    public void testExistTitle() throws FileNotFoundException {
        assertTrue("存在title", OpfReader.existTitle(FILE));
    }

    @Test
    public void testGetTitle() throws DOMException, ParserConfigurationException, SAXException, IOException {
        assertEquals("正确读取title", "[此处填写主标题]", OpfReader.getTitle(FILE));
    }

    @Test
    public void testGetManifest() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("manifest子节点数目正确", 3, XmlUtils.countChildNodes(OpfReader.getManifest(FILE)));
    }

    @Test
    public void testGetSpine() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("spine子节点数目正确", 2, XmlUtils.countChildNodes(OpfReader.getSpine(FILE)));
    }

    @Test
    public void testGetManifestItems() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("正确获取manifest的item", 3, OpfReader.getManifestItems(FILE).size());
    }

    @Test
    public void testGetSpineItemRefs() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("正确获取spine的itemref", 2, OpfReader.getSpineItemRefs(FILE).size());
    }

    @Test
    public void testGetMetaData()
            throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        assertEquals("metadata子节点数目正确", 5, XmlUtils.countChildNodes(OpfReader.getMetaData(FILE)));
    }

    @Test
    public void testGetMetaDataItems()
            throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        assertEquals("正确获取metadata的meta", 2, OpfReader.getMetaDataItems(FILE).size());
    }
}
