# 快速开始

## 项目简介

EPUBime 是一个纯 Java 库，用于解析 EPUB 文件格式。该项目提供了完整的 EPUB 文件解析功能，包括元数据提取、章节内容读取和资源文件处理。支持 EPUB 2 和 EPUB 3 格式。

## 安装依赖

在 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>fun.lzwi</groupId>
    <artifactId>epubime</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## 基础使用

以下是最基本的使用示例，演示如何解析一个 EPUB 文件并获取基本信息：

```java
import fun.lzwi.epubime.epub.*;

File epubFile = new File("path/to/your/book.epub");
EpubParser parser = new EpubParser(epubFile);
EpubBook book = parser.parse();

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

// 获取封面
EpubResource cover = book.getCover();
byte[] coverData = cover.getData();
```

## 主要功能

### 元数据解析
从 EPUB 文件中提取书籍标题、作者、出版商、语言等标准元数据信息。

### 章节解析
支持 NCX 和 NAV 两种目录格式，可获取完整的章节结构和嵌套章节。

### 资源提取
提取 EPUB 文件中的所有资源（图片、CSS、样式等），支持流式处理。

### 异常处理
提供完善的异常处理机制，包括格式异常、解析异常、路径验证异常等。

## 运行环境

- Java 8 或更高版本
- Maven 3.5+（用于构建和依赖管理）

## 后续步骤

了解更详细的使用方法，请参阅：
- [基础用法](/guide/basic-usage)
- [高级功能](/guide/advanced-features)
- [API 参考](/api/)