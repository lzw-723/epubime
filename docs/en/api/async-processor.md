# API Reference

## AsyncEpubProcessor

`AsyncEpubProcessor` is the asynchronous processor class provided by the EPUBime library, supporting asynchronous parsing and processing of EPUB files.

### Constructor

```java
public AsyncEpubProcessor()
```
Creates a new asynchronous processor instance.

### Asynchronous Parsing Methods

#### parseBookAsync()
```java
public CompletableFuture<EpubBook> parseBookAsync(File epubFile)
```
Parses EPUB file asynchronously.

Parameters:
- `epubFile`: EPUB file

Returns:
- `CompletableFuture<EpubBook>`: Asynchronous return of parsed book object

#### parseMetadataAsync()
```java
public CompletableFuture<Metadata> parseMetadataAsync(File epubFile)
```
Parses EPUB file metadata asynchronously.

Parameters:
- `epubFile`: EPUB file

Returns:
- `CompletableFuture<Metadata>`: Asynchronous return of parsed metadata object

#### getBookInfoAsync()
```java
public CompletableFuture<EpubReader.EpubInfo> getBookInfoAsync(File epubFile)
```
Gets EPUB file basic information asynchronously.

Parameters:
- `epubFile`: EPUB file

Returns:
- `CompletableFuture<EpubReader.EpubInfo>`: Asynchronous return of book basic information

### Asynchronous Processing Methods

#### processChaptersAsync()
```java
public CompletableFuture<Void> processChaptersAsync(File epubFile, BiConsumer<EpubChapter, InputStream> processor)
```
Asynchronously stream processes all chapter content.

Parameters:
- `epubFile`: EPUB file
- `processor`: Chapter content processor

Returns:
- `CompletableFuture<Void>`: Asynchronous processing completion signal

#### validateAsync()
```java
public CompletableFuture<Boolean> validateAsync(File epubFile)
```
Asynchronously validates EPUB file validity.

Parameters:
- `epubFile`: EPUB file

Returns:
- `CompletableFuture<Boolean>`: Asynchronous return of validation result

### Enhanced Object Asynchronous Loading

#### loadEnhancedBookAsync()
```java
public CompletableFuture<EpubBookEnhanced> loadEnhancedBookAsync(File epubFile)
```
Asynchronously loads enhanced book object.

Parameters:
- `epubFile`: EPUB file

Returns:
- `CompletableFuture<EpubBookEnhanced>`: Asynchronous return of enhanced book object

#### loadEnhancedMetadataAsync()
```java
public CompletableFuture<MetadataEnhanced> loadEnhancedMetadataAsync(File epubFile)
```
Asynchronously loads enhanced metadata object.

Parameters:
- `epubFile`: EPUB file

Returns:
- `CompletableFuture<MetadataEnhanced>`: Asynchronous return of enhanced metadata object

### Statistical Information Asynchronous Retrieval

#### getChapterCountAsync()
```java
public CompletableFuture<Integer> getChapterCountAsync(File epubFile)
```
Asynchronously gets chapter count.

Parameters:
- `epubFile`: EPUB file

Returns:
- `CompletableFuture<Integer>`: Asynchronous return of chapter count

#### getResourceCountAsync()
```java
public CompletableFuture<Integer> getResourceCountAsync(File epubFile)
```
Asynchronously gets resource count.

Parameters:
- `epubFile`: EPUB file

Returns:
- `CompletableFuture<Integer>`: Asynchronous return of resource count

### Batch Processing

#### processMultipleBooksAsync()
```java
public CompletableFuture<List<EpubBook>> processMultipleBooksAsync(List<File> epubFiles,
                                                                  Function<EpubBook, EpubBook> processor)
```
Asynchronously batch processes multiple EPUB files.

Parameters:
- `epubFiles`: EPUB file list
- `processor`: Book processor function

Returns:
- `CompletableFuture<List<EpubBook>>`: Asynchronous return of processed book list

### Lifecycle Management

#### shutdown()
```java
public void shutdown()
```
Shuts down the asynchronous processor and releases resources.

### Usage Examples

#### Basic Asynchronous Operations

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    // Asynchronous book parsing
    CompletableFuture<EpubBook> bookFuture = asyncProcessor.parseBookAsync(epubFile);

    // Asynchronous metadata parsing
    CompletableFuture<Metadata> metadataFuture = asyncProcessor.parseMetadataAsync(epubFile);

    // Asynchronous basic information retrieval
    CompletableFuture<EpubReader.EpubInfo> infoFuture = asyncProcessor.getBookInfoAsync(epubFile);

    // Wait for all asynchronous operations to complete
    CompletableFuture.allOf(bookFuture, metadataFuture, infoFuture).join();

    // Get results
    EpubBook book = bookFuture.get();
    Metadata metadata = metadataFuture.get();
    EpubReader.EpubInfo info = infoFuture.get();

    System.out.println("Asynchronous parsing completed:");
    System.out.println("Title: " + metadata.getTitle());
    System.out.println("Chapter count: " + book.getChapters().size());
    System.out.println("File size: " + info.getFileSize() + " bytes");

} finally {
    asyncProcessor.shutdown();
}
```

#### Asynchronous Streaming Processing

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    // Asynchronous streaming processing of all chapters
    CompletableFuture<Void> processingFuture = asyncProcessor.processChaptersAsync(
        epubFile,
        (chapter, inputStream) -> {
            try {
                System.out.println("Asynchronously processing chapter: " + chapter.getTitle());

                // Calculate chapter size
                byte[] buffer = new byte[8192];
                int bytesRead;
                int totalBytes = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    totalBytes += bytesRead;
                }

                System.out.println("Chapter '" + chapter.getTitle() + "' size: " + totalBytes + " bytes");

            } catch (IOException e) {
                System.err.println("Failed to process chapter: " + chapter.getTitle());
            }
        }
    );

    // Wait for processing to complete
    processingFuture.join();
    System.out.println("Asynchronous chapter processing completed");

} finally {
    asyncProcessor.shutdown();
}
```

#### Asynchronous Validation and Enhanced Objects

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    // Asynchronous validation
    asyncProcessor.validateAsync(epubFile)
        .thenAccept(isValid -> {
            System.out.println("EPUB validation result: " + (isValid ? "Valid" : "Invalid"));
        })
        .join();

    // Asynchronous loading of enhanced book object
    CompletableFuture<EpubBookEnhanced> enhancedBookFuture =
        asyncProcessor.loadEnhancedBookAsync(epubFile);

    EpubBookEnhanced enhancedBook = enhancedBookFuture.get();

    System.out.println("Enhanced book information:");
    System.out.println("Title: " + enhancedBook.getTitle());
    System.out.println("Total chapter count: " + enhancedBook.getChapterCount());
    System.out.println("Image resource count: " + enhancedBook.getImageResources().size());

    // Asynchronous loading of enhanced metadata
    CompletableFuture<MetadataEnhanced> enhancedMetadataFuture =
        asyncProcessor.loadEnhancedMetadataAsync(epubFile);

    MetadataEnhanced enhancedMetadata = enhancedMetadataFuture.get();

    System.out.println("Enhanced metadata information:");
    System.out.println("Summary: " + enhancedMetadata.getSummary());

    if (enhancedMetadata.hasAccessibilityFeatures()) {
        System.out.println("Accessibility features: " + enhancedMetadata.getAccessibilityFeatures());
    }

} finally {
    asyncProcessor.shutdown();
}
```

#### Batch Processing Example

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    List<File> epubFiles = Arrays.asList(
        new File("book1.epub"),
        new File("book2.epub"),
        new File("book3.epub"),
        new File("book4.epub")
    );

    // Asynchronous batch processing of multiple books
    asyncProcessor.processMultipleBooksAsync(epubFiles, book -> {
        String title = book.getMetadata().getTitle();
        int chapterCount = book.getChapters().size();
        int resourceCount = book.getResources().size();

        System.out.println("Processing completed: '" + title + "'");
        System.out.println("  Chapter count: " + chapterCount);
        System.out.println("  Resource count: " + resourceCount);

        return book;
    }).thenAccept(books -> {
        System.out.println("Batch processing completed, processed " + books.size() + " books");

        // Calculate statistics
        int totalChapters = books.stream().mapToInt(b -> b.getChapters().size()).sum();
        int totalResources = books.stream().mapToInt(b -> b.getResources().size()).sum();

        System.out.println("Total chapters: " + totalChapters);
        System.out.println("Total resources: " + totalResources);
    }).join();

} finally {
    asyncProcessor.shutdown();
}
```

#### Advanced Asynchronous Patterns

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    File epubFile = new File("large-book.epub");

    // Combine multiple asynchronous operations
    CompletableFuture<EpubBook> bookFuture = asyncProcessor.parseBookAsync(epubFile);
    CompletableFuture<Metadata> metadataFuture = asyncProcessor.parseMetadataAsync(epubFile);
    CompletableFuture<EpubReader.EpubInfo> infoFuture = asyncProcessor.getBookInfoAsync(epubFile);

    // When book parsing completes, asynchronously process its content
    CompletableFuture<Void> contentProcessingFuture = bookFuture.thenCompose(book -> {
        return asyncProcessor.processChaptersAsync(epubFile, (chapter, inputStream) -> {
            // Asynchronously process each chapter
            processChapterAsync(chapter, inputStream);
        });
    });

    // When metadata parsing completes, asynchronously validate and enhance
    CompletableFuture<Void> metadataProcessingFuture = metadataFuture.thenCompose(metadata -> {
        return asyncProcessor.loadEnhancedMetadataAsync(epubFile)
            .thenAccept(enhanced -> {
                System.out.println("Enhanced metadata summary: " + enhanced.getSummary());
            });
    });

    // Wait for all operations to complete
    CompletableFuture.allOf(
        bookFuture,
        metadataFuture,
        infoFuture,
        contentProcessingFuture,
        metadataProcessingFuture
    ).join();

    System.out.println("All asynchronous operations completed");

} finally {
    asyncProcessor.shutdown();
}

// Helper method
private void processChapterAsync(EpubChapter chapter, InputStream inputStream) {
    // Logic for asynchronously processing chapter content
    CompletableFuture.runAsync(() -> {
        try {
            // Actual chapter processing logic
            byte[] buffer = new byte[1024];
            int bytesRead;
            int totalBytes = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                totalBytes += bytesRead;
            }

            System.out.println("Asynchronously processed chapter '" + chapter.getTitle() + "': " + totalBytes + " bytes");

        } catch (IOException e) {
            System.err.println("Asynchronous chapter processing failed: " + chapter.getTitle());
        }
    });
}
```

#### Error Handling and Timeouts

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    // Set timeout
    CompletableFuture<EpubBook> bookFuture = asyncProcessor.parseBookAsync(epubFile)
        .orTimeout(30, TimeUnit.SECONDS)  // 30 second timeout
        .exceptionally(throwable -> {
            System.err.println("Parsing failed: " + throwable.getMessage());
            return null;  // Return default value
        });

    // Process result
    EpubBook book = bookFuture.get();
    if (book != null) {
        System.out.println("Successfully parsed: " + book.getMetadata().getTitle());
    } else {
        System.out.println("Parsing failed, using default handling");
    }

    // Error handling in batch processing
    List<File> epubFiles = Arrays.asList(file1, file2, file3);

    asyncProcessor.processMultipleBooksAsync(epubFiles, book -> {
        // Process each book
        return book;
    }).exceptionally(throwable -> {
        System.err.println("Batch processing error: " + throwable.getMessage());
        return Collections.emptyList();  // Return empty list
    }).thenAccept(books -> {
        System.out.println("Successfully processed " + books.size() + " books");
    }).join();

} finally {
    asyncProcessor.shutdown();
}
```

#### Custom Executor

```java
// Use custom thread pool
ExecutorService customExecutor = Executors.newFixedThreadPool(4);
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor(customExecutor);

try {
    // Use custom executor for asynchronous processing
    List<File> epubFiles = Arrays.asList(file1, file2, file3, file4, file5);

    asyncProcessor.processMultipleBooksAsync(epubFiles, book -> {
        System.out.println("Processing with custom executor: " + book.getMetadata().getTitle());
        return book;
    }).thenAccept(books -> {
        System.out.println("Custom executor processing completed: " + books.size() + " books");
    }).join();

} finally {
    asyncProcessor.shutdown();
    customExecutor.shutdown();  // Shutdown custom executor
}
```