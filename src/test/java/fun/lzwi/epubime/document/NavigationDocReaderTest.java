package fun.lzwi.epubime.document;

import fun.lzwi.Utils;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class NavigationDocReaderTest {
    private NavigationDocument navigationDocument;

    @Before
    public void setUp() throws SAXException, IOException, ParserConfigurationException {
        NavigationDocReader navigationDocReader = new NavigationDocReader(
                Files.newInputStream(Utils.getFile("nav.xhtml").toPath()), "test/entry");
        navigationDocument = navigationDocReader.read();
    }

    @Test
    public void testNavs() {
        assertEquals(2, navigationDocument.getNavs().size());
    }
    @Test
    public void testHref() {
        assertEquals("test/entry", navigationDocument.getHref());
    }
    @Test
    public void testTitle() {
        assertEquals("ePub NAV", navigationDocument.getTitle());
    }
}
