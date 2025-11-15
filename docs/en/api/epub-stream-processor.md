# API Reference

## EpubStreamProcessor

`EpubStreamProcessor` is the dedicated class for handling EPUB streaming operations, following the Single Responsibility Principle, avoiding loading entire files into memory.

### Constructor

```java
public EpubStreamProcessor(File epubFile)
```
Create a stream processor instance.

Parameters:
- `epubFile`: EPUB file object

Throws:
- `IllegalArgumentException`: If file parameter is null

### Chapter Stream Processing

#### processHtmlChapter()
```java
public void processHtmlChapter(String htmlFileName, Consumer<InputStream> processor)
    throws BaseEpubException, EpubPathValidationException
```
Stream process a single HTML chapter content.

Parameters:
- `htmlFileName`: HTML file name
- `processor`: Content processor, receives InputStream

Throws:
- `BaseEpubException`: Processing exception
- `EpubPathValidationException`: Path validation exception

#### processMultipleHtmlChapters()
```java
public void processMultipleHtmlChapters(List<String> htmlFileNames,
    BiConsumer<String, InputStream> processor)
    throws BaseEpubException, EpubPathValidationException
```
Stream process multiple HTML chapter content.

Parameters:
- `htmlFileNames`: HTML file name list
- `processor`: Content processor, receives file name and InputStream

Throws:
- `BaseEpubException`: Processing exception
- `EpubPathValidationException`: Path validation exception

#### processBookChapters()
```java
public void processBookChapters(EpubBook book, BiConsumer<EpubChapter, InputStream> processor)
    throws BaseEpubException
```
Stream process all chapter content in a book.

Parameters:
- `book`: EpubBook instance
- `processor`: Content processor, receives EpubChapter and InputStream

Throws:
- `BaseEpubException`: Processing exception

## Usage Example

```java
File epubFile = new File("book.epub");
EpubStreamProcessor processor = new EpubStreamProcessor(epubFile);

// Process single chapter
processor.processHtmlChapter("chapter1.xhtml", inputStream -> {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        String line;
        while ((line = reader.readLine()) != null) {
            // Process each line of content
            System.out.println(line);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
});

// Process multiple chapters
List<String> chapterFiles = Arrays.asList("chapter1.xhtml", "chapter2.xhtml");
processor.processMultipleHtmlChapters(chapterFiles, (fileName, inputStream) -> {
    System.out.println("Processing file: " + fileName);
    // Process file content
});

// Process all chapters in a book
EpubBook book = EpubReader.fromFile(epubFile).parse();
processor.processBookChapters(book, (chapter, inputStream) -> {
    System.out.println("Processing chapter: " + chapter.getTitle());
    // Process chapter content
});
```

## Notes

- Streaming processing can effectively reduce memory usage, suitable for processing large EPUB files
- InputStream is automatically closed after the processor function executes
- Path validation prevents directory traversal attacks
- Exception handling should include both BaseEpubException and EpubPathValidationException