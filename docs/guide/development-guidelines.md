# 开发规范

## 代码风格

- **包命名**: 使用 `fun.lzwi.epubime` 作为基础包名
- **类命名**: 遵循 Java 标准命名约定，使用驼峰命名法
- **方法命名**: 语义化命名，使用驼峰命名法
- **注释**: 使用中文注释，解释功能意图而非实现细节
- **编码标准**: UTF-8 编码
- **静态分析**: 通过 SpotBugs 工具进行静态代码分析，确保代码质量并避免常见缺陷

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
│   │           │   ├── EpubBook.java    # EPUB 书籍对象
│   │           │   ├── EpubChapter.java # 章节模型
│   │           │   ├── EpubParser.java  # 主要解析器
│   │           │   ├── EpubResource.java# 资源模型
│   │           │   └── Metadata.java    # 元数据模型
│   │           ├── exception/          # 异常处理
│   │           │   ├── EpubFormatException.java
│   │           │   ├── EpubParseException.java
│   │           │   ├── EpubPathValidationException.java
│   │           │   ├── EpubResourceException.java
│   │           │   └── EpubZipException.java
│   │           └── zip/                # ZIP 工具
│   │               ├── PathValidator.java
│   │               ├── ZipFileManager.java
│   │               └── ZipUtils.java
│   └── test/
│       ├── java/
│       │   └── fun/lzwi/epubime/
│       │       ├── epub/               # EPUB 解析测试
│       │       │   ├── Epub33MetadataTest.java
│       │       │   ├── EpubBookTest.java
│       │       │   ├── EpubCacheTest.java
│       │       │   ├── EpubFormatExceptionTest.java
│       │       │   ├── EpubimeVsEpublibBenchmarkTest.java
│       │       │   ├── EpubParseExceptionEnhancedTest.java
│       │       │   ├── EpubParseExceptionTest.java
│       │       │   ├── EpubParserTest.java
│       │       │   ├── EpubPathValidationExceptionTest.java
│       │       │   ├── EpubResourceExceptionTest.java
│       │       │   ├── EpubResourceFallbackTest.java
│       │       │   ├── EpubResourceTest.java
│       │       │   ├── EpubZipExceptionTest.java
│       │       │   ├── MetadataTest.java
│       │       │   └── PerformanceBenchmarkTest.java
│       │       ├── zip/                # ZIP 工具测试
│       │       │   ├── PathValidatorTest.java
│       │       │   ├── ZipFileManagerIntegrationTest.java
│       │       │   ├── ZipFileManagerTest.java
│       │       │   └── ZipUtilsTest.java
│       │       └── ResUtils.java       # 测试资源工具
│       └── resources/
│           └── fun/lzwi/epubime/epub/ 《坟》鲁迅.epub # 测试用示例文件
├── pom.xml                            # Maven 项目配置
├── .github/workflows/maven.yml        # CI/CD 配置
└── .gitignore                         # Git 忽略文件配置
```

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

## 测试规范

### 测试覆盖率
- **最低要求**: 80% 指令覆盖率，100% 类覆盖率
- **测试文件**: `*Test.java` 命名规范
- **测试资源**: 放在 `src/test/resources/` 目录
- **测试工具**: 提供 `ResUtils` 工具类用于资源文件获取

### 测试类型
- **单元测试**: 针对单个类或方法的测试
- **集成测试**: 验证多个组件之间的交互
- **性能测试**: 验证库的性能表现
- **基准测试**: 使用JMH进行专业基准测试，与epublib等库进行性能对比