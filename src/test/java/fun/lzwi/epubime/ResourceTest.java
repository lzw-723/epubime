package fun.lzwi.epubime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import fun.lzwi.Utils;

public class ResourceTest {
    private Resource resource;

    @Before
    public void setUp() throws UnsupportedEncodingException, IOException {
        resource = new Resource(new EpubFile(Utils.getFile("《坟》鲁迅.epub")));
        resource.setBase("/OEBPS");
        resource.setHref("./TOC.xhtml#toc-1");
    }

    @Test
    public void testGetInputStream() throws IOException {
        assertNotNull(resource.getInputStream());
    }

    @Test
    public void testGetParent() {
        assertEquals("OEBPS", resource.getParent());
    }

    @Test
    public void testGetPath() {
        assertEquals("OEBPS/TOC.xhtml", resource.getPath());
    }

    @Test
    public void testGetHash() {
        assertEquals("toc-1", resource.getHash());
    }
}
