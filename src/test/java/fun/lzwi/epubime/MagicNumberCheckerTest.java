package fun.lzwi.epubime;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import org.junit.Test;

import fun.lzwi.Util;

public class MagicNumberCheckerTest {

    @Test
    public void testCheck0() throws IOException {
        File file = Util.getFile("《坟》鲁迅.epub");
        assertTrue("魔数30正确", MagicNumberChecker.check0(file));
    }

    @Test
    public void testCheck30() throws IOException {
        File file = Util.getFile("《坟》鲁迅.epub");
        assertTrue("魔数30正确", MagicNumberChecker.check30(file));
    }

    @Test
    public void testCheck38() throws IOException {
        File file = Util.getFile("《坟》鲁迅.epub");
        assertTrue("魔数38正确", MagicNumberChecker.check38(file));
    }
}
