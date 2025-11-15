# Advanced Features

## Fluent API Usage

EPUBime provides a modern Fluent API that supports chained method calls:

```java
// Basic Fluent API usage
EpubBook book = EpubReader.fromFile(epubFile)
    .withCache(true)
    .withLazyLoading(true)
    .withParallelProcessing(true)
    .parse();

// Parse only metadata
Metadata metadata = EpubReader.fromFile(epubFile)
    .withCache(false)
    .parseMetadata();

// Parse only table of contents
List<EpubChapter> chapters = EpubReader.fromFile(epubFile)
    .parseTableOfContents();

// Get book information
EpubReader.EpubInfo info = EpubReader.fromFile(epubFile).getInfo();
System.out.println("Title: " + info.getTitle());
System.out.println("Chapter count: " + info.getChapterCount());
```

## Async Processing

EPUBime supports asynchronous processing to improve application responsiveness:

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    // Parse book asynchronously
    CompletableFuture<EpubBook> bookFuture = asyncProcessor.parseBookAsync(epubFile);
    
    // Parse metadata asynchronously
    CompletableFuture<Metadata> metadataFuture = asyncProcessor.parseMetadataAsync(epubFile);
    
    // Get book info asynchronously
    CompletableFuture<EpubReader.EpubInfo> infoFuture = asyncProcessor.getBookInfoAsync(epubFile);
    
    // Wait for all async operations to complete
    CompletableFuture.allOf(bookFuture, metadataFuture, infoFuture).join();
    
    // Get results
    EpubBook book = bookFuture.get();
    Metadata metadata = metadataFuture.get();
    EpubReader.EpubInfo info = infoFuture.get();
    
    System.out.println("Async parsing complete:");
    System.out.println("Title: " + metadata.getTitle());
    System.out.println("Chapters: " + book.getChapters().size());
    System.out.println("File size: " + info.getFileSize());
    
} finally {
    asyncProcessor.shutdown();
}
```

## Streaming Processing

For large EPUB files, streaming processing can be used to optimize memory usage:

```java
// Stream process all chapters without loading everything into memory
EpubReader.fromFile(epubFile)
    .streamChapters((chapter, inputStream) -> {
        try {
            System.out.println("Processing chapter: " + chapter.getTitle());
            
            // Read content stream (example: calculate size)
            byte[] buffer = new byte[8192];
            int bytesRead;
            int totalBytes = 0;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                totalBytes += bytesRead;
            }
            
            System.out.println("Chapter size: " + totalBytes + " bytes");
            
        } catch (IOException e) {
            System.err.println("Failed to process chapter: " + e.getMessage());
        }
    });

// Stream process specific chapter
EpubReader.fromFile(epubFile)
    .streamChapter("chapter1", inputStream -> {
        try {
            // Read complete content
            String content = readStreamContent(inputStream);
            System.out.println("Chapter 1 content length: " + content.length());
            
            // Or parse HTML with JSoup
            Document doc = Jsoup.parse(content);
            String text = doc.text();
            System.out.println("Chapter 1 text length: " + text.length());
            
        } catch (IOException e) {
            System.err.println("Failed to read chapter: " + e.getMessage());
        }
    });
```

## Enhanced API Features

### Enhanced Book Object

```java
// Use enhanced book object
EpubBook book = EpubReader.fromFile(epubFile).parse();
EpubBookEnhanced enhancedBook = new EpubBookEnhanced(book, epubFile);

// Get all chapters (including nested)
List<EpubChapter> allChapters = enhancedBook.getAllChapters();

// Find chapter by title
EpubChapter chapter = enhancedBook.findChapterByTitle("Introduction");

// Get specific type resources
List<EpubResource> images = enhancedBook.getImageResources();
List<EpubResource> cssFiles = enhancedBook.getCssResources();

// Check for cover
if (enhancedBook.hasCover()) {
    System.out.println("Book has cover image");
}
```

### Enhanced Metadata

```java
// Use enhanced metadata object
Metadata metadata = EpubReader.fromFile(epubFile).parseMetadata();
MetadataEnhanced enhancedMetadata = new MetadataEnhanced(metadata);

// Get parsed date
LocalDate date = enhancedMetadata.getParsedDate();
if (date != null) {
    System.out.println("Publication date: " + date);
}

// Check accessibility features
if (enhancedMetadata.hasAccessibilityFeatures()) {
    System.out.println("Accessibility features: " + 
            enhancedMetadata.getAccessibilityFeatures());
}

// Get summary
System.out.println("\nMetadata Summary:");
System.out.println(enhancedMetadata.getSummary());
```

## Multiple Navigation Type Support

EPUBime supports multiple navigation types, including NCX and NAV formats:

```java
// Parse NCX navigation (EPUB 2)
String ncxPath = EpubParser.getNcxPath(opfContent, "OEBPS/");
String ncxContent = EpubParser.readEpubContent(epubFile, ncxPath);
List<EpubChapter> ncxChapters = EpubParser.parseNcx(ncxContent);

// Parse NAV navigation (EPUB 3)
String navPath = EpubParser.getNavPath(opfContent, "OEBPS/");
if (navPath != null) {
    String navContent = EpubParser.readEpubContent(epubFile, navPath);
    List<EpubChapter> navChapters = EpubParser.parseNav(navContent);
    
    // Parse other navigation types
    List<EpubChapter> landmarks = EpubParser.parseNavByType(navContent, "landmarks");
    List<EpubChapter> pageList = EpubParser.parseNavByType(navContent, "page-list");
}
```

## Direct Parsing of Specific Files

Specific content within EPUB files can be parsed directly:

```java
// Directly read internal file content
String container = EpubParser.readEpubContent(epubFile, "META-INF/container.xml");
String opfContent = EpubParser.readEpubContent(epubFile, "OEBPS/content.opf");

// Parse metadata
Metadata metadata = EpubParser.parseMetadata(opfContent);

// Parse resource files
List<EpubResource> resources = EpubParser.parseResources(opfContent, "OEBPS/", epubFile);
```

## Secure Path Validation

EPUBime provides a path validation mechanism to prevent directory traversal attacks:

```java
// Use built-in path validator
try {
    String validatedPath = PathValidator.validatePath("OEBPS/chapter1.html");
} catch (EpubPathValidationException e) {
    // Handle path validation exception
    System.err.println("Path validation failed: " + e.getMessage());
}
```

## Custom ZIP Processing

Use `ZipFileManager` for advanced ZIP operations:

```java
ZipFileManager zipManager = new ZipFileManager(epubFile);
List<String> allFiles = zipManager.listAllFiles();

// Get specific file content
byte[] fileContent = zipManager.getFileContent("OEBPS/content.opf");

// Safely close resources
zipManager.close();

// Use try-with-resources statement to ensure resources are properly released
try (ZipFileManager zipManager = new ZipFileManager(epubFile)) {
    // Perform ZIP operations
    String content = new String(zipManager.getFileContent("OEBPS/toc.ncx"));
} catch (EpubZipException e) {
    // Handle ZIP-related exceptions
}
```

## Batch Operations

EPUBime supports batch operations for improved performance:

```java
// Batch read multiple resource files
List<String> filePaths = Arrays.asList("OEBPS/chapter1.html", "OEBPS/chapter2.html");
Map<String, byte[]> contents = zipManager.getMultipleFileContents(filePaths);

// Batch process multiple chapter contents
List<String> chapterFiles = Arrays.asList("chapter1.html", "chapter2.html");
EpubParser.processMultipleHtmlChapters(epubFile, chapterFiles, (fileName, inputStream) -> {
    // Process each file's input stream
});
```

## Content Processing Example

Complete example of processing HTML chapter content:

```java
// Use JSoup to parse chapter content
EpubParser.processHtmlChapterContent(epubFile, "chapter1.html", inputStream -> {
    Document doc = Jsoup.parse(inputStream, "UTF-8", "");
    
    // Extract text content
    String text = doc.text();
    
    // Extract specific elements
    Elements headings = doc.select("h1, h2, h3, h4, h5, h6");
    for (Element heading : headings) {
        System.out.println("Heading: " + heading.text());
    }
    
    // Extract image references
    Elements images = doc.select("img");
    for (Element img : images) {
        String src = img.attr("src");
        System.out.println("Image reference: " + src);
    }
});
```

## Custom Exception Handling

EPUBime provides a complete exception handling system:

```java
try {
    EpubParser parser = new EpubParser(epubFile);
    EpubBook book = parser.parse();
} catch (EpubParseException e) {
    // Parse exception: Handle errors during EPUB file parsing
    System.err.println("Parse error: " + e.getMessage());
    System.err.println("File: " + e.getFileName());
    System.err.println("Path: " + e.getPath());
} catch (EpubFormatException e) {
    // Format exception: Handle cases where EPUB format does not comply with standards
    System.err.println("Format error: " + e.getMessage());
} catch (EpubPathValidationException e) {
    // Path validation exception: Handle path validation errors
    System.err.println("Path validation error: " + e.getMessage());
} catch (EpubResourceException e) {
    // Resource exception: Handle resource file access errors
    System.err.println("Resource error: " + e.getMessage());
} catch (EpubZipException e) {
    // ZIP exception: Handle ZIP file operation errors
    System.err.println("ZIP error: " + e.getMessage());
}
```

## Batch Processing

EPUBime supports batch processing of multiple EPUB files:

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    // Multiple EPUB files
    List<File> epubFiles = Arrays.asList(
        new File("book1.epub"),
        new File("book2.epub"),
        new File("book3.epub")
    );
    
    // Process multiple books in parallel
    asyncProcessor.processMultipleBooksAsync(epubFiles, book -> {
        System.out.println("Processing book: " + book.getMetadata().getTitle());
        System.out.println("  Chapters: " + book.getChapters().size());
        System.out.println("  Resources: " + book.getResources().size());
        return book;
    }).thenAccept(books -> {
        System.out.println("Completed processing " + books.size() + " books");
    }).join();
    
    // Get chapter counts for all books
    List<CompletableFuture<Integer>> chapterCountFutures = epubFiles.stream()
        .map(asyncProcessor::getChapterCountAsync)
        .collect(Collectors.toList());
    
    CompletableFuture.allOf(chapterCountFutures.toArray(new CompletableFuture[0]))
        .thenRun(() -> {
            List<Integer> chapterCounts = chapterCountFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
            
            System.out.println("Chapter counts: " + chapterCounts);
        })
        .join();
        
} finally {
    asyncProcessor.shutdown();
}
```