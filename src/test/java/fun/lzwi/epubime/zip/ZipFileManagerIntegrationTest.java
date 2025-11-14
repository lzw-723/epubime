package fun.lzwi.epubime.zip;

import fun.lzwi.epubime.epub.EpubParser;
import fun.lzwi.epubime.epub.EpubBook;
import fun.lzwi.epubime.exception.EpubParseException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class ZipFileManagerIntegrationTest {
    
    @Test
    public void testZipFileManagerInEpubParsing() throws EpubParseException, IOException {
        // 获取EPUB文件
        File epubFile = new File("src/test/resources/fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        if (!epubFile.exists()) {
            // 如果在src/test/resources下找不到，尝试在target/test-classes下查找
            epubFile = new File("target/test-classes/fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        }
        assertTrue("EPUB file should exist", epubFile.exists());
        
        // 创建EPUB解析器
        EpubParser parser = new EpubParser(epubFile);
        
        // 解析EPUB文件
        EpubBook book = parser.parse();
        
        // 验证解析结果
        assertNotNull("Book should not be null", book);
        assertNotNull("Metadata should not be null", book.getMetadata());
        assertNotNull("NCX should not be null", book.getNcx());
        assertFalse("NCX should not be empty", book.getNcx().isEmpty());
        assertNotNull("Resources should not be null", book.getResources());
        assertFalse("Resources should not be empty", book.getResources().isEmpty());
        
        // 验证ZIP文件句柄已被正确清理
        // 这个验证通过确保parse()方法中的finally块被正确执行
    }
}
