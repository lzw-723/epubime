# Exception Classes

EPUBime provides a complete exception handling system, with all exceptions inheriting from the `EpubException` base class.

## Inheritance Hierarchy

```
Exception
 └── EpubException
     ├── EpubParseException      (Parse Exception)
     ├── EpubFormatException     (Format Exception)
     ├── EpubPathValidationException (Path Validation Exception)
     ├── EpubResourceException   (Resource Exception)
     └── EpubZipException        (ZIP Exception)
```

## EpubException

`EpubException` is the base class for all EPUB-related exceptions.

### Constructor

```java
public EpubException(String message)
```
Creates a new EpubException instance.

Parameters:
- `message`: Exception message

```java
public EpubException(String message, Throwable cause)
```
Creates a new EpubException instance.

Parameters:
- `message`: Exception message
- `cause`: Original exception

```java
public EpubException(String fileName, String path, String message)
```
Creates a new EpubException instance with filename and path information.

Parameters:
- `fileName`: Filename
- `path`: File path
- `message`: Exception message

```java
public EpubException(String fileName, String path, String message, Throwable cause)
```
Creates a new EpubException instance with filename, path information, and original exception.

Parameters:
- `fileName`: Filename
- `path`: File path
- `message`: Exception message
- `cause`: Original exception

### Main Methods

#### getFileName()
```java
public String getFileName()
```
Gets the name of the related file.

Returns:
- `String`: Filename

#### getPath()
```java
public String getPath()
```
Gets the path of the related file.

Returns:
- `String`: File path

#### getFullMessage()
```java
public String getFullMessage()
```
Gets the complete error message including filename and path.

Returns:
- `String`: Complete error message

## EpubParseException

Parse exception, used to handle errors during EPUB file parsing.

### Constructor

```java
public EpubParseException(String message)
```
Creates a new EpubParseException instance.

Parameters:
- `message`: Exception message

```java
public EpubParseException(String fileName, String path, String message)
```
Creates a new EpubParseException instance with filename and path information.

Parameters:
- `fileName`: Filename
- `path`: File path
- `message`: Exception message

```java
public EpubParseException(String fileName, String path, String message, Throwable cause)
```
Creates a new EpubParseException instance with filename, path information, and original exception.

Parameters:
- `fileName`: Filename
- `path`: File path
- `message`: Exception message
- `cause`: Original exception

## EpubFormatException

Format exception, used to handle cases where EPUB format does not comply with specifications.

### Constructor

```java
public EpubFormatException(String message)
```
Creates a new EpubFormatException instance.

Parameters:
- `message`: Exception message

```java
public EpubFormatException(String fileName, String path, String message)
```
Creates a new EpubFormatException instance with filename and path information.

Parameters:
- `fileName`: Filename
- `path`: File path
- `message`: Exception message

```java
public EpubFormatException(String fileName, String path, String message, Throwable cause)
```
Creates a new EpubFormatException instance with filename, path information, and original exception.

Parameters:
- `fileName`: Filename
- `path`: File path
- `message`: Exception message
- `cause`: Original exception

## EpubPathValidationException

Path validation exception, used to prevent directory traversal attacks.

### Constructor

```java
public EpubPathValidationException(String path, String message)
```
Creates a new EpubPathValidationException instance.

Parameters:
- `path`: Invalid path
- `message`: Exception message

```java
public EpubPathValidationException(String path, String message, Throwable cause)
```
Creates a new EpubPathValidationException instance.

Parameters:
- `path`: Invalid path
- `message`: Exception message
- `cause`: Original exception

## EpubResourceException

Resource exception, used to handle resource file access errors.

### Constructor

```java
public EpubResourceException(String message)
```
Creates a new EpubResourceException instance.

Parameters:
- `message`: Exception message

```java
public EpubResourceException(String path, String message)
```
Creates a new EpubResourceException instance with resource path information.

Parameters:
- `path`: Resource path
- `message`: Exception message

```java
public EpubResourceException(String path, String message, Throwable cause)
```
Creates a new EpubResourceException instance with resource path and original exception.

Parameters:
- `path`: Resource path
- `message`: Exception message
- `cause`: Original exception

## EpubZipException

ZIP exception, used to handle ZIP file operation errors.

### Constructor

```java
public EpubZipException(String message)
```
Creates a new EpubZipException instance.

Parameters:
- `message`: Exception message

```java
public EpubZipException(String fileName, String message)
```
Creates a new EpubZipException instance with filename information.

Parameters:
- `fileName`: Filename
- `message`: Exception message

```java
public EpubZipException(String fileName, String message, Throwable cause)
```
Creates a new EpubZipException instance with filename and original exception.

Parameters:
- `fileName`: Filename
- `message`: Exception message
- `cause`: Original exception

## Usage Example

```java
try {
    EpubParser parser = new EpubParser(new File("book.epub"));
    EpubBook book = parser.parse();
} catch (EpubParseException e) {
    // Handle parse exception
    System.err.println("Parse error: " + e.getFullMessage());
    e.printStackTrace();
} catch (EpubFormatException e) {
    // Handle format exception
    System.err.println("Format error: " + e.getFullMessage());
} catch (EpubPathValidationException e) {
    // Handle path validation exception
    System.err.println("Path validation failed: " + e.getMessage() + ", Path: " + e.getPath());
} catch (EpubResourceException e) {
    // Handle resource exception
    System.err.println("Resource error: " + e.getMessage() + ", Path: " + e.getPath());
} catch (EpubZipException e) {
    // Handle ZIP exception
    System.err.println("ZIP error: " + e.getFullMessage());
} catch (Exception e) {
    // Handle other exceptions
    System.err.println("Unexpected error: " + e.getMessage());
    e.printStackTrace();
}
```