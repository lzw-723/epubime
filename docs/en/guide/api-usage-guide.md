# API Usage Guide

## Overview

EPUBime now provides a more modern and fluent API design while maintaining full backward compatibility with existing code. The new API greatly improves the development experience through fluent patterns, asynchronous processing, and enhanced utility methods.

## Quick Start

### Basic Usage

```java
import fun.lzwi.epubime.api.EpubReader;
import fun.lzwi.epubime.epub.EpubBook;

// Simple parsing
EpubBook book = EpubReader.fromFile("book.epub").parse();

// Get basic information
System.out.println("Title: " + book.getMetadata().getTitle());
System.out.println("Author: " + book.getMetadata().getCreator());
System.out.println("Chapter count: " + book.getChapters().size());
```

### Fluent API

```java
// Chain configuration
EpubBook book = EpubReader.fromFile(new File("book.epub"))
    .withCache(true)
    .withLazyLoading(true)
    .parse();

// Quick information retrieval
EpubReader.EpubInfo info = EpubReader.fromFile("book.epub").getInfo();
System.out.println("Book title: " + info.getTitle());
System.out.println("File size: " + info.getFileSize() + " bytes");
```

## Enhanced Features

### 1. Enhanced EpubBook

```java
import fun.lzwi.epubime.api.EpubBookEnhanced;

EpubBook book = EpubReader.fromFile("book.epub").parse();
EpubBookEnhanced enhanced = new EpubBookEnhanced(book, epubFile);

// Convenient access
String title = enhanced.getTitle();
String author = enhanced.getAuthor();

// Smart search
EpubChapter chapter = enhanced.findChapterByTitle("Chapter 1");
List<EpubChapter> chapters = enhanced.findChaptersByContentPattern(".html");

// Resource categorization
List<EpubResource> images = enhanced.getImageResources();
List<EpubResource> cssFiles = enhanced.getCssResources();
```

### 2. Enhanced Metadata

```java
import fun.lzwi.epubime.api.MetadataEnhanced;

Metadata metadata = EpubReader.fromFile("book.epub").parseMetadata();
MetadataEnhanced enhanced = new MetadataEnhanced(metadata);

// Type-safe access
String title = enhanced.getTitle();
LocalDate date = enhanced.getParsedDate(); // Automatic date parsing

// Convenient checks
boolean hasCover = enhanced.hasCover();
boolean hasAccessibility = enhanced.hasAccessibilityFeatures();

// Formatted summary
String summary = enhanced.getSummary();
```

### 3. Asynchronous Processing

```java
import fun.lzwi.epubime.api.AsyncEpubProcessor;

AsyncEpubProcessor processor = new AsyncEpubProcessor();

// Asynchronous parsing
CompletableFuture<EpubBook> bookFuture = processor.parseBookAsync(epubFile);
bookFuture.thenAccept(book -> {
    System.out.println("Asynchronous parsing completed: " + book.getMetadata().getTitle());
});

// Asynchronous metadata
CompletableFuture<Metadata> metadataFuture = processor.parseMetadataAsync(epubFile);

// Batch processing
List<File> files = Arrays.asList(file1, file2, file3);
processor.processMultipleBooksAsync(files, book -> {
    // Process each book
    return book;
});
```

### 4. Streaming Processing

```java
// Stream processing chapters (memory efficient)
EpubReader.fromFile(epubFile)
    .streamChapters((chapter, inputStream) -> {
        System.out.println("Processing chapter: " + chapter.getTitle());
        // Real-time content processing without loading everything into memory
        processContentStream(inputStream);
    });

// Stream processing specific chapter
EpubReader.fromFile(epubFile)
    .streamChapter("chapter1", inputStream -> {
        String content = readStreamContent(inputStream);
        System.out.println("Chapter content length: " + content.length());
    });
```

## Practical Application Examples

### 1. Mobile Application Development

```java
// Quick book information retrieval (suitable for list display)
public CompletableFuture<BookInfo> getBookInfoAsync(String filePath) {
    return AsyncEpubProcessor()
        .getBookInfoAsync(new File(filePath))
        .thenApply(info -> new BookInfo(
            info.getTitle(),
            info.getAuthor(),
            info.getChapterCount()
        ));
}

// Stream processing large files
public void processLargeBook(File epubFile) {
    EpubReader.fromFile(epubFile)
        .streamChapters((chapter, stream) -> {
            // Process chapter by chapter to avoid memory overflow
            String content = extractText(stream);
            saveChapterContent(chapter.getTitle(), content);
        });
}
```

### 2. Web Application Development

```java
// REST API endpoint
@GetMapping("/api/books/{id}/info")
public ResponseEntity<BookInfo> getBookInfo(@PathVariable String id) {
    try {
        File bookFile = getBookFile(id);
        EpubReader.EpubInfo info = EpubReader.fromFile(bookFile).getInfo();
        return ResponseEntity.ok(new BookInfo(info));
    } catch (EpubParseException e) {
        return ResponseEntity.badRequest().build();
    }
}

// Batch processing uploaded files
@PostMapping("/api/books/batch")
public CompletableEntity<List<UploadResult>> batchUpload(@RequestParam("files") MultipartFile[] files) {
    List<File> epubFiles = saveUploadedFiles(files);
    
    return AsyncEpubProcessor()
        .processMultipleBooksAsync(epubFiles, book -> {
            // Process each book
            saveBookMetadata(book);
            return book;
        })
        .thenApply(results -> ResponseEntity.ok(createUploadResults(results)));
}
```

### 3. Desktop Application Development

```java
// Background processing without blocking UI
public void processBooksInBackground(List<File> files) {
    AsyncEpubProcessor processor = new AsyncEpubProcessor();
    
    processor.processMultipleBooksAsync(files, book -> {
        // Update UI (needs to be executed in UI thread)
        Platform.runLater(() -> {
            updateProgress(book.getMetadata().getTitle());
        });
        return book;
    })
    .thenRun(() -> {
        Platform.runLater(() -> {
            showCompletionDialog();
        });
    });
}

// Quick preview
public void showBookPreview(File epubFile) {
    try {
        // Quick basic information retrieval
        EpubReader.EpubInfo info = EpubReader.fromFile(epubFile).getInfo();
        
        previewTitle.setText(info.getTitle());
        previewAuthor.setText(info.getAuthor());
        previewChapterCount.setText(String.valueOf(info.getChapterCount()));
        
        // Get cover (if exists)
        EpubResource cover = EpubReader.fromFile(epubFile).getCover();
        if (cover != null) {
            Image coverImage = new Image(new ByteArrayInputStream(cover.getData()));
            previewCover.setImage(coverImage);
        }
    } catch (EpubParseException e) {
        showErrorDialog("Unable to parse EPUB file");
    }
}
```

## Performance Optimization Recommendations

### 1. Memory Usage Optimization

```java
// Use streaming processing for large files
EpubReader.fromFile(largeEpubFile)
    .withLazyLoading(true)  // Lazy loading
    .streamChapters(processor);  // Streaming processing

// Use asynchronous processing for batch operations
AsyncEpubProcessor processor = new AsyncEpubProcessor();
processor.processMultipleBooksAsync(files, processingFunction);
```

### 2. Speed Optimization

```java
// Enable caching to avoid repeated parsing
EpubBook book = EpubReader.fromFile(epubFile)
    .withCache(true)
    .parse();

// Parallel processing of multiple resources
EpubReader.fromFile(epubFile)
    .withParallelProcessing(true)
    .processResources(resourceProcessor);
```

### 3. Resource Management

```java
// Close asynchronous processor promptly
try (AsyncEpubProcessor processor = new AsyncEpubProcessor()) {
    // Use processor
    processor.parseBookAsync(epubFile)
        .thenAccept(this::processBook)
        .join();
} // Auto-close

// Configure thread pool reasonably
ExecutorService customExecutor = Executors.newFixedThreadPool(4);
AsyncEpubProcessor processor = new AsyncEpubProcessor(customExecutor);
```

## Error Handling

### 1. Asynchronous Operation Error Handling

```java
processor.parseBookAsync(epubFile)
    .exceptionally(throwable -> {
        System.err.println("Parsing failed: " + throwable.getMessage());
        return null;
    })
    .thenAccept(book -> {
        if (book != null) {
            processBook(book);
        }
    });
```

### 2. Streaming Processing Error Handling

```java
try {
    EpubReader.fromFile(epubFile).streamChapters((chapter, stream) -> {
        try {
            processChapter(chapter, stream);
        } catch (IOException e) {
            System.err.println("Chapter processing failed: " + chapter.getTitle());
        }
    });
} catch (EpubParseException e) {
    System.err.println("EPUB parsing failed: " + e.getMessage());
}
```

## Migration Guide

### Migrating from Old API

#### Basic Parsing (remains unchanged)
```java
// Old code - still valid
EpubParser parser = new EpubParser(epubFile);
EpubBook book = parser.parse();

// New code - more concise
EpubBook book = EpubReader.fromFile(epubFile).parse();
```

#### Enhanced Features (new additions)
```java
// New functionality
MetadataEnhanced enhancedMetadata = new MetadataEnhanced(book.getMetadata());
LocalDate parsedDate = enhancedMetadata.getParsedDate();

// New asynchronous support
AsyncEpubProcessor processor = new AsyncEpubProcessor();
CompletableFuture<EpubBook> future = processor.parseBookAsync(epubFile);
```

## Best Practices

1. **Choose appropriate API level**: Use `EpubReader` for simple scenarios, enhanced classes for complex processing
2. **Performance optimization**: Use streaming processing for large files, asynchronous operations for batch processing
3. **Error handling**: Properly handle exceptions in asynchronous operations, properly handle I/O exceptions in streaming processing
4. **Resource management**: Close asynchronous processors promptly, configure thread pools reasonably
5. **Memory management**: Release large objects promptly, use lazy loading to reduce memory usage

## Summary

The new EPUBime API provides:

- 🚀 **More concise syntax**: Fluent API design
- ⚡ **Better performance**: Streaming processing and asynchronous operations
- 🔧 **Richer functionality**: Enhanced utility methods
- 🛡️ **Type safety**: Reduces runtime errors
- 🔄 **Full backward compatibility**: Existing code requires no modification

Whether developing mobile applications, web applications, or desktop applications, the new API can provide better development experience and performance.