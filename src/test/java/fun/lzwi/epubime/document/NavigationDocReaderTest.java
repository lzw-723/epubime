package fun.lzwi.epubime.document;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import fun.lzwi.Utils;

public class NavigationDocReaderTest {
    @Test
    public void testRead() throws SAXException, IOException, ParserConfigurationException {
        NavigationDocReader navigationDocReader = new NavigationDocReader(new FileInputStream(Utils.getFile("nav.xhtml")));
        NavigationDocument navigationDocument = navigationDocReader.read();
        assertTrue(navigationDocument.getNavs().size() == 2);
    }
}
