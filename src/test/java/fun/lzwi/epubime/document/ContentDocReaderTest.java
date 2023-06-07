package fun.lzwi.epubime.document;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import fun.lzwi.Utils;

public class ContentDocReaderTest {
    @Test
    public void testRead() throws SAXException, IOException, ParserConfigurationException {
        ContentDocReader contentDocReader = new ContentDocReader(new FileInputStream(Utils.getFile("section1.xhtml")));
        ContentDocument contentDocument = contentDocReader.read();
        assertTrue(contentDocument.getTitle().equals("第一章"));
    }
}
