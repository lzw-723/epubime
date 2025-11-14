# EpubParser

`EpubParser` is the main parser class of the EPUBime library, responsible for parsing EPUB files and generating `EpubBook` objects.

### Constructor

```java
public EpubParser(File epubFile)
```
Creates a new EPUB parser instance.

Parameters:
- `epubFile`: The EPUB file to parse

### Main Methods

#### parse()
```java
public EpubBook parse() throws EpubException
```
Parses the EPUB file and returns an EpubBook object. If results are cached, returns the cached parsing result.

Returns:
- `EpubBook`: The parsed EPUB book object

Throws:
- `EpubException`: Error occurred during parsing

#### parseWithoutCache()
```java
public EpubBook parseWithoutCache() throws EpubException
```
Parses the EPUB file and returns an EpubBook object, skipping cache.

Returns:
- `EpubBook`: The parsed EPUB book object

Throws:
- `EpubException`: Error occurred during parsing

#### readEpubContent()
```java
public static String readEpubContent(File epubFile, String entryPath) throws EpubException
```
Directly reads specific content from an EPUB file.

Parameters:
- `epubFile`: The EPUB file
- `entryPath`: The internal file path to read

Returns:
- `String`: File content

Throws:
- `EpubException`: Error occurred during reading

#### processHtmlChapterContent()
```java
public static void processHtmlChapterContent(File epubFile, String entryPath, ContentProcessor processor) throws EpubException
```
Streaming process HTML chapter content.

Parameters:
- `epubFile`: The EPUB file
- `entryPath`: Chapter file path
- `processor`: Content processor

Throws:
- `EpubException`: Error occurred during processing

#### processMultipleHtmlChapters()
```java
public static void processMultipleHtmlChapters(File epubFile, List<String> entryPaths, ChapterContentProcessor processor) throws EpubException
```
Batch streaming process multiple HTML chapters.

Parameters:
- `epubFile`: The EPUB file
- `entryPaths`: List of chapter file paths
- `processor`: Chapter content processor

Throws:
- `EpubException`: Error occurred during processing

#### parseMetadata()
```java
public static Metadata parseMetadata(String opfContent) throws EpubException
```
Parses OPF file content and extracts metadata.

Parameters:
- `opfContent`: OPF file content

Returns:
- `Metadata`: The parsed metadata object

Throws:
- `EpubException`: Error occurred during parsing

#### parseNcx()
```java
public static List<EpubChapter> parseNcx(String ncxContent) throws EpubException
```
Parses NCX file content and extracts chapter information.

Parameters:
- `ncxContent`: NCX file content

Returns:
- `List<EpubChapter>`: The parsed chapter list

Throws:
- `EpubException`: Error occurred during parsing

#### parseNav()
```java
public static List<EpubChapter> parseNav(String navContent) throws EpubException
```
Parses NAV file content and extracts chapter information.

Parameters:
- `navContent`: NAV file content

Returns:
- `List<EpubChapter>`: The parsed chapter list

Throws:
- `EpubException`: Error occurred during parsing

#### parseNavByType()
```java
public static List<EpubChapter> parseNavByType(String navContent, String type) throws EpubException
```
Parses NAV file content by type.

Parameters:
- `navContent`: NAV file content
- `type`: Navigation type (e.g., "landmarks", "page-list")

Returns:
- `List<EpubChapter>`: The parsed chapter list

Throws:
- `EpubException`: Error occurred during parsing

#### getNcxPath()
```java
public static String getNcxPath(String opfContent, String basePath) throws EpubException
```
Gets the NCX file path from OPF file.

Parameters:
- `opfContent`: OPF file content
- `basePath`: Base path

Returns:
- `String`: NCX file path

Throws:
- `EpubException`: Error occurred during parsing

#### getNavPath()
```java
public static String getNavPath(String opfContent, String basePath) throws EpubException
```
Gets the NAV file path from OPF file.

Parameters:
- `opfContent`: OPF file content
- `basePath`: Base path

Returns:
- `String`: NAV file path, or null if not found

Throws:
- `EpubException`: Error occurred during parsing

#### parseResources()
```java
public static List<EpubResource> parseResources(String opfContent, String basePath, File epubFile) throws EpubException
```
Parses OPF file content and extracts all resource files.

Parameters:
- `opfContent`: OPF file content
- `basePath`: Base path
- `epubFile`: EPUB file

Returns:
- `List<EpubResource>`: The parsed resource list

Throws:
- `EpubException`: Error occurred during parsing