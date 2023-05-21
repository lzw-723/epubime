package fun.lzwi;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void testResources() throws IOException {
        String path = AppTest.class.getClassLoader().getResource("文本.txt").getPath();
        assertTrue(new File(URLDecoder.decode(path, "utf-8")).exists());
    }
}
