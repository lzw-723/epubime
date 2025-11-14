package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.exception.EpubResourceException;
import org.junit.Test;

import static org.junit.Assert.*;

public class EpubResourceExceptionTest {

    @Test
    public void testEpubResourceExceptionWithDetails() {
        // Test constructor with details
        EpubResourceException exception = new EpubResourceException(
                "Resource loading error", 
                "test.epub", 
                "path/to/test.epub");
        
        assertEquals("[6002: Resource loading failed] Resource loading error [File: test.epub, Path: path/to/test.epub, Operation: resourceLoading]", exception.getMessage());
        assertEquals("test.epub", exception.getFileName());
        assertEquals("path/to/test.epub", exception.getFilePath());
        assertEquals("resourceLoading", exception.getOperation());
    }

    @Test
    public void testEpubResourceExceptionWithCause() {
        // Test constructor with cause
        Exception cause = new Exception("Root cause");
        EpubResourceException exception = new EpubResourceException(
                "Resource loading error", 
                "test.epub", 
                "path/to/test.epub", 
                cause);
        
        assertEquals("[6002: Resource loading failed] Resource loading error [File: test.epub, Path: path/to/test.epub, Operation: resourceLoading]", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("test.epub", exception.getFileName());
        assertEquals("path/to/test.epub", exception.getFilePath());
        assertEquals("resourceLoading", exception.getOperation());
    }
}