package fun.lzwi.epubime;

import fun.lzwi.Utils;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class EpubReaderTest {
    @Test
    public void testRead() throws IOException, ParserConfigurationException, SAXException {
        EpubReader reader = new EpubReader(new EpubFile(Utils.getFile("《坟》鲁迅.epub")));
        Epub epub = reader.read();
        assertNotNull(epub);
    }
}
