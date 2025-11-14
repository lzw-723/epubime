# Quick Start

## Project Overview

EPUBime is a pure Java library for parsing EPUB file format. It provides complete EPUB file parsing functionality, including metadata extraction, chapter content reading, and resource file processing. It supports both EPUB 2 and EPUB 3 formats.

## Installation

Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>fun.lzwi</groupId>
    <artifactId>epubime</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Basic Usage

Here's the most basic example demonstrating how to parse an EPUB file and get basic information:

```java
import fun.lzwi.epubime.epub.*;

File epubFile = new File("path/to/your/book.epub");
EpubParser parser = new EpubParser(epubFile);
EpubBook book = parser.parse();

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

// Get cover
EpubResource cover = book.getCover();
byte[] coverData = cover.getData();
```

## Key Features

### Metadata Parsing
Extract standard metadata information such as book title, author, publisher, language, etc. from EPUB files.

### Chapter Parsing
Support for both NCX and NAV directory formats, with access to complete chapter structure and nested chapters.

### Resource Extraction
Extract all resources (images, CSS, styles, etc.) from EPUB files with streaming support.

### Error Handling
Comprehensive exception handling mechanism including format exceptions, parsing exceptions, path validation exceptions, etc.

## Runtime Environment

- Java 8 or higher
- Maven 3.5+ (for building and dependency management)

## Next Steps

To learn more detailed usage, see:
- [Basic Usage](/en/guide/basic-usage)
- [Advanced Features](/en/guide/advanced-features)
- [API Reference](/en/api/)