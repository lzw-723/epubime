# EpubReader

`EpubReader` 是 EPUBime 库的现代 Fluent API 入口点，提供了一种链式调用的方式来配置和解析 EPUB 文件。

## 类定义

```java
public class EpubReader
```

## 构造方法

该类不能直接实例化，需要使用静态工厂方法。

## 静态方法

### fromFile(File epubFile)
创建一个使用默认配置的 EpubReader 实例。

**参数:**
- `epubFile`: 要解析的 EPUB 文件

**返回值:**
- `EpubReader`: 新的 EpubReader 实例

**异常:**
- `IllegalArgumentException`: 如果 epubFile 为 null

### fromFile(String filePath)
从文件路径创建一个使用默认配置的 EpubReader 实例。

**参数:**
- `filePath`: EPUB 文件的路径

**返回值:**
- `EpubReader`: 新的 EpubReader 实例

**异常:**
- `IllegalArgumentException`: 如果 filePath 为 null

### fromFile(File epubFile, EpubReaderConfig config)
创建一个使用自定义配置的 EpubReader 实例。

**参数:**
- `epubFile`: 要解析的 EPUB 文件
- `config`: 配置对象

**返回值:**
- `EpubReader`: 新的 EpubReader 实例

**异常:**
- `IllegalArgumentException`: 如果 epubFile 或 config 为 null

## 实例方法

### parse()
解析 EPUB 文件并返回 EpubBook 对象。

**返回值:**
- `EpubBook`: 解析后的 EPUB 书籍对象

**异常:**
- `BaseEpubException`: 解析过程中发生的异常
- `IOException`: I/O 操作失败
- `EpubPathValidationException`: 路径验证失败

### parseMetadata()
仅解析 EPUB 文件的元数据。

**返回值:**
- `Metadata`: EPUB 元数据

**异常:**
- `BaseEpubException`: 解析过程中发生的异常
- `IOException`: I/O 操作失败
- `EpubPathValidationException`: 路径验证失败

### parseTableOfContents()
仅解析 EPUB 文件的目录结构。

**返回值:**
- `List<EpubChapter>`: 章节列表

**异常:**
- `BaseEpubException`: 解析过程中发生的异常
- `IOException`: I/O 操作失败
- `EpubPathValidationException`: 路径验证失败

### streamChapters(BiConsumer<EpubChapter, InputStream> processor)
流式处理章节内容，不将整个内容加载到内存中。

**参数:**
- `processor`: 处理每个章节和其内容流的消费者函数

**异常:**
- `BaseEpubException`: 处理过程中发生的异常
- `IOException`: I/O 操作失败
- `EpubPathValidationException`: 路径验证失败

### streamChapter(String chapterId, Consumer<InputStream> processor)
流式处理特定章节的内容。

**参数:**
- `chapterId`: 要处理的章节 ID
- `processor`: 处理章节内容流的消费者函数

**异常:**
- `BaseEpubException`: 处理过程中发生的异常
- `EpubPathValidationException`: 路径验证失败
- `IOException`: I/O 操作失败

### processResources(Function<EpubResource, Void> processor)
处理所有资源文件。

**参数:**
- `processor`: 处理每个资源的函数

**异常:**
- `BaseEpubException`: 处理过程中发生的异常
- `IOException`: I/O 操作失败
- `EpubPathValidationException`: 路径验证失败

### getResource(String resourceId)
根据资源 ID 获取特定资源。

**参数:**
- `resourceId`: 资源 ID

**返回值:**
- `EpubResource`: 资源对象，如果未找到则返回 null

**异常:**
- `BaseEpubException`: 解析过程中发生的异常
- `IOException`: I/O 操作失败
- `EpubPathValidationException`: 路径验证失败

### getCover()
获取 EPUB 文件的封面资源。

**返回值:**
- `EpubResource`: 封面资源，如果未找到则返回 null

**异常:**
- `BaseEpubException`: 解析过程中发生的异常
- `IOException`: I/O 操作失败
- `EpubPathValidationException`: 路径验证失败

### isValid()
检查 EPUB 文件是否有效。

**返回值:**
- `boolean`: 如果文件有效返回 true，否则返回 false

### getInfo()
获取 EPUB 文件的基本信息，无需完整解析。

**返回值:**
- `EpubInfo`: EPUB 文件的基本信息

**异常:**
- `BaseEpubException`: 解析过程中发生的异常
- `IOException`: I/O 操作失败
- `EpubPathValidationException`: 路径验证失败

## 内部类

### EpubInfo
EPUB 文件的基本信息类。

#### 构造方法
```java
public EpubInfo(String title, String author, String language, int chapterCount, long fileSize)
```

#### 方法
- `getTitle()`: 获取标题
- `getAuthor()`: 获取作者
- `getLanguage()`: 获取语言
- `getChapterCount()`: 获取章节数量
- `getFileSize()`: 获取文件大小
- `toString()`: 返回格式化的字符串表示

## 使用示例

```java
// 基本使用
EpubBook book = EpubReader.fromFile(new File("book.epub")).parse();

// 使用自定义配置
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)
    .withLazyLoading(false)
    .withParallelProcessing(true);
EpubBook book = EpubReader.fromFile(new File("book.epub"), config).parse();

// 流式处理章节
EpubReader.fromFile(new File("book.epub"))
    .streamChapters((chapter, inputStream) -> {
        System.out.println("处理章节: " + chapter.getTitle());
        // 处理内容流
    });

// 获取基本信息
EpubReader.EpubInfo info = EpubReader.fromFile(new File("book.epub")).getInfo();
System.out.println(info);
```