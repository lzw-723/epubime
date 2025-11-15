# API 参考

## AsyncEpubProcessor

`AsyncEpubProcessor` 是 EPUBime 库提供的异步处理器类，支持异步解析和处理 EPUB 文件。

### 构造函数

```java
public AsyncEpubProcessor()
```
创建一个新的异步处理器实例。

### 异步解析方法

#### parseBookAsync()
```java
public CompletableFuture<EpubBook> parseBookAsync(File epubFile)
```
异步解析 EPUB 文件。

参数:
- `epubFile`: EPUB 文件

返回:
- `CompletableFuture<EpubBook>`: 异步返回解析出的书籍对象

#### parseMetadataAsync()
```java
public CompletableFuture<Metadata> parseMetadataAsync(File epubFile)
```
异步解析 EPUB 文件的元数据。

参数:
- `epubFile`: EPUB 文件

返回:
- `CompletableFuture<Metadata>`: 异步返回解析出的元数据对象

#### getBookInfoAsync()
```java
public CompletableFuture<EpubReader.EpubInfo> getBookInfoAsync(File epubFile)
```
异步获取 EPUB 文件的基本信息。

参数:
- `epubFile`: EPUB 文件

返回:
- `CompletableFuture<EpubReader.EpubInfo>`: 异步返回书籍基本信息

### 异步处理方法

#### processChaptersAsync()
```java
public CompletableFuture<Void> processChaptersAsync(File epubFile, BiConsumer<EpubChapter, InputStream> processor)
```
异步流式处理所有章节内容。

参数:
- `epubFile`: EPUB 文件
- `processor`: 章节内容处理器

返回:
- `CompletableFuture<Void>`: 异步处理完成信号

#### validateAsync()
```java
public CompletableFuture<Boolean> validateAsync(File epubFile)
```
异步验证 EPUB 文件的有效性。

参数:
- `epubFile`: EPUB 文件

返回:
- `CompletableFuture<Boolean>`: 异步返回验证结果

### 增强对象异步加载

#### loadEnhancedBookAsync()
```java
public CompletableFuture<EpubBookEnhanced> loadEnhancedBookAsync(File epubFile)
```
异步加载增强的书籍对象。

参数:
- `epubFile`: EPUB 文件

返回:
- `CompletableFuture<EpubBookEnhanced>`: 异步返回增强的书籍对象

#### loadEnhancedMetadataAsync()
```java
public CompletableFuture<MetadataEnhanced> loadEnhancedMetadataAsync(File epubFile)
```
异步加载增强的元数据对象。

参数:
- `epubFile`: EPUB 文件

返回:
- `CompletableFuture<MetadataEnhanced>`: 异步返回增强的元数据对象

### 统计信息异步获取

#### getChapterCountAsync()
```java
public CompletableFuture<Integer> getChapterCountAsync(File epubFile)
```
异步获取章节数量。

参数:
- `epubFile`: EPUB 文件

返回:
- `CompletableFuture<Integer>`: 异步返回章节数量

#### getResourceCountAsync()
```java
public CompletableFuture<Integer> getResourceCountAsync(File epubFile)
```
异步获取资源数量。

参数:
- `epubFile`: EPUB 文件

返回:
- `CompletableFuture<Integer>`: 异步返回资源数量

### 批量处理

#### processMultipleBooksAsync()
```java
public CompletableFuture<List<EpubBook>> processMultipleBooksAsync(List<File> epubFiles, Function<EpubBook, EpubBook> processor)
```
异步批量处理多个 EPUB 文件。

参数:
- `epubFiles`: EPUB 文件列表
- `processor`: 书籍处理器函数

返回:
- `CompletableFuture<List<EpubBook>>`: 异步返回处理后的书籍列表

### 生命周期管理

#### shutdown()
```java
public void shutdown()
```
关闭异步处理器，释放资源。

### 使用示例

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    // 异步解析书籍
    CompletableFuture<EpubBook> bookFuture = asyncProcessor.parseBookAsync(epubFile);
    
    // 异步解析元数据
    CompletableFuture<Metadata> metadataFuture = asyncProcessor.parseMetadataAsync(epubFile);
    
    // 等待完成并获取结果
    EpubBook book = bookFuture.get();
    Metadata metadata = metadataFuture.get();
    
    System.out.println("书籍标题: " + metadata.getTitle());
    System.out.println("章节数: " + book.getChapters().size());
    
    // 异步处理章节
    asyncProcessor.processChaptersAsync(epubFile, (chapter, inputStream) -> {
        System.out.println("处理章节: " + chapter.getTitle());
        // 处理章节内容
    }).join();
    
    // 异步验证
    asyncProcessor.validateAsync(epubFile)
        .thenAccept(isValid -> {
            System.out.println("EPUB 文件有效: " + isValid);
        })
        .join();
        
} finally {
    asyncProcessor.shutdown();
}
```

### 批量处理示例

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    List<File> epubFiles = Arrays.asList(
        new File("book1.epub"),
        new File("book2.epub"),
        new File("book3.epub")
    );
    
    // 批量处理多个书籍
    asyncProcessor.processMultipleBooksAsync(epubFiles, book -> {
        System.out.println("处理书籍: " + book.getMetadata().getTitle());
        return book;
    }).thenAccept(books -> {
        System.out.println("完成处理 " + books.size() + " 本书籍");
    }).join();
    
} finally {
    asyncProcessor.shutdown();
}
```