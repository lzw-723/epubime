# API Reference

This section provides complete API reference documentation for the EPUBime library. EPUBime is a pure Java library for parsing EPUB file format, supporting both EPUB 2 and EPUB 3 formats.

## Modern API (Recommended)

- [EpubReader](/en/api/epub-reader) - Modern Fluent API with support for chained method calls and advanced features
- [AsyncEpubProcessor](/en/api/async-processor) - Asynchronous processor supporting async parsing and processing

## Traditional Core Classes

- [EpubParser](/en/api/epub-parser) - EPUB file parser, responsible for parsing EPUB files and generating EpubBook objects
- [EpubBook](/en/api/epub-book) - Represents a parsed EPUB book, containing metadata, chapters, and resources
- [Metadata](/en/api/metadata) - Represents metadata information of an EPUB book
- [EpubChapter](/en/api/epub-chapter) - Represents a chapter in an EPUB book
- [EpubResource](/en/api/epub-resource) - Represents a resource file in an EPUB book

## Enhanced Feature Classes

- [EpubBookEnhanced](/en/api/epub-book-enhanced) - Enhanced book object providing more convenient methods
- [MetadataEnhanced](/en/api/metadata-enhanced) - Enhanced metadata object supporting more metadata operations

## Exception Handling

- [Exception Classes](/en/api/exceptions) - EPUBime's exception class hierarchy, all exceptions inherit from EpubException

## Overview

EPUBime provides two sets of APIs:

### Modern Fluent API (Recommended)
Use the Fluent API provided by `EpubReader` with support for chained method calls:

```java
EpubBook book = EpubReader.fromFile(epubFile)
    .withCache(true)
    .withLazyLoading(true)
    .parse();
```

### Traditional API
Use `EpubParser` for traditional parsing approach:

```java
EpubParser parser = new EpubParser(epubFile);
EpubBook book = parser.parse();
```

### Async Processing
Use `AsyncEpubProcessor` for asynchronous operations:

```java
AsyncEpubProcessor processor = new AsyncEpubProcessor();
CompletableFuture<EpubBook> future = processor.parseBookAsync(epubFile);
```

Select a class name from the left navigation bar to view detailed API documentation.