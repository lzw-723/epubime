package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.exception.EpubZipException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EpubZipExceptionTest {

    @Test
    public void testEpubZipExceptionWithDetails() {
        // Test constructor with details
        EpubZipException exception = new EpubZipException(
                "ZIP processing error", 
                "test.epub", 
                "path/to/test.epub");
        
        assertEquals("[2001: Invalid ZIP file format] ZIP processing error [File: test.epub] [Path: path/to/test.epub] [Operation: zipProcessing]", exception.getMessage());
        assertEquals("test.epub", exception.getFileName());
        assertEquals("path/to/test.epub", exception.getFilePath());
        assertEquals("zipProcessing", exception.getOperation());
    }

    @Test
    public void testEpubZipExceptionWithCause() {
        // Test constructor with cause
        Exception cause = new Exception("Root cause");
        EpubZipException exception = new EpubZipException(
                "ZIP processing error", 
                "test.epub", 
                "path/to/test.epub", 
                cause);
        
        assertEquals("[2001: Invalid ZIP file format] ZIP processing error [File: test.epub] [Path: path/to/test.epub] [Operation: zipProcessing]", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("test.epub", exception.getFileName());
        assertEquals("path/to/test.epub", exception.getFilePath());
        assertEquals("zipProcessing", exception.getOperation());
    }
}