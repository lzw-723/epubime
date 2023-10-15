package fun.lzwi.epubime.bean;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class SpineItemRefTest {
    @Test
    public void testToString() {
        assertFalse(new SpineItemRef().toString().isEmpty());
    }
}
