package fun.lzwi.epubime.bean;

import org.junit.Test;

import static org.junit.Assert.*;

public class PackageInfoTest {
    @Test
    public void testClone() {
        PackageInfo packageInfo = new PackageInfo();
        PackageInfo packageInfo2 = packageInfo.clone();
        assertNotEquals(packageInfo, packageInfo2);
        assertEquals(packageInfo.toString(), packageInfo2.toString());
    }

    @Test
    public void testToString() {
        PackageInfo packageInfo = new PackageInfo();
        assertFalse(packageInfo.toString().isEmpty());

    }
}
