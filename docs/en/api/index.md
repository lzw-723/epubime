# API Reference

This section provides complete API reference documentation for the EPUBime library. EPUBime is a pure Java library for parsing EPUB file format, supporting both EPUB 2 and EPUB 3 formats. The library strictly follows the Single Responsibility Principle (SRP).

## Modern Fluent API (Recommended)

- [EpubReader](/en/api/epub-reader) - Modern Fluent API focused on API coordination and user interaction
- [EpubReaderConfig](/en/api/epub-reader-config) - EpubReader configuration class managing parsing options
- [AsyncEpubProcessor](/en/api/async-processor) - Asynchronous processor supporting async parsing and processing

## Core Data Models

- [EpubBook](/en/api/epub-book) - EPUB book data model storing book information
- [Metadata](/en/api/metadata) - EPUB book metadata information
- [EpubChapter](/en/api/epub-chapter) - Chapters in EPUB books
- [EpubResource](/en/api/epub-resource) - Resource files in EPUB books

## Dedicated Processor Classes

- [EpubParser](/en/api/epub-parser) - EPUB file parser focused on parsing logic
- [EpubFileReader](/en/api/epub-file-reader) - File reader providing secure file reading
- [EpubStreamProcessor](/en/api/epub-stream-processor) - Stream processor for memory-optimized operations
- [EpubBookProcessor](/en/api/epub-book-processor) - Book processor handling book business logic

## Enhanced Feature Classes

- [EpubBookEnhanced](/en/api/epub-book-enhanced) - Enhanced book object providing more convenient methods
- [MetadataEnhanced](/en/api/metadata-enhanced) - Enhanced metadata object supporting more metadata operations

## Exception Handling

- [Exception Classes](/en/api/exceptions) - EPUBime's exception class hierarchy, all exceptions inherit from BaseEpubException

## Overview

EPUBime provides a modernized API design that strictly follows the Single Responsibility Principle:

### Modern Fluent API (Recommended)
Use `EpubReader` and `EpubReaderConfig` for configurable parsing:

```java
// Use default configuration
EpubBook book = EpubReader.fromFile(epubFile).parse();

// Use custom configuration
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)
    .withLazyLoading(false)
    .withParallelProcessing(true);
EpubBook book = EpubReader.fromFile(epubFile, config).parse();
```

### Dedicated Processor Pattern
According to the Single Responsibility Principle, different operations use dedicated processors:

```java
// File reading
EpubFileReader fileReader = new EpubFileReader(epubFile);
String content = fileReader.readContent("mimetype");

// Book processing
EpubResource cover = EpubBookProcessor.getCover(book);

// Stream processing
EpubStreamProcessor streamProcessor = new EpubStreamProcessor(epubFile);
streamProcessor.processBookChapters(book, (chapter, inputStream) -> {
    // Process chapter content
});
```

### Traditional Parsing API
Use `EpubParser` for core parsing:

```java
EpubParser parser = new EpubParser(epubFile);
EpubBook book = parser.parse();
```

### Async Processing
Use `AsyncEpubProcessor` for asynchronous operations:

```java
AsyncEpubProcessor processor = new AsyncEpubProcessor();
EpubReaderConfig config = new EpubReaderConfig().withCache(true);
CompletableFuture<EpubBook> future = processor.parseBookAsync(epubFile, true, false);
```

Select a class name from the left navigation bar to view detailed API documentation.