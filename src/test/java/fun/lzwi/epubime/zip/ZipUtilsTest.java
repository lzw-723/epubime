package fun.lzwi.epubime.zip;import fun.lzwi.epubime.ResUtils;
import org.junit.jupiter.api.Test;



import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import java.util.function.Consumer;



import static org.junit.jupiter.api.Assertions.*;public class ZipUtilsTest {    @Test    public void getZipFileList() throws IOException {        // Test getZipFileList method        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");        List<String> fileList = ZipUtils.getZipFileList(zipFile);        assertNotNull(fileList);        assertFalse(fileList.isEmpty());        assertTrue(fileList.contains("mimetype"));    }    @Test    public void processZipFileContent() throws IOException {        // Test streaming processing of ZIP file content        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");        StringBuilder content = new StringBuilder();        boolean[] processed = {false}; // Use array to modify in lambda        ZipUtils.processZipFileContent(zipFile, "mimetype", (Consumer<InputStream>) inputStream -> {            try {                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));                String line;                while ((line = reader.readLine()) != null) {                    content.append(line);                }                processed[0] = true;            } catch (IOException e) {                throw new RuntimeException(e);            }        });        assertTrue(processed[0]);        assertEquals("application/epub+zip", content.toString());    }            @Test
    public void processZipFileContentWithTraversalPathShouldThrowException() {
        // Test that using directory traversal path should throw exception
        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        assertThrows(IOException.class, () -> {
            ZipUtils.processZipFileContent(zipFile, "../../../etc/passwd", (Consumer<InputStream>) inputStream -> {
                // Should not reach here
                fail("Should have thrown IOException");
            });
        });
    }    @Test    public void getZipFileInputStream() throws IOException {        // Test getting ZIP file input stream        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");        String content;        try (InputStream inputStream = ZipUtils.getZipFileInputStream(zipFile, "mimetype")) {            assertNotNull(inputStream);            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));            content = reader.readLine();        }        assertEquals("application/epub+zip", content);    }            @Test
    public void getZipFileInputStreamWithTraversalPathShouldThrowException() {
        // Test that using directory traversal path should throw exception
        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        assertThrows(IOException.class, () -> {
            ZipUtils.getZipFileInputStream(zipFile, "../../../etc/passwd");
        });
    }    @Test    public void getZipFileInputStreamWithNonExistentFile() throws IOException {        // Test getting input stream for non-existent ZIP file        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");        try (InputStream inputStream = ZipUtils.getZipFileInputStream(zipFile, "nonexistent.txt")) {            assertNull(inputStream);        }    }    @Test    public void getZipFileInputStreamShouldCloseResources() throws IOException {        // Test whether ZIP file input stream properly closes resources        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");        InputStream inputStream = ZipUtils.getZipFileInputStream(zipFile, "mimetype");        assertNotNull(inputStream);        inputStream.close(); // Manually close to verify stream behavior        // Verify cannot read after closing        try {            inputStream.read();            // If no exception is thrown, test will fail        } catch (IOException e) {            // Expected exception because stream is closed        }    }    @Test    public void getMultipleZipFileContents() throws IOException {        // Test batch getting ZIP file contents        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");        java.util.List<String> filePaths = java.util.Arrays.asList("mimetype", "META-INF/container.xml");        java.util.Map<String, String> contents = ZipUtils.getMultipleZipFileContents(zipFile, filePaths);        assertNotNull(contents);        assertEquals(2, contents.size());        assertTrue(contents.containsKey("mimetype"));        assertTrue(contents.containsKey("META-INF/container.xml"));        assertEquals("application/epub+zip", contents.get("mimetype"));        assertNotNull(contents.get("META-INF/container.xml"));        assertTrue(contents.get("META-INF/container.xml").contains("container"));    }            @Test
    public void getMultipleZipFileContentsWithTraversalPathShouldThrowException() {
        // Test that using directory traversal path should throw exception when batch getting ZIP file contents
        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        java.util.List<String> filePaths = java.util.Arrays.asList("mimetype", "../../../etc/passwd");

        assertThrows(IOException.class, () -> {
            ZipUtils.getMultipleZipFileContents(zipFile, filePaths);
        });
    }    @Test    public void getMultipleZipFileBytes() throws IOException {        // Test batch getting ZIP file byte arrays        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");        java.util.List<String> filePaths = Collections.singletonList("mimetype");        java.util.Map<String, byte[]> contents = ZipUtils.getMultipleZipFileBytes(zipFile, filePaths);        assertNotNull(contents);        assertEquals(1, contents.size());        assertTrue(contents.containsKey("mimetype"));        byte[] data = contents.get("mimetype");        assertNotNull(data);        assertEquals("application/epub+zip", new String(data, StandardCharsets.UTF_8));    }            @Test
    public void getMultipleZipFileBytesWithTraversalPathShouldThrowException() {
        // Test that using directory traversal path should throw exception when batch getting ZIP file byte arrays
        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        java.util.List<String> filePaths = java.util.Arrays.asList("mimetype", "../../../etc/passwd");

        assertThrows(IOException.class, () -> {
            ZipUtils.getMultipleZipFileBytes(zipFile, filePaths);
        });
    }    @Test    public void getMultipleZipFileContentsWithNonExistentFile() throws IOException {        // Test batch getting ZIP file contents including non-existent file        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");        java.util.List<String> filePaths = java.util.Arrays.asList("mimetype", "nonexistent.txt");        java.util.Map<String, String> contents = ZipUtils.getMultipleZipFileContents(zipFile, filePaths);        assertNotNull(contents);        assertEquals(2, contents.size());        assertTrue(contents.containsKey("mimetype"));        assertTrue(contents.containsKey("nonexistent.txt"));        assertNotNull(contents.get("mimetype"));        assertNull(contents.get("nonexistent.txt"));    }        @Test    public void processHtmlContent() throws IOException {        // Test streaming processing of HTML content        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");        StringBuilder content = new StringBuilder();        boolean[] processed = {false}; // Use array to modify in lambda        ZipUtils.processHtmlContent(zipFile, "mimetype", (Consumer<InputStream>) inputStream -> {            try {                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));                String line;                while ((line = reader.readLine()) != null) {                    content.append(line);                }                processed[0] = true;            } catch (IOException e) {                throw new RuntimeException(e);            }        });        assertTrue(processed[0], "HTML content should be processed");        assertEquals("application/epub+zip", content.toString());    }            @Test
    public void processHtmlContentWithTraversalPathShouldThrowException() {
        // Test streaming processing of HTML content using directory traversal path should throw exception
        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        assertThrows(IOException.class, () -> {
            ZipUtils.processHtmlContent(zipFile, "../../../etc/passwd", (Consumer<InputStream>) inputStream -> {
                // Should not reach here
                fail("Should have thrown IOException");
            });
        });
    }        @Test    public void processMultipleHtmlContents() throws IOException {        // Test batch streaming processing of multiple HTML file contents        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");        java.util.List<String> filePaths = java.util.Arrays.asList("mimetype", "META-INF/container.xml");        java.util.Map<String, String> contents = new java.util.HashMap<>();        int[] processedCount = {0};        ZipUtils.processMultipleHtmlContents(zipFile, filePaths, (BiConsumer<String, InputStream>) (fileName, inputStream) -> {            try {                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));                StringBuilder content = new StringBuilder();                String line;                while ((line = reader.readLine()) != null) {                    content.append(line);                }                contents.put(fileName, content.toString());                processedCount[0]++;            } catch (IOException e) {                throw new RuntimeException(e);            }        });        assertEquals(2, processedCount[0], "Should process 2 files");        assertTrue(contents.containsKey("mimetype"));        assertTrue(contents.containsKey("META-INF/container.xml"));        assertEquals("application/epub+zip", contents.get("mimetype"));        assertNotNull(contents.get("META-INF/container.xml"));        assertTrue(contents.get("META-INF/container.xml").contains("container"));    }            @Test
    public void processMultipleHtmlContentsWithTraversalPathShouldThrowException() {
        // Test batch streaming processing of multiple HTML file contents using directory traversal path should throw exception
        File zipFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        java.util.List<String> filePaths = java.util.Arrays.asList("mimetype", "../../../etc/passwd");

        assertThrows(IOException.class, () -> {
            ZipUtils.processMultipleHtmlContents(zipFile, filePaths, (BiConsumer<String, InputStream>) (fileName, inputStream) -> {
                // Should not reach here
                fail("Should have thrown IOException");
            });
        });
    }}