# API 参考

本节提供了 EPUBime 库的完整 API 参考文档。EPUBime 是一个纯 Java 库，用于解析 EPUB 文件格式，支持 EPUB 2 和 EPUB 3 格式。库遵循单一职责原则(SRP)，每个类都有明确的职责。

## 现代 Fluent API（推荐）

- [EpubReader](/api/epub-reader) - 现代 Fluent API，专注于 API 协调和用户交互
- [EpubReaderConfig](/api/epub-reader-config) - EpubReader 配置类，管理解析选项
- [AsyncEpubProcessor](/api/async-processor) - 异步处理器，支持异步解析和处理

## 核心数据模型

- [EpubBook](/api/epub-book) - EPUB 书籍数据模型，存储书籍信息
- [Metadata](/api/metadata) - EPUB 书籍元数据信息
- [EpubChapter](/api/epub-chapter) - EPUB 书籍中的章节
- [EpubResource](/api/epub-resource) - EPUB 书籍中的资源文件

## 专用处理器类

- [EpubParser](/api/epub-parser) - EPUB 文件解析器，负责解析逻辑
- [EpubFileReader](/api/epub-file-reader) - 文件读取器，负责安全的文件读取
- [EpubStreamProcessor](/api/epub-stream-processor) - 流处理器，负责流式操作
- [EpubBookProcessor](/api/epub-book-processor) - 书籍处理器，负责书籍业务逻辑

## 增强功能类

- [EpubBookEnhanced](/api/epub-book-enhanced) - 增强的书籍对象，提供更多便利方法
- [MetadataEnhanced](/api/metadata-enhanced) - 增强的元数据对象，支持更多元数据操作

## 异常处理

- [异常类](/api/exceptions) - EPUBime 的异常类体系，所有异常都继承自 BaseEpubException

## 概述

EPUBime 提供了现代化的 API 设计，严格遵循单一职责原则：

### 现代 Fluent API（推荐）
使用 `EpubReader` 和 `EpubReaderConfig` 进行配置化解析：

```java
// 使用默认配置
EpubBook book = EpubReader.fromFile(epubFile).parse();

// 使用自定义配置
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)
    .withLazyLoading(false)
    .withParallelProcessing(true);
EpubBook book = EpubReader.fromFile(epubFile, config).parse();
```

### 专用处理器模式
根据单一职责原则，不同的操作使用专门的处理器：

```java
// 文件读取
EpubFileReader fileReader = new EpubFileReader(epubFile);
String content = fileReader.readContent("mimetype");

// 书籍处理
EpubResource cover = EpubBookProcessor.getCover(book);

// 流式处理
EpubStreamProcessor streamProcessor = new EpubStreamProcessor(epubFile);
streamProcessor.processBookChapters(book, (chapter, inputStream) -> {
    // 处理章节内容
});
```

### 传统解析 API
使用 `EpubParser` 进行核心解析：

```java
EpubParser parser = new EpubParser(epubFile);
EpubBook book = parser.parse();
```

### 异步处理
使用 `AsyncEpubProcessor` 进行异步操作：

```java
AsyncEpubProcessor processor = new AsyncEpubProcessor();
EpubReaderConfig config = new EpubReaderConfig().withCache(true);
CompletableFuture<EpubBook> future = processor.parseBookAsync(epubFile, true, false);
```

选择左侧导航栏中的类名查看详细 API 文档。