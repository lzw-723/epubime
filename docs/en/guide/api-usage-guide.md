# User Integration Guide

This guide will help you integrate EPUBime into your project and provide detailed usage instructions.

## Project Integration

### Maven Integration

Add the following dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>fun.lzwi</groupId>
    <artifactId>epubime</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Gradle Integration

If you are using Gradle, add the following to your `build.gradle` file:

```gradle
dependencies {
    implementation 'fun.lzwi:epubime:1.0-SNAPSHOT'
}
```

## Basic Usage Flow

### 1. Create EpubReader Instance

```java
import fun.lzwi.epubime.api.*;
import fun.lzwi.epubime.epub.*;
import java.io.File;

// Create from file
File epubFile = new File("path/to/book.epub");
EpubReader reader = EpubReader.fromFile(epubFile);

// Create from input stream
InputStream inputStream = new FileInputStream("path/to/book.epub");
EpubReader reader = EpubReader.fromInputStream(inputStream);
```

### 2. Configure Parsing Options (Optional)

```java
// Use default configuration
EpubBook book = reader.parse();

// Or use custom configuration
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)              // Enable cache
    .withLazyLoading(true)        // Enable lazy loading
    .withParallelProcessing(true); // Enable parallel processing

EpubBook book = reader.parse(config);
```

### 3. Access Book Content

```java
// Get metadata
Metadata metadata = book.getMetadata();
System.out.println("Title: " + metadata.getTitle());
System.out.println("Author: " + metadata.getCreator());
System.out.println("Language: " + metadata.getLanguage());
System.out.println("Publication Date: " + metadata.getDate());

// Get chapter list
List<EpubChapter> chapters = book.getChapters();
for (EpubChapter chapter : chapters) {
    System.out.println("Chapter: " + chapter.getTitle());
    System.out.println("Content Path: " + chapter.getContent());
    
    // Get chapter content (if lazy loading is enabled, this will trigger actual loading)
    String content = chapter.getContentAsString();
}

// Get resource files
List<EpubResource> resources = book.getResources();
for (EpubResource resource : resources) {
    System.out.println("Resource: " + resource.getHref());
    System.out.println("Media Type: " + resource.getMediaType());
    
    // Get resource data
    byte[] data = resource.getData();
}
```

## Advanced Feature Usage

### Asynchronous Processing

EPUBime supports asynchronous parsing to avoid blocking the main thread:

```java
import fun.lzwi.epubime.api.AsyncEpubProcessor;

AsyncEpubProcessor processor = new AsyncEpubProcessor();
CompletableFuture<EpubBook> future = processor.parseAsync(epubFile);

future.thenAccept(book -> {
    // Process book object after parsing completes
    System.out.println("Book parsing complete: " + book.getMetadata().getTitle());
}).exceptionally(throwable -> {
    // Handle parsing exceptions
    System.err.println("Parsing failed: " + throwable.getMessage());
    return null;
});
```

### Error Handling and Recovery

EPUBime provides flexible error handling mechanisms:

```java
try {
    // Use lenient mode to allow partial error recovery
    ParseOptions options = ParseOptions.lenient()
        .withContinueOnMetadataError(true)
        .withContinueOnResourceError(true);
    
    ParseResult result = reader.parseWithOptions(options);
    
    if (result.isSuccess()) {
        // Complete success
        EpubBook book = result.getEpubBook();
    } else if (result.isPartialSuccess()) {
        // Partial success with warning messages
        EpubBook book = result.getEpubBook();
        ErrorContext errorContext = result.getErrorContext();
        System.out.println("Parsing completed with warnings: " + errorContext.getStatistics());
    } else {
        // Parsing failure
        ErrorContext errorContext = result.getErrorContext();
        System.err.println("Parsing failed: " + errorContext.generateReport());
    }
} catch (EpubParseException e) {
    System.err.println("Parse exception: " + e.getMessage());
    System.err.println("Error code: " + e.getErrorCode());
    System.err.println("Recovery suggestion: " + e.getRecoverySuggestion());
}
```

### Caching Mechanism

To improve performance, EPUBime provides a built-in caching mechanism:

```java
// Enable cache
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true);

// Or use custom cache configuration
EpubCacheManager cacheManager = EpubCacheManager.getInstance();
cacheManager.setMaxCacheSize(100); // Maximum cache of 100 books
cacheManager.setCacheTimeout(3600000); // Cache timeout of 1 hour

EpubBook book = reader.parse(config);
```

## Performance Optimization Suggestions

### 1. Memory Management

For large files or batch processing, it is recommended to configure memory appropriately:

```java
EpubReaderConfig config = new EpubReaderConfig()
    .withLazyLoading(true)        // Enable lazy loading
    .withParallelProcessing(false); // Disable parallel processing under memory constraints
```

### 2. Batch Processing

When processing multiple files, use caching and appropriate resource management:

```java
EpubCacheManager cacheManager = EpubCacheManager.getInstance();
cacheManager.clear(); // Clear cache before batch processing

List<File> epubFiles = getEpubFiles(); // Get EPUB file list
List<EpubBook> books = new ArrayList<>();

for (File file : epubFiles) {
    try {
        EpubBook book = EpubReader.fromFile(file).parse();
        books.add(book);
        
        // Periodic cleanup to release memory
        if (books.size() % 10 == 0) {
            System.gc(); // Suggest garbage collection
        }
    } catch (Exception e) {
        System.err.println("Failed to process file: " + file.getName() + " - " + e.getMessage());
    }
}
```

### 3. Resource Release

Ensure proper resource release after use:

```java
EpubBook book = reader.parse();
try {
    // Use book object
    processBook(book);
} finally {
    // Clean up resources
    if (book != null) {
        book.close(); // Release related resources
    }
}
```

## FAQ

### Q: How to handle protected EPUB files?
A: EPUBime currently does not support DRM-protected EPUB files. You need to remove DRM protection before parsing.

### Q: What to do if memory shortage error occurs when parsing large files?
A: It is recommended to enable lazy loading and caching mechanisms, and adjust JVM memory parameters appropriately.

### Q: How to get the actual content of chapters?
A: Use the `chapter.getContentAsString()` method to get the HTML content of the chapter.

### Q: What EPUB 3 features are supported?
A: EPUBime supports most features of the EPUB 3.3 specification, including navigation documents, media overlays, fixed layouts, etc.

## API Reference

For detailed API documentation, please refer to [API Reference](/en/api/).