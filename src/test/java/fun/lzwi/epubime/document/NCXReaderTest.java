package fun.lzwi.epubime.document;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import fun.lzwi.Utils;

public class NCXReaderTest {
    private NCX ncx;

    @Before
    public void setUp() throws ParserConfigurationException, SAXException, IOException {
        NCXReader ncxReader = new NCXReader(Files.newInputStream(Utils.getFile("book.ncx").toPath()), "href/test");
        ncx = ncxReader.read();
    }

    @Test
    public void testHref() {
        assertEquals("href/test", ncx.getHref());
    }

    @Test
    public void testHead() {
        assertEquals(4, ncx.getHead().size());
    }

    @Test
    public void testDocTitle() {
        assertEquals("坟", ncx.getDocTitle());
    }

    @Test
    public void testDocAuthor() {
        assertEquals("鲁迅", ncx.getDocAuthor());
    }

    @Test
    public void testNavMap() {
        assertEquals(28, ncx.getNavMap().size());
    }

}
