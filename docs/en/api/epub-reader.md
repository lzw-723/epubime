# EpubReader

`EpubReader` is the modern Fluent API entry point for the EPUBime library, providing a chained method approach to configure and parse EPUB files.

## Class Definition

```java
public class EpubReader
```

## Constructors

This class cannot be instantiated directly and must be created using static factory methods.

## Static Methods

### fromFile(File epubFile)
Creates an EpubReader instance with default configuration.

**Parameters:**
- `epubFile`: The EPUB file to parse

**Returns:**
- `EpubReader`: A new EpubReader instance

**Exceptions:**
- `IllegalArgumentException`: If epubFile is null

### fromFile(String filePath)
Creates an EpubReader instance with default configuration from a file path.

**Parameters:**
- `filePath`: Path to the EPUB file

**Returns:**
- `EpubReader`: A new EpubReader instance

**Exceptions:**
- `IllegalArgumentException`: If filePath is null

### fromFile(File epubFile, EpubReaderConfig config)
Creates an EpubReader instance with custom configuration.

**Parameters:**
- `epubFile`: The EPUB file to parse
- `config`: Configuration object

**Returns:**
- `EpubReader`: A new EpubReader instance

**Exceptions:**
- `IllegalArgumentException`: If epubFile or config is null

## Instance Methods

### parse()
Parses the EPUB file and returns an EpubBook object.

**Returns:**
- `EpubBook`: The parsed EPUB book object

**Exceptions:**
- `BaseEpubException`: Exceptions occurring during parsing
- `IOException`: I/O operation failures
- `EpubPathValidationException`: Path validation failures

### parseMetadata()
Parses only the metadata of the EPUB file.

**Returns:**
- `Metadata`: EPUB metadata

**Exceptions:**
- `BaseEpubException`: Exceptions occurring during parsing
- `IOException`: I/O operation failures
- `EpubPathValidationException`: Path validation failures

### parseTableOfContents()
Parses only the table of contents of the EPUB file.

**Returns:**
- `List<EpubChapter>`: List of chapters

**Exceptions:**
- `BaseEpubException`: Exceptions occurring during parsing
- `IOException`: I/O operation failures
- `EpubPathValidationException`: Path validation failures

### streamChapters(BiConsumer<EpubChapter, InputStream> processor)
Processes chapter content in a streaming manner without loading the entire content into memory.

**Parameters:**
- `processor`: Consumer function to process each chapter and its content stream

**Exceptions:**
- `BaseEpubException`: Exceptions occurring during processing
- `IOException`: I/O operation failures
- `EpubPathValidationException`: Path validation failures

### streamChapter(String chapterId, Consumer<InputStream> processor)
Processes the content of a specific chapter in a streaming manner.

**Parameters:**
- `chapterId`: The ID of the chapter to process
- `processor`: Consumer function to process the chapter content stream

**Exceptions:**
- `BaseEpubException`: Exceptions occurring during processing
- `EpubPathValidationException`: Path validation failures
- `IOException`: I/O operation failures

### processResources(Function<EpubResource, Void> processor)
Processes all resource files.

**Parameters:**
- `processor`: Function to process each resource

**Exceptions:**
- `BaseEpubException`: Exceptions occurring during processing
- `IOException`: I/O operation failures
- `EpubPathValidationException`: Path validation failures

### getResource(String resourceId)
Gets a specific resource by resource ID.

**Parameters:**
- `resourceId`: The resource ID

**Returns:**
- `EpubResource`: The resource object, or null if not found

**Exceptions:**
- `BaseEpubException`: Exceptions occurring during parsing
- `IOException`: I/O operation failures
- `EpubPathValidationException`: Path validation failures

### getCover()
Gets the cover resource of the EPUB file.

**Returns:**
- `EpubResource`: The cover resource, or null if not found

**Exceptions:**
- `BaseEpubException`: Exceptions occurring during parsing
- `IOException`: I/O operation failures
- `EpubPathValidationException`: Path validation failures

### isValid()
Checks if the EPUB file is valid.

**Returns:**
- `boolean`: True if the file is valid, false otherwise

### getInfo()
Gets basic information about the EPUB file without full parsing.

**Returns:**
- `EpubInfo`: Basic information about the EPUB file

**Exceptions:**
- `BaseEpubException`: Exceptions occurring during parsing
- `IOException`: I/O operation failures
- `EpubPathValidationException`: Path validation failures

## Inner Classes

### EpubInfo
Basic information class for EPUB files.

#### Constructor
```java
public EpubInfo(String title, String author, String language, int chapterCount, long fileSize)
```

#### Methods
- `getTitle()`: Gets the title
- `getAuthor()`: Gets the author
- `getLanguage()`: Gets the language
- `getChapterCount()`: Gets the number of chapters
- `getFileSize()`: Gets the file size
- `toString()`: Returns a formatted string representation

## Usage Examples

```java
// Basic usage
EpubBook book = EpubReader.fromFile(new File("book.epub")).parse();

// Using custom configuration
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)
    .withLazyLoading(false)
    .withParallelProcessing(true);
EpubBook book = EpubReader.fromFile(new File("book.epub"), config).parse();

// Streaming chapter processing
EpubReader.fromFile(new File("book.epub"))
    .streamChapters((chapter, inputStream) -> {
        System.out.println("Processing chapter: " + chapter.getTitle());
        // Process content stream
    });

// Getting basic information
EpubReader.EpubInfo info = EpubReader.fromFile(new File("book.epub")).getInfo();
System.out.println(info);
```