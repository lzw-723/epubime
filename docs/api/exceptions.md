# 异常类

EPUBime 提供了完整的异常处理体系，所有异常都继承自 `BaseEpubException` 基类。EPUBime 还提供了增强的错误处理功能，包括错误码系统、详细的错误上下文收集、灵活的错误恢复策略等。

## 继承关系

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
public enum ErrorCode {
    // 文件相关错误 1000-1999
    FILE_NOT_FOUND(1001, "File not found"),
    FILE_ACCESS_DENIED(1002, "File access denied"),
    FILE_CORRUPTED(1003, "File appears to be corrupted"),
    INVALID_FILE_FORMAT(1004, "Invalid file format"),
    
    // ZIP相关错误 2000-2999
    ZIP_INVALID(2001, "Invalid ZIP file format"),
    ZIP_ENTRY_NOT_FOUND(2002, "ZIP entry not found"),
    ZIP_DECOMPRESSION_FAILED(2003, "ZIP decompression failed"),
    
    // XML解析错误 3000-3999
    XML_PARSE_ERROR(3001, "XML parsing failed"),
    XML_INVALID_STRUCTURE(3002, "Invalid XML structure"),
    XML_MISSING_REQUIRED_ELEMENT(3003, "Missing required XML element"),
    XML_INVALID_ATTRIBUTE(3004, "Invalid XML attribute"),
    
    // EPUB格式错误 4000-4999
    EPUB_INVALID_CONTAINER(4001, "Invalid EPUB container"),
    EPUB_MISSING_MIMETYPE(4002, "Missing mimetype file"),
    EPUB_INVALID_OPF(4003, "Invalid OPF file"),
    EPUB_INVALID_NCX(4004, "Invalid NCX file"),
    EPUB_INVALID_NAV(4005, "Invalid NAV file"),
    
    // 路径验证错误 5000-5999
    PATH_TRAVERSAL_ATTACK(5001, "Path traversal attack detected"),
    PATH_INVALID_CHARACTER(5002, "Invalid character in path"),
    PATH_TOO_LONG(5003, "Path too long"),
    
    // 资源相关错误 6000-6999
    RESOURCE_NOT_FOUND(6001, "Resource not found"),
    RESOURCE_LOAD_FAILED(6002, "Resource loading failed"),
    RESOURCE_INVALID_TYPE(6003, "Invalid resource type"),
    
    // 通用错误 9000-9999
    UNKNOWN_ERROR(9001, "Unknown error occurred"),
    OPERATION_NOT_SUPPORTED(9002, "Operation not supported"),
    INTERNAL_ERROR(9003, "Internal error");
}
```

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

## BaseEpubException

`BaseEpubException` 是所有 EPUB 相关异常的基类。

### 构造函数

```java
public BaseEpubException(String message)
```
创建一个新的 BaseEpubException 实例。

参数:
- `message`: 异常消息

```java
public BaseEpubException(String message, Throwable cause)
```
创建一个新的 BaseEpubException 实例。

参数:
- `message`: 异常消息
- `cause`: 原始异常

```java
public BaseEpubException(Throwable cause)
```
创建一个新的 BaseEpubException 实例。

参数:
- `cause`: 原始异常

## EpubParseException

解析异常，用于处理 EPUB 文件解析过程中的错误。

### 构造函数

```java
public EpubParseException(String message)
```
创建一个新的 EpubParseException 实例。

参数:
- `message`: 异常消息

```java
public EpubParseException(String message, ErrorCode errorCode)
```
创建一个新的 EpubParseException 实例，包含错误码。

参数:
- `message`: 异常消息
- `errorCode`: 错误码

```java
public EpubParseException(String message, Throwable cause)
```
创建一个新的 EpubParseException 实例，包含原始异常。

参数:
- `message`: 异常消息
- `cause`: 原始异常

```java
public EpubParseException(String message, ErrorCode errorCode, Throwable cause)
```
创建一个新的 EpubParseException 实例，包含错误码和原始异常。

参数:
- `message`: 异常消息
- `errorCode`: 错误码
- `cause`: 原始异常

```java
public EpubParseException(String message, String fileName, String filePath, String operation, Throwable cause)
```
创建一个新的 EpubParseException 实例，包含文件名、路径、操作类型和原始异常。

参数:
- `message`: 异常消息
- `fileName`: 文件名
- `filePath`: 文件路径
- `operation`: 操作类型
- `cause`: 原始异常

```java
public EpubParseException(String message, String fileName, String filePath, String operation, ErrorCode errorCode, Throwable cause)
```
创建一个新的 EpubParseException 实例，包含文件名、路径、操作类型、错误码和原始异常。

参数:
- `message`: 异常消息
- `fileName`: 文件名
- `filePath`: 文件路径
- `operation`: 操作类型
- `errorCode`: 错误码
- `cause`: 原始异常

```java
public EpubParseException(String message, String fileName, String filePath, String operation)
```
创建一个新的 EpubParseException 实例，包含文件名、路径和操作类型。

参数:
- `message`: 异常消息
- `fileName`: 文件名
- `filePath`: 文件路径
- `operation`: 操作类型

```java
public EpubParseException(String message, String fileName, String filePath, String operation, ErrorCode errorCode)
```
创建一个新的 EpubParseException 实例，包含文件名、路径、操作类型和错误码。

参数:
- `message`: 异常消息
- `fileName`: 文件名
- `filePath`: 文件路径
- `operation`: 操作类型
- `errorCode`: 错误码

```java
public EpubParseException(Builder builder)
```
使用构建器创建一个新的 EpubParseException 实例。

参数:
- `builder`: 构建器

### 主要方法

#### getFileName()
```java
public String getFileName()
```
获取相关文件的名称。

返回:
- `String`: 文件名

#### getFilePath()
```java
public String getFilePath()
```
获取相关文件的路径。

返回:
- `String`: 文件路径

#### getOperation()
```java
public String getOperation()
```
获取操作类型。

返回:
- `String`: 操作类型

#### getErrorCode()
```java
public ErrorCode getErrorCode()
```
获取错误码。

返回:
- `ErrorCode`: 错误码

#### getErrorCodeValue()
```java
public int getErrorCodeValue()
```
获取错误码数值。

返回:
- `int`: 错误码数值

#### getRecoverySuggestion()
```java
public String getRecoverySuggestion()
```
获取恢复建议。

返回:
- `String`: 恢复建议

#### getLineNumber()
```java
public int getLineNumber()
```
获取行号。

返回:
- `int`: 行号，如果不适用返回-1

#### getColumnNumber()
```java
public int getColumnNumber()
```
获取列号。

返回:
- `int`: 列号，如果不适用返回-1

#### addContext()
```java
public void addContext(String key, Object value)
```
添加上下文信息。

参数:
- `key`: 键
- `value`: 值

#### getContext()
```java
public Object getContext(String key)
```
获取上下文信息。

参数:
- `key`: 键

返回:
- `Object`: 值

#### getAllContext()
```java
public Map<String, Object> getAllContext()
```
获取所有上下文信息。

返回:
- `Map<String, Object>`: 上下文映射

#### getRootCause()
```java
public Throwable getRootCause()
```
获取根异常。

返回:
- `Throwable`: 根异常

## EpubXmlParseException

XML解析异常，专门用于处理XML格式错误的问题。

### 构造函数

```java
public EpubXmlParseException(String message, String fileName)
```
创建一个新的 EpubXmlParseException 实例。

参数:
- `message`: 异常消息
- `fileName`: 文件名

```java
public EpubXmlParseException(String message, String fileName, int lineNumber, int columnNumber, Throwable cause)
```
创建一个新的 EpubXmlParseException 实例。

参数:
- `message`: 异常消息
- `fileName`: 文件名
- `lineNumber`: 行号
- `columnNumber`: 列号
- `cause`: 原始异常

```java
public EpubXmlParseException(String message, String fileName, Throwable cause)
```
创建一个新的 EpubXmlParseException 实例。

参数:
- `message`: 异常消息
- `fileName`: 文件名
- `cause`: 原始异常

### 主要方法

#### getFileName()
```java
public String getFileName()
```
获取文件名。

返回:
- `String`: 文件名

#### getLineNumber()
```java
public int getLineNumber()
```
获取行号。

返回:
- `int`: 行号

#### getColumnNumber()
```java
public int getColumnNumber()
```
获取列号。

返回:
- `int`: 列号

#### getFilePath()
```java
public String getFilePath()
```
获取文件路径（为了向后兼容）。

返回:
- `String`: 文件路径

#### getXPath()
```java
public String getXPath()
```
获取XPath（为了向后兼容）。

返回:
- `String`: XPath，简化设计中返回null

#### getErrorCode()
```java
public Object getErrorCode()
```
获取错误码（为了向后兼容）。

返回:
- `Object`: 错误码，简化设计中返回null

#### getOperation()
```java
public String getOperation()
```
获取操作类型（为了向后兼容）。

返回:
- `String`: 操作类型

## EpubFormatException

格式异常，用于处理 EPUB 格式不符合规范的情况。

### 构造函数

```java
public EpubFormatException(String message, File file)
```
创建一个新的 EpubFormatException 实例。

参数:
- `message`: 异常消息
- `file`: 文件

```java
public EpubFormatException(String message, File file, String details)
```
创建一个新的 EpubFormatException 实例。

参数:
- `message`: 异常消息
- `file`: 文件
- `details`: 详细信息

```java
public EpubFormatException(String message, File file, String details, Throwable cause)
```
创建一个新的 EpubFormatException 实例。

参数:
- `message`: 异常消息
- `file`: 文件
- `details`: 详细信息
- `cause`: 原始异常

```java
public EpubFormatException(String message, String fileName, String filePath)
```
创建一个新的 EpubFormatException 实例（为了向后兼容）。

参数:
- `message`: 异常消息
- `fileName`: 文件名
- `filePath`: 文件路径

```java
public EpubFormatException(String message, String fileName, String filePath, Throwable cause)
```
创建一个新的 EpubFormatException 实例（为了向后兼容）。

参数:
- `message`: 异常消息
- `fileName`: 文件名
- `filePath`: 文件路径
- `cause`: 原始异常

### 静态工厂方法

```java
public static EpubFormatException missingMimetype(String fileName, String filePath)
```
创建缺失mimetype文件的异常。

参数:
- `fileName`: 文件名
- `filePath`: 文件路径

返回:
- `EpubFormatException`: 异常实例

```java
public static EpubFormatException invalidContainer(String fileName, String filePath, String details)
```
创建无效container.xml文件的异常。

参数:
- `fileName`: 文件名
- `filePath`: 文件路径
- `details`: 详细信息

返回:
- `EpubFormatException`: 异常实例

```java
public static EpubFormatException invalidOpf(String fileName, String filePath, String opfPath, String details)
```
创建无效OPF文件的异常。

参数:
- `fileName`: 文件名
- `filePath`: 文件路径
- `opfPath`: OPF文件路径
- `details`: 详细信息

返回:
- `EpubFormatException`: 异常实例

```java
public static EpubFormatException invalidNcx(String fileName, String filePath, String ncxPath, String details)
```
创建无效NCX文件的异常。

参数:
- `fileName`: 文件名
- `filePath`: 文件路径
- `ncxPath`: NCX文件路径
- `details`: 详细信息

返回:
- `EpubFormatException`: 异常实例

```java
public static EpubFormatException invalidNav(String fileName, String filePath, String navPath, String details)
```
创建无效NAV文件的异常。

参数:
- `fileName`: 文件名
- `filePath`: 文件路径
- `navPath`: NAV文件路径
- `details`: 详细信息

返回:
- `EpubFormatException`: 异常实例

### 主要方法

#### getFile()
```java
public File getFile()
```
获取文件。

返回:
- `File`: 文件

#### getDetails()
```java
public String getDetails()
```
获取详细信息。

返回:
- `String`: 详细信息

#### getFileName()
```java
public String getFileName()
```
获取文件名（为了向后兼容）。

返回:
- `String`: 文件名

#### getFilePath()
```java
public String getFilePath()
```
获取文件路径（为了向后兼容）。

返回:
- `String`: 文件路径

#### getOperation()
```java
public String getOperation()
```
获取操作类型（为了向后兼容）。

返回:
- `String`: 操作类型

#### getErrorCode()
```java
public Object getErrorCode()
```
获取错误码（为了向后兼容）。

返回:
- `Object`: 错误码

#### getRecoverySuggestion()
```java
public String getRecoverySuggestion()
```
获取恢复建议（为了向后兼容）。

返回:
- `String`: 恢复建议

## EpubPathValidationException

路径验证异常，用于防止目录遍历攻击。

### 构造函数

```java
public EpubPathValidationException(String message, String invalidPath)
```
创建一个新的 EpubPathValidationException 实例。

参数:
- `message`: 异常消息
- `invalidPath`: 无效路径

```java
public EpubPathValidationException(String message, String invalidPath, Throwable cause)
```
创建一个新的 EpubPathValidationException 实例。

参数:
- `message`: 异常消息
- `invalidPath`: 无效路径
- `cause`: 原始异常

```java
public EpubPathValidationException(String message, String invalidPath, String fileName, Throwable cause)
```
创建一个新的 EpubPathValidationException 实例。

参数:
- `message`: 异常消息
- `invalidPath`: 无效路径
- `fileName`: 文件名
- `cause`: 原始异常

### 静态工厂方法

```java
public static EpubPathValidationException createForCompatibility(String message, String fileName, String filePath)
```
创建兼容性异常实例。

参数:
- `message`: 异常消息
- `fileName`: 文件名
- `filePath`: 文件路径

返回:
- `EpubPathValidationException`: 异常实例

```java
public static EpubPathValidationException createForCompatibility(String message, String fileName, String filePath, Throwable cause)
```
创建兼容性异常实例。

参数:
- `message`: 异常消息
- `fileName`: 文件名
- `filePath`: 文件路径
- `cause`: 原始异常

返回:
- `EpubPathValidationException`: 异常实例

### 主要方法

#### getInvalidPath()
```java
public String getInvalidPath()
```
获取无效路径。

返回:
- `String`: 无效路径

#### getFileName()
```java
public String getFileName()
```
获取文件名（为了向后兼容）。

返回:
- `String`: 文件名

#### getFilePath()
```java
public String getFilePath()
```
获取文件路径（为了向后兼容）。

返回:
- `String`: 文件路径

#### getOperation()
```java
public String getOperation()
```
获取操作类型（为了向后兼容）。

返回:
- `String`: 操作类型

#### getErrorCode()
```java
public ErrorCode getErrorCode()
```
获取错误码（为了向后兼容）。

返回:
- `ErrorCode`: 错误码

## EpubResourceException

资源异常，用于处理资源文件访问错误。

### 构造函数

```java
public EpubResourceException(String message, File file)
```
创建一个新的 EpubResourceException 实例。

参数:
- `message`: 异常消息
- `file`: 文件

```java
public EpubResourceException(String message, File file, String resourcePath)
```
创建一个新的 EpubResourceException 实例。

参数:
- `message`: 异常消息
- `file`: 文件
- `resourcePath`: 资源路径

```java
public EpubResourceException(String message, File file, String resourceId, String resourcePath, Throwable cause)
```
创建一个新的 EpubResourceException 实例。

参数:
- `message`: 异常消息
- `file`: 文件
- `resourceId`: 资源ID
- `resourcePath`: 资源路径
- `cause`: 原始异常

```java
public EpubResourceException(String message, String fileName, String resourcePath)
```
创建一个新的 EpubResourceException 实例（为了向后兼容）。

参数:
- `message`: 异常消息
- `fileName`: 文件名
- `resourcePath`: 资源路径

```java
public EpubResourceException(String message, String fileName, String resourcePath, Throwable cause)
```
创建一个新的 EpubResourceException 实例（为了向后兼容）。

参数:
- `message`: 异常消息
- `fileName`: 文件名
- `resourcePath`: 资源路径
- `cause`: 原始异常

### 主要方法

#### getFile()
```java
public File getFile()
```
获取文件。

返回:
- `File`: 文件

#### getResourceId()
```java
public String getResourceId()
```
获取资源ID。

返回:
- `String`: 资源ID

#### getResourcePath()
```java
public String getResourcePath()
```
获取资源路径。

返回:
- `String`: 资源路径

#### getFileName()
```java
public String getFileName()
```
获取文件名（为了向后兼容）。

返回:
- `String`: 文件名

#### getFilePath()
```java
public String getFilePath()
```
获取文件路径（为了向后兼容）。

返回:
- `String`: 文件路径

#### getOperation()
```java
public String getOperation()
```
获取操作类型（为了向后兼容）。

返回:
- `String`: 操作类型

#### getErrorCode()
```java
public Object getErrorCode()
```
获取错误码（为了向后兼容）。

返回:
- `Object`: 错误码

## EpubZipException

ZIP 异常，用于处理 ZIP 文件操作错误。

### 构造函数

```java
public EpubZipException(String message, File file)
```
创建一个新的 EpubZipException 实例。

参数:
- `message`: 异常消息
- `file`: 文件

```java
public EpubZipException(String message, File file, Throwable cause)
```
创建一个新的 EpubZipException 实例。

参数:
- `message`: 异常消息
- `file`: 文件
- `cause`: 原始异常

```java
public EpubZipException(String message, File file, String entryName, Throwable cause)
```
创建一个新的 EpubZipException 实例。

参数:
- `message`: 异常消息
- `file`: 文件
- `entryName`: 条目名称
- `cause`: 原始异常

```java
public EpubZipException(String message, String fileName, String filePath)
```
创建一个新的 EpubZipException 实例（为了向后兼容）。

参数:
- `message`: 异常消息
- `fileName`: 文件名
- `filePath`: 文件路径

```java
public EpubZipException(String message, String fileName, String filePath, Throwable cause)
```
创建一个新的 EpubZipException 实例（为了向后兼容）。

参数:
- `message`: 异常消息
- `fileName`: 文件名
- `filePath`: 文件路径
- `cause`: 原始异常

### 主要方法

#### getFile()
```java
public File getFile()
```
获取文件。

返回:
- `File`: 文件

#### getEntryName()
```java
public String getEntryName()
```
获取条目名称。

返回:
- `String`: 条目名称

#### getFileName()
```java
public String getFileName()
```
获取文件名（为了向后兼容）。

返回:
- `String`: 文件名

#### getFilePath()
```java
public String getFilePath()
```
获取文件路径（为了向后兼容）。

返回:
- `String`: 文件路径

#### getOperation()
```java
public String getOperation()
```
获取操作类型（为了向后兼容）。

返回:
- `String`: 操作类型

#### getErrorCode()
```java
public Object getErrorCode()
```
获取错误码（为了向后兼容）。

返回:
- `Object`: 错误码

## 使用示例

```java
try {
    // 配置解析选项
    ParseOptions options = ParseOptions.lenient()
        .withLogLevel(ParseOptions.LogLevel.INFO)
        .withContinueOnMetadataError(true)
        .withContinueOnResourceError(true);
    
    // 使用配置选项解析EPUB
    EpubParser parser = new EpubParser(new File("book.epub"));
    ParseResult result = parser.parseWithOptions(new File("book.epub"), options);
    
    if (result.isSuccess() || result.isPartialSuccess()) {
        EpubBook book = result.getEpubBook();
        // 处理成功解析的书籍
    } else {
        // 处理解析失败
        System.err.println("解析失败: " + result.getParseSummary());
    }
} catch (EpubParseException e) {
    // 处理解析异常
    System.err.println("解析错误: " + e.getMessage());
    System.err.println("错误码: " + e.getErrorCode());
    System.err.println("恢复建议: " + e.getRecoverySuggestion());
    e.printStackTrace();
} catch (EpubXmlParseException e) {
    // 处理XML解析异常
    System.err.println("XML解析错误: " + e.getMessage());
    System.err.println("文件: " + e.getFileName());
    System.err.println("行号: " + e.getLineNumber());
    System.err.println("列号: " + e.getColumnNumber());
} catch (EpubFormatException e) {
    // 处理格式异常
    System.err.println("格式错误: " + e.getMessage());
    System.err.println("文件: " + e.getFileName());
    System.err.println("恢复建议: " + e.getRecoverySuggestion());
} catch (EpubPathValidationException e) {
    // 处理路径验证异常
    System.err.println("路径验证失败: " + e.getMessage() + ", 路径: " + e.getFilePath());
} catch (EpubResourceException e) {
    // 处理资源异常
    System.err.println("资源错误: " + e.getMessage() + ", 路径: " + e.getFilePath());
} catch (EpubZipException e) {
    // 处理 ZIP 异常
    System.err.println("ZIP 错误: " + e.getMessage() + ", 文件: " + e.getFileName());
} catch (Exception e) {
    // 处理其他异常
    System.err.println("未预期的错误: " + e.getMessage());
    e.printStackTrace();
}
```