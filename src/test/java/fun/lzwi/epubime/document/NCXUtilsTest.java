package fun.lzwi.epubime.document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fun.lzwi.Utils;

public class NCXUtilsTest {
    private Node ncxElement;

    @Before
    public void setUp() throws ParserConfigurationException, SAXException, IOException {
        FileInputStream in = new FileInputStream(Utils.getFile("book.ncx"));
        ncxElement = NCXUtils.getNcxElement(in);
    }

    @Test
    public void testGetNcxElement() {
        assertNotNull(ncxElement);
    }

    @Test
    public void testGetDocAuthor() {
        assertEquals("鲁迅", NCXUtils.getDocAuthor(ncxElement));
    }

    @Test
    public void testGetDocTitle() {
        assertEquals("坟", NCXUtils.getDocTitle(ncxElement));
    }

    @Test
    public void testGetHead() {
        assertEquals(4, NCXUtils.getHead(ncxElement).size());
    }

    @Test
    public void testGetNavMap() {
        assertEquals(28, NCXUtils.getNavMap(ncxElement).size());
    }
}
