package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.exception.EpubFormatException;
import org.junit.Test;

import static org.junit.Assert.*;

public class EpubFormatExceptionTest {

    @Test
    public void testEpubFormatExceptionWithDetails() {
        // Test constructor with details
        EpubFormatException exception = new EpubFormatException(
                "EPUB format error", 
                "test.epub", 
                "path/to/test.epub");
        
        assertEquals("EPUB format error [File: test.epub, Path: path/to/test.epub, Operation: formatParsing]", exception.getMessage());
        assertEquals("test.epub", exception.getFileName());
        assertEquals("path/to/test.epub", exception.getFilePath());
        assertEquals("formatParsing", exception.getOperation());
    }

    @Test
    public void testEpubFormatExceptionWithCause() {
        // Test constructor with cause
        Exception cause = new Exception("Root cause");
        EpubFormatException exception = new EpubFormatException(
                "EPUB format error", 
                "test.epub", 
                "path/to/test.epub", 
                cause);
        
        assertEquals("EPUB format error [File: test.epub, Path: path/to/test.epub, Operation: formatParsing]", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("test.epub", exception.getFileName());
        assertEquals("path/to/test.epub", exception.getFilePath());
        assertEquals("formatParsing", exception.getOperation());
    }
}