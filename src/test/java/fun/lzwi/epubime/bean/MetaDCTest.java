package fun.lzwi.epubime.bean;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MetaDCTest {
    @Test
    public void testToString() {
        assertTrue(new MetaDC().toString().length() > 0);
    }
}
