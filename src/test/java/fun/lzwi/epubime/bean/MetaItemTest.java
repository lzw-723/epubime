package fun.lzwi.epubime.bean;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MetaItemTest {
    @Test
    public void testToString() {
        assertTrue(new MetaItem().toString().length() > 0);
    }
}
