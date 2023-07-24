package fun.lzwi.epubime.document;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import fun.lzwi.Utils;

public class NCXReaderTest {
    @Test
    public void testRead() throws ParserConfigurationException, SAXException, IOException {
        NCXReader ncxReader = new NCXReader(new FileInputStream(Utils.getFile("book.ncx")));
        NCX ncx = ncxReader.read();
        assertNotNull(ncx);
    }
}
