# EPUBime

EPUBime 是一个纯 Java 库，用于解析 EPUB 文件格式。该项目提供了完整的 EPUB 文件解析功能，包括元数据提取、章节内容读取和资源文件处理。支持 EPUB 2 和 EPUB 3 格式。

## 功能特性

- **元数据解析**: 提取标题、作者、出版商、语言、标识符等标准 EPUB 元数据
- **目录解析**: 支持 NCX 和 NAV 两种目录格式
- **资源提取**: 提取 EPUB 文件中的所有资源（图片、CSS、样式等）
- **ZIP 处理**: 基于 ZIP 文件格式的 EPUB 文档解析
- **错误处理**: 专门的异常处理机制
- **缓存管理**: 提供智能缓存机制，避免重复解析相同内容
- **流式处理**: 支持对大型 EPUB 文件的流式处理，优化内存使用

## 技术栈

- **Java 8+**: 主要开发语言
- **Maven**: 项目构建和依赖管理
- **JSoup 1.18.3**: HTML/XML 解析库
- **JUnit 4.13.1**: 单元测试框架
- **SpotBugs 4.7.3.6**: 静态代码分析工具，用于检测潜在的代码缺陷

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

### 高级功能

```java
// 使用缓存解析（推荐用于重复解析同一文件）
EpubParser parser = new EpubParser(epubFile);
EpubBook book = parser.parse(); // 第一次解析后会缓存结果

// 流式处理大文件章节内容（避免将整个文件加载到内存）
parser.processHtmlChapterContent(epubFile, "chapter1.html", inputStream -> {
    // 处理输入流，例如解析HTML内容
});

// 批量流式处理多个章节
List<String> chapterFiles = Arrays.asList("chapter1.html", "chapter2.html");
EpubParser.processMultipleHtmlChapters(epubFile, chapterFiles, (fileName, inputStream) -> {
    // 处理每个文件的输入流
});

// 直接解析特定文件
String container = EpubParser.readEpubContent(epubFile, "META-INF/container.xml");
String opfContent = EpubParser.readEpubContent(epubFile, "OEBPS/content.opf");

// 解析元数据
Metadata metadata = EpubParser.parseMetadata(opfContent);

// 解析章节
String ncxPath = EpubParser.getNcxPath(opfContent, "OEBPS/");
String ncxContent = EpubParser.readEpubContent(epubFile, ncxPath);
List<EpubChapter> chapters = EpubParser.parseNcx(ncxContent);

// 获取所有资源
List<EpubResource> resources = EpubParser.parseResources(opfContent, "OEBPS/", epubFile);
```

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
```

## 项目结构

```
epubime/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── fun/lzwi/epubime/
│   │           ├── cache/              # 缓存管理
│   │           │   └── EpubCacheManager.java # 缓存管理器
│   │           ├── epub/               # EPUB 解析核心
│   │           │   ├── EpubParser.java  # 主要解析器
│   │           │   ├── EpubBook.java    # EPUB 书籍对象
│   │           │   ├── Metadata.java    # 元数据模型
│   │           │   ├── EpubChapter.java # 章节模型
│   │           │   ├── EpubResource.java# 资源模型
│   │           │   └── EpubParseException.java # 异常类
│   │           └── zip/
│   │               ├── ZipFileManager.java # ZIP 文件句柄管理器
│   │               └── ZipUtils.java    # ZIP 文件工具类
│   └── test/
│       ├── java/
│       │   └── fun/lzwi/epubime/
│       │       ├── epub/               # EPUB 解析测试
│       │       │   ├── EpubCacheTest.java # 缓存功能测试
│       │       │   └── other_test_files... # 其他解析测试
│       │       ├── zip/                # ZIP 工具测试
│       │       └── ResUtils.java       # 测试资源工具
│       └── resources/
│           └── fun/lzwi/epubime/epub/ 《坟》鲁迅.epub # 测试用示例文件
├── pom.xml                            # Maven 项目配置
├── .github/workflows/maven.yml        # CI/CD 配置
└── .gitignore                         # Git 忽略文件配置
```

## 开发规范

### 代码风格
- **包命名**: 使用 `fun.lzwi.epubime` 作为基础包名
- **类命名**: 遵循 Java 标准命名约定，使用驼峰命名法
- **方法命名**: 语义化命名，使用驼峰命名法
- **注释**: 使用中文注释，解释功能意图而非实现细节
- **编码标准**: UTF-8 编码
- **静态分析**: 通过 SpotBugs 工具进行静态代码分析，确保代码质量并避免常见缺陷

### 测试规范
- **测试覆盖率**: 最低 80% 指令覆盖率，100% 类覆盖率
- **测试文件**: `*Test.java` 命名规范
- **测试资源**: 放在 `src/test/resources/` 目录
- **测试工具**: 提供 `ResUtils` 工具类用于资源文件获取

## 扩展指南

### 添加新的元数据字段
1. 在 `Metadata.java` 中添加字段和相关方法
2. 在 `EpubParser.parseMetadata()` 中添加解析逻辑
3. 添加对应的测试用例

### 支持新的 EPUB 特性
1. 分析 EPUB 规范文档
2. 在 `EpubParser` 中添加相应的解析方法
3. 更新模型类以支持新特性
4. 添加综合测试用例

## 版权许可

该项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件。