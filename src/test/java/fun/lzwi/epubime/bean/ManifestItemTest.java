package fun.lzwi.epubime.bean;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ManifestItemTest {
    @Test
    public void testToString() {
        assertTrue(new ManifestItem().toString().length() > 0);
    }
}
