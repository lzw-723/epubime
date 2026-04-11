package fun.lzwi.epubime.zip;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

public class ZipFileManagerTest {
    
    @Test
    public void testGetInstance() {
        ZipFileManager manager1 = ZipFileManager.getInstance();
        ZipFileManager manager2 = ZipFileManager.getInstance();
        assertSame(manager1, manager2);
    }
    
    @Test
    public void testGetZipFile() throws IOException {
        // 获取EPUB文件
        File epubFile = new File("src/test/resources/fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        if (!epubFile.exists()) {
            // 如果在src/test/resources下找不到，尝试在target/test-classes下查找
            epubFile = new File("target/test-classes/fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        }
        assertTrue(epubFile.exists(), "EPUB file should exist");
        
        ZipFileManager manager = ZipFileManager.getInstance();
        ZipFile zipFile1 = manager.getZipFile(epubFile);
        ZipFile zipFile2 = manager.getZipFile(epubFile);
        
        // 验证ZIP文件句柄重用
        assertSame(zipFile1, zipFile2, "ZIP file should be reused");
        
        // 清理资源
        manager.closeCurrentZipFile();
    }
    
    @Test
    public void testReleaseZipFile() throws IOException {
        // 获取EPUB文件
        File epubFile = new File("src/test/resources/fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        if (!epubFile.exists()) {
            // 如果在src/test/resources下找不到，尝试在target/test-classes下查找
            epubFile = new File("target/test-classes/fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        }
        assertTrue(epubFile.exists(), "EPUB file should exist");

        ZipFileManager manager = ZipFileManager.getInstance();
        manager.cleanup(); // 清理之前的状态
        
        ZipFile zipFile1 = manager.getZipFile(epubFile);
        ZipFile zipFile2 = manager.getZipFile(epubFile);

        // 验证ZIP文件句柄重用
        assertSame(zipFile1, zipFile2, "ZIP file should be reused");

        // 释放ZIP文件句柄
        manager.releaseZipFile(epubFile);
        manager.releaseZipFile(epubFile);

        // 新的LRU池设计：句柄会被缓存，所以可能重用
        // 我们只验证不会抛出异常
        ZipFile zipFile3 = manager.getZipFile(epubFile);
        assertNotNull(zipFile3, "Should get a valid ZIP file");

        // 清理资源
        manager.cleanup();
    }
}