# API Reference

## EpubBookProcessor

`EpubBookProcessor` is the dedicated class for handling EpubBook-related business logic, following the Single Responsibility Principle, specifically responsible for book data processing and operations.

### Cover Processing

#### getCover()
```java
public static EpubResource getCover(EpubBook book)
```
Get the cover resource of the book.

Parameters:
- `book`: EpubBook instance

Returns:
- `EpubResource`: Cover resource object, or null if not found

#### Cover Lookup Logic
1. Prioritize finding resources with `properties="cover-image"`
2. If not found, fall back to the cover field in metadata
3. Automatically apply resource fallback mechanism

### Resource Processing

#### getResource()
```java
public static EpubResource getResource(EpubBook book, String resourceId)
```
Get resource by resource ID.

Parameters:
- `book`: EpubBook instance
- `resourceId`: Resource ID

Returns:
- `EpubResource`: Found resource object, or null if not found

#### getResourceWithFallback()
```java
public static EpubResource getResourceWithFallback(EpubBook book, String resourceId)
```
Get resource by resource ID and automatically apply fallback mechanism.

Parameters:
- `book`: EpubBook instance
- `resourceId`: Resource ID

Returns:
- `EpubResource`: Resource object with fallback mechanism applied, or null if not found

### Data Loading

#### loadAllResourceData()
```java
public static void loadAllResourceData(EpubBook book) throws IOException
```
Batch load all resource data into memory.

Parameters:
- `book`: EpubBook instance

Throws:
- `IOException`: File reading exception

## Usage Example

```java
// Get cover resource
EpubResource cover = EpubBookProcessor.getCover(book);
if (cover != null) {
    System.out.println("Cover: " + cover.getHref());
}

// Get specific resource
EpubResource image = EpubBookProcessor.getResource(book, "image1");
if (image != null) {
    System.out.println("Image type: " + image.getType());
}

// Get resource with fallback mechanism
EpubResource resource = EpubBookProcessor.getResourceWithFallback(book, "fallback-resource");

// Batch load resource data
try {
    EpubBookProcessor.loadAllResourceData(book);
    System.out.println("All resource data loaded");
} catch (IOException e) {
    System.err.println("Failed to load resource data: " + e.getMessage());
}
```</content>
<parameter name="filePath">docs/en/api/epub-book-processor.md