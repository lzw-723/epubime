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

#### 基础异步操作

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    // 异步解析书籍
    CompletableFuture<EpubBook> bookFuture = asyncProcessor.parseBookAsync(epubFile);

    // 异步解析元数据
    CompletableFuture<Metadata> metadataFuture = asyncProcessor.parseMetadataAsync(epubFile);

    // 异步获取基本信息
    CompletableFuture<EpubReader.EpubInfo> infoFuture = asyncProcessor.getBookInfoAsync(epubFile);

    // 等待所有操作完成
    CompletableFuture.allOf(bookFuture, metadataFuture, infoFuture).join();

    // 获取结果
    EpubBook book = bookFuture.get();
    Metadata metadata = metadataFuture.get();
    EpubReader.EpubInfo info = infoFuture.get();

    System.out.println("异步解析完成:");
    System.out.println("标题: " + metadata.getTitle());
    System.out.println("章节数: " + book.getChapters().size());
    System.out.println("文件大小: " + info.getFileSize() + " 字节");

} finally {
    asyncProcessor.shutdown();
}
```

#### 异步流式处理

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    // 异步流式处理所有章节
    CompletableFuture<Void> processingFuture = asyncProcessor.processChaptersAsync(
        epubFile,
        (chapter, inputStream) -> {
            try {
                System.out.println("异步处理章节: " + chapter.getTitle());

                // 计算章节大小
                byte[] buffer = new byte[8192];
                int bytesRead;
                int totalBytes = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    totalBytes += bytesRead;
                }

                System.out.println("章节 '" + chapter.getTitle() + "' 大小: " + totalBytes + " 字节");

            } catch (IOException e) {
                System.err.println("处理章节失败: " + chapter.getTitle());
            }
        }
    );

    // 等待处理完成
    processingFuture.join();
    System.out.println("异步章节处理完成");

} finally {
    asyncProcessor.shutdown();
}
```

#### 异步验证和增强对象

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    // 异步验证文件
    asyncProcessor.validateAsync(epubFile)
        .thenAccept(isValid -> {
            System.out.println("EPUB文件验证结果: " + (isValid ? "有效" : "无效"));
        })
        .join();

    // 异步加载增强书籍对象
    CompletableFuture<EpubBookEnhanced> enhancedBookFuture =
        asyncProcessor.loadEnhancedBookAsync(epubFile);

    EpubBookEnhanced enhancedBook = enhancedBookFuture.get();

    System.out.println("增强书籍信息:");
    System.out.println("标题: " + enhancedBook.getTitle());
    System.out.println("总章节数: " + enhancedBook.getChapterCount());
    System.out.println("图片资源数: " + enhancedBook.getImageResources().size());

    // 异步加载增强元数据
    CompletableFuture<MetadataEnhanced> enhancedMetadataFuture =
        asyncProcessor.loadEnhancedMetadataAsync(epubFile);

    MetadataEnhanced enhancedMetadata = enhancedMetadataFuture.get();

    System.out.println("增强元数据信息:");
    System.out.println("摘要: " + enhancedMetadata.getSummary());

    if (enhancedMetadata.hasAccessibilityFeatures()) {
        System.out.println("可访问性特性: " + enhancedMetadata.getAccessibilityFeatures());
    }

} finally {
    asyncProcessor.shutdown();
}
```

#### 批量处理示例

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    List<File> epubFiles = Arrays.asList(
        new File("book1.epub"),
        new File("book2.epub"),
        new File("book3.epub"),
        new File("book4.epub")
    );

    // 批量异步处理多个书籍
    asyncProcessor.processMultipleBooksAsync(epubFiles, book -> {
        String title = book.getMetadata().getTitle();
        int chapterCount = book.getChapters().size();
        int resourceCount = book.getResources().size();

        System.out.println("处理完成: 《" + title + "》");
        System.out.println("  章节数: " + chapterCount);
        System.out.println("  资源数: " + resourceCount);

        return book;
    }).thenAccept(books -> {
        System.out.println("批量处理完成，共处理 " + books.size() + " 本书籍");

        // 计算统计信息
        int totalChapters = books.stream().mapToInt(b -> b.getChapters().size()).sum();
        int totalResources = books.stream().mapToInt(b -> b.getResources().size()).sum();

        System.out.println("总章节数: " + totalChapters);
        System.out.println("总资源数: " + totalResources);
        System.out.println("平均每本书章节数: " + (totalChapters / books.size()));
    }).join();

} finally {
    asyncProcessor.shutdown();
}
```

#### 高级异步模式

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    File epubFile = new File("large-book.epub");

    // 组合多个异步操作
    CompletableFuture<EpubBook> bookFuture = asyncProcessor.parseBookAsync(epubFile);
    CompletableFuture<Metadata> metadataFuture = asyncProcessor.parseMetadataAsync(epubFile);
    CompletableFuture<EpubReader.EpubInfo> infoFuture = asyncProcessor.getBookInfoAsync(epubFile);

    // 当书籍解析完成后，异步处理其内容
    CompletableFuture<Void> contentProcessingFuture = bookFuture.thenCompose(book -> {
        return asyncProcessor.processChaptersAsync(epubFile, (chapter, inputStream) -> {
            // 异步处理每个章节
            processChapterAsync(chapter, inputStream);
        });
    });

    // 当元数据解析完成后，异步验证和增强
    CompletableFuture<Void> metadataProcessingFuture = metadataFuture.thenCompose(metadata -> {
        return asyncProcessor.loadEnhancedMetadataAsync(epubFile)
            .thenAccept(enhanced -> {
                System.out.println("增强元数据摘要: " + enhanced.getSummary());
            });
    });

    // 等待所有操作完成
    CompletableFuture.allOf(
        bookFuture,
        metadataFuture,
        infoFuture,
        contentProcessingFuture,
        metadataProcessingFuture
    ).join();

    System.out.println("所有异步操作完成");

} finally {
    asyncProcessor.shutdown();
}

// 辅助方法
private void processChapterAsync(EpubChapter chapter, InputStream inputStream) {
    // 异步处理章节内容的逻辑
    CompletableFuture.runAsync(() -> {
        try {
            // 实际的章节处理逻辑
            byte[] buffer = new byte[1024];
            int bytesRead;
            int totalBytes = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                totalBytes += bytesRead;
            }

            System.out.println("异步处理章节 '" + chapter.getTitle() + "': " + totalBytes + " 字节");

        } catch (IOException e) {
            System.err.println("异步处理章节失败: " + chapter.getTitle());
        }
    });
}
```

#### 错误处理和超时

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    // 设置超时时间
    CompletableFuture<EpubBook> bookFuture = asyncProcessor.parseBookAsync(epubFile)
        .orTimeout(30, TimeUnit.SECONDS)  // 30秒超时
        .exceptionally(throwable -> {
            System.err.println("解析失败: " + throwable.getMessage());
            return null;  // 返回默认值
        });

    // 处理结果
    EpubBook book = bookFuture.get();
    if (book != null) {
        System.out.println("成功解析: " + book.getMetadata().getTitle());
    } else {
        System.out.println("解析失败，返回默认处理");
    }

    // 批量处理中的错误处理
    List<File> epubFiles = Arrays.asList(file1, file2, file3);

    asyncProcessor.processMultipleBooksAsync(epubFiles, book -> {
        // 处理每本书
        return book;
    }).exceptionally(throwable -> {
        System.err.println("批量处理出现错误: " + throwable.getMessage());
        return Collections.emptyList();  // 返回空列表
    }).thenAccept(books -> {
        System.out.println("成功处理 " + books.size() + " 本书籍");
    }).join();

} finally {
    asyncProcessor.shutdown();
}
```

#### 自定义执行器

```java
// 使用自定义线程池
ExecutorService customExecutor = Executors.newFixedThreadPool(4);
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor(customExecutor);

try {
    // 使用自定义执行器进行异步处理
    List<File> epubFiles = Arrays.asList(file1, file2, file3, file4, file5);

    asyncProcessor.processMultipleBooksAsync(epubFiles, book -> {
        System.out.println("使用自定义执行器处理: " + book.getMetadata().getTitle());
        return book;
    }).thenAccept(books -> {
        System.out.println("自定义执行器处理完成: " + books.size() + " 本书");
    }).join();

} finally {
    asyncProcessor.shutdown();
    customExecutor.shutdown();  // 关闭自定义执行器
}
```