# User Guide

This guide is for users of the EPUBime library.

## Basic Usage

To get started with EPUBime, you first need to add it to your project dependencies:

```xml
<dependency>
    <groupId>fun.lzwi</groupId>
    <artifactId>epubime</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Then, you can use the following code to parse an EPUB file:

```java
import fun.lzwi.epubime.api.*;
import fun.lzwi.epubime.epub.*;

File epubFile = new File("path/to/your/book.epub");
EpubBook book = EpubReader.fromFile(epubFile).parse();

// Get metadata
Metadata metadata = book.getMetadata();
System.out.println("Title: " + metadata.getTitle());
System.out.println("Author: " + metadata.getCreator());
System.out.println("Language: " + metadata.getLanguage());

// Get chapter list
List<EpubChapter> chapters = book.getChapters();
for (EpubChapter chapter : chapters) {
    System.out.println("Chapter: " + chapter.getTitle());
    System.out.println("Content Path: " + chapter.getContent());
}
```

## Advanced Features

EPUBime provides several advanced features, including:

- Custom configuration options
- Asynchronous processing
- Error handling and recovery mechanisms
- Caching mechanism
- Resource management

## Configuration Options

You can use `EpubReaderConfig` to configure parsing behavior:

```java
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)              // Enable cache
    .withLazyLoading(false)       // Disable lazy loading
    .withParallelProcessing(true); // Enable parallel processing

EpubBook book = EpubReader.fromFile(epubFile, config).parse();
```

## Exception Handling

EPUBime provides detailed exception handling mechanisms, allowing you to catch and handle various exceptions:

```java
try {
    EpubBook book = EpubReader.fromFile(epubFile).parse();
    // Process book object
} catch (EpubParseException e) {
    System.err.println("Parse error: " + e.getMessage());
    System.err.println("Error code: " + e.getErrorCode());
} catch (EpubResourceException e) {
    System.err.println("Resource error: " + e.getMessage());
} catch (Exception e) {
    System.err.println("Other error: " + e.getMessage());
}
```

## Performance Optimization

For optimal performance, it is recommended to:

1. Enable caching mechanism to avoid re-parsing the same files
2. Use parallel processing options when handling large files
3. Configure memory usage appropriately, especially when processing multiple files