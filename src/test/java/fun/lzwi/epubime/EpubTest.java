package fun.lzwi.epubime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.zip.ZipException;

import org.junit.BeforeClass;
import org.junit.Test;

public class EpubTest {
    static String path;
    static File file;
    static EpubFile epub;

    @Test
    public void testForEach() throws ZipException, IOException {
        epub.forEach(e -> {
            System.out.println(e.getName());
        });
        assertTrue(true);
    }

    @BeforeClass
    public static void beforeClass() throws ZipException, IOException {
        path = EpubTest.class.getClassLoader().getResource("《坟》鲁迅.epub").getPath();
        file = new File(URLDecoder.decode(path, "utf-8"));
        epub = new EpubFile(file);
    }

    @Test
    public void testGetEntry() {
        assertNotNull(epub.getEntry("mimetype"));;
    }

    @Test
    public void testGetInputStream() throws IOException {
        assertEquals(new String(epub.getInputStream("mimetype").readAllBytes()), "application/epub+zip");
    }
}
