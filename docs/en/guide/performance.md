# Performance Optimization

EPUBime focuses on performance optimization and provides multiple mechanisms to ensure efficient processing of EPUB files. Here are detailed performance optimizations and best practices.

## Performance Features

### Smart Caching

EPUBime uses `EpubCacheManager` to cache parsing results, avoiding re-parsing the same content:

```java
// First parse will cache results
EpubParser parser = new EpubParser(epubFile);
EpubBook book1 = parser.parse(); // Actual parsing

// Subsequent calls will use cached results
EpubBook book2 = parser.parse(); // Retrieved from cache

// Force re-parse (skip cache)
EpubBook book3 = parser.parseWithoutCache(); // Forced re-parse
```

Advantages of caching:
- Reduces repeated file I/O operations
- Improves performance for repeated parsing
- Reduces CPU usage

### Streaming Processing

Supports streaming processing of large files, avoiding loading entire EPUB files into memory:

```java
// Streaming process chapter content
EpubParser.processHtmlChapterContent(epubFile, "chapter1.html", inputStream -> {
    // Process input stream, e.g., parse HTML content
    // inputStream will be automatically closed after use
    // Does not load entire file into memory
});

// Batch streaming process multiple chapters
List<String> chapterFiles = Arrays.asList("chapter1.html", "chapter2.html");
EpubParser.processMultipleHtmlChapters(epubFile, chapterFiles, (fileName, inputStream) -> {
    // Process each file's input stream
});
```

Advantages of streaming processing:
- Reduced memory usage
- Suitable for large file processing
- Avoids OutOfMemoryError

### Batch Operations

Supports batch reading of multiple files, reducing ZIP file operation counts:

```java
// Batch read multiple resource files
List<String> filePaths = Arrays.asList("OEBPS/chapter1.html", "OEBPS/chapter2.html");
ZipFileManager zipManager = new ZipFileManager(epubFile);
Map<String, byte[]> contents = zipManager.getMultipleFileContents(filePaths);
```

Advantages of batch operations:
- Reduces ZIP file open/close counts
- Improves I/O efficiency
- Reduces system call overhead

### Lazy Resource Loading

Resource files are loaded on-demand, not occupying unnecessary memory:

```java
// Get resource object but don't immediately load data
EpubResource resource = book.getResourceByHref("images/cover.jpg");

// Data is loaded only when needed
byte[] imageData = resource.getData(); // Data is read from ZIP file at this time
```

Advantages of lazy loading:
- Reduces initial memory footprint
- Improves application startup speed
- On-demand resource usage

## Performance Benchmarking

EPUBime integrates professional benchmarking tools JMH (Java Microbenchmark Harness) to provide precise performance measurements and comparisons with industry-standard libraries.

### Running Benchmarks

```bash
# Run professional benchmarks (recommended)
mvn exec:java -Dexec.mainClass="fun.lzwi.epubime.epub.EpubJmhBenchmark" -Dexec.classpathScope=test

# Run traditional performance tests
mvn test -Dtest=PerformanceBenchmarkTest

# Run comparison tests with epublib
mvn test -Dtest=EpubimeVsEpublibBenchmarkTest
```

### Latest Benchmark Results

In standard test environments, EPUBime outperforms epublib significantly:

#### 1. Simple Parsing Performance
- **EPUBime Average Parse Time**: **4.24ms**
- **epublib Average Parse Time**: **7.13ms**
- **Performance Improvement**: **40.5%** (EPUBime uses only 59% of epublib's time)

#### 2. Real Usage Scenario (Parse + Access)
- **EPUBime Average Time**: **3.15ms**
- **epublib Average Time**: **7.23ms**
- **Performance Improvement**: **56.5%** (EPUBime uses only 44% of epublib's time)
- **Test Content**: parsing + metadata access + chapter list + resource list

#### 3. Full Workflow Performance
- **EPUBime Average Time**: **3.18ms**
- **Test Content**: parsing + metadata access + chapter list + resource list + cover retrieval + first chapter content reading

#### 4. File Reading Performance
- mimetype file: 0.27ms
- OPF file: 0.28ms
- NCX file: 0.41ms

#### Performance Advantages Summary
1. **Parsing Speed**: ~40-56% faster (depending on usage scenario)
2. **Real-world Performance**: Performance advantage is more pronounced in actual application scenarios
3. **Memory Usage**: ~25-40% reduction
4. **Cache Efficiency**: Performance improvement of 80% or more for repeated parsing
5. **Streaming Processing**: Stable memory usage when processing large files
6. **Professional Benchmarking**: Uses JMH for precise, scientific performance measurements

## Performance Optimization Best Practices

### 1. Use Caching Appropriately

```java
// Recommended: Reuse the same parser instance
EpubParser parser = new EpubParser(epubFile);
EpubBook book1 = parser.parse(); // First parse
// ... Other operations
EpubBook book2 = parser.parse(); // Use cache

// Not recommended: Create new parser each time
EpubBook book1 = new EpubParser(epubFile).parse();
EpubBook book2 = new EpubParser(epubFile).parse(); // Repeated parsing
```

### 2. Use Streaming Processing for Large Files

```java
// Use streaming processing for large files
if (isLargeFile(epubFile)) {
    EpubParser.processHtmlChapterContent(epubFile, "large-chapter.html", inputStream -> {
        // Streaming process content
        processContentStream(inputStream);
    });
} else {
    // Small files can be loaded directly
    EpubBook book = new EpubParser(epubFile).parse();
    // Process content directly
}
```

### 3. Batch Operation Optimization

```java
// Batch get multiple resources
List<String> imagePaths = getImagePaths(book);
ZipFileManager zipManager = new ZipFileManager(epubFile);

// Get all image data in one operation
Map<String, byte[]> imageDataMap = zipManager.getMultipleFileContents(imagePaths);

// Process all image data
imageDataMap.forEach((path, data) -> {
    processImageData(path, data);
});
```

### 4. Release Resources Promptly

```java
// Use try-with-resources to ensure resources are properly released
try (ZipFileManager zipManager = new ZipFileManager(epubFile)) {
    // Perform ZIP operations
    byte[] content = zipManager.getFileContent("OEBPS/content.opf");
    // Process content
} catch (EpubZipException e) {
    // Handle exception
}
// zipManager will be automatically closed
```

### 5. Avoid Unnecessary Data Loading

```java
// Load resource data only when needed
for (EpubResource resource : book.getResources()) {
    // Check if this resource is actually needed
    if (isResourceNeeded(resource)) {
        byte[] data = resource.getData(); // Load only when needed
        processResourceData(data);
    }
}
```

## Memory Monitoring and Tuning

Memory usage can be monitored via JVM parameters:

```bash
# Enable memory monitoring
java -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:gc.log YourApplication

# Set heap memory size
java -Xms512m -Xmx2g YourApplication
```

## Performance Testing Code Example

```java
public class PerformanceTest {
    public void testParsingPerformance() {
        File epubFile = new File("large-book.epub");
        long startTime = System.currentTimeMillis();
        
        // Test first parse performance
        EpubParser parser = new EpubParser(epubFile);
        EpubBook book = parser.parse();
        long firstParseTime = System.currentTimeMillis() - startTime;
        
        // Test cached parse performance
        startTime = System.currentTimeMillis();
        EpubBook cachedBook = parser.parse();
        long cachedParseTime = System.currentTimeMillis() - startTime;
        
        System.out.println("First parse time: " + firstParseTime + "ms");
        System.out.println("Cached parse time: " + cachedParseTime + "ms");
        System.out.println("Performance improvement: " + (firstParseTime - cachedParseTime) * 100.0 / firstParseTime + "%");
    }
}
```

With these performance optimization measures, EPUBime can efficiently process EPUB files of various sizes while maintaining low memory usage and good response speed.