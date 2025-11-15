# API Reference

## EpubFileReader

`EpubFileReader` is the dedicated class for reading EPUB file content, following the Single Responsibility Principle, providing secure file reading functionality.

### Constructor

```java
public EpubFileReader(File epubFile)
```
Create a file reader instance.

Parameters:
- `epubFile`: EPUB file object

Throws:
- `IllegalArgumentException`: If file parameter is null

### File Reading Methods

#### readContent()
```java
public String readContent(String path) throws EpubZipException, EpubPathValidationException
```
Read content from the specified path in the EPUB file.

Parameters:
- `path`: File path

Returns:
- `String`: File content

Throws:
- `EpubZipException`: ZIP file reading exception
- `EpubPathValidationException`: Path validation exception

#### processHtmlChapterContent()
```java
public void processHtmlChapterContent(String htmlFileName, Consumer<InputStream> processor)
    throws EpubZipException, EpubPathValidationException
```
Stream process HTML chapter content.

Parameters:
- `htmlFileName`: HTML file name
- `processor`: Content processor, receives InputStream

Throws:
- `EpubZipException`: ZIP file processing exception
- `EpubPathValidationException`: Path validation exception

#### processMultipleHtmlChapters()
```java
public void processMultipleHtmlChapters(List<String> htmlFileNames,
    BiConsumer<String, InputStream> processor)
    throws EpubZipException, EpubPathValidationException
```
Stream process multiple HTML chapter content.

Parameters:
- `htmlFileNames`: HTML file name list
- `processor`: Content processor, receives file name and InputStream

Throws:
- `EpubZipException`: ZIP file processing exception
- `EpubPathValidationException`: Path validation exception

## Security Features

- **Path Validation**: Automatically validate file paths to prevent directory traversal attacks
- **ZIP Security**: Use secure ZIP file processing mechanisms
- **Exception Handling**: Provide detailed exception information for debugging

## Usage Example

```java
File epubFile = new File("book.epub");
EpubFileReader reader = new EpubFileReader(epubFile);

// Read text content
try {
    String mimetype = reader.readContent("mimetype");
    System.out.println("MIME type: " + mimetype);

    String container = reader.readContent("META-INF/container.xml");
    System.out.println("Container content: " + container);
} catch (EpubZipException | EpubPathValidationException e) {
    System.err.println("Failed to read file: " + e.getMessage());
}

// Stream process HTML content
reader.processHtmlChapterContent("chapter1.xhtml", inputStream -> {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
        String line;
        while ((line = br.readLine()) != null) {
            // Process each line
            System.out.println(line);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
});

// Batch process multiple files
List<String> files = Arrays.asList("chapter1.xhtml", "chapter2.xhtml");
reader.processMultipleHtmlChapters(files, (fileName, inputStream) -> {
    System.out.println("Processing file: " + fileName);
    // Process file content
});
```

## Notes

- All paths are validated for security
- InputStream is automatically closed after processor execution
- Supports all compression formats in ZIP files
- Provides detailed error information for problem diagnosis