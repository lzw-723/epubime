package fun.lzwi.epubime.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
        assertTrue(packageInfo.toString().length() > 0);

    }
}
