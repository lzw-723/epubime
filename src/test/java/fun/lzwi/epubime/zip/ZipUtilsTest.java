package fun.lzwi.epubime.zip;

import fun.lzwi.epubime.ResUtils;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.function.Consumer;

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

    @Test
    public void processZipFileContent() throws IOException {
        // 测试流式处理ZIP文件内容的功能
        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        StringBuilder content = new StringBuilder();
        boolean[] processed = {false}; // 使用数组以便在lambda中修改

        ZipUtils.processZipFileContent(zipFile, "mimetype", new Consumer<InputStream>() {
            @Override
            public void accept(InputStream inputStream) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    processed[0] = true;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        assertTrue(processed[0]);
        assertEquals("application/epub+zip", content.toString());
    }

    @Test
    public void getZipFileInputStream() throws IOException {
        // 测试获取ZIP文件输入流的功能
        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        String content = "";

        try (InputStream inputStream = ZipUtils.getZipFileInputStream(zipFile, "mimetype")) {
            assertNotNull(inputStream);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            content = reader.readLine();
        }

        assertEquals("application/epub+zip", content);
    }

    @Test
    public void getZipFileInputStreamWithNonExistentFile() throws IOException {
        // 测试获取不存在的ZIP文件输入流的功能
        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");

        try (InputStream inputStream = ZipUtils.getZipFileInputStream(zipFile, "nonexistent.txt")) {
            assertNull(inputStream);
        }
    }

    @Test
    public void getZipFileInputStreamShouldCloseResources() throws IOException {
        // 测试ZIP文件输入流是否正确关闭资源
        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        InputStream inputStream = ZipUtils.getZipFileInputStream(zipFile, "mimetype");
        assertNotNull(inputStream);
        inputStream.close(); // 手动关闭以验证流的行为
        // 验证关闭后无法读取
        try {
            inputStream.read();
            // 如果没有抛出异常，测试会失败
        } catch (IOException e) {
            // 期望的异常，因为流已被关闭
        }
    }

    @Test
    public void getMultipleZipFileContents() throws IOException {
        // 测试批量获取ZIP文件内容的功能
        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        java.util.List<String> filePaths = java.util.Arrays.asList("mimetype", "META-INF/container.xml");

        java.util.Map<String, String> contents = ZipUtils.getMultipleZipFileContents(zipFile, filePaths);

        assertNotNull(contents);
        assertEquals(2, contents.size());
        assertTrue(contents.containsKey("mimetype"));
        assertTrue(contents.containsKey("META-INF/container.xml"));
        assertEquals("application/epub+zip", contents.get("mimetype"));
        assertNotNull(contents.get("META-INF/container.xml"));
        assertTrue(contents.get("META-INF/container.xml").contains("container"));
    }

    @Test
    public void getMultipleZipFileBytes() throws IOException {
        // 测试批量获取ZIP文件字节数组的功能
        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        java.util.List<String> filePaths = java.util.Arrays.asList("mimetype");

        java.util.Map<String, byte[]> contents = ZipUtils.getMultipleZipFileBytes(zipFile, filePaths);

        assertNotNull(contents);
        assertEquals(1, contents.size());
        assertTrue(contents.containsKey("mimetype"));
        byte[] data = contents.get("mimetype");
        assertNotNull(data);
        assertEquals("application/epub+zip", new String(data, "UTF-8"));
    }

    @Test
    public void getMultipleZipFileContentsWithNonExistentFile() throws IOException {
        // 测试批量获取ZIP文件内容时包含不存在文件的功能
        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        java.util.List<String> filePaths = java.util.Arrays.asList("mimetype", "nonexistent.txt");

        java.util.Map<String, String> contents = ZipUtils.getMultipleZipFileContents(zipFile, filePaths);

        assertNotNull(contents);
        assertEquals(2, contents.size());
        assertTrue(contents.containsKey("mimetype"));
        assertTrue(contents.containsKey("nonexistent.txt"));
        assertNotNull(contents.get("mimetype"));
        assertNull(contents.get("nonexistent.txt"));
    }
}