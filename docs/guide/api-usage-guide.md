# 用户集成指南

本指南将帮助您将 EPUBime 集成到您的项目中，并提供详细的使用说明。

## 项目集成

### Maven 集成

在您的 `pom.xml` 文件中添加以下依赖：

```xml
<dependency>
    <groupId>fun.lzwi</groupId>
    <artifactId>epubime</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Gradle 集成

如果您使用 Gradle，请在 `build.gradle` 文件中添加：

```gradle
dependencies {
    implementation 'fun.lzwi:epubime:1.0-SNAPSHOT'
}
```

## 基本使用流程

### 1. 创建 EpubReader 实例

```java
import fun.lzwi.epubime.api.*;
import fun.lzwi.epubime.epub.*;
import java.io.File;

// 从文件创建
File epubFile = new File("path/to/book.epub");
EpubReader reader = EpubReader.fromFile(epubFile);

// 从输入流创建
InputStream inputStream = new FileInputStream("path/to/book.epub");
EpubReader reader = EpubReader.fromInputStream(inputStream);
```

### 2. 配置解析选项（可选）

```java
// 使用默认配置
EpubBook book = reader.parse();

// 或使用自定义配置
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)              // 启用缓存
    .withLazyLoading(true)        // 启用延迟加载
    .withParallelProcessing(true); // 启用并行处理

EpubBook book = reader.parse(config);
```

### 3. 访问书籍内容

```java
// 获取元数据
Metadata metadata = book.getMetadata();
System.out.println("标题: " + metadata.getTitle());
System.out.println("作者: " + metadata.getCreator());
System.out.println("语言: " + metadata.getLanguage());
System.out.println("出版日期: " + metadata.getDate());

// 获取章节列表
List<EpubChapter> chapters = book.getChapters();
for (EpubChapter chapter : chapters) {
    System.out.println("章节: " + chapter.getTitle());
    System.out.println("内容路径: " + chapter.getContent());
    
    // 获取章节内容（如果启用了延迟加载，这将触发实际加载）
    String content = chapter.getContentAsString();
}

// 获取资源文件
List<EpubResource> resources = book.getResources();
for (EpubResource resource : resources) {
    System.out.println("资源: " + resource.getHref());
    System.out.println("媒体类型: " + resource.getMediaType());
    
    // 获取资源数据
    byte[] data = resource.getData();
}
```

## 高级功能使用

### 异步处理

EPUBime 支持异步解析，以避免阻塞主线程：

```java
import fun.lzwi.epubime.api.AsyncEpubProcessor;

AsyncEpubProcessor processor = new AsyncEpubProcessor();
CompletableFuture<EpubBook> future = processor.parseAsync(epubFile);

future.thenAccept(book -> {
    // 在解析完成后处理书籍对象
    System.out.println("书籍解析完成: " + book.getMetadata().getTitle());
}).exceptionally(throwable -> {
    // 处理解析异常
    System.err.println("解析失败: " + throwable.getMessage());
    return null;
});
```

### 错误处理和恢复

EPUBime 提供了灵活的错误处理机制：

```java
try {
    // 使用宽松模式解析，允许部分错误恢复
    ParseOptions options = ParseOptions.lenient()
        .withContinueOnMetadataError(true)
        .withContinueOnResourceError(true);
    
    ParseResult result = reader.parseWithOptions(options);
    
    if (result.isSuccess()) {
        // 完全成功
        EpubBook book = result.getEpubBook();
    } else if (result.isPartialSuccess()) {
        // 部分成功，有警告信息
        EpubBook book = result.getEpubBook();
        ErrorContext errorContext = result.getErrorContext();
        System.out.println("解析完成但有警告: " + errorContext.getStatistics());
    } else {
        // 解析失败
        ErrorContext errorContext = result.getErrorContext();
        System.err.println("解析失败: " + errorContext.generateReport());
    }
} catch (EpubParseException e) {
    System.err.println("解析异常: " + e.getMessage());
    System.err.println("错误码: " + e.getErrorCode());
    System.err.println("恢复建议: " + e.getRecoverySuggestion());
}
```

### 缓存机制

为了提高性能，EPUBime 提供了内置的缓存机制：

```java
// 启用缓存
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true);

// 或使用自定义缓存配置
EpubCacheManager cacheManager = EpubCacheManager.getInstance();
cacheManager.setMaxCacheSize(100); // 最大缓存100本书籍
cacheManager.setCacheTimeout(3600000); // 缓存超时1小时

EpubBook book = reader.parse(config);
```

## 性能优化建议

### 1. 内存管理

对于大文件或批量处理，建议合理配置内存：

```java
EpubReaderConfig config = new EpubReaderConfig()
    .withLazyLoading(true)        // 启用延迟加载
    .withParallelProcessing(false); // 在内存受限时禁用并行处理
```

### 2. 批量处理

处理多个文件时，使用缓存和适当的资源管理：

```java
EpubCacheManager cacheManager = EpubCacheManager.getInstance();
cacheManager.clear(); // 在批量处理前清理缓存

List<File> epubFiles = getEpubFiles(); // 获取EPUB文件列表
List<EpubBook> books = new ArrayList<>();

for (File file : epubFiles) {
    try {
        EpubBook book = EpubReader.fromFile(file).parse();
        books.add(book);
        
        // 定期清理以释放内存
        if (books.size() % 10 == 0) {
            System.gc(); // 建议垃圾回收
        }
    } catch (Exception e) {
        System.err.println("处理文件失败: " + file.getName() + " - " + e.getMessage());
    }
}
```

### 3. 资源释放

确保在使用完资源后正确释放：

```java
EpubBook book = reader.parse();
try {
    // 使用书籍对象
    processBook(book);
} finally {
    // 清理资源
    if (book != null) {
        book.close(); // 释放相关资源
    }
}
```

## 常见问题解答

### Q: 如何处理受保护的EPUB文件？
A: EPUBime 目前不支持 DRM 保护的 EPUB 文件。您需要先移除 DRM 保护再进行解析。

### Q: 解析大文件时出现内存不足错误怎么办？
A: 建议启用延迟加载和缓存机制，并适当调整 JVM 内存参数。

### Q: 如何获取章节的实际内容？
A: 使用 `chapter.getContentAsString()` 方法获取章节的 HTML 内容。

### Q: 支持 EPUB 3 的哪些特性？
A: EPUBime 支持 EPUB 3.3 规范的大部分特性，包括导航文档、媒体覆盖、固定布局等。

## API 参考

详细的 API 文档请参阅 [API 参考](/api/)。