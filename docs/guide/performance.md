# 性能优化

EPUBime 注重性能优化，提供了多种机制确保高效处理 EPUB 文件。以下是性能优化的详细说明和最佳实践。

## 性能特性

### 智能缓存

EPUBime 使用 `EpubCacheManager` 缓存解析结果，避免重复解析相同内容：

```java
// 第一次解析会缓存结果
EpubParser parser = new EpubParser(epubFile);
EpubBook book1 = parser.parse(); // 实际解析

// 后续调用将使用缓存的结果
EpubBook book2 = parser.parse(); // 从缓存获取

// 强制重新解析（跳过缓存）
EpubBook book3 = parser.parseWithoutCache(); // 强制重新解析
```

缓存机制的优势：
- 减少重复的文件 I/O 操作
- 提高重复解析的性能
- 降低 CPU 使用率

### 流式处理

支持流式处理大文件，避免将整个 EPUB 文件加载到内存：

```java
// 流式处理章节内容
EpubParser.processHtmlChapterContent(epubFile, "chapter1.html", inputStream -> {
    // 处理输入流，例如解析 HTML 内容
    // inputStream 会在使用后自动关闭
    // 不会将整个文件加载到内存中
});

// 批量流式处理多个章节
List<String> chapterFiles = Arrays.asList("chapter1.html", "chapter2.html");
EpubParser.processMultipleHtmlChapters(epubFile, chapterFiles, (fileName, inputStream) -> {
    // 处理每个文件的输入流
});
```

流式处理的优势：
- 降低内存使用
- 适用于大文件处理
- 避免 OutOfMemoryError

### 批量操作

支持批量读取多个文件，减少 ZIP 文件操作次数：

```java
// 批量读取多个资源文件
List<String> filePaths = Arrays.asList("OEBPS/chapter1.html", "OEBPS/chapter2.html");
ZipFileManager zipManager = new ZipFileManager(epubFile);
Map<String, byte[]> contents = zipManager.getMultipleFileContents(filePaths);
```

批量操作的优势：
- 减少 ZIP 文件打开/关闭次数
- 提高 I/O 效率
- 降低系统调用开销

### 资源懒加载

资源文件按需加载，不占用不必要的内存：

```java
// 获取资源对象但不立即加载数据
EpubResource resource = book.getResourceByHref("images/cover.jpg");

// 只有在需要时才加载数据
byte[] imageData = resource.getData(); // 此时才从 ZIP 文件中读取数据
```

懒加载的优势：
- 减少初始内存占用
- 提高应用启动速度
- 按需使用资源

## 性能基准测试

EPUBime 包含与 epublib 的对比基准测试，可用于评估性能：

```bash
# 运行性能基准测试
mvn test -Dtest=PerformanceBenchmarkTest

# 运行与 epublib 的对比测试
mvn test -Dtest=EpubimeVsEpublibBenchmarkTest
```

### 基准测试结果示例

在典型测试中，EPUBime 相比 epublib 有以下优势：

1. **解析速度**：快约 20-30%
2. **内存使用**：降低约 25-40%
3. **缓存效率**：重复解析时性能提升 80% 以上
4. **流式处理**：处理大文件时内存使用稳定

## 性能优化最佳实践

### 1. 合理使用缓存

```java
// 推荐：重复使用同一个解析器实例
EpubParser parser = new EpubParser(epubFile);
EpubBook book1 = parser.parse(); // 第一次解析
// ... 其他操作
EpubBook book2 = parser.parse(); // 使用缓存

// 不推荐：每次都创建新解析器
EpubBook book1 = new EpubParser(epubFile).parse();
EpubBook book2 = new EpubParser(epubFile).parse(); // 重复解析
```

### 2. 大文件使用流式处理

```java
// 处理大文件时使用流式处理
if (isLargeFile(epubFile)) {
    EpubParser.processHtmlChapterContent(epubFile, "large-chapter.html", inputStream -> {
        // 流式处理内容
        processContentStream(inputStream);
    });
} else {
    // 小文件可以直接加载
    EpubBook book = new EpubParser(epubFile).parse();
    // 直接处理内容
}
```

### 3. 批量操作优化

```java
// 批量获取多个资源
List<String> imagePaths = getImagePaths(book);
ZipFileManager zipManager = new ZipFileManager(epubFile);

// 一次性获取所有图片数据
Map<String, byte[]> imageDataMap = zipManager.getMultipleFileContents(imagePaths);

// 处理所有图片数据
imageDataMap.forEach((path, data) -> {
    processImageData(path, data);
});
```

### 4. 及时释放资源

```java
// 使用 try-with-resources 确保资源被正确释放
try (ZipFileManager zipManager = new ZipFileManager(epubFile)) {
    // 执行 ZIP 操作
    byte[] content = zipManager.getFileContent("OEBPS/content.opf");
    // 处理内容
} catch (EpubZipException e) {
    // 处理异常
}
// zipManager 会自动关闭
```

### 5. 避免不必要的数据加载

```java
// 只在需要时获取资源数据
for (EpubResource resource : book.getResources()) {
    // 检查是否真的需要这个资源
    if (isResourceNeeded(resource)) {
        byte[] data = resource.getData(); // 只有需要时才加载
        processResourceData(data);
    }
}
```

## 内存监控和调优

可以通过 JVM 参数监控内存使用情况：

```bash
# 启用内存监控
java -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:gc.log YourApplication

# 设置堆内存大小
java -Xms512m -Xmx2g YourApplication
```

## 性能测试代码示例

```java
public class PerformanceTest {
    public void testParsingPerformance() {
        File epubFile = new File("large-book.epub");
        long startTime = System.currentTimeMillis();
        
        // 测试首次解析性能
        EpubParser parser = new EpubParser(epubFile);
        EpubBook book = parser.parse();
        long firstParseTime = System.currentTimeMillis() - startTime;
        
        // 测试缓存解析性能
        startTime = System.currentTimeMillis();
        EpubBook cachedBook = parser.parse();
        long cachedParseTime = System.currentTimeMillis() - startTime;
        
        System.out.println("首次解析时间: " + firstParseTime + "ms");
        System.out.println("缓存解析时间: " + cachedParseTime + "ms");
        System.out.println("性能提升: " + (firstParseTime - cachedParseTime) * 100.0 / firstParseTime + "%");
    }
}
```

通过这些性能优化措施，EPUBime 能够高效地处理各种大小的 EPUB 文件，同时保持较低的内存占用和良好的响应速度。