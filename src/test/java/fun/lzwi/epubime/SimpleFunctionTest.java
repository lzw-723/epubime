package fun.lzwi.epubime;

import fun.lzwi.epubime.api.EpubReader;
import fun.lzwi.epubime.api.EpubReaderConfig;
import fun.lzwi.epubime.epub.EpubBook;
import fun.lzwi.epubime.epub.Metadata;
import fun.lzwi.epubime.exception.BaseEpubException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

/**
 * Simple function test to verify core functionality works
 */
public class SimpleFunctionTest {
    
    private File testFile;
    
    @BeforeEach
    public void setUp() {
        // Use actual test file from project
        testFile = new File("src/test/resources/fun/lzwi/epubime/epub/《坟》鲁迅.epub");
    }
    
    @Test
    public void testBasicEpubParsing() throws Exception {
        if (!testFile.exists()) {
            fail("Test file not found: " + testFile.getAbsolutePath());
        }

        // Test basic parsing
        EpubReaderConfig config = new EpubReaderConfig().withCache(false);
        EpubBook book = EpubReader.fromFile(testFile, config).parse();

        assertNotNull(book, "Book should not be null");

        // Test metadata
        Metadata metadata = book.getMetadata();
        assertNotNull(metadata, "Metadata should not be null");

        // Test chapters
        int chapterCount = book.getChapters().size();
        assertTrue(chapterCount > 0, "Should have chapters");

        // Test resources
        int resourceCount = book.getResources().size();
        assertTrue(resourceCount > 0, "Should have resources");

        // Test book info
        EpubReader.EpubInfo info = EpubReader.fromFile(testFile).getInfo();
        assertNotNull(info, "Book info should not be null");

        // Test validation
        boolean isValid = EpubReader.fromFile(testFile).isValid();
        assertTrue(isValid, "EPUB should be valid");
    }
    
    @Test
    public void testExceptionSystem() {
        try {
            // Test file exception
            throw new fun.lzwi.epubime.exception.EpubFileException("File not found", new File("test.epub"));
        } catch (fun.lzwi.epubime.exception.EpubFileException e) {
            assertTrue(e.getMessage().contains("test.epub"), "Message should contain file info");
        }

        try {
            // Test ZIP exception
            throw new fun.lzwi.epubime.exception.EpubZipException("ZIP error", new File("test.epub"), "content.html", null);
        } catch (fun.lzwi.epubime.exception.EpubZipException e) {
            assertTrue(e.getMessage().contains("ZIP error"), "Message should contain ZIP info");
        }

        try {
            // Test format exception
            throw new fun.lzwi.epubime.exception.EpubFormatException("Format error", new File("test.epub"), "Missing mimetype");
        } catch (fun.lzwi.epubime.exception.EpubFormatException e) {
            assertTrue(e.getMessage().contains("Format error"), "Message should contain format info");
        }

        try {
            // Test path validation exception
            throw new fun.lzwi.epubime.exception.EpubPathValidationException("Path validation failed", "../../../etc/passwd");
        } catch (fun.lzwi.epubime.exception.EpubPathValidationException e) {
            assertTrue(e.getMessage().contains("Path validation failed"), "Message should contain path info");
        }

        try {
            // Test XML parsing exception
            throw new fun.lzwi.epubime.exception.EpubXmlParseException("XML parsing failed", "content.opf", 10, 25, null);
        } catch (fun.lzwi.epubime.exception.EpubXmlParseException e) {
            assertTrue(e.getMessage().contains("XML parsing failed"), "Message should contain XML info");
        }
    }
}