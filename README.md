# EPUBime

[English](README_en.md) | [中文](README.md)

EPUBime 是一个纯 Java 库，用于解析 EPUB 文件格式。该项目提供了完整的 EPUB 文件解析功能，包括元数据提取、章节内容读取和资源文件处理。支持 EPUB 2 和 EPUB 3 格式。

## 功能特性

- **元数据解析**: 提取标题、作者、出版商、语言、标识符等标准 EPUB 元数据
- **目录解析**: 支持 NCX 和 NAV 两种目录格式
- **资源提取**: 提取 EPUB 文件中的所有资源（图片、CSS、样式等）
- **ZIP 处理**: 基于 ZIP 文件格式的 EPUB 文档解析
- **错误处理**: 完善的异常处理体系
- **缓存管理**: 提供智能缓存机制，避免重复解析相同内容
- **流式处理**: 支持对大型 EPUB 文件的流式处理，优化内存使用
- **EPUB 3.3 支持**: 兼容 EPUB 3.3 规范
- **嵌套章节**: 支持解析嵌套的章节结构
- **多种导航类型**: 支持 landmarks、page-list 等多种导航类型

## 快速开始

### Maven 依赖

在 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>fun.lzwi</groupId>
    <artifactId>epubime</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 基本使用

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

更多使用示例和高级功能，请查看[完整文档](https://lzw-723.github.io/epubime/)。

## 版权许可

该项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件。