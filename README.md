# EPUBime

[English](README_en.md) | [中文](README.md)

EPUBime 是一个纯 Java 库，用于解析 EPUB 文件格式。该项目提供了完整的 EPUB 文件解析功能，包括元数据提取、章节内容读取和资源文件处理。支持 EPUB 2 和 EPUB 3 格式。

## 功能特性

- **单一职责原则**: 严格遵循 SOLID 原则，每个类职责明确，架构清晰
- **元数据解析**: 提取标题、作者、出版商、语言、标识符等标准 EPUB 元数据
- **目录解析**: 支持 NCX 和 NAV 两种目录格式，智能选择最佳格式
- **资源提取**: 提取 EPUB 文件中的所有资源（图片、CSS、样式等）
- **ZIP 处理**: 基于 ZIP 文件格式的 EPUB 文档解析，安全高效
- **错误处理**: 完善的异常处理体系，支持详细错误信息和恢复机制
- **缓存管理**: 提供智能缓存机制，避免重复解析相同内容
- **流式处理**: 支持对大型 EPUB 文件的流式处理，优化内存使用
- **EPUB 3.3 支持**: 完全兼容 EPUB 3.3 规范，包括可访问性元数据
- **嵌套章节**: 支持解析复杂的嵌套章节结构
- **多种导航类型**: 支持 landmarks、page-list、toc 等多种导航类型
- **现代 Fluent API**: 简洁的 Fluent API 设计，支持方法链式调用
- **专用处理器模式**: 按功能划分的专用处理器，提高代码可维护性
- **异步处理**: 支持异步解析和处理，提高应用响应性
- **并行处理**: 支持多资源并行处理，提升处理效率
- **资源回退机制**: 智能的资源回退处理，确保兼容性
- **路径安全验证**: 防止目录遍历攻击的安全机制
- **专业基准测试**: 集成 JMH (Java Microbenchmark Harness) 进行精确性能测试

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
import fun.lzwi.epubime.api.*;
import fun.lzwi.epubime.epub.*;

File epubFile = new File("path/to/your/book.epub");

// 使用现代 Fluent API（推荐）
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)
    .withLazyLoading(false)
    .withParallelProcessing(true);
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

// 流式处理章节内容（适合大文件）
EpubReader.fromFile(epubFile)
    .streamChapters((chapter, inputStream) -> {
        System.out.println("处理章节: " + chapter.getTitle());
        // 处理章节内容流
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    });

// 异步处理
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();
CompletableFuture<EpubBook> futureBook = asyncProcessor.parseBookAsync(epubFile);
EpubBook asyncBook = futureBook.get(); // 等待完成
```

更多使用示例和高级功能，请查看[完整文档](https://lzw-723.github.io/epubime/)和[实际应用示例](docs/practical-examples.md)。

## 性能基准测试

EPUBime 集成了专业的基准测试工具 JMH (Java Microbenchmark Harness)，提供精确的性能测量和与行业标准库的对比。

### 运行基准测试

```bash
# 运行专业基准测试
mvn exec:java -Dexec.mainClass="fun.lzwi.epubime.epub.EpubJmhBenchmark" -Dexec.classpathScope=test

# 运行传统性能测试
mvn test -Dtest=PerformanceBenchmarkTest

# 运行与 epublib 的对比测试
mvn test -Dtest=EpubimeVsEpublibBenchmarkTest
```

### 最新基准测试结果

在标准测试环境中，EPUBime 相比 epublib 表现出色：

#### 简单解析性能
- **EPUBime**：4.37ms vs **epublib**：7.04ms（**37.9% 性能提升**）

#### 实际使用场景（解析+访问）
- **EPUBime**：3.72ms vs **epublib**：7.42ms（**49.8% 性能提升**）
- 包含：解析 + 元数据访问 + 章节列表 + 资源列表

#### 完整工作流性能
- **EPUBime**：3.15ms（包含：解析 + 元数据 + 章节 + 资源 + 封面 + 第一章内容读取）

#### 文件读取性能
- mimetype：0.31ms，OPF：0.40ms，NCX：0.34ms

#### 内存和缓存效率
- 智能缓存和流式处理，内存使用降低 25-40%
- 重复解析时性能提升 80% 以上

### 性能优势

1. **高速解析**：优化的解析算法，显著快于传统库
2. **智能缓存**：避免重复 I/O 操作，提升重复访问性能
3. **流式处理**：支持大文件处理，内存使用稳定
4. **批量操作**：减少系统调用，提高 I/O 效率
5. **懒加载**：按需加载资源，降低内存占用

## 版权许可

该项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件。