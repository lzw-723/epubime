package fun.lzwi.epubime.bean;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SpineItemRefTest {
    @Test
    public void testToString() {
        assertTrue(new SpineItemRef().toString().length() > 0);
    }
}
