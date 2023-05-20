package fun.lzwi.epubime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import fun.lzwi.Util;

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
    public static void beforeClass() throws ZipException, IOException, ParserConfigurationException, SAXException {
        file = Util.getFile("《坟》鲁迅.epub");
        epub = new EpubFile(file);
    }

    @Test
    public void testGetEntry() {
        assertNotNull(epub.getEntry("mimetype"));;
    }

    @Test
    public void testGetInputStream() throws IOException {
        assertEquals("application/epub+zip", new String(epub.getInputStream("mimetype").readAllBytes()));
    }
}
