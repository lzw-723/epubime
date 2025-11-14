# Basic Usage

## Parsing EPUB Files

Use the `EpubParser` class to parse EPUB files:

```java
File epubFile = new File("path/to/your/book.epub");
EpubParser parser = new EpubParser(epubFile);
EpubBook book = parser.parse();
```

## Getting Metadata

Access book metadata through the `Metadata` object:

```java
Metadata metadata = book.getMetadata();
String title = metadata.getTitle();
String creator = metadata.getCreator();
String language = metadata.getLanguage();
String publisher = metadata.getPublisher();
String identifier = metadata.getIdentifier();
Date date = metadata.getDate();
String description = metadata.getDescription();
List<String> subjects = metadata.getSubjects();
```

## Handling Chapters

Get the book's chapter structure:

```java
List<EpubChapter> chapters = book.getChapters();
for (EpubChapter chapter : chapters) {
    System.out.println("Chapter title: " + chapter.getTitle());
    System.out.println("Content file path: " + chapter.getContent());
    
    // Handle nested chapters
    if (chapter.hasChildren()) {
        for (EpubChapter child : chapter.getChildren()) {
            System.out.println("Subchapter: " + child.getTitle());
        }
    }
}
```

## Accessing Resource Files

Get resource files (such as images, CSS, etc.) from the book:

```java
List<EpubResource> resources = book.getResources();
for (EpubResource resource : resources) {
    String href = resource.getHref();
    String mediaType = resource.getMediaType();
    byte[] data = resource.getData();
    
    // Save resource file to disk
    resource.saveToFile("output/" + href);
}
```

## Getting the Cover

Directly get the book cover:

```java
EpubResource cover = book.getCover();
if (cover != null) {
    byte[] coverData = cover.getData();
    // Save cover image
    cover.saveToFile("cover.jpg");
}
```

## Using Cache

EPUBime provides a smart caching mechanism to avoid re-parsing the same file:

```java
// First parse will cache the result
EpubBook book1 = parser.parse();

// Subsequent calls will use the cached result
EpubBook book2 = parser.parse();

// Force re-parse (skip cache)
EpubBook book3 = parser.parseWithoutCache();
```

## Streaming Large Files

For large EPUB files, streaming processing can be used to avoid memory issues:

```java
// Process single chapter content
EpubParser.processHtmlChapterContent(epubFile, "chapter1.html", inputStream -> {
    // Process the input stream, e.g., parse HTML content
    // inputStream will be automatically closed after use
});

// Batch process multiple chapters
List<String> chapterFiles = Arrays.asList("chapter1.html", "chapter2.html");
EpubParser.processMultipleHtmlChapters(epubFile, chapterFiles, (fileName, inputStream) -> {
    // Process each file's input stream
});
```