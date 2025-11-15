# EPUBime 优化API使用指南

## 概述

EPUBime现在提供了更现代、流畅的API设计，同时保持与现有代码的完全向后兼容性。新API通过Fluent模式、异步处理和增强的工具方法，大大提升了开发体验。

## 快速开始

### 基本使用

```java
import fun.lzwi.epubime.api.EpubReader;
import fun.lzwi.epubime.epub.EpubBook;

// 简单解析
EpubBook book = EpubReader.fromFile("book.epub").parse();

// 获取基本信息
System.out.println("标题: " + book.getMetadata().getTitle());
System.out.println("作者: " + book.getMetadata().getCreator());
System.out.println("章节数: " + book.getChapters().size());
```

### Fluent API

```java
// 链式配置
EpubBook book = EpubReader.fromFile(new File("book.epub"))
    .withCache(true)
    .withLazyLoading(true)
    .parse();

// 快速获取信息
EpubReader.EpubInfo info = EpubReader.fromFile("book.epub").getInfo();
System.out.println("书名: " + info.getTitle());
System.out.println("文件大小: " + info.getFileSize() + " bytes");
```

## 增强功能

### 1. 增强的EpubBook

```java
import fun.lzwi.epubime.api.EpubBookEnhanced;

EpubBook book = EpubReader.fromFile("book.epub").parse();
EpubBookEnhanced enhanced = new EpubBookEnhanced(book, epubFile);

// 便捷访问
String title = enhanced.getTitle();
String author = enhanced.getAuthor();

// 智能搜索
EpubChapter chapter = enhanced.findChapterByTitle("第一章");
List<EpubChapter> chapters = enhanced.findChaptersByContentPattern(".html");

// 资源分类
List<EpubResource> images = enhanced.getImageResources();
List<EpubResource> cssFiles = enhanced.getCssResources();
```

### 2. 增强的元数据

```java
import fun.lzwi.epubime.api.MetadataEnhanced;

Metadata metadata = EpubReader.fromFile("book.epub").parseMetadata();
MetadataEnhanced enhanced = new MetadataEnhanced(metadata);

// 类型安全访问
String title = enhanced.getTitle();
LocalDate date = enhanced.getParsedDate(); // 自动解析日期

// 便捷检查
boolean hasCover = enhanced.hasCover();
boolean hasAccessibility = enhanced.hasAccessibilityFeatures();

// 格式化摘要
String summary = enhanced.getSummary();
```

### 3. 异步处理

```java
import fun.lzwi.epubime.api.AsyncEpubProcessor;

AsyncEpubProcessor processor = new AsyncEpubProcessor();

// 异步解析
CompletableFuture<EpubBook> bookFuture = processor.parseBookAsync(epubFile);
bookFuture.thenAccept(book -> {
    System.out.println("异步解析完成: " + book.getMetadata().getTitle());
});

// 异步元数据
CompletableFuture<Metadata> metadataFuture = processor.parseMetadataAsync(epubFile);

// 批量处理
List<File> files = Arrays.asList(file1, file2, file3);
processor.processMultipleBooksAsync(files, book -> {
    // 处理每本书
    return book;
});
```

### 4. 流式处理

```java
// 流式处理章节（内存效率高）
EpubReader.fromFile(epubFile)
    .streamChapters((chapter, inputStream) -> {
        System.out.println("处理章节: " + chapter.getTitle());
        // 实时处理内容，无需全部加载到内存
        processContentStream(inputStream);
    });

// 流式处理特定章节
EpubReader.fromFile(epubFile)
    .streamChapter("chapter1", inputStream -> {
        String content = readStreamContent(inputStream);
        System.out.println("章节内容长度: " + content.length());
    });
```

## 实际应用示例

### 1. 移动应用开发

```java
// 快速获取书籍信息（适合列表显示）
public CompletableFuture<BookInfo> getBookInfoAsync(String filePath) {
    return AsyncEpubProcessor()
        .getBookInfoAsync(new File(filePath))
        .thenApply(info -> new BookInfo(
            info.getTitle(),
            info.getAuthor(),
            info.getChapterCount()
        ));
}

// 流式处理大文件
public void processLargeBook(File epubFile) {
    EpubReader.fromFile(epubFile)
        .streamChapters((chapter, stream) -> {
            // 逐章处理，避免内存溢出
            String content = extractText(stream);
            saveChapterContent(chapter.getTitle(), content);
        });
}
```

### 2. Web应用开发

```java
// REST API端点
@GetMapping("/api/books/{id}/info")
public ResponseEntity<BookInfo> getBookInfo(@PathVariable String id) {
    try {
        File bookFile = getBookFile(id);
        EpubReader.EpubInfo info = EpubReader.fromFile(bookFile).getInfo();
        return ResponseEntity.ok(new BookInfo(info));
    } catch (EpubParseException e) {
        return ResponseEntity.badRequest().build();
    }
}

// 批量处理上传的文件
@PostMapping("/api/books/batch")
public CompletableEntity<List<UploadResult>> batchUpload(@RequestParam("files") MultipartFile[] files) {
    List<File> epubFiles = saveUploadedFiles(files);
    
    return AsyncEpubProcessor()
        .processMultipleBooksAsync(epubFiles, book -> {
            // 处理每本书
            saveBookMetadata(book);
            return book;
        })
        .thenApply(results -> ResponseEntity.ok(createUploadResults(results)));
}
```

### 3. 桌面应用开发

```java
// 后台处理不阻塞UI
public void processBooksInBackground(List<File> files) {
    AsyncEpubProcessor processor = new AsyncEpubProcessor();
    
    processor.processMultipleBooksAsync(files, book -> {
        // 更新UI（需要在UI线程中执行）
        Platform.runLater(() -> {
            updateProgress(book.getMetadata().getTitle());
        });
        return book;
    })
    .thenRun(() -> {
        Platform.runLater(() -> {
            showCompletionDialog();
        });
    });
}

// 快速预览
public void showBookPreview(File epubFile) {
    try {
        // 快速获取基本信息
        EpubReader.EpubInfo info = EpubReader.fromFile(epubFile).getInfo();
        
        previewTitle.setText(info.getTitle());
        previewAuthor.setText(info.getAuthor());
        previewChapterCount.setText(String.valueOf(info.getChapterCount()));
        
        // 获取封面（如果存在）
        EpubResource cover = EpubReader.fromFile(epubFile).getCover();
        if (cover != null) {
            Image coverImage = new Image(new ByteArrayInputStream(cover.getData()));
            previewCover.setImage(coverImage);
        }
    } catch (EpubParseException e) {
        showErrorDialog("无法解析EPUB文件");
    }
}
```

## 性能优化建议

### 1. 内存使用优化

```java
// 大文件使用流式处理
EpubReader.fromFile(largeEpubFile)
    .withLazyLoading(true)  // 延迟加载
    .streamChapters(processor);  // 流式处理

// 批量处理使用异步
AsyncEpubProcessor processor = new AsyncEpubProcessor();
processor.processMultipleBooksAsync(files, processingFunction);
```

### 2. 速度优化

```java
// 启用缓存避免重复解析
EpubBook book = EpubReader.fromFile(epubFile)
    .withCache(true)
    .parse();

// 并行处理多个资源
EpubReader.fromFile(epubFile)
    .withParallelProcessing(true)
    .processResources(resourceProcessor);
```

### 3. 资源管理

```java
// 及时关闭异步处理器
try (AsyncEpubProcessor processor = new AsyncEpubProcessor()) {
    // 使用处理器
    processor.parseBookAsync(epubFile)
        .thenAccept(this::processBook)
        .join();
} // 自动关闭

// 合理配置线程池
ExecutorService customExecutor = Executors.newFixedThreadPool(4);
AsyncEpubProcessor processor = new AsyncEpubProcessor(customExecutor);
```

## 错误处理

### 1. 异步操作错误处理

```java
processor.parseBookAsync(epubFile)
    .exceptionally(throwable -> {
        System.err.println("解析失败: " + throwable.getMessage());
        return null;
    })
    .thenAccept(book -> {
        if (book != null) {
            processBook(book);
        }
    });
```

### 2. 流式处理错误处理

```java
try {
    EpubReader.fromFile(epubFile).streamChapters((chapter, stream) -> {
        try {
            processChapter(chapter, stream);
        } catch (IOException e) {
            System.err.println("处理章节失败: " + chapter.getTitle());
        }
    });
} catch (EpubParseException e) {
    System.err.println("EPUB解析失败: " + e.getMessage());
}
```

## 迁移指南

### 从旧API迁移

#### 基本解析（保持不变）
```java
// 旧代码 - 仍然有效
EpubParser parser = new EpubParser(epubFile);
EpubBook book = parser.parse();

// 新代码 - 更简洁
EpubBook book = EpubReader.fromFile(epubFile).parse();
```

#### 增强功能（新增）
```java
// 新增功能
MetadataEnhanced enhancedMetadata = new MetadataEnhanced(book.getMetadata());
LocalDate parsedDate = enhancedMetadata.getParsedDate();

// 新增异步支持
AsyncEpubProcessor processor = new AsyncEpubProcessor();
CompletableFuture<EpubBook> future = processor.parseBookAsync(epubFile);
```

## 最佳实践

1. **选择合适的API级别**：简单场景使用`EpubReader`，复杂处理使用增强类
2. **性能优化**：大文件使用流式处理，批量处理使用异步操作
3. **错误处理**：异步操作正确处理异常，流式处理妥善处理I/O异常
4. **资源管理**：及时关闭异步处理器，合理配置线程池
5. **内存管理**：及时释放大对象，使用延迟加载减少内存占用

## 总结

新的EPUBime API提供了：

- 🚀 **更简洁的语法**：Fluent API设计
- ⚡ **更好的性能**：流式处理和异步操作
- 🔧 **更丰富的功能**：增强的工具方法
- 🛡️ **类型安全**：减少运行时错误
- 🔄 **完全向后兼容**：现有代码无需修改

无论是开发移动应用、Web应用还是桌面应用，新的API都能提供更好的开发体验和性能表现。