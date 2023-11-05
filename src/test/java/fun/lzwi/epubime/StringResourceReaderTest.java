package fun.lzwi.epubime;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.junit.Test;

import fun.lzwi.Utils;

public class StringResourceReaderTest {
    @Test
    public void testRead() throws UnsupportedEncodingException, IOException {
        Resource resource = new Resource(new EpubFile(Utils.getFile("《坟》鲁迅.epub")));
        resource.setHref("mimetype");
        StringResourceReader reader = new StringResourceReader();
        String txt = reader.read(resource);
        assertEquals("application/epub+zip", txt);
    }

    @Test
    public void testRead2() throws UnsupportedEncodingException, IOException, URISyntaxException {

        Resource resource = new Resource(new EpubFile(Utils.getFile("《坟》鲁迅.epub")));
        resource.setBase("OEBPS");
        resource.setHref("../mimetype");
        StringResourceReader reader = new StringResourceReader();
        String txt = reader.read(resource);
        assertEquals("application/epub+zip", txt);
    }

    @Test
    public void testRead3() throws UnsupportedEncodingException, IOException, URISyntaxException {

        Resource resource = new Resource(new EpubFile(Utils.getFile("《坟》鲁迅.epub")));
        resource.setBase("OEBPS");
        resource.setHref("TOC.xhtml");
        StringResourceReader reader = new StringResourceReader();
        String txt = reader.read(new Resource(resource, "./styles.css"));
        assertEquals(434, txt.length());
    }
}
