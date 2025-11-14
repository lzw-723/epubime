package fun.lzwi.epubime.epub;

import org.junit.Test;

import static org.junit.Assert.*;

public class EpubParseExceptionEnhancedTest {

    @Test
    public void testEpubParseExceptionWithDetails() {
        // Test basic constructor
        EpubParseException exception = new EpubParseException("Test Exception");
        assertEquals("Test Exception", exception.getMessage());
        assertNull(exception.getFileName());
        assertNull(exception.getFilePath());
        assertNull(exception.getOperation());
    }

    @Test
    public void testEpubParseExceptionWithCause() {
        // Test constructor with cause
        Exception cause = new Exception("Root cause");
        EpubParseException exception = new EpubParseException("Test Exception", cause);
        assertEquals("Test Exception", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertNull(exception.getFileName());
        assertNull(exception.getFilePath());
        assertNull(exception.getOperation());
    }

    @Test
    public void testEpubParseExceptionWithFullDetails() {
        // Test constructor with full details
        Exception cause = new Exception("Root cause");
        EpubParseException exception = new EpubParseException(
                "Failed to read file", 
                "test.epub", 
                "path/to/test.epub", 
                "readEpubContent", 
                cause);
        
        assertEquals("Failed to read file [File: test.epub, Path: path/to/test.epub, Operation: readEpubContent]", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("test.epub", exception.getFileName());
        assertEquals("path/to/test.epub", exception.getFilePath());
        assertEquals("readEpubContent", exception.getOperation());
    }

    @Test
    public void testEpubParseExceptionWithoutCause() {
        // Test constructor without cause but with details
        EpubParseException exception = new EpubParseException(
                "File not found", 
                "missing.epub", 
                "path/to/missing.epub", 
                "parse");
        
        assertEquals("File not found [File: missing.epub, Path: path/to/missing.epub, Operation: parse]", exception.getMessage());
        assertNull(exception.getCause());
        assertEquals("missing.epub", exception.getFileName());
        assertEquals("path/to/missing.epub", exception.getFilePath());
        assertEquals("parse", exception.getOperation());
    }

    @Test
    public void testEpubParseExceptionWithPartialDetails() {
        // Test constructor with partial details
        EpubParseException exception = new EpubParseException(
                "Processing error", 
                "test.epub", 
                null, 
                "processHtmlChapterContent");
        
        assertEquals("Processing error [File: test.epub, Operation: processHtmlChapterContent]", exception.getMessage());
        assertEquals("test.epub", exception.getFileName());
        assertNull(exception.getFilePath());
        assertEquals("processHtmlChapterContent", exception.getOperation());
    }
}