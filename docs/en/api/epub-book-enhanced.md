# EpubBookEnhanced

`EpubBookEnhanced` is an enhanced book object in the EPUBime library that provides more convenience methods and improved usability. This class is a wrapper around the basic `EpubBook` class, offering richer functionality and a simpler API.

## Class Definition

```java
public class EpubBookEnhanced
```

## Constructors

### EpubBookEnhanced(EpubBook book, File epubFile)
Creates an enhanced book object.

**Parameters:**
- `book`: The basic EpubBook object
- `epubFile`: The EPUB file

## Methods

### getMetadata()
Gets metadata, guaranteed to be non-null.

**Returns:**
- `Metadata`: The metadata object

### getTitle()
Gets the book title, returning an empty string if the title is null.

**Returns:**
- `String`: The book title

### getAuthor()
Gets the book author, returning an empty string if the author is null.

**Returns:**
- `String`: The book author

### getLanguage()
Gets the book language, returning an empty string if the language is null.

**Returns:**
- `String`: The book language

### getAllChapters()
Gets a flat list of all chapters (including nested chapters).

**Returns:**
- `List<EpubChapter>`: List of all chapters

### findChapterByTitle(String title)
Finds a chapter by title (case-insensitive).

**Parameters:**
- `title`: The chapter title to search for

**Returns:**
- `EpubChapter`: The found chapter, or null if not found

### findChaptersByContentPattern(String pattern)
Finds chapters by content path pattern.

**Parameters:**
- `pattern`: The content path pattern to match

**Returns:**
- `List<EpubChapter>`: List of matching chapters

### getChaptersByType(String type)
Gets chapters by navigation type.

**Parameters:**
- `type`: Navigation type ("ncx", "nav", "landmarks", "page-list")

**Returns:**
- `List<EpubChapter>`: List of chapters for the specified navigation type

### getFirstChapter()
Gets the first chapter.

**Returns:**
- `EpubChapter`: The first chapter, or null if no chapters exist

### getLastChapter()
Gets the last chapter.

**Returns:**
- `EpubChapter`: The last chapter, or null if no chapters exist

### getChapter(int index)
Gets a chapter by index.

**Parameters:**
- `index`: Chapter index (0-based)

**Returns:**
- `EpubChapter`: The chapter at the specified index, or null if the index is invalid

### getChapterCount()
Gets the total chapter count (including nested chapters).

**Returns:**
- `int`: Total number of chapters

### getResourcesByType(String mimeType)
Gets resources by type.

**Parameters:**
- `mimeType`: The MIME type to filter by

**Returns:**
- `List<EpubResource>`: List of resources with the specified type

### getImageResources()
Gets image resources.

**Returns:**
- `List<EpubResource>`: List of image resources

### getCssResources()
Gets CSS resources.

**Returns:**
- `List<EpubResource>`: List of CSS resources

### getJsResources()
Gets JavaScript resources.

**Returns:**
- `List<EpubResource>`: List of JavaScript resources

### getCover()
Gets the cover resource (with fallback mechanism).

**Returns:**
- `EpubResource`: The cover resource, or null if not found

### hasCover()
Checks if the book has a cover.

**Returns:**
- `boolean`: True if the book has a cover

### processChapterContent(EpubChapter chapter, Consumer&lt;InputStream&gt; processor)
Processes chapter content using stream processing.

**Parameters:**
- `chapter`: The chapter to process
- `processor`: Content processor

**Exceptions:**
- `BaseEpubException`: Thrown when processing fails

### getChapterContentAsString(EpubChapter chapter)
Gets chapter content as a string.

**Parameters:**
- `chapter`: The chapter object

**Returns:**
- `String`: Chapter content as string, or null if failed

### loadAllResources()
Loads all resource data into memory.

**Exceptions:**
- `IOException`: Thrown when loading fails

### getOriginalBook()
Gets the underlying EpubBook instance.

**Returns:**
- `EpubBook`: A copy of the original EpubBook

### getBookInfo()
Gets basic book information.

**Returns:**
- `String`: Formatted book information

## Usage Examples

```java
// Create enhanced book object
EpubBook book = EpubReader.fromFile(new File("book.epub")).parse();
EpubBookEnhanced enhancedBook = new EpubBookEnhanced(book, new File("book.epub"));

// Get book information
System.out.println("Title: " + enhancedBook.getTitle());
System.out.println("Author: " + enhancedBook.getAuthor());
System.out.println("Language: " + enhancedBook.getLanguage());

// Get all chapters
List<EpubChapter> allChapters = enhancedBook.getAllChapters();
System.out.println("Total chapters: " + enhancedBook.getChapterCount());

// Find specific chapter
EpubChapter chapter = enhancedBook.findChapterByTitle("Introduction");
if (chapter != null) {
    System.out.println("Found chapter: " + chapter.getTitle());
}

// Get resources of specific types
List<EpubResource> images = enhancedBook.getImageResources();
List<EpubResource> cssFiles = enhancedBook.getCssResources();

// Check if book has a cover
if (enhancedBook.hasCover()) {
    EpubResource cover = enhancedBook.getCover();
    System.out.println("Cover file: " + cover.getHref());
}

// Get book information summary
System.out.println(enhancedBook.getBookInfo());
```

## Notes

- `EpubBookEnhanced` is a wrapper around `EpubBook` and does not modify the original object.
- Provides richer query and processing methods, simplifying common operations.
- Provides reasonable default values (such as empty strings) for methods that might return null.