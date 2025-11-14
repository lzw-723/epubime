# EpubBook

`EpubBook` class represents a parsed EPUB book, containing all book information including metadata, chapters, and resources.

## Constructor

```java
public EpubBook(Metadata metadata, List<EpubChapter> chapters, List<EpubResource> resources)
```
Creates a new EpubBook instance.

Parameters:
- `metadata`: Book metadata
- `chapters`: Chapter list
- `resources`: Resource list

## Main Methods

### getMetadata()
```java
public Metadata getMetadata()
```
Gets book metadata.

Returns:
- `Metadata`: Book metadata object

### getChapters()
```java
public List<EpubChapter> getChapters()
```
Gets book chapter list.

Returns:
- `List<EpubChapter>`: Chapter list

### getResources()
```java
public List<EpubResource> getResources()
```
Gets book resource list.

Returns:
- `List<EpubResource>`: Resource list

### getCover()
```java
public EpubResource getCover()
```
Gets book cover resource.

Returns:
- `EpubResource`: Cover resource object, or null if no cover is found

### getResourceByHref()
```java
public EpubResource getResourceByHref(String href)
```
Finds resource by href.

Parameters:
- `href`: Resource href attribute

Returns:
- `EpubResource`: Found resource object, or null if not found

### getChapterById()
```java
public EpubChapter getChapterById(String id)
```
Finds chapter by ID.

Parameters:
- `id`: Chapter ID

Returns:
- `EpubChapter`: Found chapter object, or null if not found

### getChapterByPath()
```java
public EpubChapter getChapterByPath(String path)
```
Finds chapter by path.

Parameters:
- `path`: Chapter path

Returns:
- `EpubChapter`: Found chapter object, or null if not found

### getTableOfContents()
```java
public List<EpubChapter> getTableOfContents()
```
Gets book's table of contents structure.

Returns:
- `List<EpubChapter>`: Table of contents chapter list

### hasCover()
```java
public boolean hasCover()
```
Checks if book has a cover.

Returns:
- `boolean`: Returns true if book has a cover, otherwise false

### hasResources()
```java
public boolean hasResources()
```
Checks if book has resource files.

Returns:
- `boolean`: Returns true if book has resource files, otherwise false

### hasChapters()
```java
public boolean hasChapters()
```
Checks if book has chapters.

Returns:
- `boolean`: Returns true if book has chapters, otherwise false

### getResourceCount()
```java
public int getResourceCount()
```
Gets resource file count.

Returns:
- `int`: Resource file count

### getChapterCount()
```java
public int getChapterCount()
```
Gets chapter count.

Returns:
- `int`: Chapter count

### getAllChapters()
```java
public List<EpubChapter> getAllChapters()
```
Gets all chapters, including nested subchapters.

Returns:
- `List<EpubChapter>`: List of all chapters

### getChaptersByMediaType()
```java
public List<EpubChapter> getChaptersByMediaType(String mediaType)
```
Gets chapters by media type.

Parameters:
- `mediaType`: Media type (e.g., "application/xhtml+xml")

Returns:
- `List<EpubChapter>`: Chapter list matching media type

### getResourcesByMediaType()
```java
public List<EpubResource> getResourcesByMediaType(String mediaType)
```
Gets resources by media type.

Parameters:
- `mediaType`: Media type (e.g., "image/jpeg")

Returns:
- `List<EpubResource>`: Resource list matching media type

### getImages()
```java
public List<EpubResource> getImages()
```
Gets all image resources.

Returns:
- `List<EpubResource>`: Image resource list

### getStylesheets()
```java
public List<EpubResource> getStylesheets()
```
Gets all stylesheet resources.

Returns:
- `List<EpubResource>`: Stylesheet resource list

### getFonts()
```java
public List<EpubResource> getFonts()
```
Gets all font resources.

Returns:
- `List<EpubResource>`: Font resource list

### getResourcePaths()
```java
public Set<String> getResourcePaths()
```
Gets collection of all resource paths.

Returns:
- `Set<String>`: Resource path collection

### getChapterPaths()
```java
public Set<String> getChapterPaths()
```
Gets collection of all chapter paths.

Returns:
- `Set<String>`: Chapter path collection