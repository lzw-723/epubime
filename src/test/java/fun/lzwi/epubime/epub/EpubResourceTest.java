package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.*;

public class EpubResourceTest {

    @Test
    public void testEpubResourceStreamProcessing() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubBook book = new EpubParser(epubFile).parse();
        
        // 验证资源现在只存储引用而不立即加载数据
        assertFalse(book.getResources().isEmpty());
        
        // 验证第一个资源可以通过流访问
        EpubResource resource = book.getResources().get(0);
        assertNotNull(resource.getHref());
        assertNotNull(resource.getId());
        
        // 验证可以通过流访问资源
        try (InputStream inputStream = resource.getInputStream()) {
            assertNotNull("InputStream should not be null for valid resource", inputStream);
            assertTrue("InputStream should be able to read data", inputStream.read() != -1);
        }
        
        // 验证getData()仍然可以按需加载数据
        byte[] data = resource.getData();
        assertNotNull("Data should be available when requested", data);
        assertTrue("Data array should have content", data.length > 0);
    }

    @Test
    public void testEpubResourceWithNullEpubFile() throws Exception {
        // 测试当epubFile为null时的行为
        EpubResource resource = new EpubResource();
        resource.setHref("test/path");
        resource.setEpubFile(null);
        
        // 当epubFile为null时，getInputStream应该返回null
        try (InputStream inputStream = resource.getInputStream()) {
            assertNull("InputStream should be null when epubFile is null", inputStream);
        }
        
        // 当epubFile为null时，getData也应该返回null
        byte[] data = resource.getData();
        assertNull("Data should be null when epubFile is null", data);
    }

    @Test
    public void testEpubResourceWithNullHref() throws Exception {
        // 测试当href为null时的行为
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubResource resource = new EpubResource();
        resource.setEpubFile(epubFile);
        resource.setHref(null);
        
        // 当href为null时，getInputStream应该返回null
        try (InputStream inputStream = resource.getInputStream()) {
            assertNull("InputStream should be null when href is null", inputStream);
        }
    }
}