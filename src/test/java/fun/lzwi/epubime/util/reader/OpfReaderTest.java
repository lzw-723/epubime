package fun.lzwi.epubime.util.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import fun.lzwi.Utils;

public class OpfReaderTest {
    private static OpfReader opfReader;

    @BeforeClass
    public static void beforeClass() throws ParserConfigurationException, SAXException, IOException {
        opfReader = new OpfReader(Utils.getFile("content.opf"));
    }

    @Test
    public void testConstructors() throws ParserConfigurationException, SAXException, IOException {
        assertNotNull(new OpfReader(Utils.getFile("content.opf")));
        assertNotNull(new OpfReader(Utils.getFile("content.opf").getPath()));
        assertNotNull(new OpfReader(new FileInputStream(Utils.getFile("content.opf").getPath())));
    }

    @Test
    public void testGetUniqueIdentifier() {
        assertEquals("正确读取unique-identifier", "BookId", opfReader.getUniqueIdentifier());
    }

    @Test
    public void testGetVersion() {
        assertEquals("正确读取version", "3.0", opfReader.getVersion());
    }
}
