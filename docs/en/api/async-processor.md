# AsyncEpubProcessor

`AsyncEpubProcessor` is an asynchronous processor in the EPUBime library that provides non-blocking EPUB processing operations. This class allows users to execute various EPUB parsing and processing tasks in background threads, avoiding blocking the main thread.

## Class Definition

```java
public class AsyncEpubProcessor
```

## Constructors

### AsyncEpubProcessor()
Creates an asynchronous processor with the default executor (cached thread pool).

### AsyncEpubProcessor(Executor executor)
Creates an asynchronous processor with a custom executor.

**Parameters:**
- `executor`: The executor to use for asynchronous operations

## Methods

### parseBookAsync(File epubFile)
Asynchronously parses an EPUB file.

**Parameters:**
- `epubFile`: The EPUB file to parse

**Returns:**
- `CompletableFuture<EpubBook>`: CompletableFuture containing the parsed EpubBook

### parseBookAsync(File epubFile, boolean useCache, boolean lazyLoading)
Asynchronously parses an EPUB file with specified cache and lazy loading options.

**Parameters:**
- `epubFile`: The EPUB file to parse
- `useCache`: Whether to use caching
- `lazyLoading`: Whether to use lazy loading

**Returns:**
- `CompletableFuture<EpubBook>`: CompletableFuture containing the parsed EpubBook

### parseMetadataAsync(File epubFile)
Asynchronously parses the metadata of an EPUB file.

**Parameters:**
- `epubFile`: The EPUB file to parse

**Returns:**
- `CompletableFuture<Metadata>`: CompletableFuture containing the metadata

### parseTableOfContentsAsync(File epubFile)
Asynchronously parses the table of contents of an EPUB file.

**Parameters:**
- `epubFile`: The EPUB file to parse

**Returns:**
- `CompletableFuture<List<EpubChapter>>`: CompletableFuture containing the list of chapters

### getBookInfoAsync(File epubFile)
Asynchronously gets basic information about an EPUB file.

**Parameters:**
- `epubFile`: The EPUB file to analyze

**Returns:**
- `CompletableFuture<EpubReader.EpubInfo>`: CompletableFuture containing basic EPUB information

### processChaptersAsync(File epubFile, BiConsumer&lt;EpubChapter, InputStream&gt; processor)
Asynchronously processes chapters using stream processing.

**Parameters:**
- `epubFile`: The EPUB file to process
- `processor`: Processor for each chapter

**Returns:**
- `CompletableFuture<Void>`: CompletableFuture that completes when all chapters are processed

### processChapterAsync(File epubFile, String chapterId, Consumer&lt;InputStream&gt; processor)
Asynchronously processes a specific chapter.

**Parameters:**
- `epubFile`: The EPUB file
- `chapterId`: The ID of the chapter to process
- `processor`: Processor for the chapter content

**Returns:**
- `CompletableFuture<Void>`: CompletableFuture that completes when the chapter is processed

### processResourcesAsync(File epubFile, Function&lt;EpubResource, Void&gt; processor)
Asynchronously processes all resources.

**Parameters:**
- `epubFile`: The EPUB file
- `processor`: Function to process each resource

**Returns:**
- `CompletableFuture<Void>`: CompletableFuture that completes when processing is done

### getCoverAsync(File epubFile)
Asynchronously gets the cover resource.

**Parameters:**
- `epubFile`: The EPUB file

**Returns:**
- `CompletableFuture<EpubResource>`: CompletableFuture containing the cover resource, or null if not found

### getResourceAsync(File epubFile, String resourceId)
Asynchronously gets a specific resource.

**Parameters:**
- `epubFile`: The EPUB file
- `resourceId`: The resource ID

**Returns:**
- `CompletableFuture<EpubResource>`: CompletableFuture containing the resource, or null if not found

### validateAsync(File epubFile)
Asynchronously validates an EPUB file.

**Parameters:**
- `epubFile`: The EPUB file to validate

**Returns:**
- `CompletableFuture<Boolean>`: CompletableFuture containing the validation result

### loadEnhancedBookAsync(File epubFile)
Asynchronously loads an enhanced book object.

**Parameters:**
- `epubFile`: The EPUB file

**Returns:**
- `CompletableFuture<EpubBookEnhanced>`: CompletableFuture containing the enhanced book object

### loadEnhancedMetadataAsync(File epubFile)
Asynchronously loads enhanced metadata.

**Parameters:**
- `epubFile`: The EPUB file

**Returns:**
- `CompletableFuture<MetadataEnhanced>`: CompletableFuture containing the enhanced metadata

### processMultipleBooksAsync(List&lt;File&gt; epubFiles, Function&lt;EpubBook, EpubBook&gt; processor)
Processes multiple EPUB files in parallel.

**Parameters:**
- `epubFiles`: List of EPUB files to process
- `processor`: Function to process each book

**Returns:**
- `CompletableFuture<List<EpubBook>>`: CompletableFuture containing the list of processed books

### getChapterCountAsync(File epubFile)
Asynchronously gets the chapter count.

**Parameters:**
- `epubFile`: The EPUB file

**Returns:**
- `CompletableFuture<Integer>`: CompletableFuture containing the chapter count

### getResourceCountAsync(File epubFile)
Asynchronously gets the resource count.

**Parameters:**
- `epubFile`: The EPUB file

**Returns:**
- `CompletableFuture<Integer>`: CompletableFuture containing the resource count

### shutdown()
Shuts down the processor and releases resources.

## Usage Examples

```java
// Create asynchronous processor
AsyncEpubProcessor processor = new AsyncEpubProcessor();

// Asynchronously parse book
CompletableFuture<EpubBook> futureBook = processor.parseBookAsync(new File("book.epub"));
futureBook.thenAccept(book -> {
    System.out.println("Parsing completed: " + book.getMetadata().getTitle());
}).exceptionally(throwable -> {
    System.err.println("Parsing failed: " + throwable.getMessage());
    return null;
});

// Asynchronously get cover
CompletableFuture<EpubResource> futureCover = processor.getCoverAsync(new File("book.epub"));
futureCover.thenAccept(cover -> {
    if (cover != null) {
        System.out.println("Cover found: " + cover.getHref());
    }
});

// Asynchronously process multiple books
List<File> epubFiles = Arrays.asList(new File("book1.epub"), new File("book2.epub"));
CompletableFuture<List<EpubBook>> futureBooks = processor.processMultipleBooksAsync(epubFiles, 
    book -> {
        // Process each book
        return book;
    });
futureBooks.thenAccept(books -> {
    System.out.println("Processed " + books.size() + " books");
});

// Shutdown processor when done
processor.shutdown();
```

## Notes

- When using the asynchronous processor, ensure that the processor is shut down before the application ends to release thread resources.
- The results of asynchronous operations are provided through CompletableFuture, which can be followed up with methods like thenAccept and thenApply.
- For long-running asynchronous operations, it is recommended to use appropriate thread pool configurations.