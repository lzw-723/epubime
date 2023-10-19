package fun.lzwi.epubime.bean;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class MetaDCTest {
    @Test
    public void testToString() {
        assertFalse(new MetaDC().toString().isEmpty());
    }
}
