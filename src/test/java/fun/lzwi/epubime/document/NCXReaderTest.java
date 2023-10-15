package fun.lzwi.epubime.document;

import fun.lzwi.Utils;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertNotNull;

public class NCXReaderTest {
    @Test
    public void testRead() throws ParserConfigurationException, SAXException, IOException {
        NCXReader ncxReader = new NCXReader(Files.newInputStream(Utils.getFile("book.ncx").toPath()));
        NCX ncx = ncxReader.read();
        assertNotNull(ncx);
    }
}
