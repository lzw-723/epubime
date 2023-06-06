package fun.lzwi.epubime.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LoggerUtilsTest {
    @Test
    public void testFrom() {
        LoggerUtils.from(getClass()).info("此信息只显示一次");
        assertTrue(true);
    }

    @Test
    public void testSetEnable() {
        LoggerUtils.setEnable(false);
        LoggerUtils.from(getClass()).info("此信息只显示一次");
        assertTrue(true);
    }
}
