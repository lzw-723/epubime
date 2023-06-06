package fun.lzwi.epubime.util;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class ListUtilsTest {
    @Test
    public void testCopy() {
        List<String> list = List.of(new String[] { "a", "b" });
        List<String> copy = ListUtils.copy(list);
        assertTrue(list.containsAll(copy));
    }
}
