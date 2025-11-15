# API 参考

## EpubReader

`EpubReader` 是 EPUBime 库提供的现代 Fluent API 类，支持链式方法调用和高级功能。

### 创建实例

```java
// 从文件对象创建
public static EpubReader fromFile(File epubFile)

// 从文件路径创建
public static EpubReader fromFile(String filePath)
```

参数:
- `epubFile`: EPUB 文件对象
- `filePath`: EPUB 文件路径

返回:
- `EpubReader`: EpubReader 实例

抛出:
- `IllegalArgumentException`: 如果文件参数为 null

### 配置方法

#### withCache()
```java
public EpubReader withCache(boolean useCache)
```
启用或禁用缓存机制。

参数:
- `useCache`: 是否使用缓存

返回:
- `EpubReader`: this，用于方法链式调用

#### withLazyLoading()
```java
public EpubReader withLazyLoading(boolean lazyLoading)
```
启用或禁用延迟加载。

参数:
- `lazyLoading`: 是否使用延迟加载

返回:
- `EpubReader`: this，用于方法链式调用

#### withParallelProcessing()
```java
public EpubReader withParallelProcessing(boolean parallelProcessing)
```
启用或禁用并行处理。

参数:
- `parallelProcessing`: 是否使用并行处理

返回:
- `EpubReader`: this，用于方法链式调用

### 解析方法

#### parse()
```java
public EpubBook parse() throws EpubParseException
```
解析 EPUB 文件并返回 EpubBook 对象。

返回:
- `EpubBook`: 解析出的 EPUB 书籍对象

抛出:
- `EpubParseException`: 解析过程中发生错误

#### parseMetadata()
```java
public Metadata parseMetadata() throws EpubParseException
```
仅解析元数据，不解析完整书籍内容。

返回:
- `Metadata`: 解析出的元数据对象

抛出:
- `EpubParseException`: 解析过程中发生错误

#### parseTableOfContents()
```java
public List<EpubChapter> parseTableOfContents() throws EpubParseException
```
仅解析目录结构，不解析完整书籍内容。

返回:
- `List<EpubChapter>`: 章节列表

抛出:
- `EpubParseException`: 解析过程中发生错误

### 流式处理方法

#### streamChapters()
```java
public void streamChapters(BiConsumer<EpubChapter, InputStream> processor) throws EpubParseException
```
流式处理所有章节内容，避免将整个文件加载到内存中。

参数:
- `processor`: 章节内容处理器，接收章节对象和输入流

抛出:
- `EpubParseException`: 处理过程中发生错误

#### streamChapter()
```java
public void streamChapter(String chapterId, Consumer<InputStream> processor) throws EpubParseException
```
流式处理特定章节内容。

参数:
- `chapterId`: 章节 ID
- `processor`: 内容处理器，接收输入流

抛出:
- `EpubParseException`: 处理过程中发生错误

### 资源处理方法

#### processResources()
```java
public void processResources(Function<EpubResource, Void> processor) throws EpubParseException
```
处理所有资源文件。

参数:
- `processor`: 资源处理器函数

抛出:
- `EpubParseException`: 处理过程中发生错误

#### getResource()
```java
public EpubResource getResource(String resourceId) throws EpubParseException
```
获取特定资源。

参数:
- `resourceId`: 资源 ID

返回:
- `EpubResource`: 资源对象，如果未找到则返回 null

抛出:
- `EpubParseException`: 解析过程中发生错误

#### getCover()
```java
public EpubResource getCover() throws EpubParseException
```
获取封面资源。

返回:
- `EpubResource`: 封面资源对象，如果未找到则返回 null

抛出:
- `EpubParseException`: 解析过程中发生错误

### 验证和工具方法

#### isValid()
```java
public boolean isValid()
```
检查 EPUB 文件是否有效。

返回:
- `boolean`: true 如果文件有效，false 否则

#### getInfo()
```java
public EpubInfo getInfo() throws EpubParseException
```
获取 EPUB 文件的基本信息，无需完整解析。

返回:
- `EpubInfo`: 包含基本信息的对象

抛出:
- `EpubParseException`: 获取信息过程中发生错误

### 内部类

#### EpubInfo
```java
public static class EpubInfo {
    public String getTitle()     // 获取标题
    public String getAuthor()    // 获取作者
    public String getLanguage()  // 获取语言
    public int getChapterCount() // 获取章节数
    public long getFileSize()    // 获取文件大小（字节）
}
```

表示 EPUB 文件基本信息的内部类。

### 使用示例

```java
// 基础使用
EpubBook book = EpubReader.fromFile(epubFile)
    .withCache(true)
    .parse();

// 流式处理
EpubReader.fromFile(epubFile)
    .streamChapters((chapter, inputStream) -> {
        System.out.println("处理章节: " + chapter.getTitle());
        // 处理章节内容
    });

// 获取基本信息
EpubReader.EpubInfo info = EpubReader.fromFile(epubFile).getInfo();
System.out.println("标题: " + info.getTitle());
System.out.println("章节数: " + info.getChapterCount());

// 验证文件
boolean isValid = EpubReader.fromFile(epubFile).isValid();
System.out.println("文件有效: " + isValid);
```