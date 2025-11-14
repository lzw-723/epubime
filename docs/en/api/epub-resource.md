# EpubResource

`EpubResource` class represents a resource file in an EPUB book, such as images, CSS, fonts, etc.

## Constructor

```java
public EpubResource()
```
Creates a new EpubResource instance.

```java
public EpubResource(String href, String mediaType)
```
Creates a new EpubResource instance.

Parameters:
- `href`: Resource's href attribute
- `mediaType`: Resource's media type

```java
public EpubResource(String id, String href, String mediaType, byte[] data)
```
Creates a new EpubResource instance.

Parameters:
- `id`: Resource ID
- `href`: Resource's href attribute
- `mediaType`: Resource's media type
- `data`: Resource data

## Main Methods

### getId()
```java
public String getId()
```
Gets resource ID.

Returns:
- `String`: Resource ID

### setId()
```java
public void setId(String id)
```
Sets resource ID.

Parameters:
- `id`: Resource ID

### getHref()
```java
public String getHref()
```
Gets resource's href attribute.

Returns:
- `String`: Resource's href attribute

### setHref()
```java
public void setHref(String href)
```
Sets resource's href attribute.

Parameters:
- `href`: Resource's href attribute

### getMediaType()
```java
public String getMediaType()
```
Gets resource's media type.

Returns:
- `String`: Resource's media type (e.g., "image/jpeg", "text/css")

### setMediaType()
```java
public void setMediaType(String mediaType)
```
Sets resource's media type.

Parameters:
- `mediaType`: Resource's media type

### getData()
```java
public byte[] getData()
```
Gets resource data.

Returns:
- `byte[]`: Resource data

### setData()
```java
public void setData(byte[] data)
```
Sets resource data.

Parameters:
- `data`: Resource data

### getFileName()
```java
public String getFileName()
```
Gets resource filename (extracted from href).

Returns:
- `String`: Resource filename

### getExtension()
```java
public String getExtension()
```
Gets resource file extension.

Returns:
- `String`: Resource file extension (e.g., "jpg", "css")

### getSize()
```java
public long getSize()
```
Gets resource size (in bytes).

Returns:
- `long`: Resource size

### isImage()
```java
public boolean isImage()
```
Checks if resource is an image.

Returns:
- `boolean`: Returns true if resource is an image, otherwise false

### isStylesheet()
```java
public boolean isStylesheet()
```
Checks if resource is a stylesheet.

Returns:
- `boolean`: Returns true if resource is a stylesheet, otherwise false

### isScript()
```java
public boolean isScript()
```
Checks if resource is a script file.

Returns:
- `boolean`: Returns true if resource is a script file, otherwise false

### isFont()
```java
public boolean isFont()
```
Checks if resource is a font file.

Returns:
- `boolean`: Returns true if resource is a font file, otherwise false

### isAudio()
```java
public boolean isAudio()
```
Checks if resource is an audio file.

Returns:
- `boolean`: Returns true if resource is an audio file, otherwise false

### isVideo()
```java
public boolean isVideo()
```
Checks if resource is a video file.

Returns:
- `boolean`: Returns true if resource is a video file, otherwise false

### saveToFile()
```java
public void saveToFile(String filePath) throws EpubResourceException
```
Saves resource to file.

Parameters:
- `filePath`: Target file path

Throws:
- `EpubResourceException`: Error occurred during saving

### saveToDirectory()
```java
public void saveToDirectory(String directoryPath) throws EpubResourceException
```
Saves resource to specified directory.

Parameters:
- `directoryPath`: Target directory path

Throws:
- `EpubResourceException`: Error occurred during saving

### toInputStream()
```java
public InputStream toInputStream()
```
Converts resource data to input stream.

Returns:
- `InputStream`: Input stream of resource data

### toBufferedImage()
```java
public BufferedImage toBufferedImage() throws EpubResourceException
```
Converts image resource to BufferedImage object.

Returns:
- `BufferedImage`: BufferedImage object of image

Throws:
- `EpubResourceException`: Error occurred during conversion (if resource is not valid image)

### toCSSDocument()
```java
public String toCSSDocument() throws EpubResourceException
```
Converts CSS resource to string.

Returns:
- `String`: CSS content string

Throws:
- `EpubResourceException`: If resource is not CSS file

### getMimeType()
```java
public String getMimeType()
```
Gets resource's MIME type.

Returns:
- `String`: MIME type

### isBinary()
```java
public boolean isBinary()
```
Checks if resource is a binary file.

Returns:
- `boolean`: Returns true if resource is a binary file, otherwise false

### getTextContent()
```java
public String getTextContent() throws EpubResourceException
```
Gets text resource content.

Returns:
- `String`: Text content

Throws:
- `EpubResourceException`: If resource is not text file or reading fails

### updateContent()
```java
public void updateContent(String newContent) throws EpubResourceException
```
Updates text resource content.

Parameters:
- `newContent`: New text content

Throws:
- `EpubResourceException`: If resource is not text file or update fails

### getAbsolutePath()
```java
public String getAbsolutePath()
```
Gets resource's absolute path.

Returns:
- `String`: Resource's absolute path

### getRelativePath()
```java
public String getRelativePath(String basePath)
```
Gets resource's relative path to specified base path.

Parameters:
- `basePath`: Base path

Returns:
- `String`: Relative path

### isValid()
```java
public boolean isValid()
```
Checks if resource is valid (has valid href and media type).

Returns:
- `boolean`: Returns true if resource is valid, otherwise false

### toString()
```java
public String toString()
```
Returns string representation of resource.

Returns:
- `String`: String representation of resource