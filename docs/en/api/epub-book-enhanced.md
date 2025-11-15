# API Reference

## EpubBookEnhanced

`EpubBookEnhanced` is an enhanced book object class that provides more convenient methods and advanced features.

### Constructor

```java
public EpubBookEnhanced(EpubBook book, File epubFile)
```
Creates an enhanced book object.

Parameters:
- `book`: Base EpubBook object
- `epubFile`: EPUB file

### Basic Information Methods

#### getTitle()
```java
public String getTitle()
```
Gets the book title.

Returns:
- `String`: Book title

#### getAuthor()
```java
public String getAuthor()
```
Gets the book author.

Returns:
- `String`: Book author

#### getLanguage()
```java
public String getLanguage()
```
Gets the book language.

Returns:
- `String`: Book language code

#### getBookInfo()
```java
public String getBookInfo()
```
Gets the complete book information string.

Returns:
- `String`: Formatted book information

### Chapter Related Methods

#### getAllChapters()
```java
public List<EpubChapter> getAllChapters()
```
Gets all chapters, including nested chapters (flattened list).

Returns:
- `List<EpubChapter>`: All chapters list

#### getChapterCount()
```java
public int getChapterCount()
```
Gets the total chapter count (including nested chapters).

Returns:
- `int`: Total chapter count

#### getChapter()
```java
public EpubChapter getChapter(int index)
```
Gets chapter by index.

Parameters:
- `index`: Chapter index (0-based)

Returns:
- `EpubChapter`: Chapter object, or null if index is invalid

#### findChapterByTitle()
```java
public EpubChapter findChapterByTitle(String title)
```
Finds chapter by title.

Parameters:
- `title`: Chapter title

Returns:
- `EpubChapter`: Found chapter object, or null if not found

#### findChaptersByContentPattern()
```java
public List<EpubChapter> findChaptersByContentPattern(String pattern)
```
Finds chapters by content path pattern.

Parameters:
- `pattern`: Content path pattern

Returns:
- `List<EpubChapter>`: Matching chapters list

### Resource Related Methods

#### getImageResources()
```java
public List<EpubResource> getImageResources()
```
Gets all image resources.

Returns:
- `List<EpubResource>`: Image resources list

#### getCssResources()
```java
public List<EpubResource> getCssResources()
```
Gets all CSS resources.

Returns:
- `List<EpubResource>`: CSS resources list

#### getJsResources()
```java
public List<EpubResource> getJsResources()
```
Gets all JavaScript resources.

Returns:
- `List<EpubResource>`: JavaScript resources list

#### getResourcesByType()
```java
public List<EpubResource> getResourcesByType(String mimeType)
```
Gets resources by MIME type.

Parameters:
- `mediaType`: MIME type (e.g. "image/jpeg", "text/css")

Returns:
- `List<EpubResource>`: Matching resources list

### Cover Related Methods

#### hasCover()
```java
public boolean hasCover()
```
Checks if the book has a cover.

Returns:
- `boolean`: true if cover exists, false otherwise

#### getCover()
```java
public EpubResource getCover()
```
Gets the cover resource.

Returns:
- `EpubResource`: Cover resource, or null if not found

#### processChapterContent()
```java
public void processChapterContent(EpubChapter chapter, Consumer<InputStream> processor) throws BaseEpubException
```
Stream processes chapter content.

Parameters:
- `chapter`: Chapter to process
- `processor`: Content processor

Throws:
- `BaseEpubException`: Thrown if processing fails

#### getChapterContentAsString()
```java
public String getChapterContentAsString(EpubChapter chapter)
```
Gets chapter content as string.

Parameters:
- `chapter`: Chapter object

Returns:
- `String`: Chapter content string, or null if failed

### Navigation Related Methods

#### getChaptersByType()
```java
public List<EpubChapter> getChaptersByType(String type)
```
Gets chapters by navigation type.

Parameters:
- `type`: Navigation type ("ncx", "nav", "landmarks", "page-list")

Returns:
- `List<EpubChapter>`: Chapters list for specified type

### Other Methods

#### loadAllResources()
```java
public void loadAllResources() throws IOException
```
Loads all resource data into memory.

Throws:
- `IOException`: Thrown if loading fails

#### getOriginalBook()
```java
public EpubBook getOriginalBook()
```
Gets the underlying EpubBook instance.

Returns:
- `EpubBook`: Copy of original EpubBook

#### getBookInfo()
```java
public String getBookInfo()
```
Gets formatted book information.

Returns:
- `String`: Formatted book information

### Usage Examples

```java
// Create enhanced book object
EpubBook book = EpubReader.fromFile(epubFile).parse();
EpubBookEnhanced enhancedBook = new EpubBookEnhanced(book, epubFile);

// Get enhanced information
System.out.println("Book information:");
System.out.println(enhancedBook.getBookInfo());

// Find chapter
EpubChapter chapter = enhancedBook.findChapterByTitle("Preface");
if (chapter != null) {
    System.out.println("Found chapter: " + chapter.getTitle());
}

// Get all chapters (including nested)
List<EpubChapter> allChapters = enhancedBook.getAllChapters();
System.out.println("Total chapters: " + allChapters.size());

// Get specific type resources
List<EpubResource> images = enhancedBook.getImageResources();
List<EpubResource> cssFiles = enhancedBook.getCssResources();
List<EpubResource> jsFiles = enhancedBook.getJsResources();

System.out.println("Image resources: " + images.size());
System.out.println("CSS files: " + cssFiles.size());
System.out.println("JavaScript files: " + jsFiles.size());

// Check cover
if (enhancedBook.hasCover()) {
    System.out.println("Book has cover");
    EpubResource cover = enhancedBook.getCover();
    if (cover != null) {
        System.out.println("Cover type: " + cover.getType());
    }
}
```