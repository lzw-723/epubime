# API 参考

本节提供了 EPUBime 库的完整 API 参考文档。EPUBime 是一个纯 Java 库，用于解析 EPUB 文件格式，支持 EPUB 2 和 EPUB 3 格式。

## 现代API（推荐）

- [EpubReader](/api/epub-reader) - 现代 Fluent API，支持链式方法调用和高级功能
- [AsyncEpubProcessor](/api/async-processor) - 异步处理器，支持异步解析和处理

## 传统核心类

- [EpubParser](/api/epub-parser) - EPUB 文件解析器，负责解析 EPUB 文件并生成 EpubBook 对象
- [EpubBook](/api/epub-book) - 代表一个解析后的 EPUB 书籍，包含元数据、章节和资源
- [Metadata](/api/metadata) - 代表 EPUB 书籍的元数据信息
- [EpubChapter](/api/epub-chapter) - 代表 EPUB 书籍中的一个章节
- [EpubResource](/api/epub-resource) - 代表 EPUB 书籍中的一个资源文件

## 增强功能类

- [EpubBookEnhanced](/api/epub-book-enhanced) - 增强的书籍对象，提供更多便利方法
- [MetadataEnhanced](/api/metadata-enhanced) - 增强的元数据对象，支持更多元数据操作

## 异常处理

- [异常类](/api/exceptions) - EPUBime 的异常类体系，所有异常都继承自 EpubException

## 概述

EPUBime 提供了两套 API：

### 现代 Fluent API（推荐）
使用 `EpubReader` 提供的 Fluent API，支持链式方法调用：

```java
EpubBook book = EpubReader.fromFile(epubFile)
    .withCache(true)
    .withLazyLoading(true)
    .parse();
```

### 传统 API
使用 `EpubParser` 进行传统方式的解析：

```java
EpubParser parser = new EpubParser(epubFile);
EpubBook book = parser.parse();
```

### 异步处理
使用 `AsyncEpubProcessor` 进行异步操作：

```java
AsyncEpubProcessor processor = new AsyncEpubProcessor();
CompletableFuture<EpubBook> future = processor.parseBookAsync(epubFile);
```

选择左侧导航栏中的类名查看详细 API 文档。