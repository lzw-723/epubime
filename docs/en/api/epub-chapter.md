# EpubChapter

`EpubChapter` class represents a chapter in an EPUB book, containing chapter title, content path, and other related information.

## Constructor

```java
public EpubChapter()
```
Creates a new EpubChapter instance.

```java
public EpubChapter(String title, String content)
```
Creates a new EpubChapter instance.

Parameters:
- `title`: Chapter title
- `content`: Chapter content path

```java
public EpubChapter(String id, String title, String content)
```
Creates a new EpubChapter instance.

Parameters:
- `id`: Chapter ID
- `title`: Chapter title
- `content`: Chapter content path

## Main Methods

### getId()
```java
public String getId()
```
Gets chapter ID.

Returns:
- `String`: Chapter ID

### setId()
```java
public void setId(String id)
```
Sets chapter ID.

Parameters:
- `id`: Chapter ID

### getTitle()
```java
public String getTitle()
```
Gets chapter title.

Returns:
- `String`: Chapter title

### setTitle()
```java
public void setTitle(String title)
```
Sets chapter title.

Parameters:
- `title`: Chapter title

### getContent()
```java
public String getContent()
```
Gets chapter content path.

Returns:
- `String`: Chapter content path

### setContent()
```java
public void setContent(String content)
```
Sets chapter content path.

Parameters:
- `content`: Chapter content path

### getHref()
```java
public String getHref()
```
Gets chapter's href attribute.

Returns:
- `String`: Chapter's href attribute

### setHref()
```java
public void setHref(String href)
```
Sets chapter's href attribute.

Parameters:
- `href`: Chapter's href attribute

### getMediaType()
```java
public String getMediaType()
```
Gets chapter's media type.

Returns:
- `String`: Chapter's media type (e.g., "application/xhtml+xml")

### setMediaType()
```java
public void setMediaType(String mediaType)
```
Sets chapter's media type.

Parameters:
- `mediaType`: Chapter's media type

### getParent()
```java
public EpubChapter getParent()
```
Gets chapter's parent chapter.

Returns:
- `EpubChapter`: Parent chapter object, or null if it's a top-level chapter

### setParent()
```java
public void setParent(EpubChapter parent)
```
Sets chapter's parent chapter.

Parameters:
- `parent`: Parent chapter object

### getChildren()
```java
public List<EpubChapter> getChildren()
```
Gets chapter's child chapter list.

Returns:
- `List<EpubChapter>`: Child chapter list

### addChild()
```java
public void addChild(EpubChapter child)
```
Adds child chapter.

Parameters:
- `child`: Child chapter object

### hasChildren()
```java
public boolean hasChildren()
```
Checks if chapter has child chapters.

Returns:
- `boolean`: Returns true if chapter has child chapters, otherwise false

### getDepth()
```java
public int getDepth()
```
Gets chapter's depth (level in the table of contents tree).

Returns:
- `int`: Chapter depth, root chapter is 0

### getPath()
```java
public String getPath()
```
Gets chapter's full path.

Returns:
- `String`: Chapter's full path

### getOrder()
```java
public int getOrder()
```
Gets chapter's order in the book.

Returns:
- `int`: Chapter order

### setOrder()
```java
public void setOrder(int order)
```
Sets chapter's order in the book.

Parameters:
- `order`: Chapter order

### getPlayOrder()
```java
public int getPlayOrder()
```
Gets chapter's play order (for TTS or audio).

Returns:
- `int`: Chapter play order

### setPlayOrder()
```java
public void setPlayOrder(int playOrder)
```
Sets chapter's play order (for TTS or audio).

Parameters:
- `playOrder`: Chapter play order

### getNavPoint()
```java
public Object getNavPoint()
```
Gets navigation point object (internal use).

Returns:
- `Object`: Navigation point object

### setNavPoint()
```java
public void setNavPoint(Object navPoint)
```
Sets navigation point object (internal use).

Parameters:
- `navPoint`: Navigation point object

### getAnchor()
```java
public String getAnchor()
```
Gets chapter's anchor (specific position within the chapter).

Returns:
- `String`: Chapter's anchor

### setAnchor()
```java
public void setAnchor(String anchor)
```
Sets chapter's anchor (specific position within the chapter).

Parameters:
- `anchor`: Chapter's anchor

### getChildrenCount()
```java
public int getChildrenCount()
```
Gets child chapter count.

Returns:
- `int`: Child chapter count

### getAllChildren()
```java
public List<EpubChapter> getAllChildren()
```
Gets all child chapters (including nested child chapters).

Returns:
- `List<EpubChapter>`: All child chapters list

### isNested()
```java
public boolean isNested()
```
Checks if chapter is nested (has parent chapter).

Returns:
- `boolean`: Returns true if chapter is nested, otherwise false

### findChildById()
```java
public EpubChapter findChildById(String id)
```
Finds specified ID chapter in child chapters.

Parameters:
- `id`: Chapter ID to find

Returns:
- `EpubChapter`: Found chapter object, or null if not found

### findChildByTitle()
```java
public EpubChapter findChildByTitle(String title)
```
Finds specified title chapter in child chapters.

Parameters:
- `title`: Chapter title to find

Returns:
- `EpubChapter`: Found chapter object, or null if not found

### findChildByPath()
```java
public EpubChapter findChildByPath(String path)
```
Finds specified path chapter in child chapters.

Parameters:
- `path`: Chapter path to find

Returns:
- `EpubChapter`: Found chapter object, or null if not found

### getContentText()
```java
public String getContentText(File epubFile) throws EpubException
```
Gets chapter content's text representation.

Parameters:
- `epubFile`: EPUB file containing the chapter

Returns:
- `String`: Chapter content's text representation

Throws:
- `EpubException`: Error occurred during content reading

### getContentDocument()
```java
public Document getContentDocument(File epubFile) throws EpubException
```
Gets chapter content's JSoup Document object.

Parameters:
- `epubFile`: EPUB file containing the chapter

Returns:
- `Document`: Chapter content's JSoup Document object

Throws:
- `EpubException`: Error occurred during content parsing

### getFirstParagraph()
```java
public String getFirstParagraph(File epubFile) throws EpubException
```
Gets chapter's first paragraph content.

Parameters:
- `epubFile`: EPUB file containing the chapter

Returns:
- `String`: Chapter's first paragraph content

Throws:
- `EpubException`: Error occurred during content reading

### getHeadings()
```java
public List<String> getHeadings(File epubFile) throws EpubException
```
Gets all headings in the chapter.

Parameters:
- `epubFile`: EPUB file containing the chapter

Returns:
- `List<String>`: Chapter's heading list

Throws:
- `EpubException`: Error occurred during content reading

### getImages()
```java
public List<String> getImages(File epubFile) throws EpubException
```
Gets all image paths referenced in the chapter.

Parameters:
- `epubFile`: EPUB file containing the chapter

Returns:
- `List<String>`: Chapter's image path list

Throws:
- `EpubException`: Error occurred during content reading

### getWordCount()
```java
public int getWordCount(File epubFile) throws EpubException
```
Gets chapter's word count.

Parameters:
- `epubFile`: EPUB file containing the chapter

Returns:
- `int`: Chapter's word count

Throws:
- `EpubException`: Error occurred during content reading

### toString()
```java
public String toString()
```
Returns string representation of chapter.

Returns:
- `String`: String representation of chapter