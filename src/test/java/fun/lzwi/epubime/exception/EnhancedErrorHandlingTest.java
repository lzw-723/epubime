package fun.lzwi.epubime.exception;

import fun.lzwi.epubime.epub.EpubBook;
import fun.lzwi.epubime.epub.Metadata;
import org.junit.Test;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 增强错误处理测试类
 * 测试新的错误处理功能，包括：
 * - 错误码系统
 * - 详细的异常信息
 * - 错误恢复机制
 * - 错误上下文收集
 */
public class EnhancedErrorHandlingTest {
    
    private ErrorContext errorContext;
    
    @Before
    public void setUp() {
        errorContext = new ErrorContext(100, ParseOptions.LogLevel.DEBUG);
    }
    
    @Test
    public void testErrorCodeSystem() {
        // 测试错误码创建
        EpubParseException exception = new EpubParseException(
                "Test error message",
                "test.epub",
                "/path/to/test.epub",
                "testOperation",
                EpubParseException.ErrorCode.FILE_NOT_FOUND
        );
        
        assertEquals(EpubParseException.ErrorCode.FILE_NOT_FOUND, exception.getErrorCode());
        assertEquals(1001, exception.getErrorCodeValue());
        assertEquals("[1001: File not found] Test error message [File: test.epub, Path: /path/to/test.epub, Operation: testOperation]", exception.getMessage());
        assertEquals("test.epub", exception.getFileName());
        assertEquals("/path/to/test.epub", exception.getFilePath());
        assertEquals("testOperation", exception.getOperation());
        assertNotNull(exception.getRecoverySuggestion());
    }
    
    @Test
    public void testExceptionBuilder() {
        // 测试构建器模式
        EpubParseException exception = new EpubParseException.Builder()
                .message("Custom error message")
                .fileName("custom.epub")
                .filePath("/custom/path")
                .operation("customOperation")
                .errorCode(EpubParseException.ErrorCode.XML_PARSE_ERROR)
                .lineNumber(10)
                .columnNumber(5)
                .addContext("customKey", "customValue")
                .recoverySuggestion("Custom recovery suggestion")
                .build();
        
        assertEquals("[3001: XML parsing failed] Custom error message [File: custom.epub, Path: /custom/path, Operation: customOperation]", exception.getMessage());
        assertEquals("custom.epub", exception.getFileName());
        assertEquals("/custom/path", exception.getFilePath());
        assertEquals("customOperation", exception.getOperation());
        assertEquals(EpubParseException.ErrorCode.XML_PARSE_ERROR, exception.getErrorCode());
        assertEquals(10, exception.getLineNumber());
        assertEquals(5, exception.getColumnNumber());
        assertEquals("customValue", exception.getContext("customKey"));
        assertEquals("Custom recovery suggestion", exception.getRecoverySuggestion());
    }
    
    @Test
    public void testXmlParseException() {
        // 测试XML解析异常
        EpubXmlParseException exception = new EpubXmlParseException(
                "Invalid XML structure",
                "test.opf",
                "OEBPS/content.opf",
                "/package/metadata",
                15,
                20
        );
        
        assertEquals("[3001: XML parsing failed] Invalid XML structure [File: test.opf, Path: OEBPS/content.opf, Operation: xmlParsing]", exception.getMessage());
        assertEquals("test.opf", exception.getFileName());
        assertEquals("OEBPS/content.opf", exception.getFilePath());
        assertEquals("/package/metadata", exception.getXPath());
        assertEquals(15, exception.getLineNumber());
        assertEquals(20, exception.getColumnNumber());
        assertEquals(EpubParseException.ErrorCode.XML_PARSE_ERROR, exception.getErrorCode());
    }
    
    @Test
    public void testParseOptions() {
        // 测试解析选项
        ParseOptions strictOptions = ParseOptions.strict();
        assertEquals(ParseOptions.ErrorHandlingStrategy.STRICT, strictOptions.getErrorHandlingStrategy());
        assertEquals(ParseOptions.LogLevel.ERROR, strictOptions.getLogLevel());
        assertFalse(strictOptions.isContinueOnMetadataError());
        
        ParseOptions lenientOptions = ParseOptions.lenient();
        assertEquals(ParseOptions.ErrorHandlingStrategy.LENIENT, lenientOptions.getErrorHandlingStrategy());
        assertEquals(ParseOptions.LogLevel.WARNING, lenientOptions.getLogLevel());
        assertTrue(lenientOptions.isContinueOnMetadataError());
        assertTrue(lenientOptions.isContinueOnNavigationError());
        assertTrue(lenientOptions.isContinueOnResourceError());
        
        ParseOptions bestEffortOptions = ParseOptions.bestEffort();
        assertEquals(ParseOptions.ErrorHandlingStrategy.BEST_EFFORT, bestEffortOptions.getErrorHandlingStrategy());
        assertTrue(bestEffortOptions.isSkipInvalidResources());
    }
    
    @Test
    public void testErrorContext() {
        // 测试错误上下文
        errorContext.debug("Debug message", "test.epub", "/test", "debugOp", null);
        errorContext.info("Info message", "test.epub", "/test", "infoOp", null);
        errorContext.warning("Warning message", "test.epub", "/test", "warningOp", 
                           EpubParseException.ErrorCode.XML_INVALID_STRUCTURE, null);
        errorContext.error("Error message", "test.epub", "/test", "errorOp", 
                         EpubParseException.ErrorCode.FILE_NOT_FOUND, null, null);
        errorContext.fatal("Fatal message", "test.epub", "/test", "fatalOp", 
                         EpubParseException.ErrorCode.EPUB_INVALID_CONTAINER, null, null);
        errorContext.recovered("Recovered error", "test.epub", "/test", "recoveredOp", 
                             EpubParseException.ErrorCode.XML_PARSE_ERROR, null);
        
        // 注意：recovered错误也被计入错误总数，且既算warning也算recovered
        assertEquals(6, errorContext.getErrors().size());
        assertTrue(errorContext.hasErrors());
        assertTrue(errorContext.hasWarnings());
        assertTrue(errorContext.hasFatalErrors());
        assertEquals(1, errorContext.getFatalErrors().size());
        assertEquals(1, errorContext.getRecoveredErrors().size());
        
        ErrorContext.ErrorStatistics stats = errorContext.getStatistics();
        assertEquals(1, stats.getDebugCount());
        assertEquals(1, stats.getInfoCount());
        assertEquals(2, stats.getWarningCount()); // 1个warning + 1个recovered(warning级别)
        assertEquals(1, stats.getErrorCount());
        assertEquals(1, stats.getFatalCount());
        assertEquals(1, stats.getRecoveredCount());
    }
    
    @Test
    public void testParseResult() {
        // 测试解析结果
        EpubBook book = new EpubBook();
        book.setMetadata(new Metadata());
        
        // 成功结果
        ParseResult successResult = ParseResult.success(book, 100);
        assertEquals(ParseResult.ParseStatus.SUCCESS, successResult.getStatus());
        assertTrue(successResult.isSuccess());
        assertFalse(successResult.isFailure());
        assertEquals(book, successResult.getEpubBook());
        assertEquals(100, successResult.getParseTimeMs());
        
        // 失败结果
        ErrorContext errorContext = new ErrorContext(100, ParseOptions.LogLevel.ERROR);
        errorContext.fatal("Fatal error", "test.epub", "/test", "testOp", 
                         EpubParseException.ErrorCode.EPUB_INVALID_CONTAINER, null, null);
        
        ParseResult failureResult = ParseResult.failure(errorContext, 200);
        assertEquals(ParseResult.ParseStatus.FAILURE, failureResult.getStatus());
        assertFalse(failureResult.isSuccess());
        assertTrue(failureResult.isFailure());
        assertNull(failureResult.getEpubBook());
        assertEquals(200, failureResult.getParseTimeMs());
        assertTrue(failureResult.hasCriticalErrors());
        
        // 部分成功结果（使用警告而不是致命错误）
        // 注意：需要使用DEBUG级别来确保warning被记录
        ErrorContext warningContext = new ErrorContext(100, ParseOptions.LogLevel.DEBUG);
        warningContext.warning("Warning message", "test.epub", "/test", "testOp", 
                             EpubParseException.ErrorCode.XML_INVALID_STRUCTURE, null);
        
        ParseResult partialResult = ParseResult.partialSuccess(book, warningContext, 150);
        assertEquals(ParseResult.ParseStatus.PARTIAL_SUCCESS, partialResult.getStatus());
        assertFalse(partialResult.isSuccess());
        assertTrue(partialResult.isPartialSuccess());
        assertFalse(partialResult.isFailure());
        assertEquals(book, partialResult.getEpubBook());
        assertEquals(150, partialResult.getParseTimeMs());
    }
    
    @Test
    public void testErrorRecovery() {
        // 测试错误恢复机制
        ParseOptions lenientOptions = ParseOptions.lenient();
        
        // 创建一个可恢复的错误
        EpubParseException recoverableError = new EpubParseException(
                "Missing optional metadata",
                "test.epub",
                "/test",
                "metadataParsing",
                EpubParseException.ErrorCode.XML_MISSING_REQUIRED_ELEMENT
        );
        
        // 在宽松模式下应该继续
        assertTrue(lenientOptions.shouldContinueOnError(recoverableError));
        
        // 在严格模式下不应该继续
        ParseOptions strictOptions = ParseOptions.strict();
        assertFalse(strictOptions.shouldContinueOnError(recoverableError));
        
        // 致命错误在任何模式下都不应该继续
        EpubParseException fatalError = new EpubParseException(
                "Corrupted EPUB file",
                "test.epub",
                "/test",
                "epubValidation",
                EpubParseException.ErrorCode.EPUB_INVALID_CONTAINER
        );
        
        assertFalse(lenientOptions.shouldContinueOnError(fatalError));
        assertFalse(strictOptions.shouldContinueOnError(fatalError));
    }
    
    @Test
    public void testExceptionChaining() {
        // 测试异常链
        Exception rootCause = new IllegalArgumentException("Root cause");
        Exception middleCause = new RuntimeException("Middle cause", rootCause);
        EpubParseException topException = new EpubParseException.Builder()
                .message("Top exception")
                .cause(middleCause)
                .build();
        
        // 测试根因获取
        Throwable rootCauseResult = topException.getRootCause();
        assertEquals(rootCause, rootCauseResult);
        
        // 测试异常链
        assertEquals(middleCause, topException.getCause());
        assertEquals(rootCause, topException.getCause().getCause());
    }
    
    @Test
    public void testErrorFiltering() {
        // 测试错误过滤
        ParseOptions options = ParseOptions.lenient()
                .withIgnoredErrorPattern("optional")
                .withIgnoredErrorPattern("missing");
        
        EpubParseException ignoredError = new EpubParseException(
                "Missing optional metadata field",
                "test.epub",
                "/test",
                "metadataParsing",
                EpubParseException.ErrorCode.XML_MISSING_REQUIRED_ELEMENT
        );
        
        // 应该被忽略
        assertTrue(options.shouldIgnoreError(ignoredError.getMessage()));
        
        EpubParseException criticalError = new EpubParseException(
                "Corrupted EPUB structure",
                "test.epub",
                "/test",
                "structureValidation",
                EpubParseException.ErrorCode.EPUB_INVALID_CONTAINER
        );
        
        // 不应该被忽略
        assertFalse(options.shouldIgnoreError(criticalError.getMessage()));
    }
    
    @Test
    public void testErrorReportGeneration() {
        // 测试错误报告生成
        errorContext.addGlobalContext("epubFile", "test.epub");
        errorContext.addGlobalContext("parserVersion", "1.0");
        
        errorContext.error("Test error", "test.epub", "/test", "testOp", 
                         EpubParseException.ErrorCode.FILE_NOT_FOUND, null, null);
        errorContext.warning("Test warning", "test.epub", "/test", "testOp", 
                           EpubParseException.ErrorCode.XML_INVALID_STRUCTURE, null);
        
        String report = errorContext.generateReport();
        
        assertNotNull(report);
        assertTrue(report.contains("EPUB解析错误报告"));
        assertTrue(report.contains("错误统计"));
        assertTrue(report.contains("警告: 1"));
        assertTrue(report.contains("错误: 1"));
        assertTrue(report.contains("Test error"));
        assertTrue(report.contains("Test warning"));
    }
}