# MetadataEnhanced

`MetadataEnhanced` is an enhanced metadata object in the EPUBime library that provides more convenience methods and type-safe access. This class is a wrapper around the basic `Metadata` class, offering richer functionality and a simpler API.

## Class Definition

```java
public class MetadataEnhanced
```

## Constructors

### MetadataEnhanced(Metadata metadata)
Creates an enhanced metadata object.

**Parameters:**
- `metadata`: The basic Metadata object

## Methods

### getTitle()
Gets the primary title.

**Returns:**
- `String`: The primary title, or empty string if none

### getTitles()
Gets all titles.

**Returns:**
- `List<String>`: List of all titles

### getAuthor()
Gets the primary author/creator.

**Returns:**
- `String`: The primary author, or empty string if none

### getAuthors()
Gets all authors/creators.

**Returns:**
- `List<String>`: List of all authors

### getLanguage()
Gets the primary language.

**Returns:**
- `String`: The primary language, or empty string if none

### getLanguages()
Gets all languages.

**Returns:**
- `List<String>`: List of all languages

### getPublisher()
Gets the primary publisher.

**Returns:**
- `String`: The primary publisher, or empty string if none

### getPublishers()
Gets all publishers.

**Returns:**
- `List<String>`: List of all publishers

### getIdentifier()
Gets the primary identifier.

**Returns:**
- `String`: The primary identifier, or empty string if none

### getIdentifiers()
Gets all identifiers.

**Returns:**
- `List<String>`: List of all identifiers

### getDescription()
Gets the primary description.

**Returns:**
- `String`: The primary description, or empty string if none

### getDescriptions()
Gets all descriptions.

**Returns:**
- `List<String>`: List of all descriptions

### getDate()
Gets the primary date.

**Returns:**
- `String`: The primary date, or empty string if none

### getDates()
Gets all dates.

**Returns:**
- `List<String>`: List of all dates

### getParsedDate()
Gets the parsed date as a LocalDate object.

**Returns:**
- `LocalDate`: The parsed date, or null if parsing fails

### getRights()
Gets the rights information.

**Returns:**
- `String`: Rights information, or empty string if none

### getRightsList()
Gets all rights information.

**Returns:**
- `List<String>`: List of all rights information

### getSource()
Gets the source.

**Returns:**
- `String`: Source, or empty string if none

### getSources()
Gets all sources.

**Returns:**
- `List<String>`: List of all sources

### getSubject()
Gets the primary subject/genre.

**Returns:**
- `String`: Primary subject, or empty string if none

### getSubjects()
Gets all subjects/genres.

**Returns:**
- `List<String>`: List of all subjects

### getType()
Gets the primary type.

**Returns:**
- `String`: Primary type, or empty string if none

### getTypes()
Gets all types.

**Returns:**
- `List<String>`: List of all types

### getFormat()
Gets the primary format.

**Returns:**
- `String`: Primary format, or empty string if none

### getFormats()
Gets all formats.

**Returns:**
- `List<String>`: List of all formats

### getCoverId()
Gets the cover ID.

**Returns:**
- `String`: Cover ID, or empty string if none

### getModified()
Gets the modified date.

**Returns:**
- `String`: Modified date, or empty string if none

### getUniqueIdentifier()
Gets the unique identifier.

**Returns:**
- `String`: Unique identifier, or empty string if none

### getAccessibilityFeatures()
Gets accessibility features.

**Returns:**
- `List<String>`: List of accessibility features

### getAccessibilityHazards()
Gets accessibility hazards.

**Returns:**
- `List<String>`: List of accessibility hazards

### getAccessibilitySummary()
Gets accessibility summary.

**Returns:**
- `String`: Accessibility summary, or empty string if none

### getLayout()
Gets the layout property.

**Returns:**
- `String`: Layout property, or empty string if none

### getOrientation()
Gets the orientation property.

**Returns:**
- `String`: Orientation property, or empty string if none

### getSpread()
Gets the spread property.

**Returns:**
- `String`: Spread property, or empty string if none

### getViewport()
Gets the viewport property.

**Returns:**
- `String`: Viewport property, or empty string if none

### getMedia()
Gets the media property.

**Returns:**
- `String`: Media property, or empty string if none

### getFlow()
Gets the flow property.

**Returns:**
- `String`: Flow property, or empty string if none

### isAlignXCenter()
Checks if align-x-center is enabled.

**Returns:**
- `boolean`: True if align-x-center is enabled

### hasCover()
Checks if the book has a specified cover.

**Returns:**
- `boolean`: True if a cover is specified

### hasAccessibilityFeatures()
Checks if the book has accessibility features.

**Returns:**
- `boolean`: True if accessibility features are present

### hasAccessibilityHazards()
Checks if the book has accessibility hazards.

**Returns:**
- `boolean`: True if accessibility hazards are specified

### hasDescription()
Checks if the book has a description.

**Returns:**
- `boolean`: True if a description is present

### hasSubjects()
Checks if the book has subjects/genres.

**Returns:**
- `boolean`: True if subjects are present

### getSummary()
Gets a formatted summary of the metadata.

**Returns:**
- `String`: Formatted metadata summary

### getOriginalMetadata()
Gets the underlying Metadata instance.

**Returns:**
- `Metadata`: A copy of the original Metadata

## Usage Examples

```java
// Parse EPUB and get enhanced metadata
EpubBook book = EpubReader.fromFile(new File("book.epub")).parse();
MetadataEnhanced metadata = new MetadataEnhanced(book.getMetadata());

// Get basic metadata information
System.out.println("Title: " + metadata.getTitle());
System.out.println("Author: " + metadata.getAuthor());
System.out.println("Language: " + metadata.getLanguage());
System.out.println("Publisher: " + metadata.getPublisher());
System.out.println("Date: " + metadata.getDate());

// Get all metadata items
List<String> allTitles = metadata.getTitles();
List<String> allAuthors = metadata.getAuthors();
List<String> allSubjects = metadata.getSubjects();

// Get accessibility information
if (metadata.hasAccessibilityFeatures()) {
    List<String> features = metadata.getAccessibilityFeatures();
    System.out.println("Accessibility features: " + String.join(", ", features));
}

// Get parsed date
LocalDate parsedDate = metadata.getParsedDate();
if (parsedDate != null) {
    System.out.println("Parsed date: " + parsedDate);
}

// Get metadata summary
System.out.println("Metadata summary:");
System.out.println(metadata.getSummary());
```

## Notes

- `MetadataEnhanced` is a wrapper around `Metadata` and does not modify the original object.
- Provides richer query methods, simplifying common operations.
- Provides reasonable default values (such as empty strings) for methods that might return null.
- Provides date parsing functionality, supporting multiple common date formats.
- Includes support for accessibility metadata from the EPUB 3.3 specification.