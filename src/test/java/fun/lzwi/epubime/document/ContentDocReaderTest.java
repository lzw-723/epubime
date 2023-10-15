package fun.lzwi.epubime.document;

import fun.lzwi.Utils;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class ContentDocReaderTest {
    @Test
    public void testRead() throws SAXException, IOException, ParserConfigurationException {
        ContentDocReader contentDocReader = new ContentDocReader(Files.newInputStream(Utils.getFile("section1.xhtml").toPath()));
        ContentDocument contentDocument = contentDocReader.read();
        assertEquals("第一章", contentDocument.getTitle());
    }
}
