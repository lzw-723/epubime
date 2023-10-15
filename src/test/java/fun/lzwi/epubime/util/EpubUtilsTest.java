package fun.lzwi.epubime.util;

import fun.lzwi.Utils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EpubUtilsTest {
    private static File file;

    @BeforeClass
    public static void beforeClass() throws UnsupportedEncodingException {
        file = Utils.getFile("《坟》鲁迅.epub");
    }

    @Test
    public void testExistContainerXML() throws IOException {
        assertTrue("存在container.xml", EpubUtils.existContainerXML(file));
    }

    @Test
    public void testExistEntry() throws IOException {
        assertFalse(EpubUtils.existEntry(file, "test"));
    }

    @Test
    public void testExistMimeType() throws IOException {
        assertTrue("存在mimetype文件", EpubUtils.existMimeType(file));
    }

}
