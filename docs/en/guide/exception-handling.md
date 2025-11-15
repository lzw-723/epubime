# Exception Handling

EPUBime provides a comprehensive exception handling mechanism, with all exceptions inheriting from the `BaseEpubException` base class. EPUBime also provides enhanced error handling features, including error code systems, detailed error context collection, and flexible error recovery strategies.

## Exception Hierarchy

```
Exception
└── BaseEpubException
    ├── EpubParseException      (Parse Exception)
    │   └── EpubXmlParseException (XML Parse Exception)
    ├── EpubFormatException     (Format Exception)
    ├── EpubPathValidationException (Path Validation Exception)
    ├── EpubResourceException   (Resource Exception)
    └── EpubZipException        (ZIP Exception)
```

## Enhanced Error Handling Features

### Error Code System

EPUBime introduces a detailed error code system to help developers quickly locate problems:

```java
try {
    EpubBook book = parser.parse();
} catch (EpubParseException e) {
    System.err.println("Error Code: " + e.getErrorCode());
    System.err.println("Error Code Value: " + e.getErrorCodeValue());
    System.err.println("Recovery Suggestion: " + e.getRecoverySuggestion());
}
```

Available error codes include:
- File related errors (1000-1999): e.g., FILE_NOT_FOUND, FILE_CORRUPTED
- ZIP related errors (2000-2999): e.g., ZIP_INVALID, ZIP_ENTRY_NOT_FOUND
- XML parsing errors (3000-3999): e.g., XML_PARSE_ERROR, XML_INVALID_STRUCTURE
- EPUB format errors (4000-4999): e.g., EPUB_INVALID_CONTAINER, EPUB_MISSING_MIMETYPE
- Path validation errors (5000-5999): e.g., PATH_TRAVERSAL_ATTACK
- Resource related errors (6000-6999): e.g., RESOURCE_NOT_FOUND, RESOURCE_LOAD_FAILED

### Exception Builder Pattern

EPUBime provides a flexible builder pattern to create exceptions:

```java
EpubParseException exception = new EpubParseException.Builder()
    .message("Custom error message")
    .fileName("book.epub")
    .filePath("/path/to/book.epub")
    .operation("metadataParsing")
    .errorCode(EpubParseException.ErrorCode.XML_PARSE_ERROR)
    .lineNumber(10)
    .columnNumber(5)
    .addContext("customKey", "customValue")
    .recoverySuggestion("Custom recovery suggestion")
    .build();
```

## Error Context Collection

EPUBime provides the `ErrorContext` class to collect errors, warnings, and debug information during the parsing process:

```java
// Create error context
ErrorContext errorContext = new ErrorContext(100, ParseOptions.LogLevel.DEBUG);

// Record different level information
errorContext.debug("Debug information", "book.epub", "/path", "debugOp", null);
errorContext.info("General information", "book.epub", "/path", "infoOp", null);
errorContext.warning("Warning information", "book.epub", "/path", "warningOp", 
                   EpubParseException.ErrorCode.XML_INVALID_STRUCTURE, null);
errorContext.error("Error information", "book.epub", "/path", "errorOp", 
                 EpubParseException.ErrorCode.FILE_NOT_FOUND, null, null);
errorContext.fatal("Fatal error", "book.epub", "/path", "fatalOp", 
                 EpubParseException.ErrorCode.EPUB_INVALID_CONTAINER, null, null);

// Generate error report
String report = errorContext.generateReport();
System.out.println(report);

// Get error statistics
ErrorContext.ErrorStatistics stats = errorContext.getStatistics();
```

## Parse Options Configuration

Through the `ParseOptions` class, you can configure the error handling behavior during the parsing process:

```java
// Strict mode (default)
ParseOptions strictOptions = ParseOptions.strict();

// Lenient mode
ParseOptions lenientOptions = ParseOptions.lenient();

// Best effort mode
ParseOptions bestEffortOptions = ParseOptions.bestEffort();

// Custom configuration
ParseOptions customOptions = ParseOptions.lenient()
    .withErrorHandlingStrategy(ParseOptions.ErrorHandlingStrategy.LENIENT)
    .withLogLevel(ParseOptions.LogLevel.WARNING)
    .withCollectWarnings(true)
    .withContinueOnMetadataError(true)
    .withContinueOnNavigationError(true)
    .withContinueOnResourceError(true)
    .withSkipInvalidResources(true)
    .withUseFallbackMetadata(true)
    .withIgnoredErrorPattern("optional")
    .withIgnoredErrorPattern("missing");
```

## Parse Result Handling

The `ParseResult` class encapsulates the parsing results, including the parsed book object and error information:

```java
ParseResult result = parser.parseWithOptions(file, options);

// Check parsing status
if (result.isSuccess()) {
    EpubBook book = result.getEpubBook();
    // Process successfully parsed book
} else if (result.isPartialSuccess()) {
    EpubBook book = result.getEpubBook();
    ErrorContext errorContext = result.getErrorContext();
    // Process partially successful parsing results
} else if (result.isFailure()) {
    ErrorContext errorContext = result.getErrorContext();
    // Handle parsing failure
}

// Get detailed information
System.out.println("Parse Status: " + result.getStatus());
System.out.println("Parse Time: " + result.getParseTimeMs() + "ms");
System.out.println("Parse Summary: " + result.getParseSummary());
```

## EpubParseException

Parse exception, used to handle errors during EPUB file parsing.

```java
try {
    EpubBook book = parser.parse();
} catch (EpubParseException e) {
    System.err.println("Parse error: " + e.getMessage());
    System.err.println("File: " + e.getFileName());
    System.err.println("Path: " + e.getFilePath());
    System.err.println("Operation: " + e.getOperation());
    System.err.println("Error Code: " + e.getErrorCode());
    System.err.println("Recovery Suggestion: " + e.getRecoverySuggestion());
    
    // Get the original exception (if any)
    Throwable cause = e.getCause();
    if (cause != null) {
        System.err.println("Cause: " + cause.getMessage());
    }
    
    // Get root cause
    Throwable rootCause = e.getRootCause();
    if (rootCause != cause) {
        System.err.println("Root Cause: " + rootCause.getMessage());
    }
}
```

### Common Scenarios
- Corrupted or incorrectly formatted EPUB files
- Internal file format errors (such as XML format errors)
- Unprocessable data encountered during parsing

## EpubXmlParseException

XML parse exception, specifically for handling XML format errors.

```java
try {
    Metadata metadata = EpubParser.parseMetadata(opfContent);
} catch (EpubXmlParseException e) {
    System.err.println("XML parse error: " + e.getMessage());
    System.err.println("File: " + e.getFileName());
    System.err.println("Line Number: " + e.getLineNumber());
    System.err.println("Column Number: " + e.getColumnNumber());
}
```

### Common Scenarios
- Incorrect XML format
- Missing required XML elements
- Invalid XML attribute values

## EpubFormatException

Format exception, used to handle cases where EPUB format does not comply with specifications.

```java
try {
    Metadata metadata = EpubParser.parseMetadata(opfContent);
} catch (EpubFormatException e) {
    System.err.println("Format error: " + e.getMessage());
    System.err.println("File: " + e.getFileName());
    System.err.println("Path: " + e.getFilePath());
    System.err.println("Operation: " + e.getOperation());
    System.err.println("Recovery Suggestion: " + e.getRecoverySuggestion());
}
```

### Common Scenarios
- EPUB specification violations (such as missing required files or elements)
- Incorrect metadata format
- Content document format not compliant with EPUB specification

## EpubPathValidationException

Path validation exception, used to prevent directory traversal attacks.

```java
try {
    String validatedPath = PathValidator.validatePath(inputPath);
} catch (EpubPathValidationException e) {
    System.err.println("Path validation failed: " + e.getMessage());
    System.err.println("Invalid path: " + e.getFilePath());
    System.err.println("Operation: " + e.getOperation());
    System.err.println("Error Code: " + e.getErrorCode());
}
```

### Common Scenarios
- Attempting to access paths outside the EPUB file (such as `../../../etc/passwd`)
- Using illegal characters or path formats

## EpubResourceException

Resource exception, used to handle resource file access errors.

```java
try {
    EpubResource resource = book.getCover();
    byte[] data = resource.getData();
} catch (EpubResourceException e) {
    System.err.println("Resource error: " + e.getMessage());
    System.err.println("Resource path: " + e.getFilePath());
    System.err.println("Operation: " + e.getOperation());
    System.err.println("Error Code: " + e.getErrorCode());
}
```

### Common Scenarios
- Resource file does not exist
- Unable to read resource content
- Resource file is corrupted

## EpubZipException

ZIP exception, used to handle ZIP file operation errors.

```java
try (ZipFileManager zipManager = new ZipFileManager(epubFile)) {
    byte[] content = zipManager.getFileContent("OEBPS/content.opf");
} catch (EpubZipException e) {
    System.err.println("ZIP operation error: " + e.getMessage());
    System.err.println("File: " + e.getFileName());
    System.err.println("Entry: " + e.getFilePath());
    System.err.println("Operation: " + e.getOperation());
    System.err.println("Error Code: " + e.getErrorCode());
}
```

### Common Scenarios
- Corrupted ZIP files
- Unable to open ZIP files
- ZIP entries do not exist

## Comprehensive Exception Handling Example

```java
public class EpubProcessor {
    public void processEpub(File epubFile) {
        // Configure parse options
        ParseOptions options = ParseOptions.lenient()
            .withLogLevel(ParseOptions.LogLevel.INFO)
            .withContinueOnMetadataError(true)
            .withContinueOnResourceError(true);
        
        try {
            // Parse EPUB with configuration options
            EpubParser parser = new EpubParser(epubFile);
            ParseResult result = parser.parseWithOptions(epubFile, options);
            
            if (result.isSuccess() || result.isPartialSuccess()) {
                EpubBook book = result.getEpubBook();
                // Process book content
                processBook(book);
                
                // Output parse summary
                System.out.println(result.getParseSummary());
                
                // If there are warnings or errors, output detailed information
                if (result.getErrorContext() != null) {
                    ErrorContext errorContext = result.getErrorContext();
                    if (errorContext.hasWarnings()) {
                        System.out.println("Warning information:");
                        for (ErrorContext.ErrorRecord warning : errorContext.getWarnings()) {
                            System.out.println("- " + warning.toString());
                        }
                    }
                }
            } else {
                // Handle parsing failure
                System.err.println("Parsing failed: " + result.getParseSummary());
                if (result.getErrorContext() != null) {
                    System.err.println(result.getErrorContext().generateReport());
                }
            }
            
        } catch (EpubParseException e) {
            // Handle parse exception
            handleParseException(e);
        } catch (EpubFormatException e) {
            // Handle format exception
            handleFormatException(e);
        } catch (EpubPathValidationException e) {
            // Handle path validation exception
            handlePathValidationException(e);
        } catch (EpubResourceException e) {
            // Handle resource exception
            handleResourceException(e);
        } catch (EpubZipException e) {
            // Handle ZIP exception
            handleZipException(e);
        } catch (Exception e) {
            // Handle other unexpected exceptions
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleParseException(EpubParseException e) {
        System.err.println("Parse failed - File: " + e.getFileName() + 
                          ", Error: " + e.getMessage() + 
                          ", Error Code: " + e.getErrorCode());
        
        if (e.getRecoverySuggestion() != null) {
            System.err.println("Recovery Suggestion: " + e.getRecoverySuggestion());
        }
    }
    
    private void handleFormatException(EpubFormatException e) {
        System.err.println("Format error - File: " + e.getFileName() + 
                          ", Error: " + e.getMessage());
        
        if (e.getRecoverySuggestion() != null) {
            System.err.println("Recovery Suggestion: " + e.getRecoverySuggestion());
        }
    }
    
    private void handlePathValidationException(EpubPathValidationException e) {
        System.err.println("Path validation failed - Path: " + e.getFilePath() + 
                          ", Error: " + e.getMessage());
    }
    
    private void handleResourceException(EpubResourceException e) {
        System.err.println("Resource access failed - Path: " + e.getFilePath() + 
                          ", Error: " + e.getMessage());
    }
    
    private void handleZipException(EpubZipException e) {
        System.err.println("ZIP operation failed - File: " + e.getFileName() + 
                          ", Error: " + e.getMessage());
    }
    
    private void processBook(EpubBook book) {
        // Actual logic for processing book content
        System.out.println("Processing book: " + book.getMetadata().getTitle());
    }
}
```

## Best Practices

1. **Use ParseResult for parsing results**: Prefer using `ParseResult` to handle parsing results, which provides more detailed error information and status.

2. **Configure appropriate parse options**: Choose suitable `ParseOptions` according to application requirements, balancing parsing strictness and fault tolerance.

3. **Handle error codes**: Use the error code system to quickly identify and handle specific types of errors.

4. **Collect error context**: Use `ErrorContext` to collect detailed error information for debugging and problem analysis.

5. **Specific exception handling**: Prioritize catching specific exception types rather than generic exceptions.

6. **Error information logging**: Log detailed error information, including filenames, paths, operation types, error codes, etc.

7. **Resource cleanup**: Ensure proper cleanup of resources in case of exceptions (such as closing file streams).

8. **User-friendly**: Provide meaningful error information and recovery suggestions to users rather than technical details.

9. **Logging**: Use logging frameworks to record exceptions for debugging and monitoring.

Exception handling is an important part of ensuring application stability and security. EPUBime's exception system provides detailed information to help developers quickly locate and resolve problems.