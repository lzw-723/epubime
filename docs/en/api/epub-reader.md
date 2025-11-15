# API Reference

## EpubReader

`EpubReader` is the modern Fluent API class provided by the EPUBime library, supporting method chaining and advanced features. It follows the single responsibility principle, focusing on API coordination and user interaction.

### Creating Instances

```java
// Create from File object (with default config)
public static EpubReader fromFile(File epubFile)

// Create from file path (with default config)
public static EpubReader fromFile(String filePath)

// Create from File object (with custom config)
public static EpubReader fromFile(File epubFile, EpubReaderConfig config)
```

Parameters:
- `epubFile`: EPUB file object
- `filePath`: EPUB file path
- `config`: EpubReaderConfig configuration object

Returns:
- `EpubReader`: EpubReader instance

Throws:
- `IllegalArgumentException`: If file parameter or config parameter is null

### Parsing Methods

#### parse()
```java
public EpubBook parse() throws EpubParseException
```
Parses the EPUB file and returns an EpubBook object.

Returns:
- `EpubBook`: Parsed EPUB book object

Throws:
- `EpubParseException`: Error during parsing

#### parseMetadata()
```java
public Metadata parseMetadata() throws EpubParseException
```
Parses only metadata without parsing the complete book content.

Returns:
- `Metadata`: Parsed metadata object

Throws:
- `EpubParseException`: Error during parsing

#### parseTableOfContents()
```java
public List<EpubChapter> parseTableOfContents() throws EpubParseException
```
Parses only the table of contents structure without parsing the complete book content.

Returns:
- `List<EpubChapter>`: List of chapters

Throws:
- `EpubParseException`: Error during parsing

### Streaming Processing Methods

#### streamChapters()
```java
public void streamChapters(BiConsumer<EpubChapter, InputStream> processor) throws EpubParseException
```
Stream processes all chapter content to avoid loading the entire file into memory.

Parameters:
- `processor`: Chapter content processor that receives chapter object and input stream

Throws:
- `EpubParseException`: Error during processing

#### streamChapter()
```java
public void streamChapter(String chapterId, Consumer<InputStream> processor) throws EpubParseException
```
Stream processes specific chapter content.

Parameters:
- `chapterId`: Chapter ID
- `processor`: Content processor that receives input stream

Throws:
- `EpubParseException`: Error during processing

### Resource Processing Methods

#### processResources()
```java
public void processResources(Function<EpubResource, Void> processor) throws EpubParseException
```
Processes all resource files.

Parameters:
- `processor`: Resource processor function

Throws:
- `EpubParseException`: Error during processing

#### getResource()
```java
public EpubResource getResource(String resourceId) throws EpubParseException
```
Gets a specific resource.

Parameters:
- `resourceId`: Resource ID

Returns:
- `EpubResource`: Resource object, or null if not found

Throws:
- `EpubParseException`: Error during parsing

#### getCover()
```java
public EpubResource getCover() throws EpubParseException
```
Gets the cover resource.

Returns:
- `EpubResource`: Cover resource object, or null if not found

Throws:
- `EpubParseException`: Error during parsing

### Validation and Utility Methods

#### isValid()
```java
public boolean isValid()
```
Checks if the EPUB file is valid.

Returns:
- `boolean`: true if valid, false otherwise

#### getInfo()
```java
public EpubInfo getInfo() throws EpubParseException
```
Gets basic information about the EPUB file without full parsing.

Returns:
- `EpubInfo`: Object containing basic information

Throws:
- `EpubParseException`: Error during information retrieval

### Inner Class

#### EpubInfo
```java
public static class EpubInfo {
    public String getTitle()     // Get title
    public String getAuthor()    // Get author
    public String getLanguage()  // Get language
    public int getChapterCount() // Get chapter count
    public long getFileSize()    // Get file size (bytes)
}
```

Represents basic information about an EPUB file.

### Usage Examples

#### Basic Usage

```java
// Simple parsing
EpubBook book = EpubReader.fromFile(epubFile).parse();

// Get metadata
Metadata metadata = book.getMetadata();
System.out.println("Title: " + metadata.getTitle());
System.out.println("Author: " + metadata.getCreator());
System.out.println("Language: " + metadata.getLanguage());
```

#### Advanced Configuration

```java
// Use custom configuration to optimize performance
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)              // Enable caching
    .withLazyLoading(true)        // Enable lazy loading
    .withParallelProcessing(true); // Enable parallel processing

EpubBook book = EpubReader.fromFile(epubFile, config).parse();
```

#### Quick Information Retrieval

```java
// Get basic information without full parsing
EpubReader.EpubInfo info = EpubReader.fromFile(epubFile).getInfo();
System.out.println("Title: " + info.getTitle());
System.out.println("Author: " + info.getAuthor());
System.out.println("Chapter Count: " + info.getChapterCount());
System.out.println("File Size: " + info.getFileSize() + " bytes");
```

#### Selective Parsing

```java
// Parse only metadata
Metadata metadata = EpubReader.fromFile(epubFile).parseMetadata();
System.out.println("Only metadata - Title: " + metadata.getTitle());

// Parse only table of contents
List<EpubChapter> chapters = EpubReader.fromFile(epubFile).parseTableOfContents();
System.out.println("Only TOC - Chapter count: " + chapters.size());

// Validate file validity
boolean isValid = EpubReader.fromFile(epubFile).isValid();
System.out.println("EPUB file valid: " + isValid);
```

#### Streaming Large Files

```java
// Stream process all chapters (memory-friendly)
EpubReader.fromFile(epubFile)
    .streamChapters((chapter, inputStream) -> {
        System.out.println("Processing: " + chapter.getTitle());

        // Process chapter by chapter to avoid loading entire file into memory
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, "UTF-8"))) {

            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                // Process each line
            }

            System.out.println("Chapter lines: " + lineCount);

        } catch (IOException e) {
            System.err.println("Failed to process chapter: " + e.getMessage());
        }
    });
```

#### Specific Chapter Processing

```java
// Stream process specific chapter
EpubReader.fromFile(epubFile)
    .streamChapter("chapter1", inputStream -> {
        try {
            // Read complete content
            String content = new String(inputStream.readAllBytes(), "UTF-8");
            System.out.println("Chapter 1 content length: " + content.length());

            // Or parse HTML with Jsoup
            Document doc = Jsoup.parse(content);
            Elements paragraphs = doc.select("p");
            System.out.println("Paragraph count: " + paragraphs.size());

        } catch (IOException e) {
            System.err.println("Failed to read chapter");
        }
    });
```

#### Resource Processing

```java
// Process all resources in parallel
EpubReader.fromFile(epubFile)
    .withParallelProcessing(true)
    .processResources(resource -> {
        System.out.println("Processing resource: " + resource.getId() +
                          " (" + resource.getType() + ")");

        // Save resource to filesystem
        try {
            byte[] data = resource.getData();
            if (data != null) {
                Path outputPath = Paths.get("extracted", resource.getHref());
                Files.createDirectories(outputPath.getParent());
                Files.write(outputPath, data);
            }
        } catch (IOException e) {
            System.err.println("Failed to save resource: " + resource.getHref());
        }

        return null; // Function needs return value
    });
```

#### Getting Specific Resources

```java
// Get cover image
EpubResource cover = EpubReader.fromFile(epubFile).getCover();
if (cover != null && cover.getData() != null) {
    // Save cover
    Files.write(Paths.get("cover.jpg"), cover.getData());
    System.out.println("Cover saved, size: " + cover.getData().length + " bytes");
}

// Get specific resource
EpubResource image = EpubReader.fromFile(epubFile).getResource("image1");
if (image != null) {
    System.out.println("Image type: " + image.getType());
    System.out.println("Image size: " + image.getData().length);
}
```

#### Error Handling

```java
try {
    EpubBook book = EpubReader.fromFile(epubFile)
        .withCache(true)
        .parse();

    // Process book content
    System.out.println("Successfully parsed: " + book.getMetadata().getTitle());

} catch (EpubParseException e) {
    System.err.println("Parse error: " + e.getMessage());
    System.err.println("File: " + e.getFileName());
    System.err.println("Path: " + e.getPath());

} catch (EpubFormatException e) {
    System.err.println("Format error: EPUB format does not comply with specification");

} catch (EpubPathValidationException e) {
    System.err.println("Path validation error: " + e.getMessage());

} catch (Exception e) {
    System.err.println("Unknown error: " + e.getMessage());
    e.printStackTrace();
}
```

#### Performance Optimization Example

```java
// Optimize configuration for large files
EpubReaderConfig optimizedConfig = new EpubReaderConfig()
    .withCache(true)           // Avoid repeated I/O
    .withLazyLoading(true)     // Load on demand
    .withParallelProcessing(true); // Process in parallel

long startTime = System.nanoTime();
EpubBook book = EpubReader.fromFile(largeEpubFile, optimizedConfig).parse();
long endTime = System.nanoTime();

System.out.println("Parse time: " + (endTime - startTime) / 1_000_000 + "ms");
System.out.println("Chapter count: " + book.getChapters().size());
System.out.println("Resource count: " + book.getResources().size());
```