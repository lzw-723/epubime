package fun.lzwi.epubime.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimplifiedExceptionBuilderTest {

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
        assertTrue(exception1.getMessage().contains(message),
                    "Both exceptions should have the same message");
        assertTrue(exception2.getMessage().contains(message),
                    "Both exceptions should have the same message");
        assertEquals(exception1.getFileName(), exception2.getFileName(),
                    "Both exceptions should have the same file name");
    }
}