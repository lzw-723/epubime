package fun.lzwi.epubime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import fun.lzwi.Utils;

public class BytesResourceReaderTest {
    private Resource resource;

    @Before
    public void setUp() throws UnsupportedEncodingException, IOException {
        resource = new Resource(new EpubFile(Utils.getFile("《坟》鲁迅.epub")));
    }

    @Test
    public void testReadTXT() throws UnsupportedEncodingException, IOException {
        resource.setHref("mimetype");
        BytesResourceReader reader = new BytesResourceReader();
        String txt = new String(reader.read(resource));
        assertEquals("application/epub+zip", txt);
    }

    @Test
    public void testReadIMG() throws UnsupportedEncodingException, IOException {
        resource.setHref("OEBPS/images/Cover.jpg");
        BytesResourceReader reader = new BytesResourceReader();
        byte[] img = reader.read(resource);

        InputStream is = Files.newInputStream(Utils.getFile("Cover.jpg").toPath());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int read;
        while ((read = is.read(buf)) != -1) {
            bos.write(buf, 0, read);
        }
        byte[] bytes = bos.toByteArray();
        bos.close();
        is.close();

        assertEquals(new String(bytes), new String(img));
    }

    @Test
    public void testReadImgZH() throws UnsupportedEncodingException, IOException {
        resource = new Resource(new EpubFile(Utils.getFile("test3.epub")));
        resource.setHref("OEBPS/Images/两句诗.jpg");
        BytesResourceReader reader = new BytesResourceReader();
        byte[] img = reader.read(resource);

        assertTrue(img.length > 0);
    }
    @Test
    public void testReadImgZhURL() throws UnsupportedEncodingException, IOException {
        resource = new Resource(new EpubFile(Utils.getFile("test3.epub")));
        resource.setHref("OEBPS/Images/%E4%B8%A4%E5%8F%A5%E8%AF%97.jpg");
        BytesResourceReader reader = new BytesResourceReader();
        byte[] img = reader.read(resource);

        assertTrue(img.length > 0);
    }
}
