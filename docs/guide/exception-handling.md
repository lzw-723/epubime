# 异常处理

EPUBime 提供了完善的异常处理机制，所有异常都继承自 `BaseEpubException` 基类。EPUBime 还提供了增强的错误处理功能，包括错误码系统、详细的错误上下文收集、灵活的错误恢复策略等。

## 异常体系

```
Exception
└── BaseEpubException
    ├── EpubParseException      (解析异常)
    │   └── EpubXmlParseException (XML解析异常)
    ├── EpubFormatException     (格式异常)
    ├── EpubPathValidationException (路径验证异常)
    ├── EpubResourceException   (资源异常)
    └── EpubZipException        (ZIP 异常)
```

## 增强错误处理功能

### 错误码系统

EPUBime 引入了详细的错误码系统，帮助开发者快速定位问题：

```java
try {
    EpubBook book = parser.parse();
} catch (EpubParseException e) {
    System.err.println("错误码: " + e.getErrorCode());
    System.err.println("错误码值: " + e.getErrorCodeValue());
    System.err.println("恢复建议: " + e.getRecoverySuggestion());
}
```

可用的错误码包括：
- 文件相关错误 (1000-1999)：如 FILE_NOT_FOUND, FILE_CORRUPTED
- ZIP相关错误 (2000-2999)：如 ZIP_INVALID, ZIP_ENTRY_NOT_FOUND
- XML解析错误 (3000-3999)：如 XML_PARSE_ERROR, XML_INVALID_STRUCTURE
- EPUB格式错误 (4000-4999)：如 EPUB_INVALID_CONTAINER, EPUB_MISSING_MIMETYPE
- 路径验证错误 (5000-5999)：如 PATH_TRAVERSAL_ATTACK
- 资源相关错误 (6000-6999)：如 RESOURCE_NOT_FOUND, RESOURCE_LOAD_FAILED

### 异常构建器模式

EPUBime 提供了灵活的构建器模式来创建异常：

```java
EpubParseException exception = new EpubParseException.Builder()
    .message("自定义错误消息")
    .fileName("book.epub")
    .filePath("/path/to/book.epub")
    .operation("metadataParsing")
    .errorCode(EpubParseException.ErrorCode.XML_PARSE_ERROR)
    .lineNumber(10)
    .columnNumber(5)
    .addContext("customKey", "customValue")
    .recoverySuggestion("自定义恢复建议")
    .build();
```

## 错误上下文收集

EPUBime 提供了 `ErrorContext` 类来收集解析过程中的错误、警告和调试信息：

```java
// 创建错误上下文
ErrorContext errorContext = new ErrorContext(100, ParseOptions.LogLevel.DEBUG);

// 记录不同级别的信息
errorContext.debug("调试信息", "book.epub", "/path", "debugOp", null);
errorContext.info("普通信息", "book.epub", "/path", "infoOp", null);
errorContext.warning("警告信息", "book.epub", "/path", "warningOp", 
                   EpubParseException.ErrorCode.XML_INVALID_STRUCTURE, null);
errorContext.error("错误信息", "book.epub", "/path", "errorOp", 
                 EpubParseException.ErrorCode.FILE_NOT_FOUND, null, null);
errorContext.fatal("致命错误", "book.epub", "/path", "fatalOp", 
                 EpubParseException.ErrorCode.EPUB_INVALID_CONTAINER, null, null);

// 生成错误报告
String report = errorContext.generateReport();
System.out.println(report);

// 获取错误统计
ErrorContext.ErrorStatistics stats = errorContext.getStatistics();
```

## 解析选项配置

通过 `ParseOptions` 类可以配置解析过程中的错误处理行为：

```java
// 严格模式（默认）
ParseOptions strictOptions = ParseOptions.strict();

// 宽松模式
ParseOptions lenientOptions = ParseOptions.lenient();

// 最佳努力模式
ParseOptions bestEffortOptions = ParseOptions.bestEffort();

// 自定义配置
ParseOptions customOptions = ParseOptions.lenient()
    .withErrorHandlingStrategy(ParseOptions.ErrorHandlingStrategy.LENIENT)
    .withLogLevel(ParseOptions.LogLevel.WARNING)
    .withCollectWarnings(true)
    .withContinueOnMetadataError(true)
    .withContinueOnNavigationError(true)
    .withContinueOnResourceError(true)
    .withSkipInvalidResources(true)
    .withUseFallbackMetadata(true)
    .withIgnoredErrorPattern("optional")
    .withIgnoredErrorPattern("missing");
```

## 解析结果处理

`ParseResult` 类封装了解析的结果，包括解析的书籍对象和错误信息：

```java
ParseResult result = parser.parseWithOptions(file, options);

// 检查解析状态
if (result.isSuccess()) {
    EpubBook book = result.getEpubBook();
    // 处理成功解析的书籍
} else if (result.isPartialSuccess()) {
    EpubBook book = result.getEpubBook();
    ErrorContext errorContext = result.getErrorContext();
    // 处理部分成功的解析结果
} else if (result.isFailure()) {
    ErrorContext errorContext = result.getErrorContext();
    // 处理解析失败
}

// 获取详细信息
System.out.println("解析状态: " + result.getStatus());
System.out.println("解析时间: " + result.getParseTimeMs() + "ms");
System.out.println("解析摘要: " + result.getParseSummary());
```

## EpubParseException

解析异常，用于处理 EPUB 文件解析过程中的错误。

```java
try {
    EpubBook book = parser.parse();
} catch (EpubParseException e) {
    System.err.println("解析错误: " + e.getMessage());
    System.err.println("文件: " + e.getFileName());
    System.err.println("路径: " + e.getFilePath());
    System.err.println("操作: " + e.getOperation());
    System.err.println("错误码: " + e.getErrorCode());
    System.err.println("恢复建议: " + e.getRecoverySuggestion());
    
    // 获取原始异常（如果有的话）
    Throwable cause = e.getCause();
    if (cause != null) {
        System.err.println("原因: " + cause.getMessage());
    }
    
    // 获取根异常
    Throwable rootCause = e.getRootCause();
    if (rootCause != cause) {
        System.err.println("根因: " + rootCause.getMessage());
    }
}
```

### 常见场景
- EPUB 文件损坏或格式不正确
- 内部文件格式错误（如 XML 格式错误）
- 解析过程中遇到无法处理的数据

## EpubXmlParseException

XML解析异常，专门用于处理XML格式错误的问题。

```java
try {
    Metadata metadata = EpubParser.parseMetadata(opfContent);
} catch (EpubXmlParseException e) {
    System.err.println("XML解析错误: " + e.getMessage());
    System.err.println("文件: " + e.getFileName());
    System.err.println("行号: " + e.getLineNumber());
    System.err.println("列号: " + e.getColumnNumber());
}
```

### 常见场景
- XML格式不正确
- 缺少必需的XML元素
- XML属性值无效

## EpubFormatException

格式异常，用于处理 EPUB 格式不符合规范的情况。

```java
try {
    Metadata metadata = EpubParser.parseMetadata(opfContent);
} catch (EpubFormatException e) {
    System.err.println("格式错误: " + e.getMessage());
    System.err.println("文件: " + e.getFileName());
    System.err.println("路径: " + e.getFilePath());
    System.err.println("操作: " + e.getOperation());
    System.err.println("恢复建议: " + e.getRecoverySuggestion());
}
```

### 常见场景
- EPUB 规范违反（如缺少必需的文件或元素）
- 元数据格式不正确
- 内容文档格式不符合 EPUB 规范

## EpubPathValidationException

路径验证异常，用于防止目录遍历攻击。

```java
try {
    String validatedPath = PathValidator.validatePath(inputPath);
} catch (EpubPathValidationException e) {
    System.err.println("路径验证失败: " + e.getMessage());
    System.err.println("无效路径: " + e.getFilePath());
    System.err.println("操作: " + e.getOperation());
    System.err.println("错误码: " + e.getErrorCode());
}
```

### 常见场景
- 尝试访问 EPUB 文件外部的路径（如 `../../../etc/passwd`）
- 使用非法字符或路径格式

## EpubResourceException

资源异常，用于处理资源文件访问错误。

```java
try {
    EpubResource resource = book.getCover();
    byte[] data = resource.getData();
} catch (EpubResourceException e) {
    System.err.println("资源错误: " + e.getMessage());
    System.err.println("资源路径: " + e.getFilePath());
    System.err.println("操作: " + e.getOperation());
    System.err.println("错误码: " + e.getErrorCode());
}
```

### 常见场景
- 资源文件不存在
- 无法读取资源内容
- 资源文件损坏

## EpubZipException

ZIP 异常，用于处理 ZIP 文件操作错误。

```java
try (ZipFileManager zipManager = new ZipFileManager(epubFile)) {
    byte[] content = zipManager.getFileContent("OEBPS/content.opf");
} catch (EpubZipException e) {
    System.err.println("ZIP 操作错误: " + e.getMessage());
    System.err.println("文件: " + e.getFileName());
    System.err.println("条目: " + e.getFilePath());
    System.err.println("操作: " + e.getOperation());
    System.err.println("错误码: " + e.getErrorCode());
}
```

### 常见场景
- ZIP 文件损坏
- 无法打开 ZIP 文件
- ZIP 条目不存在

## 综合异常处理示例

```java
public class EpubProcessor {
    public void processEpub(File epubFile) {
        // 配置解析选项
        ParseOptions options = ParseOptions.lenient()
            .withLogLevel(ParseOptions.LogLevel.INFO)
            .withContinueOnMetadataError(true)
            .withContinueOnResourceError(true);
        
        try {
            // 使用配置选项解析EPUB
            EpubParser parser = new EpubParser(epubFile);
            ParseResult result = parser.parseWithOptions(epubFile, options);
            
            if (result.isSuccess() || result.isPartialSuccess()) {
                EpubBook book = result.getEpubBook();
                // 处理书籍内容
                processBook(book);
                
                // 输出解析摘要
                System.out.println(result.getParseSummary());
                
                // 如果有警告或错误，输出详细信息
                if (result.getErrorContext() != null) {
                    ErrorContext errorContext = result.getErrorContext();
                    if (errorContext.hasWarnings()) {
                        System.out.println("警告信息:");
                        for (ErrorContext.ErrorRecord warning : errorContext.getWarnings()) {
                            System.out.println("- " + warning.toString());
                        }
                    }
                }
            } else {
                // 处理解析失败
                System.err.println("解析失败: " + result.getParseSummary());
                if (result.getErrorContext() != null) {
                    System.err.println(result.getErrorContext().generateReport());
                }
            }
            
        } catch (EpubParseException e) {
            // 处理解析异常
            handleParseException(e);
        } catch (EpubFormatException e) {
            // 处理格式异常
            handleFormatException(e);
        } catch (EpubPathValidationException e) {
            // 处理路径验证异常
            handlePathValidationException(e);
        } catch (EpubResourceException e) {
            // 处理资源异常
            handleResourceException(e);
        } catch (EpubZipException e) {
            // 处理 ZIP 异常
            handleZipException(e);
        } catch (Exception e) {
            // 处理其他未预期的异常
            System.err.println("未预期的错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleParseException(EpubParseException e) {
        System.err.println("解析失败 - 文件: " + e.getFileName() + 
                          ", 错误: " + e.getMessage() + 
                          ", 错误码: " + e.getErrorCode());
        
        if (e.getRecoverySuggestion() != null) {
            System.err.println("恢复建议: " + e.getRecoverySuggestion());
        }
    }
    
    private void handleFormatException(EpubFormatException e) {
        System.err.println("格式错误 - 文件: " + e.getFileName() + 
                          ", 错误: " + e.getMessage());
        
        if (e.getRecoverySuggestion() != null) {
            System.err.println("恢复建议: " + e.getRecoverySuggestion());
        }
    }
    
    private void handlePathValidationException(EpubPathValidationException e) {
        System.err.println("路径验证失败 - 路径: " + e.getFilePath() + 
                          ", 错误: " + e.getMessage());
    }
    
    private void handleResourceException(EpubResourceException e) {
        System.err.println("资源访问失败 - 路径: " + e.getFilePath() + 
                          ", 错误: " + e.getMessage());
    }
    
    private void handleZipException(EpubZipException e) {
        System.err.println("ZIP 操作失败 - 文件: " + e.getFileName() + 
                          ", 错误: " + e.getMessage());
    }
    
    private void processBook(EpubBook book) {
        // 实际处理书籍内容的逻辑
        System.out.println("处理书籍: " + book.getMetadata().getTitle());
    }
}
```

## 最佳实践

1. **使用 ParseResult 处理解析结果**：优先使用 `ParseResult` 来处理解析结果，它提供了更丰富的错误信息和状态。

2. **配置合适的解析选项**：根据应用需求选择合适的 `ParseOptions`，平衡解析的严格性和容错性。

3. **处理错误码**：利用错误码系统快速识别和处理特定类型的错误。

4. **收集错误上下文**：使用 `ErrorContext` 收集详细的错误信息，便于调试和问题分析。

5. **具体异常处理**：优先捕获具体的异常类型，而不是通用的异常。

6. **错误信息记录**：记录详细的错误信息，包括文件名、路径、操作类型、错误码等。

7. **资源清理**：确保在异常情况下正确清理资源（如关闭文件流）。

8. **用户友好**：向用户提供有意义的错误信息和恢复建议，而不是技术细节。

9. **日志记录**：使用日志框架记录异常，便于调试和监控。

异常处理是确保应用稳定性和安全性的重要部分，EPUBime 的异常体系提供了详细的信息来帮助开发者快速定位和解决问题。