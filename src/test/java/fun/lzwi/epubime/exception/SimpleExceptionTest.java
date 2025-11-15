package fun.lzwi.epubime.exception;

import org.junit.jupiter.api.Test;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试简化的异常系统
 */
public class SimpleExceptionTest {
    
    @Test
    public void testBaseEpubException() {
        BaseEpubException exception = new BaseEpubException("Test message");
        assertEquals("Test message", exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    public void testBaseEpubExceptionWithCause() {
        Exception cause = new RuntimeException("Cause");
        BaseEpubException exception = new BaseEpubException("Test message", cause);
        assertEquals("Test message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    public void testEpubFileException() {
        File file = new File("test.epub");
        EpubFileException exception = new EpubFileException("File not found", file);
        assertTrue(exception.getMessage().contains("File not found"));
        assertTrue(exception.getMessage().contains("test.epub"));
        assertEquals(file, exception.getFile());
    }
    
    @Test
    public void testEpubZipException() {
        File file = new File("test.epub");
        EpubZipException exception = new EpubZipException("ZIP error", file, "content.html", new RuntimeException("IO error"));
        assertTrue(exception.getMessage().contains("ZIP error"));
        assertTrue(exception.getMessage().contains("test.epub"));
        assertTrue(exception.getMessage().contains("content.html"));
        assertEquals(file, exception.getFile());
        assertEquals("content.html", exception.getEntryName());
        assertNotNull(exception.getCause());
    }
    
    @Test
    public void testEpubFormatException() {
        File file = new File("test.epub");
        EpubFormatException exception = new EpubFormatException("Invalid format", file, "Missing mimetype");
        assertTrue(exception.getMessage().contains("Invalid format"));
        assertTrue(exception.getMessage().contains("test.epub"));
        assertTrue(exception.getMessage().contains("Missing mimetype"));
        assertEquals(file, exception.getFile());
        assertEquals("Missing mimetype", exception.getDetails());
    }
    
    @Test
    public void testEpubResourceException() {
        File file = new File("test.epub");
        EpubResourceException exception = new EpubResourceException("Resource not found", file, null, "image1.jpg", new RuntimeException("IO error"));
        assertTrue(exception.getMessage().contains("Resource not found"));
        assertTrue(exception.getMessage().contains("test.epub"));
        assertTrue(exception.getMessage().contains("image1.jpg"));
        assertEquals(file, exception.getFile());
        assertEquals("image1.jpg", exception.getResourcePath());
        assertNotNull(exception.getCause());
    }
    
    @Test
    public void testEpubPathValidationException() {
        EpubPathValidationException exception = new EpubPathValidationException("Invalid path", "../../../etc/passwd", new RuntimeException("Security error"));
        assertTrue(exception.getMessage().contains("Invalid path"));
        assertTrue(exception.getMessage().contains("../../../etc/passwd"));
        assertEquals("../../../etc/passwd", exception.getInvalidPath());
        assertNotNull(exception.getCause());
    }
    
    @Test
    public void testEpubXmlParseException() {
        EpubXmlParseException exception = new EpubXmlParseException("XML parsing failed", "content.opf", 10, 25, new RuntimeException("Malformed XML"));
        assertTrue(exception.getMessage().contains("XML parsing failed"));
        assertTrue(exception.getMessage().contains("content.opf"));
        assertTrue(exception.getMessage().contains("Line: 10"));
        assertTrue(exception.getMessage().contains("Column: 25"));
        assertEquals("content.opf", exception.getFileName());
        assertEquals(10, exception.getLineNumber());
        assertEquals(25, exception.getColumnNumber());
        assertNotNull(exception.getCause());
    }
}