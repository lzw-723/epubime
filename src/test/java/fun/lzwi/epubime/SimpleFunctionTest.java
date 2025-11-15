package fun.lzwi.epubime;

import fun.lzwi.epubime.api.EpubReader;
import fun.lzwi.epubime.api.EpubReaderConfig;
import fun.lzwi.epubime.epub.EpubBook;
import fun.lzwi.epubime.epub.Metadata;
import fun.lzwi.epubime.exception.BaseEpubException;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.io.File;

/**
 * Simple function test to verify core functionality works
 */
public class SimpleFunctionTest {
    
    private File testFile;
    
    @Before
    public void setUp() {
        // Use actual test file from project
        testFile = new File("src/test/resources/fun/lzwi/epubime/epub/《坟》鲁迅.epub");
    }
    
    @Test
    public void testBasicEpubParsing() throws BaseEpubException {
        if (!testFile.exists()) {
            System.out.println("Test file not found: " + testFile.getAbsolutePath());
            return;
        }
        
        System.out.println("Testing with file: " + testFile.getName());
        System.out.println("File size: " + testFile.length() + " bytes");
        
        // Test basic parsing
        EpubReaderConfig config = new EpubReaderConfig().withCache(false);
        EpubBook book = EpubReader.fromFile(testFile, config).parse();
        
        assertNotNull("Book should not be null", book);
        System.out.println("[SUCCESS] EPUB file parsing successful");
        
        // Test metadata
        Metadata metadata = book.getMetadata();
        assertNotNull("Metadata should not be null", metadata);
        
        System.out.println("Title: " + (metadata.getTitle() != null ? metadata.getTitle() : "N/A"));
        System.out.println("Author: " + (metadata.getCreator() != null ? metadata.getCreator() : "N/A"));
        System.out.println("Language: " + (metadata.getLanguage() != null ? metadata.getLanguage() : "N/A"));
        
        // Test chapters
        int chapterCount = book.getChapters().size();
        System.out.println("Chapter count: " + chapterCount);
        assertTrue("Should have chapters", chapterCount > 0);
        
        // Test resources
        int resourceCount = book.getResources().size();
        System.out.println("Resource count: " + resourceCount);
        assertTrue("Should have resources", resourceCount > 0);
        
        // Test book info
        EpubReader.EpubInfo info = EpubReader.fromFile(testFile).getInfo();
        assertNotNull("Book info should not be null", info);
        
        System.out.println("Book title: " + info.getTitle());
        System.out.println("Book author: " + info.getAuthor());
        System.out.println("Chapter count: " + info.getChapterCount());
        
        // Test validation
        boolean isValid = EpubReader.fromFile(testFile).isValid();
        System.out.println("EPUB file is valid: " + isValid);
        assertTrue("EPUB should be valid", isValid);
        
        System.out.println("[SUCCESS] All basic functionality tests passed!");
    }
    
    @Test
    public void testExceptionSystem() {
        System.out.println("\nTesting exception system:");
        
        try {
            // Test file exception
            throw new fun.lzwi.epubime.exception.EpubFileException("File not found", new File("test.epub"));
        } catch (fun.lzwi.epubime.exception.EpubFileException e) {
            System.out.println("[OK] EpubFileException working: " + e.getMessage());
            assertTrue("Message should contain file info", e.getMessage().contains("test.epub"));
        }
        
        try {
            // Test ZIP exception
            throw new fun.lzwi.epubime.exception.EpubZipException("ZIP error", new File("test.epub"), "content.html", null);
        } catch (fun.lzwi.epubime.exception.EpubZipException e) {
            System.out.println("[OK] EpubZipException working: " + e.getMessage());
            assertTrue("Message should contain ZIP info", e.getMessage().contains("ZIP error"));
        }
        
        try {
            // Test format exception
            throw new fun.lzwi.epubime.exception.EpubFormatException("Format error", new File("test.epub"), "Missing mimetype");
        } catch (fun.lzwi.epubime.exception.EpubFormatException e) {
            System.out.println("[OK] EpubFormatException working: " + e.getMessage());
            assertTrue("Message should contain format info", e.getMessage().contains("Format error"));
        }
        
        try {
            // Test path validation exception
            throw new fun.lzwi.epubime.exception.EpubPathValidationException("Path validation failed", "../../../etc/passwd");
        } catch (fun.lzwi.epubime.exception.EpubPathValidationException e) {
            System.out.println("[OK] EpubPathValidationException working: " + e.getMessage());
            assertTrue("Message should contain path info", e.getMessage().contains("Path validation failed"));
        }
        
        try {
            // Test XML parsing exception
            throw new fun.lzwi.epubime.exception.EpubXmlParseException("XML parsing failed", "content.opf", 10, 25, null);
        } catch (fun.lzwi.epubime.exception.EpubXmlParseException e) {
            System.out.println("[OK] EpubXmlParseException working: " + e.getMessage());
            assertTrue("Message should contain XML info", e.getMessage().contains("XML parsing failed"));
        }
        
        System.out.println("[SUCCESS] All exception system tests passed!");
    }
}