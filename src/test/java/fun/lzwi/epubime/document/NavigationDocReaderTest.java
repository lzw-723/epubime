package fun.lzwi.epubime.document;

import fun.lzwi.Utils;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class NavigationDocReaderTest {
    @Test
    public void testRead() throws SAXException, IOException, ParserConfigurationException {
        NavigationDocReader navigationDocReader = new NavigationDocReader(Files.newInputStream(Utils.getFile("nav.xhtml").toPath()));
        NavigationDocument navigationDocument = navigationDocReader.read();
        assertEquals(2, navigationDocument.getNavs().size());
    }
}
