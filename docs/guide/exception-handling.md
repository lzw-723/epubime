# 异常处理

EPUBime 提供了完善的异常处理机制，所有异常都继承自 `EpubException` 基类。以下是各个异常类的详细说明和使用方式。

## 异常体系

```
EpubException
├── EpubParseException      (解析异常)
├── EpubFormatException     (格式异常)
├── EpubPathValidationException (路径验证异常)
├── EpubResourceException   (资源异常)
└── EpubZipException        (ZIP 异常)
```

## EpubParseException

解析异常，用于处理 EPUB 文件解析过程中的错误。

```java
try {
    EpubBook book = parser.parse();
} catch (EpubParseException e) {
    System.err.println("解析错误: " + e.getMessage());
    System.err.println("文件: " + e.getFileName());
    System.err.println("路径: " + e.getPath());
    
    // 获取原始异常（如果有的话）
    Throwable cause = e.getCause();
    if (cause != null) {
        System.err.println("原因: " + cause.getMessage());
    }
}
```

### 常见场景
- EPUB 文件损坏或格式不正确
- 内部文件格式错误（如 XML 格式错误）
- 解析过程中遇到无法处理的数据

## EpubFormatException

格式异常，用于处理 EPUB 格式不符合规范的情况。

```java
try {
    Metadata metadata = EpubParser.parseMetadata(opfContent);
} catch (EpubFormatException e) {
    System.err.println("格式错误: " + e.getMessage());
    System.err.println("文件: " + e.getFileName());
    System.err.println("路径: " + e.getPath());
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
    System.err.println("无效路径: " + e.getPath());
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
    System.err.println("资源路径: " + e.getPath());
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
        try {
            EpubParser parser = new EpubParser(epubFile);
            EpubBook book = parser.parse();
            
            // 处理书籍内容
            processBook(book);
            
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
        System.err.println("解析失败 - 文件: " + e.getFileName() + ", 错误: " + e.getMessage());
    }
    
    private void handleFormatException(EpubFormatException e) {
        System.err.println("格式错误 - 文件: " + e.getFileName() + ", 错误: " + e.getMessage());
    }
    
    private void handlePathValidationException(EpubPathValidationException e) {
        System.err.println("路径验证失败 - 路径: " + e.getPath() + ", 错误: " + e.getMessage());
    }
    
    private void handleResourceException(EpubResourceException e) {
        System.err.println("资源访问失败 - 路径: " + e.getPath() + ", 错误: " + e.getMessage());
    }
    
    private void handleZipException(EpubZipException e) {
        System.err.println("ZIP 操作失败 - 文件: " + e.getFileName() + ", 错误: " + e.getMessage());
    }
    
    private void processBook(EpubBook book) {
        // 实际处理书籍内容的逻辑
        System.out.println("处理书籍: " + book.getMetadata().getTitle());
    }
}
```

## 最佳实践

1. **具体异常处理**：优先捕获具体的异常类型，而不是通用的异常
2. **错误信息记录**：记录详细的错误信息，包括文件名、路径等
3. **资源清理**：确保在异常情况下正确清理资源（如关闭文件流）
4. **用户友好**：向用户提供有意义的错误信息，而不是技术细节
5. **日志记录**：使用日志框架记录异常，便于调试和监控

异常处理是确保应用稳定性和安全性的重要部分，EPUBime 的异常体系提供了详细的信息来帮助开发者快速定位和解决问题。