# 快速开始

## 项目简介

EPUBime 是一个纯 Java 库，用于解析 EPUB 文件格式。该项目提供了完整的 EPUB 文件解析功能，包括元数据提取、章节内容读取和资源文件处理。支持 EPUB 2 和 EPUB 3 格式。

## 技术栈

- **Java 8+**: 主要开发语言
- **Maven**: 项目构建和依赖管理
- **JSoup 1.19.1**: HTML/XML 解析库
- **JaCoCo 0.8.10**: 代码覆盖率分析工具
- **JUnit 5.10.2**: 单元测试框架
- **SpotBugs 4.7.3.6**: 静态代码分析工具，用于检测潜在的代码缺陷

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

### 传统API方式

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
if (cover != null) {
    byte[] coverData = cover.getData();
}
```

### 现代 Fluent API（推荐）

```java
import fun.lzwi.epubime.api.*;
import fun.lzwi.epubime.epub.*;

File epubFile = new File("path/to/your/book.epub");

// 使用默认配置
EpubBook book = EpubReader.fromFile(epubFile).parse();

// 或使用自定义配置
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)              // 启用缓存
    .withLazyLoading(false)       // 禁用延迟加载
    .withParallelProcessing(true); // 启用并行处理
EpubBook book = EpubReader.fromFile(epubFile, config).parse();

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

// 获取封面（使用专用处理器）
EpubResource cover = EpubBookProcessor.getCover(book);
if (cover != null) {
    byte[] coverData = cover.getData();
}

// 快速获取书籍信息（无需完整解析）
EpubReader.EpubInfo info = EpubReader.fromFile(epubFile).getInfo();
System.out.println("书籍信息: " + info);
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

### 现代API特性
- **Fluent API**: 支持链式方法调用，代码更简洁
- **异步处理**: 支持异步解析，提高应用响应性
- **流式处理**: 支持大文件的流式处理，优化内存使用
- **并行处理**: 支持多资源并行处理，提升性能
- **智能缓存**: 自动缓存解析结果，避免重复解析

### 增强功能
- **增强的书籍对象**: `EpubBookEnhanced` 提供更多便利方法
- **增强的元数据**: `MetadataEnhanced` 支持更多元数据操作
- **异步处理器**: `AsyncEpubProcessor` 支持异步操作
- **资源回退机制**: 智能处理资源加载失败的情况

## 运行环境

- Java 8 或更高版本
- Maven 3.5+（用于构建和依赖管理）

## 构建和测试

### 基本构建命令

```bash
# 清理并编译项目
mvn clean compile

# 运行测试
mvn test

# 打包（包含测试）
mvn package

# 生成代码覆盖率报告
mvn jacoco:report

# 静态代码分析
mvn spotbugs:check

# 生成详细的SpotBugs报告
mvn spotbugs:spotbugs

# 检查依赖版本更新
mvn versions:display-dependency-updates

# 检查插件版本更新
mvn versions:display-plugin-updates
```

## 后续步骤

了解更详细的使用方法，请参阅：
- [基础用法](/guide/basic-usage)
- [高级功能](/guide/advanced-features)
- [API 参考](/api/)