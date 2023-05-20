package fun.lzwi.epubime;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.zip.ZipException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import fun.lzwi.Util;

public class EpubFileTest {
    private static EpubFile epubFile;
    @Before
    public void setUp() throws ZipException, IOException, ParserConfigurationException, SAXException {
        epubFile = new EpubFile(Util.getFile("《坟》鲁迅.epub"));
    }

    @Test
    public void testGetIdentifier() throws ParserConfigurationException, SAXException, IOException {
        String identifier = epubFile.getIdentifier();
        assertEquals("能正确读取identifier", "https://www.7sbook.com/ebook/254.html", identifier);
    }

    @Test
    public void testGetLanguage() throws DOMException, ParserConfigurationException, SAXException, IOException {
        String language = epubFile.getLanguage();
        assertEquals("能正确读取language", "zh", language);
    }

    @Test
    public void testGetTitle() throws DOMException, ParserConfigurationException, SAXException, IOException {
        String title = epubFile.getTitle();
        assertEquals("能正确读取title", "坟", title);
    }
}
