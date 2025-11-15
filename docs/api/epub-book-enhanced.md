# API 参考

## EpubBookEnhanced

`EpubBookEnhanced` 是增强的书籍对象类，提供了更多便利方法和高级功能。

### 构造函数

```java
public EpubBookEnhanced(EpubBook book, File epubFile)
```
创建增强的书籍对象。

参数:
- `book`: 基础 EpubBook 对象
- `epubFile`: EPUB 文件

### 基础信息方法

#### getTitle()
```java
public String getTitle()
```
获取书籍标题。

返回:
- `String`: 书籍标题

#### getAuthor()
```java
public String getAuthor()
```
获取书籍作者。

返回:
- `String`: 书籍作者

#### getLanguage()
```java
public String getLanguage()
```
获取书籍语言。

返回:
- `String`: 书籍语言代码

#### getBookInfo()
```java
public String getBookInfo()
```
获取书籍的完整信息字符串。

返回:
- `String`: 格式化的书籍信息

### 章节相关方法

#### getAllChapters()
```java
public List<EpubChapter> getAllChapters()
```
获取所有章节，包括嵌套章节（扁平化列表）。

返回:
- `List<EpubChapter>`: 所有章节列表

#### getChapterCount()
```java
public int getChapterCount()
```
获取章节总数（包括嵌套章节）。

返回:
- `int`: 章节总数

#### getChapter()
```java
public EpubChapter getChapter(int index)
```
按索引获取章节。

参数:
- `index`: 章节索引

返回:
- `EpubChapter`: 章节对象，如果索引无效则返回 null

#### findChapterByTitle()
```java
public EpubChapter findChapterByTitle(String title)
```
按标题查找章节。

参数:
- `title`: 章节标题

返回:
- `EpubChapter`: 找到的章节对象，如果未找到则返回 null

#### findChaptersByContentPattern()
```java
public List<EpubChapter> findChaptersByContentPattern(String pattern)
```
按内容路径模式查找章节。

参数:
- `pattern`: 内容路径模式

返回:
- `List<EpubChapter>`: 匹配的章节列表

### 资源相关方法

#### getImageResources()
```java
public List<EpubResource> getImageResources()
```
获取所有图片资源。

返回:
- `List<EpubResource>`: 图片资源列表

#### getCssResources()
```java
public List<EpubResource> getCssResources()
```
获取所有 CSS 资源。

返回:
- `List<EpubResource>`: CSS 资源列表

#### getJsResources()
```java
public List<EpubResource> getJsResources()
```
获取所有 JavaScript 资源。

返回:
- `List<EpubResource>`: JavaScript 资源列表

#### getResourcesByType()
```java
public List<EpubResource> getResourcesByType(String mediaType)
```
按媒体类型获取资源。

参数:
- `mediaType`: 媒体类型（如 "image/jpeg", "text/css"）

返回:
- `List<EpubResource>`: 匹配的资源列表



### 封面相关方法

#### hasCover()
```java
public boolean hasCover()
```
检查书籍是否有封面。

返回:
- `boolean`: true 如果有封面，false 否则

#### processChapterContent()
```java
public void processChapterContent(EpubChapter chapter, Consumer<InputStream> processor) throws BaseEpubException
```
流式处理章节内容。

参数:
- `chapter`: 要处理的章节
- `processor`: 内容处理器

抛出:
- `BaseEpubException`: 处理失败时抛出

#### getChapterContentAsString()
```java
public String getChapterContentAsString(EpubChapter chapter)
```
获取章节内容作为字符串。

参数:
- `chapter`: 章节对象

返回:
- `String`: 章节内容字符串，失败时返回 null

#### getCover()
```java
public EpubResource getCover()
```
获取封面资源。

返回:
- `EpubResource`: 封面资源，如果未找到则返回 null

### 导航相关方法

#### getChaptersByType()
```java
public List<EpubChapter> getChaptersByType(String type)
```
按导航类型获取章节列表。

参数:
- `type`: 导航类型 ("ncx", "nav", "landmarks", "page-list")

返回:
- `List<EpubChapter>`: 指定类型的章节列表

### 其他方法

#### loadAllResources()
```java
public void loadAllResources() throws IOException
```
将所有资源数据加载到内存中。

抛出:
- `IOException`: 加载失败时抛出

#### getOriginalBook()
```java
public EpubBook getOriginalBook()
```
获取底层的 EpubBook 实例。

返回:
- `EpubBook`: 原始 EpubBook 对象的副本

#### getBookInfo()
```java
public String getBookInfo()
```
获取书籍的完整信息字符串。

返回:
- `String`: 格式化的书籍信息

### 使用示例

```java
// 创建增强书籍对象
EpubBook book = EpubReader.fromFile(epubFile).parse();
EpubBookEnhanced enhancedBook = new EpubBookEnhanced(book, epubFile);

// 获取书籍信息
System.out.println("标题: " + enhancedBook.getTitle());
System.out.println("作者: " + enhancedBook.getAuthor());
System.out.println("章节数: " + enhancedBook.getChapterCount());

// 查找章节
EpubChapter chapter = enhancedBook.findChapterByTitle("前言");
if (chapter != null) {
    System.out.println("找到章节: " + chapter.getTitle());
}

// 获取特定类型资源
List<EpubResource> images = enhancedBook.getImageResources();
System.out.println("图片资源数: " + images.size());

List<EpubResource> cssFiles = enhancedBook.getCssResources();
System.out.println("CSS 文件数: " + cssFiles.size());

// 检查封面
if (enhancedBook.hasCover()) {
    System.out.println("书籍有封面");
    EpubResource cover = enhancedBook.getCover();
    if (cover != null) {
        System.out.println("封面类型: " + cover.getType());
    }
}

// 获取所有章节
List<EpubChapter> allChapters = enhancedBook.getAllChapters();
for (EpubChapter ch : allChapters) {
    System.out.println("章节: " + ch.getTitle());
}
```