# Advanced Features

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