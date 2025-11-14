package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.exception.EpubPathValidationException;
import org.junit.Test;

import static org.junit.Assert.*;

public class EpubPathValidationExceptionTest {

    @Test
    public void testEpubPathValidationExceptionWithDetails() {
        // Test constructor with details
        EpubPathValidationException exception = new EpubPathValidationException(
                "Invalid file path",
                "test.epub",
                "path/to/test.epub");

        assertEquals("Invalid file path [File: test.epub, Path: path/to/test.epub, Operation: pathValidation]",
                exception.getMessage());
        assertEquals("test.epub", exception.getFileName());
        assertEquals("path/to/test.epub", exception.getFilePath());
        assertEquals("pathValidation", exception.getOperation());
    }

    @Test
    public void testEpubPathValidationExceptionWithCause() {
        // Test constructor with cause
        Exception cause = new Exception("Root cause");
        EpubPathValidationException exception = new EpubPathValidationException(
                "Invalid file path",
                "test.epub",
                "path/to/test.epub",
                cause);

        assertEquals("Invalid file path [File: test.epub, Path: path/to/test.epub, Operation: pathValidation]",
                exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("test.epub", exception.getFileName());
        assertEquals("path/to/test.epub", exception.getFilePath());
        assertEquals("pathValidation", exception.getOperation());
    }
}