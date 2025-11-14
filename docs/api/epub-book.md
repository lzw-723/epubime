# EpubBook

`EpubBook` 类代表一个解析后的 EPUB 书籍，包含了书籍的所有信息，包括元数据、章节和资源。

## 构造函数

```java
public EpubBook(Metadata metadata, List<EpubChapter> chapters, List<EpubResource> resources)
```
创建一个新的 EpubBook 实例。

参数:
- `metadata`: 书籍元数据
- `chapters`: 章节列表
- `resources`: 资源列表

## 主要方法

### getMetadata()
```java
public Metadata getMetadata()
```
获取书籍元数据。

返回:
- `Metadata`: 书籍元数据对象

### getChapters()
```java
public List<EpubChapter> getChapters()
```
获取书籍章节列表。

返回:
- `List<EpubChapter>`: 章节列表

### getResources()
```java
public List<EpubResource> getResources()
```
获取书籍资源列表。

返回:
- `List<EpubResource>`: 资源列表

### getCover()
```java
public EpubResource getCover()
```
获取书籍封面资源。

返回:
- `EpubResource`: 封面资源对象，如果未找到封面则返回 null

### getResourceByHref()
```java
public EpubResource getResourceByHref(String href)
```
根据 href 查找资源。

参数:
- `href`: 资源的 href 属性

返回:
- `EpubResource`: 找到的资源对象，如果未找到则返回 null

### getChapterById()
```java
public EpubChapter getChapterById(String id)
```
根据 ID 查找章节。

参数:
- `id`: 章节的 ID

返回:
- `EpubChapter`: 找到的章节对象，如果未找到则返回 null

### getChapterByPath()
```java
public EpubChapter getChapterByPath(String path)
```
根据路径查找章节。

参数:
- `path`: 章节的路径

返回:
- `EpubChapter`: 找到的章节对象，如果未找到则返回 null

### getTableOfContents()
```java
public List<EpubChapter> getTableOfContents()
```
获取书籍的目录结构。

返回:
- `List<EpubChapter>`: 目录章节列表

### hasCover()
```java
public boolean hasCover()
```
检查书籍是否有封面。

返回:
- `boolean`: 如果书籍有封面则返回 true，否则返回 false

### hasResources()
```java
public boolean hasResources()
```
检查书籍是否有资源文件。

返回:
- `boolean`: 如果书籍有资源文件则返回 true，否则返回 false

### hasChapters()
```java
public boolean hasChapters()
```
检查书籍是否有章节。

返回:
- `boolean`: 如果书籍有章节则返回 true，否则返回 false

### getResourceCount()
```java
public int getResourceCount()
```
获取资源文件数量。

返回:
- `int`: 资源文件数量

### getChapterCount()
```java
public int getChapterCount()
```
获取章节数量。

返回:
- `int`: 章节数量

### getAllChapters()
```java
public List<EpubChapter> getAllChapters()
```
获取所有章节，包括嵌套的子章节。

返回:
- `List<EpubChapter>`: 所有章节的列表

### getChaptersByMediaType()
```java
public List<EpubChapter> getChaptersByMediaType(String mediaType)
```
根据媒体类型获取章节。

参数:
- `mediaType`: 媒体类型（如 "application/xhtml+xml"）

返回:
- `List<EpubChapter>`: 匹配媒体类型的章节列表

### getResourcesByMediaType()
```java
public List<EpubResource> getResourcesByMediaType(String mediaType)
```
根据媒体类型获取资源。

参数:
- `mediaType`: 媒体类型（如 "image/jpeg"）

返回:
- `List<EpubResource>`: 匹配媒体类型的资源列表

### getImages()
```java
public List<EpubResource> getImages()
```
获取所有图片资源。

返回:
- `List<EpubResource>`: 图片资源列表

### getStylesheets()
```java
public List<EpubResource> getStylesheets()
```
获取所有样式表资源。

返回:
- `List<EpubResource>`: 样式表资源列表

### getFonts()
```java
public List<EpubResource> getFonts()
```
获取所有字体资源。

返回:
- `List<EpubResource>`: 字体资源列表

### getResourcePaths()
```java
public Set<String> getResourcePaths()
```
获取所有资源的路径集合。

返回:
- `Set<String>`: 资源路径集合

### getChapterPaths()
```java
public Set<String> getChapterPaths()
```
获取所有章节的路径集合。

返回:
- `Set<String>`: 章节路径集合