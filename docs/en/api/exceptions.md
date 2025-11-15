# Exception Classes

EPUBime provides a complete exception handling system, with all exceptions inheriting from the `BaseEpubException` base class. EPUBime also provides enhanced error handling features, including error code systems, detailed error context collection, and flexible error recovery strategies.

## Inheritance Hierarchy

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
public enum ErrorCode {
    // File related errors (1000-1999)
    FILE_NOT_FOUND(1001, "File not found"),
    FILE_ACCESS_DENIED(1002, "File access denied"),
    FILE_CORRUPTED(1003, "File appears to be corrupted"),
    INVALID_FILE_FORMAT(1004, "Invalid file format"),
    
    // ZIP related errors (2000-2999)
    ZIP_INVALID(2001, "Invalid ZIP file format"),
    ZIP_ENTRY_NOT_FOUND(2002, "ZIP entry not found"),
    ZIP_DECOMPRESSION_FAILED(2003, "ZIP decompression failed"),
    
    // XML parsing errors (3000-3999)
    XML_PARSE_ERROR(3001, "XML parsing failed"),
    XML_INVALID_STRUCTURE(3002, "Invalid XML structure"),
    XML_MISSING_REQUIRED_ELEMENT(3003, "Missing required XML element"),
    XML_INVALID_ATTRIBUTE(3004, "Invalid XML attribute"),
    
    // EPUB format errors (4000-4999)
    EPUB_INVALID_CONTAINER(4001, "Invalid EPUB container"),
    EPUB_MISSING_MIMETYPE(4002, "Missing mimetype file"),
    EPUB_INVALID_OPF(4003, "Invalid OPF file"),
    EPUB_INVALID_NCX(4004, "Invalid NCX file"),
    EPUB_INVALID_NAV(4005, "Invalid NAV file"),
    
    // Path validation errors (5000-5999)
    PATH_TRAVERSAL_ATTACK(5001, "Path traversal attack detected"),
    PATH_INVALID_CHARACTER(5002, "Invalid character in path"),
    PATH_TOO_LONG(5003, "Path too long"),
    
    // Resource related errors (6000-6999)
    RESOURCE_NOT_FOUND(6001, "Resource not found"),
    RESOURCE_LOAD_FAILED(6002, "Resource loading failed"),
    RESOURCE_INVALID_TYPE(6003, "Invalid resource type"),
    
    // General errors (9000-9999)
    UNKNOWN_ERROR(9001, "Unknown error occurred"),
    OPERATION_NOT_SUPPORTED(9002, "Operation not supported"),
    INTERNAL_ERROR(9003, "Internal error");
}
```

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

## BaseEpubException

`BaseEpubException` is the base class for all EPUB-related exceptions.

### Constructor

```java
public BaseEpubException(String message)
```
Creates a new BaseEpubException instance.

Parameters:
- `message`: Exception message

```java
public BaseEpubException(String message, Throwable cause)
```
Creates a new BaseEpubException instance.

Parameters:
- `message`: Exception message
- `cause`: Original exception

```java
public BaseEpubException(Throwable cause)
```
Creates a new BaseEpubException instance.

Parameters:
- `cause`: Original exception

## EpubParseException

Parse exception, used to handle errors during EPUB file parsing.

### Constructor

```java
public EpubParseException(String message)
```
Creates a new EpubParseException instance.

Parameters:
- `message`: Exception message

```java
public EpubParseException(String message, ErrorCode errorCode)
```
Creates a new EpubParseException instance with error code.

Parameters:
- `message`: Exception message
- `errorCode`: Error code

```java
public EpubParseException(String message, Throwable cause)
```
Creates a new EpubParseException instance with original exception.

Parameters:
- `message`: Exception message
- `cause`: Original exception

```java
public EpubParseException(String message, ErrorCode errorCode, Throwable cause)
```
Creates a new EpubParseException instance with error code and original exception.

Parameters:
- `message`: Exception message
- `errorCode`: Error code
- `cause`: Original exception

```java
public EpubParseException(String message, String fileName, String filePath, String operation, Throwable cause)
```
Creates a new EpubParseException instance with filename, path, operation type, and original exception.

Parameters:
- `message`: Exception message
- `fileName`: Filename
- `filePath`: File path
- `operation`: Operation type
- `cause`: Original exception

```java
public EpubParseException(String message, String fileName, String filePath, String operation, ErrorCode errorCode, Throwable cause)
```
Creates a new EpubParseException instance with filename, path, operation type, error code, and original exception.

Parameters:
- `message`: Exception message
- `fileName`: Filename
- `filePath`: File path
- `operation`: Operation type
- `errorCode`: Error code
- `cause`: Original exception

```java
public EpubParseException(String message, String fileName, String filePath, String operation)
```
Creates a new EpubParseException instance with filename, path, and operation type.

Parameters:
- `message`: Exception message
- `fileName`: Filename
- `filePath`: File path
- `operation`: Operation type

```java
public EpubParseException(String message, String fileName, String filePath, String operation, ErrorCode errorCode)
```
Creates a new EpubParseException instance with filename, path, operation type, and error code.

Parameters:
- `message`: Exception message
- `fileName`: Filename
- `filePath`: File path
- `operation`: Operation type
- `errorCode`: Error code

```java
public EpubParseException(Builder builder)
```
Creates a new EpubParseException instance using builder.

Parameters:
- `builder`: Builder

### Main Methods

#### getFileName()
```java
public String getFileName()
```
Gets the name of the related file.

Returns:
- `String`: Filename

#### getFilePath()
```java
public String getFilePath()
```
Gets the path of the related file.

Returns:
- `String`: File path

#### getOperation()
```java
public String getOperation()
```
Gets the operation type.

Returns:
- `String`: Operation type

#### getErrorCode()
```java
public ErrorCode getErrorCode()
```
Gets the error code.

Returns:
- `ErrorCode`: Error code

#### getErrorCodeValue()
```java
public int getErrorCodeValue()
```
Gets the error code value.

Returns:
- `int`: Error code value

#### getRecoverySuggestion()
```java
public String getRecoverySuggestion()
```
Gets the recovery suggestion.

Returns:
- `String`: Recovery suggestion

#### getLineNumber()
```java
public int getLineNumber()
```
Gets the line number.

Returns:
- `int`: Line number, returns -1 if not applicable

#### getColumnNumber()
```java
public int getColumnNumber()
```
Gets the column number.

Returns:
- `int`: Column number, returns -1 if not applicable

#### addContext()
```java
public void addContext(String key, Object value)
```
Adds context information.

Parameters:
- `key`: Key
- `value`: Value

#### getContext()
```java
public Object getContext(String key)
```
Gets context information.

Parameters:
- `key`: Key

Returns:
- `Object`: Value

#### getAllContext()
```java
public Map<String, Object> getAllContext()
```
Gets all context information.

Returns:
- `Map<String, Object>`: Context map

#### getRootCause()
```java
public Throwable getRootCause()
```
Gets the root cause.

Returns:
- `Throwable`: Root cause

## EpubXmlParseException

XML parse exception, specifically for handling XML format errors.

### Constructor

```java
public EpubXmlParseException(String message, String fileName)
```
Creates a new EpubXmlParseException instance.

Parameters:
- `message`: Exception message
- `fileName`: Filename

```java
public EpubXmlParseException(String message, String fileName, int lineNumber, int columnNumber, Throwable cause)
```
Creates a new EpubXmlParseException instance.

Parameters:
- `message`: Exception message
- `fileName`: Filename
- `lineNumber`: Line number
- `columnNumber`: Column number
- `cause`: Original exception

```java
public EpubXmlParseException(String message, String fileName, Throwable cause)
```
Creates a new EpubXmlParseException instance.

Parameters:
- `message`: Exception message
- `fileName`: Filename
- `cause`: Original exception

### Main Methods

#### getFileName()
```java
public String getFileName()
```
Gets the filename.

Returns:
- `String`: Filename

#### getLineNumber()
```java
public int getLineNumber()
```
Gets the line number.

Returns:
- `int`: Line number

#### getColumnNumber()
```java
public int getColumnNumber()
```
Gets the column number.

Returns:
- `int`: Column number

#### getFilePath()
```java
public String getFilePath()
```
Gets the file path (for backward compatibility).

Returns:
- `String`: File path

#### getXPath()
```java
public String getXPath()
```
Gets the XPath (for backward compatibility).

Returns:
- `String`: XPath, returns null in simplified design

#### getErrorCode()
```java
public Object getErrorCode()
```
Gets the error code (for backward compatibility).

Returns:
- `Object`: Error code, returns null in simplified design

#### getOperation()
```java
public String getOperation()
```
Gets the operation type (for backward compatibility).

Returns:
- `String`: Operation type

## EpubFormatException

Format exception, used to handle cases where EPUB format does not comply with specifications.

### Constructor

```java
public EpubFormatException(String message, File file)
```
Creates a new EpubFormatException instance.

Parameters:
- `message`: Exception message
- `file`: File

```java
public EpubFormatException(String message, File file, String details)
```
Creates a new EpubFormatException instance.

Parameters:
- `message`: Exception message
- `file`: File
- `details`: Details

```java
public EpubFormatException(String message, File file, String details, Throwable cause)
```
Creates a new EpubFormatException instance.

Parameters:
- `message`: Exception message
- `file`: File
- `details`: Details
- `cause`: Original exception

```java
public EpubFormatException(String message, String fileName, String filePath)
```
Creates a new EpubFormatException instance (for backward compatibility).

Parameters:
- `message`: Exception message
- `fileName`: Filename
- `filePath`: File path

```java
public EpubFormatException(String message, String fileName, String filePath, Throwable cause)
```
Creates a new EpubFormatException instance (for backward compatibility).

Parameters:
- `message`: Exception message
- `fileName`: Filename
- `filePath`: File path
- `cause`: Original exception

### Static Factory Methods

```java
public static EpubFormatException missingMimetype(String fileName, String filePath)
```
Creates an exception for missing mimetype file.

Parameters:
- `fileName`: Filename
- `filePath`: File path

Returns:
- `EpubFormatException`: Exception instance

```java
public static EpubFormatException invalidContainer(String fileName, String filePath, String details)
```
Creates an exception for invalid container.xml file.

Parameters:
- `fileName`: Filename
- `filePath`: File path
- `details`: Details

Returns:
- `EpubFormatException`: Exception instance

```java
public static EpubFormatException invalidOpf(String fileName, String filePath, String opfPath, String details)
```
Creates an exception for invalid OPF file.

Parameters:
- `fileName`: Filename
- `filePath`: File path
- `opfPath`: OPF file path
- `details`: Details

Returns:
- `EpubFormatException`: Exception instance

```java
public static EpubFormatException invalidNcx(String fileName, String filePath, String ncxPath, String details)
```
Creates an exception for invalid NCX file.

Parameters:
- `fileName`: Filename
- `filePath`: File path
- `ncxPath`: NCX file path
- `details`: Details

Returns:
- `EpubFormatException`: Exception instance

```java
public static EpubFormatException invalidNav(String fileName, String filePath, String navPath, String details)
```
Creates an exception for invalid NAV file.

Parameters:
- `fileName`: Filename
- `filePath`: File path
- `navPath`: NAV file path
- `details`: Details

Returns:
- `EpubFormatException`: Exception instance

### Main Methods

#### getFile()
```java
public File getFile()
```
Gets the file.

Returns:
- `File`: File

#### getDetails()
```java
public String getDetails()
```
Gets the details.

Returns:
- `String`: Details

#### getFileName()
```java
public String getFileName()
```
Gets the filename (for backward compatibility).

Returns:
- `String`: Filename

#### getFilePath()
```java
public String getFilePath()
```
Gets the file path (for backward compatibility).

Returns:
- `String`: File path

#### getOperation()
```java
public String getOperation()
```
Gets the operation type (for backward compatibility).

Returns:
- `String`: Operation type

#### getErrorCode()
```java
public Object getErrorCode()
```
Gets the error code (for backward compatibility).

Returns:
- `Object`: Error code

#### getRecoverySuggestion()
```java
public String getRecoverySuggestion()
```
Gets the recovery suggestion (for backward compatibility).

Returns:
- `String`: Recovery suggestion

## EpubPathValidationException

Path validation exception, used to prevent directory traversal attacks.

### Constructor

```java
public EpubPathValidationException(String message, String invalidPath)
```
Creates a new EpubPathValidationException instance.

Parameters:
- `message`: Exception message
- `invalidPath`: Invalid path

```java
public EpubPathValidationException(String message, String invalidPath, Throwable cause)
```
Creates a new EpubPathValidationException instance.

Parameters:
- `message`: Exception message
- `invalidPath`: Invalid path
- `cause`: Original exception

```java
public EpubPathValidationException(String message, String invalidPath, String fileName, Throwable cause)
```
Creates a new EpubPathValidationException instance.

Parameters:
- `message`: Exception message
- `invalidPath`: Invalid path
- `fileName`: Filename
- `cause`: Original exception

### Static Factory Methods

```java
public static EpubPathValidationException createForCompatibility(String message, String fileName, String filePath)
```
Creates a compatibility exception instance.

Parameters:
- `message`: Exception message
- `fileName`: Filename
- `filePath`: File path

Returns:
- `EpubPathValidationException`: Exception instance

```java
public static EpubPathValidationException createForCompatibility(String message, String fileName, String filePath, Throwable cause)
```
Creates a compatibility exception instance.

Parameters:
- `message`: Exception message
- `fileName`: Filename
- `filePath`: File path
- `cause`: Original exception

Returns:
- `EpubPathValidationException`: Exception instance

### Main Methods

#### getInvalidPath()
```java
public String getInvalidPath()
```
Gets the invalid path.

Returns:
- `String`: Invalid path

#### getFileName()
```java
public String getFileName()
```
Gets the filename (for backward compatibility).

Returns:
- `String`: Filename

#### getFilePath()
```java
public String getFilePath()
```
Gets the file path (for backward compatibility).

Returns:
- `String`: File path

#### getOperation()
```java
public String getOperation()
```
Gets the operation type (for backward compatibility).

Returns:
- `String`: Operation type

#### getErrorCode()
```java
public ErrorCode getErrorCode()
```
Gets the error code (for backward compatibility).

Returns:
- `ErrorCode`: Error code

## EpubResourceException

Resource exception, used to handle resource file access errors.

### Constructor

```java
public EpubResourceException(String message, File file)
```
Creates a new EpubResourceException instance.

Parameters:
- `message`: Exception message
- `file`: File

```java
public EpubResourceException(String message, File file, String resourcePath)
```
Creates a new EpubResourceException instance.

Parameters:
- `message`: Exception message
- `file`: File
- `resourcePath`: Resource path

```java
public EpubResourceException(String message, File file, String resourceId, String resourcePath, Throwable cause)
```
Creates a new EpubResourceException instance.

Parameters:
- `message`: Exception message
- `file`: File
- `resourceId`: Resource ID
- `resourcePath`: Resource path
- `cause`: Original exception

```java
public EpubResourceException(String message, String fileName, String resourcePath)
```
Creates a new EpubResourceException instance (for backward compatibility).

Parameters:
- `message`: Exception message
- `fileName`: Filename
- `resourcePath`: Resource path

```java
public EpubResourceException(String message, String fileName, String resourcePath, Throwable cause)
```
Creates a new EpubResourceException instance (for backward compatibility).

Parameters:
- `message`: Exception message
- `fileName`: Filename
- `resourcePath`: Resource path
- `cause`: Original exception

### Main Methods

#### getFile()
```java
public File getFile()
```
Gets the file.

Returns:
- `File`: File

#### getResourceId()
```java
public String getResourceId()
```
Gets the resource ID.

Returns:
- `String`: Resource ID

#### getResourcePath()
```java
public String getResourcePath()
```
Gets the resource path.

Returns:
- `String`: Resource path

#### getFileName()
```java
public String getFileName()
```
Gets the filename (for backward compatibility).

Returns:
- `String`: Filename

#### getFilePath()
```java
public String getFilePath()
```
Gets the file path (for backward compatibility).

Returns:
- `String`: File path

#### getOperation()
```java
public String getOperation()
```
Gets the operation type (for backward compatibility).

Returns:
- `String`: Operation type

#### getErrorCode()
```java
public Object getErrorCode()
```
Gets the error code (for backward compatibility).

Returns:
- `Object`: Error code

## EpubZipException

ZIP exception, used to handle ZIP file operation errors.

### Constructor

```java
public EpubZipException(String message, File file)
```
Creates a new EpubZipException instance.

Parameters:
- `message`: Exception message
- `file`: File

```java
public EpubZipException(String message, File file, Throwable cause)
```
Creates a new EpubZipException instance.

Parameters:
- `message`: Exception message
- `file`: File
- `cause`: Original exception

```java
public EpubZipException(String message, File file, String entryName, Throwable cause)
```
Creates a new EpubZipException instance.

Parameters:
- `message`: Exception message
- `file`: File
- `entryName`: Entry name
- `cause`: Original exception

```java
public EpubZipException(String message, String fileName, String filePath)
```
Creates a new EpubZipException instance (for backward compatibility).

Parameters:
- `message`: Exception message
- `fileName`: Filename
- `filePath`: File path

```java
public EpubZipException(String message, String fileName, String filePath, Throwable cause)
```
Creates a new EpubZipException instance (for backward compatibility).

Parameters:
- `message`: Exception message
- `fileName`: Filename
- `filePath`: File path
- `cause`: Original exception

### Main Methods

#### getFile()
```java
public File getFile()
```
Gets the file.

Returns:
- `File`: File

#### getEntryName()
```java
public String getEntryName()
```
Gets the entry name.

Returns:
- `String`: Entry name

#### getFileName()
```java
public String getFileName()
```
Gets the filename (for backward compatibility).

Returns:
- `String`: Filename

#### getFilePath()
```java
public String getFilePath()
```
Gets the file path (for backward compatibility).

Returns:
- `String`: File path

#### getOperation()
```java
public String getOperation()
```
Gets the operation type (for backward compatibility).

Returns:
- `String`: Operation type

#### getErrorCode()
```java
public Object getErrorCode()
```
Gets the error code (for backward compatibility).

Returns:
- `Object`: Error code

## Usage Example

```java
try {
    // Configure parse options
    ParseOptions options = ParseOptions.lenient()
        .withLogLevel(ParseOptions.LogLevel.INFO)
        .withContinueOnMetadataError(true)
        .withContinueOnResourceError(true);
    
    // Parse EPUB with configuration options
    EpubParser parser = new EpubParser(new File("book.epub"));
    ParseResult result = parser.parseWithOptions(new File("book.epub"), options);
    
    if (result.isSuccess() || result.isPartialSuccess()) {
        EpubBook book = result.getEpubBook();
        // Process successfully parsed book
    } else {
        // Handle parsing failure
        System.err.println("Parsing failed: " + result.getParseSummary());
    }
} catch (EpubParseException e) {
    // Handle parse exception
    System.err.println("Parse error: " + e.getMessage());
    System.err.println("Error Code: " + e.getErrorCode());
    System.err.println("Recovery Suggestion: " + e.getRecoverySuggestion());
    e.printStackTrace();
} catch (EpubXmlParseException e) {
    // Handle XML parse exception
    System.err.println("XML parse error: " + e.getMessage());
    System.err.println("File: " + e.getFileName());
    System.err.println("Line Number: " + e.getLineNumber());
    System.err.println("Column Number: " + e.getColumnNumber());
} catch (EpubFormatException e) {
    // Handle format exception
    System.err.println("Format error: " + e.getMessage());
    System.err.println("File: " + e.getFileName());
    System.err.println("Recovery Suggestion: " + e.getRecoverySuggestion());
} catch (EpubPathValidationException e) {
    // Handle path validation exception
    System.err.println("Path validation failed: " + e.getMessage() + ", Path: " + e.getFilePath());
} catch (EpubResourceException e) {
    // Handle resource exception
    System.err.println("Resource error: " + e.getMessage() + ", Path: " + e.getFilePath());
} catch (EpubZipException e) {
    // Handle ZIP exception
    System.err.println("ZIP error: " + e.getMessage() + ", File: " + e.getFileName());
} catch (Exception e) {
    // Handle other exceptions
    System.err.println("Unexpected error: " + e.getMessage());
    e.printStackTrace();
}
```