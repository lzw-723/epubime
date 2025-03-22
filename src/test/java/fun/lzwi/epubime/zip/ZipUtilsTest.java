package fun.lzwi.epubime.zip;

import fun.lzwi.epubime.ResUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class ZipUtilsTest {

    @Test
    public void getZipFileList() throws IOException {
        // 测试getZipFileList方法
        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        List<String> fileList = ZipUtils.getZipFileList(zipFile);
        assertNotNull(fileList);
        assertFalse(fileList.isEmpty());
        assertTrue(fileList.contains("mimetype"));
    }
}