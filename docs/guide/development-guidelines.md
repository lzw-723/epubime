# 用户指南

本指南适用于使用 EPUBime 库的用户。

## 基本用法

要开始使用 EPUBime，您首先需要将其添加到项目的依赖中：

```xml
<dependency>
    <groupId>fun.lzwi</groupId>
    <artifactId>epubime</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

然后，您可以使用以下代码解析 EPUB 文件：

```java
import fun.lzwi.epubime.api.*;
import fun.lzwi.epubime.epub.*;

File epubFile = new File("path/to/your/book.epub");
EpubBook book = EpubReader.fromFile(epubFile).parse();

// 获取元数据
Metadata metadata = book.getMetadata();
System.out.println("标题: " + metadata.getTitle());
System.out.println("作者: " + metadata.getCreator());
System.out.println("语言: " + metadata.getLanguage());

// 获取章节列表
List<EpubChapter> chapters = book.getChapters();
for (EpubChapter chapter : chapters) {
    System.out.println("章节: " + chapter.getTitle());
    System.out.println("内容路径: " + chapter.getContent());
}
```

## 高级功能

EPUBime 提供了多种高级功能，包括：

- 自定义配置选项
- 异步处理
- 错误处理和恢复机制
- 缓存机制
- 资源管理

## 配置选项

您可以使用 `EpubReaderConfig` 来配置解析行为：

```java
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)              // 启用缓存
    .withLazyLoading(false)       // 禁用延迟加载
    .withParallelProcessing(true); // 启用并行处理

EpubBook book = EpubReader.fromFile(epubFile, config).parse();
```

## 异常处理

EPUBime 提供了详细的异常处理机制，您可以捕获并处理各种异常：

```java
try {
    EpubBook book = EpubReader.fromFile(epubFile).parse();
    // 处理书籍对象
} catch (EpubParseException e) {
    System.err.println("解析错误: " + e.getMessage());
    System.err.println("错误码: " + e.getErrorCode());
} catch (EpubResourceException e) {
    System.err.println("资源错误: " + e.getMessage());
} catch (Exception e) {
    System.err.println("其他错误: " + e.getMessage());
}
```

## 性能优化

为了获得最佳性能，建议：

1. 启用缓存机制以避免重复解析相同的文件
2. 在处理大文件时使用并行处理选项
3. 合理配置内存使用，特别是在处理多个文件时