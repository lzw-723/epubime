# EPUBime

[English](README_en.md) | [中文](README.md)

EPUBime is a pure Java library for parsing EPUB file format. It provides complete EPUB file parsing functionality, including metadata extraction, chapter content reading, and resource file processing. Supports
both EPUB 2 and EPUB 3 formats.

## Features

- **Single Responsibility Principle**: Strictly follows SOLID principles, each class has clear responsibilities and architecture
- **Metadata Parsing**: Extract standard EPUB metadata such as title, author, publisher, language, identifier, etc.
- **TOC Parsing**: Support for both NCX and NAV table of contents formats, intelligently selects the best format
- **Resource Extraction**: Extract all resources from EPUB files (images, CSS, styles, etc.)
- **ZIP Processing**: EPUB document parsing based on ZIP file format, safe and efficient
- **Error Handling**: Comprehensive exception handling system with detailed error information and recovery mechanisms
- **Cache Management**: Provides intelligent caching mechanism to avoid re-parsing the same content
- **Streaming Processing**: Supports streaming processing of large EPUB files to optimize memory usage
- **EPUB 2.0/3.3 Support**: Fully compatible with EPUB 2.0 and EPUB 3.3 specifications, including accessibility metadata
- **Nested Chapters**: Supports parsing of complex nested chapter structures
- **Multiple Navigation Types**: Supports landmarks, page-list, toc and other navigation types
- **Modern Fluent API**: Fluent API design with method chaining support
- **Dedicated Processor Pattern**: Specialized processors divided by functionality, improving code maintainability
- **Async Processing**: Support for asynchronous parsing and processing for better application responsiveness
- **Parallel Processing**: Support for parallel processing of multiple resources to improve efficiency
- **Resource Fallback Mechanism**: Intelligent resource fallback handling to ensure compatibility
- **Path Security Validation**: Security mechanism to prevent directory traversal attacks
- **Professional Benchmarking**: Integrated JMH (Java Microbenchmark Harness) for precise performance testing

## Quick Start

### Maven Dependency

Epubime currently uses [jitpack.io](https://www.jitpack.io) as the maven repository

#### Step 1: Add JitPack Repository

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://www.jitpack.io</url>
    </repository>
</repositories>
```

#### Step 2: Add EPUBime Dependency

```xml
<dependency>
    <groupId>com.github.lzw-723</groupId>
    <artifactId>epubime</artifactId>
    <version>Tag</version>
</dependency>
```

Visit [JitPack](https://www.jitpack.io/#lzw-723/epubime/Tag) to check the latest version and replace 'Tag' in the code

### Basic Usage

<!-- @formatter:off -->
```java
import fun.lzwi.epubime.api.*;
import fun.lzwi.epubime.epub.*;

File epubFile = new File("path/to/your/book.epub");

// Using modern Fluent API (Recommended)
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)
    .withLazyLoading(false)
    .withParallelProcessing(true);
EpubBook book = EpubReader.fromFile(epubFile, config).parse();

// Get metadata
Metadata metadata = book.getMetadata();
System.out.println("Title: " + metadata.getTitle());
System.out.println("Author: " + metadata.getCreator());
System.out.println("Language: " + metadata.getLanguage());

// Get chapter list
List<EpubChapter> chapters = book.getChapters();
for (EpubChapter chapter : chapters) {
    System.out.println("Chapter: " + chapter.getTitle());
    System.out.println("Content path: " + chapter.getContent());
}

// Get cover (using dedicated processor)
EpubResource cover = EpubBookProcessor.getCover(book);
if (cover != null) {
    byte[] coverData = cover.getData();
}

// Quick book info without full parsing
EpubReader.EpubInfo info = EpubReader.fromFile(epubFile).getInfo();
System.out.println("Book info: " + info);

// Stream processing chapters (suitable for large files)
EpubReader.fromFile(epubFile)
    .streamChapters((chapter, inputStream) -> {
        System.out.println("Processing chapter: " + chapter.getTitle());
        // Process chapter content stream
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    });

// Async processing
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();
CompletableFuture<EpubBook> futureBook = asyncProcessor.parseBookAsync(epubFile);
EpubBook asyncBook = futureBook.get(); // Wait for completion
```

For more usage examples and advanced features, please check the [full documentation](https://lzw-723.github.io/epubime/) and [practical examples](docs/practical-examples.md).

## Performance Benchmarking

EPUBime integrates professional benchmarking tools JMH (Java Microbenchmark Harness) to provide precise performance measurements and comparisons with industry-standard libraries.

### Running Benchmarks

```bash
# Run professional benchmarks
mvn exec:java -Dexec.mainClass="fun.lzwi.epubime.epub.EpubJmhBenchmark" -Dexec.classpathScope=test

# Run traditional performance tests
mvn test -Dtest=PerformanceBenchmarkTest

# Run comparison tests with epublib
mvn test -Dtest=EpubimeVsEpublibBenchmarkTest
```

### Latest Benchmark Results

In standard test environments, EPUBime outperforms epublib significantly:

#### Simple Parsing Performance

- **EPUBime**: 4.37ms vs **epublib**: 7.04ms (**37.9% performance improvement**)

#### Real Usage Scenario (Parse + Access)

- **EPUBime**: 3.72ms vs **epublib**: 7.42ms (**49.8% performance improvement**)
- Includes: parsing + metadata access + chapter list + resource list

#### Full Workflow Performance

- **EPUBime**: 3.15ms (includes: parsing + metadata + chapters + resources + cover + first chapter content reading)

#### File Reading Performance

- mimetype: 0.31ms, OPF: 0.40ms, NCX: 0.34ms

#### Memory and Cache Efficiency

- Smart caching and streaming processing, 25-40% memory usage reduction
- Performance improvement of 80% or more for repeated parsing

### Performance Advantages

1. **High-Speed Parsing**: Optimized parsing algorithms, significantly faster than traditional libraries
2. **Smart Caching**: Avoids repeated I/O operations, improves performance for repeated access
3. **Streaming Processing**: Supports large file processing with stable memory usage
4. **Batch Operations**: Reduces system calls, improves I/O efficiency
5. **Lazy Loading**: Loads resources on-demand, reduces memory footprint

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.