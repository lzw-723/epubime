# EPUB2 Compatibility

The EPUBime library fully supports parsing and processing of EPUB 2 format, providing robust backward compatibility. EPUB2 is an earlier EPUB standard that has some differences from EPUB 3 in structure and metadata handling.

## Supported EPUB2 Features

### 1. Metadata Parsing

EPUBime can correctly parse EPUB2 format metadata, including:

- **Title and Author**: Parsing basic elements like `<title>`, `<creator>`
- **Language and Identifier**: Parsing elements like `<language>`, `<identifier>`
- **Publication Information**: Parsing elements like `<publisher>`, `<date>`, `<description>`
- **Subject and Rights**: Parsing elements like `<subject>`, `<rights>`, `<contributor>`
- **Cover Information**: Parsing cover through `<meta name="cover" content="cover-id"/>` tag

EPUB2 metadata parsing supports multiple formats:

```xml
<!-- No namespace format -->
<package version="2.0">
  <metadata>
    <title>EPUB2 Book Without Namespaces</title>
    <creator>Author Name</creator>
    <language>en-US</language>
    <identifier>urn:uuid:test-123</identifier>
  </metadata>
</package>

<!-- Mixed namespace format -->
<package version="2.0">
  <metadata>
    <dc:title xmlns:dc="http://purl.org/dc/elements/1.1/">EPUB2 Book</dc:title>
    <creator>Author Without Namespace</creator>
    <dc:language>zh-CN</dc:language>
  </metadata>
</package>
```

### 2. Navigation Parsing (NCX)

EPUB2 uses NCX (Navigation Control file for XML applications) files for navigation, and EPUBime provides complete NCX parsing support:

- **Basic Navigation Points**: Parsing `navPoint` elements and their `playOrder` attributes
- **Nested Structure**: Support for nested `navPoint` structures representing chapters and sub-chapters
- **Navigation Labels**: Parsing `navLabel` and `content` elements
- **Metadata**: Parsing metadata in NCX files

NCX file example:

```xml
<ncx version="2005-1">
  <navMap>
    <navPoint id="navpoint-1" playOrder="1">
      <navLabel>
        <text>Chapter 1</text>
      </navLabel>
      <content src="chapter1.html"/>
    </navPoint>
    <navPoint id="navpoint-2" playOrder="2">
      <navLabel>
        <text>Chapter 2</text>
      </navLabel>
      <content src="chapter2.html"/>
      <navPoint id="navpoint-2-1" playOrder="3">
        <navLabel>
          <text>Section 2.1</text>
        </navLabel>
        <content src="chapter2.html#section1"/>
      </navPoint>
    </navPoint>
  </navMap>
</ncx>
```

### 3. Resource Parsing

EPUB2's resource management is implemented through the `manifest` and `spine` elements of the OPF (Open Packaging Format) file:

- **Manifest Parsing**: Parsing all resource items in `manifest`
- **Content Order**: Parsing content order in `spine`
- **Cover Detection**: Detecting cover resources through `meta` tags
- **NCX Path**: Extracting NCX file path

OPF file example:

```xml
<package version="2.0">
  <metadata>
    <title>Test Book</title>
    <identifier>test-id</identifier>
  </metadata>
  <manifest>
    <item id="chapter1" href="chapter1.html" media-type="application/xhtml+xml"/>
    <item id="cover" href="cover.jpg" media-type="image/jpeg"/>
    <item id="stylesheet" href="style.css" media-type="text/css"/>
    <item id="ncx" href="toc.ncx" media-type="application/x-dtbncx+xml"/>
  </manifest>
  <spine toc="ncx">
    <itemref idref="chapter1"/>
  </spine>
</package>
```

## Usage Examples

### Parsing EPUB2 Files

```java
import fun.lzwi.epubime.api.*;
import fun.lzwi.epubime.epub.*;

File epub2File = new File("epub2-book.epub");
EpubBook book = EpubReader.fromFile(epub2File).parse();

// Get metadata
Metadata metadata = book.getMetadata();
System.out.println("Title: " + metadata.getTitle());
System.out.println("Author: " + metadata.getCreator());

// Get chapters (from NCX)
List<EpubChapter> chapters = book.getNcx(); // EPUB2 chapters usually come from NCX
for (EpubChapter chapter : chapters) {
    System.out.println("Chapter: " + chapter.getTitle());
    System.out.println("Content: " + chapter.getContent());
    
    // Process nested chapters
    if (chapter.hasChildren()) {
        for (EpubChapter subChapter : chapter.getChildren()) {
            System.out.println("  Sub-chapter: " + subChapter.getTitle());
        }
    }
}

// Get resources
List<EpubResource> resources = book.getResources();
System.out.println("Resource count: " + resources.size());
```

### Checking EPUB Version

```java
// EPUBime automatically detects and handles different versions of EPUB files
EpubBook book = EpubReader.fromFile(epub2File).parse();

// Use enhanced features to get more information
EpubBookEnhanced enhancedBook = new EpubBookEnhanced(book, epub2File);
List<EpubChapter> ncxChapters = enhancedBook.getChaptersByType("ncx");

System.out.println("NCX chapter count: " + ncxChapters.size());
```

## Compatibility Guarantee

### Automatic Version Detection
EPUBime automatically detects the EPUB file version and uses the appropriate parsing strategy:
- For EPUB2 files, use NCX navigation parsing
- For EPUB3 files, use NAV navigation parsing
- Support for mixed and no-namespace metadata formats

### Error Handling
EPUB2 files may have formatting irregularities, and EPUBime provides robust error handling mechanisms:
- Tolerate missing namespace declarations
- Handle incomplete metadata
- Provide detailed error information and recovery suggestions

### Performance Optimization
- Intelligent caching mechanism to avoid repeated parsing
- Streaming processing to optimize memory usage
- Specialized optimization for EPUB2 format

## Notes

1. **Navigation Differences**: EPUB2 uses NCX, EPUB3 uses NAV, and EPUBime automatically selects the appropriate parsing method
2. **Metadata Format**: EPUB2 metadata format is relatively simple but may have inconsistent namespace usage
3. **Resource References**: EPUB2 resource references are usually defined directly in the OPF file
4. **Backward Compatibility**: All EPUBime APIs support EPUB2 files without special configuration

EPUBime's EPUB2 compatibility ensures support for a large number of existing EPUB2 files while maintaining compatibility with EPUB3, providing users with a unified API experience.