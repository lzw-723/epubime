package fun.lzwi.epubime.bean;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class ManifestItemTest {
    @Test
    public void testToString() {
        assertFalse(new ManifestItem().toString().isEmpty());
    }
}
