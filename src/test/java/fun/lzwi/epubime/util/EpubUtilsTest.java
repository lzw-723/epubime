package fun.lzwi.epubime.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import fun.lzwi.Utils;

public class EpubUtilsTest {
    private static File file;

    @BeforeClass
    public static void beforeClass() {
        file = Utils.getFile("《坟》鲁迅.epub");
    }

    @Test
    public void testExistContainerXML() throws ZipException, IOException, ParserConfigurationException, SAXException {
        assertTrue("存在container.xml", EpubUtils.existContainerXML(file));
    }

    @Test
    public void testExistEntry() throws ZipException, IOException, ParserConfigurationException, SAXException {
        assertFalse(EpubUtils.existEntry(file, "test"));
    }

    @Test
    public void testExistMimeType() throws IOException, ParserConfigurationException, SAXException {
        assertTrue("存在mimetype文件", EpubUtils.existMimeType(file));
    }

}
