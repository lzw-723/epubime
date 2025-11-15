# EpubBookEnhanced

`EpubBookEnhanced` 是 EPUBime 库中的增强书籍对象，提供了更多便利方法和改进的可用性。该类是对基础 `EpubBook` 类的包装，提供了更丰富的功能和更简单的 API。

## 类定义

```java
public class EpubBookEnhanced
```

## 构造方法

### EpubBookEnhanced(EpubBook book, File epubFile)
创建一个增强的书籍对象。

**参数:**
- `book`: 基础 EpubBook 对象
- `epubFile`: EPUB 文件

## 方法

### getMetadata()
获取元数据，保证不为 null。

**返回值:**
- `Metadata`: 元数据对象

### getTitle()
获取书籍标题，如果标题为空则返回空字符串。

**返回值:**
- `String`: 书籍标题

### getAuthor()
获取书籍作者，如果作者为空则返回空字符串。

**返回值:**
- `String`: 书籍作者

### getLanguage()
获取书籍语言，如果语言为空则返回空字符串。

**返回值:**
- `String`: 书籍语言

### getAllChapters()
获取所有章节的扁平列表（包括嵌套章节）。

**返回值:**
- `List<EpubChapter>`: 所有章节的列表

### findChapterByTitle(String title)
根据标题查找章节（不区分大小写）。

**参数:**
- `title`: 要查找的章节标题

**返回值:**
- `EpubChapter`: 找到的章节，如果未找到则返回 null

### findChaptersByContentPattern(String pattern)
根据内容路径模式查找章节。

**参数:**
- `pattern`: 要匹配的内容路径模式

**返回值:**
- `List<EpubChapter>`: 匹配的章节列表

### getChaptersByType(String type)
根据导航类型获取章节。

**参数:**
- `type`: 导航类型 ("ncx", "nav", "landmarks", "page-list")

**返回值:**
- `List<EpubChapter>`: 指定导航类型的章节列表

### getFirstChapter()
获取第一个章节。

**返回值:**
- `EpubChapter`: 第一个章节，如果没有章节则返回 null

### getLastChapter()
获取最后一个章节。

**返回值:**
- `EpubChapter`: 最后一个章节，如果没有章节则返回 null

### getChapter(int index)
根据索引获取章节。

**参数:**
- `index`: 章节索引（从 0 开始）

**返回值:**
- `EpubChapter`: 指定索引的章节，如果索引无效则返回 null

### getChapterCount()
获取总章节数（包括嵌套章节）。

**返回值:**
- `int`: 总章节数

### getResourcesByType(String mimeType)
根据类型获取资源。

**参数:**
- `mimeType`: 要筛选的 MIME 类型

**返回值:**
- `List<EpubResource>`: 指定类型的资源列表

### getImageResources()
获取图像资源。

**返回值:**
- `List<EpubResource>`: 图像资源列表

### getCssResources()
获取 CSS 资源。

**返回值:**
- `List<EpubResource>`: CSS 资源列表

### getJsResources()
获取 JavaScript 资源。

**返回值:**
- `List<EpubResource>`: JavaScript 资源列表

### getCover()
获取封面资源（带回退机制）。

**返回值:**
- `EpubResource`: 封面资源，如果未找到则返回 null

### hasCover()
检查书籍是否有封面。

**返回值:**
- `boolean`: 如果有封面返回 true

### processChapterContent(EpubChapter chapter, Consumer<InputStream> processor)
使用流处理处理章节内容。

**参数:**
- `chapter`: 要处理的章节
- `processor`: 内容处理器

**异常:**
- `BaseEpubException`: 处理失败时抛出

### getChapterContentAsString(EpubChapter chapter)
获取章节内容为字符串。

**参数:**
- `chapter`: 章节对象

**返回值:**
- `String`: 章节内容字符串，如果失败则返回 null

### loadAllResources()
将所有资源数据加载到内存中。

**异常:**
- `IOException`: 加载失败时抛出

### getOriginalBook()
获取底层的 EpubBook 实例。

**返回值:**
- `EpubBook`: 原始 EpubBook 的副本

### getBookInfo()
获取基本书籍信息。

**返回值:**
- `String`: 格式化的书籍信息

## 使用示例

```java
// 创建增强书籍对象
EpubBook book = EpubReader.fromFile(new File("book.epub")).parse();
EpubBookEnhanced enhancedBook = new EpubBookEnhanced(book, new File("book.epub"));

// 获取书籍信息
System.out.println("标题: " + enhancedBook.getTitle());
System.out.println("作者: " + enhancedBook.getAuthor());
System.out.println("语言: " + enhancedBook.getLanguage());

// 获取所有章节
List<EpubChapter> allChapters = enhancedBook.getAllChapters();
System.out.println("总章节数: " + enhancedBook.getChapterCount());

// 查找特定章节
EpubChapter chapter = enhancedBook.findChapterByTitle("Introduction");
if (chapter != null) {
    System.out.println("找到章节: " + chapter.getTitle());
}

// 获取特定类型的资源
List<EpubResource> images = enhancedBook.getImageResources();
List<EpubResource> cssFiles = enhancedBook.getCssResources();

// 检查是否有封面
if (enhancedBook.hasCover()) {
    EpubResource cover = enhancedBook.getCover();
    System.out.println("封面文件: " + cover.getHref());
}

// 获取书籍信息摘要
System.out.println(enhancedBook.getBookInfo());
```

## 注意事项

- `EpubBookEnhanced` 是对 `EpubBook` 的包装，不会修改原始对象。
- 提供了更丰富的查询和处理方法，简化了常见操作。
- 对于可能返回 null 的方法，提供了合理的默认值（如空字符串）。