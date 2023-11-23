package fun.lzwi.epubime;

import fun.lzwi.Utils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

public class EpubFileTest {
    private EpubFile epubFile;

    @Before
    public void setUp() throws IOException {
        epubFile = new EpubFile(Utils.getFile("《坟》鲁迅.epub"));
    }

    @Test
    public void testGetInputStream() throws IOException {
        InputStream in = epubFile.getInputStream("META-INF/container.xml");
        assertNotNull(in);
    }



}
