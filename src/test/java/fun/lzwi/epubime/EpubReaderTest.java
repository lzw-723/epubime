package fun.lzwi.epubime;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.zip.ZipException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import fun.lzwi.Utils;

public class EpubReaderTest {
    @Test
    public void testRead() throws ZipException, IOException, ParserConfigurationException, SAXException {
        EpubReader reader = new EpubReader(new EpubFile(Utils.getFile("《坟》鲁迅.epub")));
        Epub epub = reader.read();
        assertNotNull(epub);
    }
}
