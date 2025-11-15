# API Reference

## MetadataEnhanced

`MetadataEnhanced` is an enhanced metadata object class that provides more convenient methods and advanced features.

### Constructor

```java
public MetadataEnhanced(Metadata metadata)
```
Creates an enhanced metadata object.

Parameters:
- `metadata`: Base Metadata object

### Basic Information Methods

#### getTitle()
```java
public String getTitle()
```
Gets the title with fallback to empty string.

Returns:
- `String`: Book title

#### getAuthor()
```java
public String getAuthor()
```
Gets the author with fallback to empty string.

Returns:
- `String`: Book author

#### getLanguage()
```java
public String getLanguage()
```
Gets the language with fallback to empty string.

Returns:
- `String`: Language code

#### getSummary()
```java
public String getSummary()
```
Gets a formatted summary of the metadata.

Returns:
- `String`: Formatted metadata summary

### Date and Time Methods

#### getParsedDate()
```java
public LocalDate getParsedDate()
```
Gets the parsed publication date.

Returns:
- `LocalDate`: Parsed date, or null if parsing fails

### Identifier Methods

#### getISBN()
```java
public String getISBN()
```
Gets the ISBN identifier.

Returns:
- `String`: ISBN, or null if not found

#### getUUID()
```java
public String getUUID()
```
Gets the UUID identifier.

Returns:
- `String`: UUID, or null if not found

### Content Methods

#### getSubjects()
```java
public List<String> getSubjects()
```
Gets the subject/keywords list.

Returns:
- `List<String>`: Subjects list

#### getKeywords()
```java
public List<String> getKeywords()
```
Gets the keywords list (may be same as subjects).

Returns:
- `List<String>`: Keywords list

### Accessibility Methods

#### hasAccessibilityFeatures()
```java
public boolean hasAccessibilityFeatures()
```
Checks if accessibility features are defined.

Returns:
- `boolean`: true if accessibility features exist

#### getAccessibilityFeatures()
```java
public String getAccessibilityFeatures()
```
Gets accessibility features description.

Returns:
- `String`: Accessibility features, or null

#### getAccessibilitySummary()
```java
public String getAccessibilitySummary()
```
Gets accessibility summary.

Returns:
- `String`: Accessibility summary, or null

### Publisher and Rights Methods

#### hasPublisher()
```java
public boolean hasPublisher()
```
Checks if publisher is defined.

Returns:
- `boolean`: true if publisher exists

#### hasRights()
```java
public boolean hasRights()
```
Checks if rights information is defined.

Returns:
- `boolean`: true if rights exist

### Content Checks

#### hasCover()
```java
public boolean hasCover()
```
Checks if cover information is available.

Returns:
- `boolean`: true if cover info exists

#### hasDescription()
```java
public boolean hasDescription()
```
Checks if description is available.

Returns:
- `boolean`: true if description exists

#### hasSubjects()
```java
public boolean hasSubjects()
```
Checks if subjects are available.

Returns:
- `boolean`: true if subjects exist

### Usage Examples

```java
// Create enhanced metadata object
Metadata metadata = EpubReader.fromFile(epubFile).parseMetadata();
MetadataEnhanced enhancedMetadata = new MetadataEnhanced(metadata);

// Get basic information
System.out.println("Title: " + enhancedMetadata.getTitle());
System.out.println("Author: " + enhancedMetadata.getAuthor());
System.out.println("Language: " + enhancedMetadata.getLanguage());

// Get parsed date
LocalDate date = enhancedMetadata.getParsedDate();
if (date != null) {
    System.out.println("Publication date: " + date);
}

// Get identifiers
String isbn = enhancedMetadata.getISBN();
if (isbn != null) {
    System.out.println("ISBN: " + isbn);
}

String uuid = enhancedMetadata.getUUID();
if (uuid != null) {
    System.out.println("UUID: " + uuid);
}

// Get subjects and keywords
List<String> subjects = enhancedMetadata.getSubjects();
if (!subjects.isEmpty()) {
    System.out.println("Subjects: " + subjects);
}

List<String> keywords = enhancedMetadata.getKeywords();
if (!keywords.isEmpty()) {
    System.out.println("Keywords: " + keywords);
}

// Check accessibility
if (enhancedMetadata.hasAccessibilityFeatures()) {
    System.out.println("Accessibility features: " + enhancedMetadata.getAccessibilityFeatures());
    System.out.println("Accessibility summary: " + enhancedMetadata.getAccessibilitySummary());
}

// Get summary
String summary = enhancedMetadata.getSummary();
System.out.println("Metadata summary: " + summary);

// Check various properties
System.out.println("Has cover: " + enhancedMetadata.hasCover());
System.out.println("Has description: " + enhancedMetadata.hasDescription());
System.out.println("Has subjects: " + enhancedMetadata.hasSubjects());
System.out.println("Has publisher: " + enhancedMetadata.hasPublisher());
System.out.println("Has rights: " + enhancedMetadata.hasRights());
```