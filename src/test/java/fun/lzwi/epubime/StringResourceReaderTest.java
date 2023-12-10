package fun.lzwi.epubime;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.junit.Before;
import org.junit.Test;

import fun.lzwi.Utils;

public class StringResourceReaderTest {
    private Resource resource;

    @Before
    public void setUp() throws UnsupportedEncodingException, IOException {
        resource = new Resource(new EpubFile(Utils.getFile("《坟》鲁迅.epub")));
    }

    @Test
    public void testRead() throws UnsupportedEncodingException, IOException {
        resource.setHref("mimetype");
        StringResourceReader reader = new StringResourceReader();
        String txt = reader.read(resource);
        assertEquals("application/epub+zip", txt);
    }

    @Test
    public void testRead2() throws UnsupportedEncodingException, IOException {
        resource.setBase("OEBPS");
        resource.setHref("../mimetype");
        StringResourceReader reader = new StringResourceReader();
        String txt = reader.read(resource);
        assertEquals("application/epub+zip", txt);
    }

    @Test
    public void testRead3() throws UnsupportedEncodingException, IOException {
        resource.setBase("OEBPS");
        resource.setHref("TOC.xhtml#test");
        StringResourceReader reader = new StringResourceReader();
        String txt = reader.read(new Resource(resource, "./styles.css"));
        assertEquals(434, txt.length());
    }
}
