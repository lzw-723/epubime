package fun.lzwi.epubime;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

import org.junit.Test;

public class MagicNumberCheckerTest {
    // @Test
    // public void testRead() throws IOException {
    // String path =
    // MagicNumberChecker.class.getClassLoader().getResource("《坟》鲁迅.epub").getPath();
    // File file = new File(URLDecoder.decode(path, "utf-8"));
    // MagicNumberChecker.read(file);
    // }

    @Test
    public void testCheck0() throws IOException {
        String path = MagicNumberChecker.class.getClassLoader().getResource("《坟》鲁迅.epub").getPath();
        File file = new File(URLDecoder.decode(path, "utf-8"));
        assertTrue(MagicNumberChecker.check0(file));
    }

    @Test
    public void testCheck30() throws IOException {
        String path = MagicNumberChecker.class.getClassLoader().getResource("《坟》鲁迅.epub").getPath();
        File file = new File(URLDecoder.decode(path, "utf-8"));
        assertTrue(MagicNumberChecker.check30(file));
    }

    @Test
    public void testCheck38() throws IOException {
        String path = MagicNumberChecker.class.getClassLoader().getResource("《坟》鲁迅.epub").getPath();
        File file = new File(URLDecoder.decode(path, "utf-8"));
        assertTrue(MagicNumberChecker.check38(file));
    }
}
