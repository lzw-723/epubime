# Exception Handling

EPUBime provides a comprehensive exception handling mechanism, with all exceptions inheriting from the `EpubException` base class. Here are detailed explanations and usage of each exception class.

## Exception Hierarchy

```
EpubException
├── EpubParseException      (Parse Exception)
├── EpubFormatException     (Format Exception)
├── EpubPathValidationException (Path Validation Exception)
├── EpubResourceException   (Resource Exception)
└── EpubZipException        (ZIP Exception)
```

## EpubParseException

Parse exception, used to handle errors during EPUB file parsing.

```java
try {
    EpubBook book = parser.parse();
} catch (EpubParseException e) {
    System.err.println("Parse error: " + e.getMessage());
    System.err.println("File: " + e.getFileName());
    System.err.println("Path: " + e.getPath());
    
    // Get the original exception (if any)
    Throwable cause = e.getCause();
    if (cause != null) {
        System.err.println("Cause: " + cause.getMessage());
    }
}
```

### Common Scenarios
- Corrupted or incorrectly formatted EPUB files
- Internal file format errors (such as XML format errors)
- Unprocessable data encountered during parsing

## EpubFormatException

Format exception, used to handle cases where EPUB format does not comply with specifications.

```java
try {
    Metadata metadata = EpubParser.parseMetadata(opfContent);
} catch (EpubFormatException e) {
    System.err.println("Format error: " + e.getMessage());
    System.err.println("File: " + e.getFileName());
    System.err.println("Path: " + e.getPath());
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
    System.err.println("Invalid path: " + e.getPath());
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
    System.err.println("Resource path: " + e.getPath());
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
        try {
            EpubParser parser = new EpubParser(epubFile);
            EpubBook book = parser.parse();
            
            // Process book content
            processBook(book);
            
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
        System.err.println("Parse failed - File: " + e.getFileName() + ", Error: " + e.getMessage());
    }
    
    private void handleFormatException(EpubFormatException e) {
        System.err.println("Format error - File: " + e.getFileName() + ", Error: " + e.getMessage());
    }
    
    private void handlePathValidationException(EpubPathValidationException e) {
        System.err.println("Path validation failed - Path: " + e.getPath() + ", Error: " + e.getMessage());
    }
    
    private void handleResourceException(EpubResourceException e) {
        System.err.println("Resource access failed - Path: " + e.getPath() + ", Error: " + e.getMessage());
    }
    
    private void handleZipException(EpubZipException e) {
        System.err.println("ZIP operation failed - File: " + e.getFileName() + ", Error: " + e.getMessage());
    }
    
    private void processBook(EpubBook book) {
        // Actual logic for processing book content
        System.out.println("Processing book: " + book.getMetadata().getTitle());
    }
}
```

## Best Practices

1. **Specific Exception Handling**: Prioritize catching specific exception types rather than generic exceptions
2. **Error Information Logging**: Log detailed error information, including filenames, paths, etc.
3. **Resource Cleanup**: Ensure proper cleanup of resources in case of exceptions (such as closing file streams)
4. **User-Friendly**: Provide meaningful error information to users rather than technical details
5. **Logging**: Use logging frameworks to record exceptions for debugging and monitoring

Exception handling is an important part of ensuring application stability and security. EPUBime's exception system provides detailed information to help developers quickly locate and resolve problems.