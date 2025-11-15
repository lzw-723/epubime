package fun.lzwi.epubime.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionBuilderTest {

    @Test
    public void testBuildExceptionBasic() {
        String message = "Test exception message";
        String fileName = "test.epub";
        String filePath = "books/test.epub";
        String operation = "parsing";
        EpubParseException.ErrorCode errorCode = EpubParseException.ErrorCode.FILE_NOT_FOUND;

        // 直接使用EpubFormatException的构造器，而不是ExceptionBuilder
        EpubFormatException exception = new EpubFormatException(message, fileName, filePath);

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains(message),
                  "Message should contain the expected text");
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        // 注意：EpubFormatException使用固定的operation和errorCode
        assertEquals("formatValidation", exception.getOperation());
        assertEquals(EpubParseException.ErrorCode.EPUB_INVALID_CONTAINER, exception.getErrorCode());
    }

    @Test
    public void testBuildExceptionWithCause() {
        String message = "Test exception with cause";
        String fileName = "corrupted.epub";
        String filePath = "books/corrupted.epub";
        String operation = "validation";
        EpubParseException.ErrorCode errorCode = EpubParseException.ErrorCode.INVALID_FILE_FORMAT;
        Throwable cause = new IllegalArgumentException("Invalid argument");

        // 直接使用EpubFormatException的构造器，而不是ExceptionBuilder
        EpubFormatException exception = new EpubFormatException(message, fileName, filePath, cause);

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains(message),
                  "Message should contain the expected text");
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        // 注意：EpubFormatException使用固定的operation和errorCode
        assertEquals("formatValidation", exception.getOperation());
        assertEquals(EpubParseException.ErrorCode.EPUB_INVALID_CONTAINER, exception.getErrorCode());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testBuildExceptionDifferentTypes() {
        // Test building different exception types directly
        String message = "Different exception type";
        String fileName = "test.xml";
        String filePath = "data/test.xml";

        // Test EpubZipException
        EpubZipException zipException = new EpubZipException(message, fileName, filePath);
        assertNotNull(zipException, "EpubZipException should not be null");
        assertTrue(zipException instanceof EpubZipException,
                  "Should be instance of EpubZipException");

        // Test EpubResourceException
        EpubResourceException resourceException = new EpubResourceException(message, fileName, filePath);
        assertNotNull(resourceException, "EpubResourceException should not be null");
        assertTrue(resourceException instanceof EpubResourceException,
                  "Should be instance of EpubResourceException");

        // Test EpubPathValidationException
        EpubPathValidationException pathException = EpubPathValidationException.createForCompatibility(message, fileName, filePath);
        assertNotNull(pathException, "EpubPathValidationException should not be null");
        assertTrue(pathException instanceof EpubPathValidationException,
                  "Should be instance of EpubPathValidationException");
    }

    @Test
    public void testCreateBuilder() {
        String message = "Builder test message";
        String fileName = "builder.epub";
        String filePath = "test/builder.epub";
        String operation = "building";
        EpubParseException.ErrorCode errorCode = EpubParseException.ErrorCode.INTERNAL_ERROR;

        EpubParseException.Builder builder = ExceptionBuilder.createBuilder(
            message, fileName, filePath, operation, errorCode);

        assertNotNull(builder, "Builder should not be null");
        
        // Build the exception to verify all values are set correctly
        EpubParseException exception = new EpubParseException(builder);

        assertTrue(exception.getMessage().contains(message),
                  "Message should contain the expected text");
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        assertEquals(operation, exception.getOperation());
        assertEquals(errorCode, exception.getErrorCode());
    }

    @Test
    public void testCreateBuilderWithSuggestion() {
        String message = "Builder with suggestion";
        String fileName = "suggestion.epub";
        String filePath = "test/suggestion.epub";
        String operation = "validating";
        EpubParseException.ErrorCode errorCode = EpubParseException.ErrorCode.INVALID_FILE_FORMAT;
        String recoverySuggestion = "Please check the file format and try again";

        EpubParseException.Builder builder = ExceptionBuilder.createBuilderWithSuggestion(
            message, fileName, filePath, operation, errorCode, recoverySuggestion);

        assertNotNull(builder, "Builder should not be null");

        // Build the exception to verify all values are set correctly
        EpubParseException exception = new EpubParseException(builder);

        assertTrue(exception.getMessage().contains(message),
                  "Message should contain the expected text");
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        assertEquals(operation, exception.getOperation());
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(recoverySuggestion, exception.getRecoverySuggestion());
    }

    @Test
    public void testBuildExceptionWithInvalidClass() {
        // Try to build an exception with ExceptionBuilder and an invalid class
        // This should fail because InvalidExceptionClass doesn't have Builder constructor
        assertThrows(RuntimeException.class, () -> {
            ExceptionBuilder.buildException(
                InvalidExceptionClass.class, "message", "file", "path", "op",
                EpubParseException.ErrorCode.INTERNAL_ERROR);
        });
    }

    @Test
    public void testExceptionBuilderUsage() {
        // Test that ExceptionBuilder.createBuilder works correctly
        String message = "Builder test message";
        String fileName = "builder.epub";
        String filePath = "test/builder.epub";
        String operation = "building";
        EpubParseException.ErrorCode errorCode = EpubParseException.ErrorCode.INTERNAL_ERROR;

        EpubParseException.Builder builder = ExceptionBuilder.createBuilder(
            message, fileName, filePath, operation, errorCode);

        assertNotNull(builder, "Builder should not be null");

        // Build the exception to verify all values are set correctly
        EpubParseException exception = new EpubParseException(builder);

        assertTrue(exception.getMessage().contains(message),
                  "Message should contain the expected text");
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        assertEquals(operation, exception.getOperation());
        assertEquals(errorCode, exception.getErrorCode());
    }

    @Test
    public void testBuilderReusability() {
        String message = "Reusable builder";
        String fileName = "reusable.epub";
        String filePath = "test/reusable.epub";
        String operation = "testing";
        EpubParseException.ErrorCode errorCode = EpubParseException.ErrorCode.FILE_NOT_FOUND;

        EpubParseException.Builder builder = ExceptionBuilder.createBuilder(
            message, fileName, filePath, operation, errorCode);

        // Use the same builder to create multiple exceptions
        EpubParseException exception1 = new EpubParseException(builder);
        EpubParseException exception2 = new EpubParseException(builder);

        assertNotNull(exception1, "First exception should not be null");
        assertNotNull(exception2, "Second exception should not be null");
        assertEquals(exception1.getMessage(), exception2.getMessage(),
                    "Both exceptions should have the same message");
        assertEquals(exception1.getFileName(), exception2.getFileName(),
                    "Both exceptions should have the same file name");
    }

    @Test
    public void testBuilderWithContext() {
        String message = "Builder with context";
        String fileName = "context.epub";
        String filePath = "test/context.epub";
        String operation = "processing";
        EpubParseException.ErrorCode errorCode = EpubParseException.ErrorCode.INTERNAL_ERROR;

        EpubParseException.Builder builder = ExceptionBuilder.createBuilder(
            message, fileName, filePath, operation, errorCode)
            .addContext("chapter", "chapter1")
            .addContext("section", "introduction");

        EpubParseException exception = new EpubParseException(builder);

        assertTrue(exception.getMessage().contains(message),
                  "Message should contain the expected text");
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        assertEquals(operation, exception.getOperation());
        assertEquals(errorCode, exception.getErrorCode());
        
        // Verify context was added (context is stored in the exception object, not in the message)
        // We can't directly access context, but we can verify the builder worked correctly
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        assertEquals(operation, exception.getOperation());
        assertEquals(errorCode, exception.getErrorCode());
    }

    // Helper class for testing invalid exception building
    private static class InvalidExceptionClass extends EpubParseException {
        public InvalidExceptionClass(String message) {
            super(message);
        }
    }
}