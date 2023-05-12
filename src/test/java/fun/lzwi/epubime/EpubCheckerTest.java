package fun.lzwi.epubime;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import fun.lzwi.Util;

public class EpubCheckerTest {

    @Test
    public void testCheck() throws IOException {
        File file = Util.getFile("《坟》鲁迅.epub");
        assertTrue(EpubChecker.check(file));
    }

}
