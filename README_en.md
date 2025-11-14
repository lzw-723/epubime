# EPUBime

[English](README_en.md) | [中文](README.md)

EPUBime is a pure Java library for parsing EPUB file format. It provides complete EPUB file parsing functionality, including metadata extraction, chapter content reading, and resource file processing. Supports both EPUB 2 and EPUB 3 formats.

## Features

- **Metadata Parsing**: Extract standard EPUB metadata such as title, author, publisher, language, identifier, etc.
- **TOC Parsing**: Support for both NCX and NAV table of contents formats
- **Resource Extraction**: Extract all resources from EPUB files (images, CSS, styles, etc.)
- **ZIP Processing**: EPUB document parsing based on ZIP file format
- **Error Handling**: Comprehensive exception handling system
- **Cache Management**: Provides intelligent caching mechanism to avoid re-parsing the same content
- **Streaming Processing**: Supports streaming processing of large EPUB files to optimize memory usage
- **EPUB 3.3 Support**: Compatible with EPUB 3.3 specification
- **Nested Chapters**: Supports parsing of nested chapter structures
- **Multiple Navigation Types**: Supports landmarks, page-list and other navigation types

## Quick Start

### Maven Dependency

Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>fun.lzwi</groupId>
    <artifactId>epubime</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Basic Usage

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

For more usage examples and advanced features, please check the [full documentation](https://lzw-723.github.io/epubime/).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.