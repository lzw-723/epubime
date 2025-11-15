# API 参考

## EpubReader

`EpubReader` 是 EPUBime 库提供的现代 Fluent API 类，支持链式方法调用和高级功能。遵循单一职责原则，专注于 API 协调和用户交互。

### 创建实例

```java
// 从文件对象创建（使用默认配置）
public static EpubReader fromFile(File epubFile)

// 从文件路径创建（使用默认配置）
public static EpubReader fromFile(String filePath)

// 从文件对象创建（使用自定义配置）
public static EpubReader fromFile(File epubFile, EpubReaderConfig config)
```

参数:
- `epubFile`: EPUB 文件对象
- `filePath`: EPUB 文件路径
- `config`: EpubReaderConfig 配置对象

返回:
- `EpubReader`: EpubReader 实例

抛出:
- `IllegalArgumentException`: 如果文件参数或配置参数为 null

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

#### 基础使用

```java
// 简单解析
EpubBook book = EpubReader.fromFile(epubFile).parse();

// 获取元数据
Metadata metadata = book.getMetadata();
System.out.println("书名: " + metadata.getTitle());
System.out.println("作者: " + metadata.getCreator());
System.out.println("出版日期: " + metadata.getDate());
```

#### 高级配置

```java
// 使用自定义配置优化性能
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)              // 启用缓存，避免重复解析
    .withLazyLoading(true)        // 延迟加载资源
    .withParallelProcessing(true); // 并行处理多个资源

EpubBook book = EpubReader.fromFile(epubFile, config).parse();
```

#### 快速信息获取

```java
// 无需完整解析即可获取基本信息
EpubReader.EpubInfo info = EpubReader.fromFile(epubFile).getInfo();
System.out.println("标题: " + info.getTitle());
System.out.println("作者: " + info.getAuthor());
System.out.println("章节数: " + info.getChapterCount());
System.out.println("文件大小: " + info.getFileSize() + " 字节");
```

#### 选择性解析

```java
// 只解析元数据
Metadata metadata = EpubReader.fromFile(epubFile).parseMetadata();

// 只解析目录结构
List<EpubChapter> chapters = EpubReader.fromFile(epubFile).parseTableOfContents();

// 验证文件有效性
boolean isValid = EpubReader.fromFile(epubFile).isValid();
System.out.println("EPUB文件有效: " + isValid);
```

#### 流式处理大文件

```java
// 流式处理所有章节（内存友好）
EpubReader.fromFile(epubFile)
    .streamChapters((chapter, inputStream) -> {
        System.out.println("正在处理: " + chapter.getTitle());

        // 逐章处理，避免加载整个文件到内存
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, "UTF-8"))) {

            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                // 处理每一行内容
            }

            System.out.println("章节行数: " + lineCount);

        } catch (IOException e) {
            System.err.println("处理章节失败: " + chapter.getTitle());
        }
    });
```

#### 特定章节处理

```java
// 处理特定章节
EpubReader.fromFile(epubFile)
    .streamChapter("chapter1", inputStream -> {
        try {
            // 读取章节内容
            String content = new String(inputStream.readAllBytes(), "UTF-8");
            System.out.println("第一章内容长度: " + content.length());

            // 使用 Jsoup 解析 HTML
            Document doc = Jsoup.parse(content);
            Elements paragraphs = doc.select("p");
            System.out.println("段落数: " + paragraphs.size());

        } catch (IOException e) {
            System.err.println("读取章节失败");
        }
    });
```

#### 资源处理

```java
// 并行处理所有资源文件
EpubReader.fromFile(epubFile)
    .withParallelProcessing(true)
    .processResources(resource -> {
        System.out.println("处理资源: " + resource.getId() +
                          " (" + resource.getType() + ")");

        // 保存资源到文件系统
        try {
            byte[] data = resource.getData();
            if (data != null) {
                Path outputPath = Paths.get("extracted", resource.getHref());
                Files.createDirectories(outputPath.getParent());
                Files.write(outputPath, data);
            }
        } catch (IOException e) {
            System.err.println("保存资源失败: " + resource.getHref());
        }

        return null; // Function 需要返回值
    });
```

#### 获取特定资源

```java
// 获取封面图片
EpubResource cover = EpubReader.fromFile(epubFile).getCover();
if (cover != null && cover.getData() != null) {
    // 保存封面
    Files.write(Paths.get("cover.jpg"), cover.getData());
    System.out.println("封面已保存，大小: " + cover.getData().length + " 字节");
}

// 获取特定资源
EpubResource image = EpubReader.fromFile(epubFile).getResource("image1");
if (image != null) {
    System.out.println("图片类型: " + image.getType());
    System.out.println("图片大小: " + image.getData().length);
}
```

#### 错误处理

```java
try {
    EpubBook book = EpubReader.fromFile(epubFile)
        .withCache(true)
        .parse();

    // 处理书籍内容
    System.out.println("成功解析: " + book.getMetadata().getTitle());

} catch (EpubParseException e) {
    System.err.println("解析错误: " + e.getMessage());
    System.err.println("文件: " + e.getFileName());
    System.err.println("路径: " + e.getPath());

} catch (EpubFormatException e) {
    System.err.println("格式错误: EPUB文件格式不符合规范");

} catch (EpubPathValidationException e) {
    System.err.println("路径验证错误: " + e.getMessage());

} catch (Exception e) {
    System.err.println("未知错误: " + e.getMessage());
    e.printStackTrace();
}
```

#### 性能优化示例

```java
// 为大文件优化配置
EpubReaderConfig optimizedConfig = new EpubReaderConfig()
    .withCache(true)           // 避免重复I/O
    .withLazyLoading(true)     // 按需加载
    .withParallelProcessing(true); // 并行处理

long startTime = System.nanoTime();
EpubBook book = EpubReader.fromFile(largeEpubFile, optimizedConfig).parse();
long endTime = System.nanoTime();

System.out.println("解析耗时: " + (endTime - startTime) / 1_000_000 + "ms");
System.out.println("章节数: " + book.getChapters().size());
System.out.println("资源数: " + book.getResources().size());
```