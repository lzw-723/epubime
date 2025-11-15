# EpubBook

`EpubBook` class represents a parsed EPUB book, following the Single Responsibility Principle, dedicated to storing book information as a data model. Business logic operations should use corresponding processor classes.

## Constructor

```java
public EpubBook()
```
Creates an empty EpubBook instance.

```java
public EpubBook(EpubBook other)
```
Copy constructor for caching.

Parameters:
- `other`: EpubBook instance to copy

## Main Methods

### Metadata Operations

#### getMetadata()
```java
public Metadata getMetadata()
```
Gets book metadata copy.

Returns:
- `Metadata`: Book metadata object copy

#### setMetadata()
```java
public void setMetadata(Metadata metadata)
```
Sets book metadata.

Parameters:
- `metadata`: Metadata object

### Chapter Operations

#### getNcx()
```java
public List<EpubChapter> getNcx()
```
Gets NCX format table of contents chapter list.

Returns:
- `List<EpubChapter>`: NCX table of contents list (unmodifiable)

#### setNcx()
```java
public void setNcx(List<EpubChapter> ncx)
```
Sets NCX format table of contents chapter list.

Parameters:
- `ncx`: NCX table of contents list

#### getNav()
```java
public List<EpubChapter> getNav()
```
Gets NAV format table of contents chapter list.

Returns:
- `List<EpubChapter>`: NAV table of contents list (unmodifiable)

#### setNav()
```java
public void setNav(List<EpubChapter> nav)
```
Sets NAV format table of contents chapter list.

Parameters:
- `nav`: NAV table of contents list

#### getLandmarks()
```java
public List<EpubChapter> getLandmarks()
```
Gets landmark navigation chapter list.

Returns:
- `List<EpubChapter>`: Landmark navigation list (unmodifiable)

#### setLandmarks()
```java
public void setLandmarks(List<EpubChapter> landmarks)
```
Sets landmark navigation chapter list.

Parameters:
- `landmarks`: Landmark navigation list

#### getPageList()
```java
public List<EpubChapter> getPageList()
```
Gets page list navigation chapter list.

Returns:
- `List<EpubChapter>`: Page list navigation list (unmodifiable)

#### setPageList()
```java
public void setPageList(List<EpubChapter> pageList)
```
Sets page list navigation chapter list.

Parameters:
- `pageList`: Page list navigation list

#### getChapters()
```java
public List<EpubChapter> getChapters()
```
Gets main chapter list, preferring NAV table of contents if NAV is empty then NCX table of contents.

Returns:
- `List<EpubChapter>`: Chapter list (unmodifiable)

### Resource Operations

#### getResources()
```java
public List<EpubResource> getResources()
```
Gets resource file list.

Returns:
- `List<EpubResource>`: Resource list (unmodifiable)

#### setResources()
```java
public void setResources(List<EpubResource> resources)
```
Sets resource file list.

Parameters:
- `resources`: Resource list

## Related Processor Classes

Since EpubBook follows the Single Responsibility Principle and is only responsible for data storage, business logic operations should use the following processor classes:

### EpubBookProcessor
Handles book-related business logic:
- `getCover(EpubBook book)` - Gets cover resource
- `getResource(EpubBook book, String resourceId)` - Gets specific resource
- `getResourceWithFallback(EpubBook book, String resourceId)` - Gets resource with fallback mechanism
- `loadAllResourceData(EpubBook book)` - Batch loads resource data

### EpubStreamProcessor
Handles streaming operations:
- `processBookChapters(EpubBook book, BiConsumer<EpubChapter, InputStream> processor)` - Streams book chapters

## Usage Example

```java
// Create EpubBook instance
EpubBook book = new EpubBook();

// Set metadata
Metadata metadata = new Metadata();
metadata.addTitle("Sample Book");
book.setMetadata(metadata);

// Set chapters
List<EpubChapter> chapters = new ArrayList<>();
// ... add chapters
book.setNcx(chapters);

// Set resources
List<EpubResource> resources = new ArrayList<>();
// ... add resources
book.setResources(resources);

// Use processor classes for business operations
EpubResource cover = EpubBookProcessor.getCover(book);
EpubResource resource = EpubBookProcessor.getResource(book, "image1");
```