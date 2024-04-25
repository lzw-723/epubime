package fun.lzwi.epubime.easy;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import fun.lzwi.Utils;
import fun.lzwi.epubime.BytesResourceReader;

public class EasyEpubTest {
    private EasyEpub book;

    @Before
    public void setUp() throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
        book = new EasyEpub(Utils.getFile("《坟》鲁迅.epub"));
    }

    @Test
    public void testGetTitle()
            throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
        assertEquals("坟", book.getTitle());
    }

    @Test
    public void testGetAuthor() {
        assertEquals("鲁迅", book.getAuthor());
    }

    @Test
    public void testGetCover() {
        assertEquals("OEBPS/images/Cover.jpg", book.getCover());
    }

    @Test
    public void testGetModified() {
        assertEquals("2022-12-06T13:14:44Z", book.getModified());
    }

    @Test
    public void testGetIdentifier() {
        assertEquals("https://www.7sbook.com/ebook/254.html", book.getIdentifier());
    }

    @Test
    public void testGetLanguage() {
        assertEquals("zh", book.getLanguage());
    }

    @Test
    public void testGetResource() throws IOException {
        assertEquals(16007, new BytesResourceReader().read(book.getResource("OEBPS/images/Cover.jpg")).length);
    }

}
