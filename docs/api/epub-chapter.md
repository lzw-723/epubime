# EpubChapter

`EpubChapter` 类代表 EPUB 书籍中的一个章节，包含章节的标题、内容路径和其他相关信息。

## 构造函数

```java
public EpubChapter()
```
创建一个新的 EpubChapter 实例。

```java
public EpubChapter(String title, String content)
```
创建一个新的 EpubChapter 实例。

参数:
- `title`: 章节标题
- `content`: 章节内容路径

```java
public EpubChapter(String id, String title, String content)
```
创建一个新的 EpubChapter 实例。

参数:
- `id`: 章节 ID
- `title`: 章节标题
- `content`: 章节内容路径

## 主要方法

### getId()
```java
public String getId()
```
获取章节 ID。

返回:
- `String`: 章节 ID

### setId()
```java
public void setId(String id)
```
设置章节 ID。

参数:
- `id`: 章节 ID

### getTitle()
```java
public String getTitle()
```
获取章节标题。

返回:
- `String`: 章节标题

### setTitle()
```java
public void setTitle(String title)
```
设置章节标题。

参数:
- `title`: 章节标题

### getContent()
```java
public String getContent()
```
获取章节内容路径。

返回:
- `String`: 章节内容路径

### setContent()
```java
public void setContent(String content)
```
设置章节内容路径。

参数:
- `content`: 章节内容路径

### getHref()
```java
public String getHref()
```
获取章节的 href 属性。

返回:
- `String`: 章节的 href 属性

### setHref()
```java
public void setHref(String href)
```
设置章节的 href 属性。

参数:
- `href`: 章节的 href 属性

### getMediaType()
```java
public String getMediaType()
```
获取章节的媒体类型。

返回:
- `String`: 章节的媒体类型（如 "application/xhtml+xml"）

### setMediaType()
```java
public void setMediaType(String mediaType)
```
设置章节的媒体类型。

参数:
- `mediaType`: 章节的媒体类型

### getParent()
```java
public EpubChapter getParent()
```
获取章节的父章节。

返回:
- `EpubChapter`: 父章节对象，如果是顶级章节则返回 null

### setParent()
```java
public void setParent(EpubChapter parent)
```
设置章节的父章节。

参数:
- `parent`: 父章节对象

### getChildren()
```java
public List<EpubChapter> getChildren()
```
获取章节的子章节列表。

返回:
- `List<EpubChapter>`: 子章节列表

### addChild()
```java
public void addChild(EpubChapter child)
```
添加子章节。

参数:
- `child`: 子章节对象

### hasChildren()
```java
public boolean hasChildren()
```
检查章节是否有子章节。

返回:
- `boolean`: 如果章节有子章节则返回 true，否则返回 false

### getDepth()
```java
public int getDepth()
```
获取章节的深度（在目录树中的层级）。

返回:
- `int`: 章节深度，根章节为 0

### getPath()
```java
public String getPath()
```
获取章节的完整路径。

返回:
- `String`: 章节的完整路径

### getOrder()
```java
public int getOrder()
```
获取章节在书籍中的顺序。

返回:
- `int`: 章节顺序

### setOrder()
```java
public void setOrder(int order)
```
设置章节在书籍中的顺序。

参数:
- `order`: 章节顺序

### getPlayOrder()
```java
public int getPlayOrder()
```
获取章节的播放顺序（用于 TTS 或音频）。

返回:
- `int`: 章节播放顺序

### setPlayOrder()
```java
public void setPlayOrder(int playOrder)
```
设置章节的播放顺序（用于 TTS 或音频）。

参数:
- `playOrder`: 章节播放顺序

### getNavPoint()
```java
public Object getNavPoint()
```
获取导航点对象（内部使用）。

返回:
- `Object`: 导航点对象

### setNavPoint()
```java
public void setNavPoint(Object navPoint)
```
设置导航点对象（内部使用）。

参数:
- `navPoint`: 导航点对象

### getAnchor()
```java
public String getAnchor()
```
获取章节的锚点（章节内的特定位置）。

返回:
- `String`: 章节的锚点

### setAnchor()
```java
public void setAnchor(String anchor)
```
设置章节的锚点（章节内的特定位置）。

参数:
- `anchor`: 章节的锚点

### getChildrenCount()
```java
public int getChildrenCount()
```
获取子章节的数量。

返回:
- `int`: 子章节数量

### getAllChildren()
```java
public List<EpubChapter> getAllChildren()
```
获取所有子章节（包括嵌套的子章节）。

返回:
- `List<EpubChapter>`: 所有子章节列表

### isNested()
```java
public boolean isNested()
```
检查章节是否为嵌套章节（有父章节）。

返回:
- `boolean`: 如果章节是嵌套章节则返回 true，否则返回 false

### findChildById()
```java
public EpubChapter findChildById(String id)
```
在子章节中查找指定 ID 的章节。

参数:
- `id`: 要查找的章节 ID

返回:
- `EpubChapter`: 找到的章节对象，如果未找到则返回 null

### findChildByTitle()
```java
public EpubChapter findChildByTitle(String title)
```
在子章节中查找指定标题的章节。

参数:
- `title`: 要查找的章节标题

返回:
- `EpubChapter`: 找到的章节对象，如果未找到则返回 null

### findChildByPath()
```java
public EpubChapter findChildByPath(String path)
```
在子章节中查找指定路径的章节。

参数:
- `path`: 要查找的章节路径

返回:
- `EpubChapter`: 找到的章节对象，如果未找到则返回 null

### getContentText()
```java
public String getContentText(File epubFile) throws EpubException
```
获取章节内容的文本表示。

参数:
- `epubFile`: 包含章节的 EPUB 文件

返回:
- `String`: 章节内容的文本表示

抛出:
- `EpubException`: 读取内容时发生错误

### getContentDocument()
```java
public Document getContentDocument(File epubFile) throws EpubException
```
获取章节内容的 JSoup Document 对象。

参数:
- `epubFile`: 包含章节的 EPUB 文件

返回:
- `Document`: 章节内容的 JSoup Document 对象

抛出:
- `EpubException`: 解析内容时发生错误

### getFirstParagraph()
```java
public String getFirstParagraph(File epubFile) throws EpubException
```
获取章节的第一段内容。

参数:
- `epubFile`: 包含章节的 EPUB 文件

返回:
- `String`: 章节的第一段内容

抛出:
- `EpubException`: 读取内容时发生错误

### getHeadings()
```java
public List<String> getHeadings(File epubFile) throws EpubException
```
获取章节中的所有标题。

参数:
- `epubFile`: 包含章节的 EPUB 文件

返回:
- `List<String>`: 章节中的标题列表

抛出:
- `EpubException`: 读取内容时发生错误

### getImages()
```java
public List<String> getImages(File epubFile) throws EpubException
```
获取章节中引用的所有图像路径。

参数:
- `epubFile`: 包含章节的 EPUB 文件

返回:
- `List<String>`: 章节中图像路径的列表

抛出:
- `EpubException`: 读取内容时发生错误

### getWordCount()
```java
public int getWordCount(File epubFile) throws EpubException
```
获取章节的字数。

参数:
- `epubFile`: 包含章节的 EPUB 文件

返回:
- `int`: 章节的字数

抛出:
- `EpubException`: 读取内容时发生错误

### toString()
```java
public String toString()
```
返回章节的字符串表示。

返回:
- `String`: 章节的字符串表示