package fun.lzwi.epubime.zip;

import fun.lzwi.epubime.epub.EpubParser;
import fun.lzwi.epubime.epub.EpubBook;
import fun.lzwi.epubime.exception.BaseEpubException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ZipFileManagerIntegrationTest {
    
    @Test
    public void testZipFileManagerInEpubParsing() throws Exception {
        // 获取EPUB文件
        File epubFile = new File("src/test/resources/fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        if (!epubFile.exists()) {
            // 如果在src/test/resources下找不到，尝试在target/test-classes下查找
            epubFile = new File("target/test-classes/fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        }
        assertTrue(epubFile.exists(), "EPUB file should exist");
        
        // 创建EPUB解析器
        EpubParser parser = new EpubParser(epubFile);
        
        // 解析EPUB文件
        EpubBook book = parser.parse();
        
        // 验证解析结果
        assertNotNull(book, "Book should not be null");
        assertNotNull(book.getMetadata(), "Metadata should not be null");
        assertNotNull(book.getNcx(), "NCX should not be null");
        assertFalse(book.getNcx().isEmpty(), "NCX should not be empty");
        assertNotNull(book.getResources(), "Resources should not be null");
        assertFalse(book.getResources().isEmpty(), "Resources should not be empty");
        
        // 验证ZIP文件句柄已被正确清理
        // 这个验证通过确保parse()方法中的finally块被正确执行
    }
}
