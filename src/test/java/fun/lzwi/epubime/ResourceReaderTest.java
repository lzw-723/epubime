package fun.lzwi.epubime;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import fun.lzwi.Utils;

public class ResourceReaderTest {
    @Test
    public void testRead() throws UnsupportedEncodingException, IOException {
        Resource resource = new Resource(new EpubFile(Utils.getFile("《坟》鲁迅.epub")));
        resource.setHref("mimetype");
        ResourceReader reader = new ResourceReader();
        String txt = reader.read(resource);
        assertEquals("application/epub+zip", txt);
    }
}
