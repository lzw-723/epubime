package fun.lzwi.epubime.zip;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;

public class ZipFileManagerTest {
    
    @Test
    public void testGetInstance() {
        ZipFileManager manager1 = ZipFileManager.getInstance();
        ZipFileManager manager2 = ZipFileManager.getInstance();
        assertSame(manager1, manager2);
    }
}