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
}