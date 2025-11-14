package fun.lzwi.epubime.zip;

import org.junit.Test;
import static org.junit.Assert.*;

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
        assertTrue("EPUB file should exist", epubFile.exists());
        
        ZipFileManager manager = ZipFileManager.getInstance();
        ZipFile zipFile1 = manager.getZipFile(epubFile);
        ZipFile zipFile2 = manager.getZipFile(epubFile);
        
        // 验证ZIP文件句柄重用
        assertSame("ZIP file should be reused", zipFile1, zipFile2);
        
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
        assertTrue("EPUB file should exist", epubFile.exists());
        
        ZipFileManager manager = ZipFileManager.getInstance();
        ZipFile zipFile1 = manager.getZipFile(epubFile);
        ZipFile zipFile2 = manager.getZipFile(epubFile);
        
        // 验证ZIP文件句柄重用
        assertSame("ZIP file should be reused", zipFile1, zipFile2);
        
        // 释放ZIP文件句柄
        manager.releaseZipFile();
        manager.releaseZipFile();
        
        // 由于引用计数已归零，下次获取应该创建新的ZIP文件句柄
        ZipFile zipFile3 = manager.getZipFile(epubFile);
        assertNotSame("New ZIP file should be created", zipFile1, zipFile3);
        
        // 清理资源
        manager.closeCurrentZipFile();
    }
}