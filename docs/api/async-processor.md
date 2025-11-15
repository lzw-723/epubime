# AsyncEpubProcessor

`AsyncEpubProcessor` 是 EPUBime 库中的异步处理器，提供非阻塞的 EPUB 处理操作。该类允许用户在后台线程中执行各种 EPUB 解析和处理任务，避免阻塞主线程。

## 类定义

```java
public class AsyncEpubProcessor
```

## 构造方法

### AsyncEpubProcessor()
使用默认执行器（缓存线程池）创建异步处理器。

### AsyncEpubProcessor(Executor executor)
使用自定义执行器创建异步处理器。

**参数:**
- `executor`: 用于异步操作的执行器

## 方法

### parseBookAsync(File epubFile)
异步解析 EPUB 文件。

**参数:**
- `epubFile`: 要解析的 EPUB 文件

**返回值:**
- `CompletableFuture<EpubBook>`: 包含解析后 EpubBook 的 CompletableFuture

### parseBookAsync(File epubFile, boolean useCache, boolean lazyLoading)
异步解析 EPUB 文件，指定缓存和延迟加载选项。

**参数:**
- `epubFile`: 要解析的 EPUB 文件
- `useCache`: 是否使用缓存
- `lazyLoading`: 是否使用延迟加载

**返回值:**
- `CompletableFuture<EpubBook>`: 包含解析后 EpubBook 的 CompletableFuture

### parseMetadataAsync(File epubFile)
异步解析 EPUB 文件的元数据。

**参数:**
- `epubFile`: 要解析的 EPUB 文件

**返回值:**
- `CompletableFuture<Metadata>`: 包含元数据的 CompletableFuture

### parseTableOfContentsAsync(File epubFile)
异步解析 EPUB 文件的目录。

**参数:**
- `epubFile`: 要解析的 EPUB 文件

**返回值:**
- `CompletableFuture<List<EpubChapter>>`: 包含章节列表的 CompletableFuture

### getBookInfoAsync(File epubFile)
异步获取 EPUB 文件的基本信息。

**参数:**
- `epubFile`: 要分析的 EPUB 文件

**返回值:**
- `CompletableFuture<EpubReader.EpubInfo>`: 包含基本 EPUB 信息的 CompletableFuture

### processChaptersAsync(File epubFile, BiConsumer<EpubChapter, InputStream> processor)
使用流处理异步处理章节。

**参数:**
- `epubFile`: 要处理的 EPUB 文件
- `processor`: 处理每个章节的处理器

**返回值:**
- `CompletableFuture<Void>`: 在所有章节处理完成时完成的 CompletableFuture

### processChapterAsync(File epubFile, String chapterId, Consumer<InputStream> processor)
异步处理特定章节。

**参数:**
- `epubFile`: EPUB 文件
- `chapterId`: 要处理的章节 ID
- `processor`: 处理章节内容的处理器

**返回值:**
- `CompletableFuture<Void>`: 在章节处理完成时完成的 CompletableFuture

### processResourcesAsync(File epubFile, Function<EpubResource, Void> processor)
异步处理所有资源。

**参数:**
- `epubFile`: EPUB 文件
- `processor`: 处理每个资源的函数

**返回值:**
- `CompletableFuture<Void>`: 在处理完成时完成的 CompletableFuture

### getCoverAsync(File epubFile)
异步获取封面资源。

**参数:**
- `epubFile`: EPUB 文件

**返回值:**
- `CompletableFuture<EpubResource>`: 包含封面资源的 CompletableFuture，如果未找到则为 null

### getResourceAsync(File epubFile, String resourceId)
异步获取特定资源。

**参数:**
- `epubFile`: EPUB 文件
- `resourceId`: 资源 ID

**返回值:**
- `CompletableFuture<EpubResource>`: 包含资源的 CompletableFuture，如果未找到则为 null

### validateAsync(File epubFile)
异步验证 EPUB 文件。

**参数:**
- `epubFile`: 要验证的 EPUB 文件

**返回值:**
- `CompletableFuture<Boolean>`: 包含验证结果的 CompletableFuture

### loadEnhancedBookAsync(File epubFile)
异步加载增强的书籍对象。

**参数:**
- `epubFile`: EPUB 文件

**返回值:**
- `CompletableFuture<EpubBookEnhanced>`: 包含增强书籍对象的 CompletableFuture

### loadEnhancedMetadataAsync(File epubFile)
异步加载增强的元数据。

**参数:**
- `epubFile`: EPUB 文件

**返回值:**
- `CompletableFuture<MetadataEnhanced>`: 包含增强元数据的 CompletableFuture

### processMultipleBooksAsync(List<File> epubFiles, Function<EpubBook, EpubBook> processor)
并行处理多个 EPUB 文件。

**参数:**
- `epubFiles`: 要处理的 EPUB 文件列表
- `processor`: 处理每个书籍的函数

**返回值:**
- `CompletableFuture<List<EpubBook>>`: 包含处理后书籍列表的 CompletableFuture

### getChapterCountAsync(File epubFile)
异步获取章节数量。

**参数:**
- `epubFile`: EPUB 文件

**返回值:**
- `CompletableFuture<Integer>`: 包含章节数量的 CompletableFuture

### getResourceCountAsync(File epubFile)
异步获取资源数量。

**参数:**
- `epubFile`: EPUB 文件

**返回值:**
- `CompletableFuture<Integer>`: 包含资源数量的 CompletableFuture

### shutdown()
关闭处理器并释放资源。

## 使用示例

```java
// 创建异步处理器
AsyncEpubProcessor processor = new AsyncEpubProcessor();

// 异步解析书籍
CompletableFuture<EpubBook> futureBook = processor.parseBookAsync(new File("book.epub"));
futureBook.thenAccept(book -> {
    System.out.println("解析完成: " + book.getMetadata().getTitle());
}).exceptionally(throwable -> {
    System.err.println("解析失败: " + throwable.getMessage());
    return null;
});

// 异步获取封面
CompletableFuture<EpubResource> futureCover = processor.getCoverAsync(new File("book.epub"));
futureCover.thenAccept(cover -> {
    if (cover != null) {
        System.out.println("找到封面: " + cover.getHref());
    }
});

// 异步处理多个书籍
List<File> epubFiles = Arrays.asList(new File("book1.epub"), new File("book2.epub"));
CompletableFuture<List<EpubBook>> futureBooks = processor.processMultipleBooksAsync(epubFiles, 
    book -> {
        // 对每本书进行处理
        return book;
    });
futureBooks.thenAccept(books -> {
    System.out.println("处理完成 " + books.size() + " 本书");
});

// 使用完毕后关闭处理器
processor.shutdown();
```

## 注意事项

- 使用异步处理器时，应确保在应用结束前关闭处理器以释放线程资源。
- 异步操作的结果通过 CompletableFuture 提供，可以使用 thenAccept、thenApply 等方法进行后续处理。
- 对于需要长时间运行的异步操作，建议使用适当的线程池配置。