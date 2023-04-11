package fun.lzwi.epubime;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

import org.junit.Test;

public class EpubCheckerTest {

    @Test
    public void testCheck() throws IOException {
        String path = new String(EpubCheckerTest.class.getClassLoader().getResource("《坟》鲁迅.epub").getPath());
        File file = new File(URLDecoder.decode(path, "utf-8"));
        assertTrue(EpubChecker.check(file));
    }

}
