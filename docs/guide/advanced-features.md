# 高级功能

## Fluent API 使用

EPUBime 提供了现代化的 Fluent API，支持链式方法调用：

```java
// 基础 Fluent API 使用
EpubBook book = EpubReader.fromFile(epubFile)
    .withCache(true)
    .withLazyLoading(true)
    .withParallelProcessing(true)
    .parse();

// 只解析元数据
Metadata metadata = EpubReader.fromFile(epubFile)
    .withCache(false)
    .parseMetadata();

// 只解析目录
List<EpubChapter> chapters = EpubReader.fromFile(epubFile)
    .parseTableOfContents();

// 获取书籍信息
EpubReader.EpubInfo info = EpubReader.fromFile(epubFile).getInfo();
System.out.println("标题: " + info.getTitle());
System.out.println("章节数: " + info.getChapterCount());
```

## 异步处理

EPUBime 支持异步处理，提高应用响应性：

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    // 异步解析书籍
    CompletableFuture<EpubBook> bookFuture = asyncProcessor.parseBookAsync(epubFile);
    
    // 异步解析元数据
    CompletableFuture<Metadata> metadataFuture = asyncProcessor.parseMetadataAsync(epubFile);
    
    // 异步获取书籍信息
    CompletableFuture<EpubReader.EpubInfo> infoFuture = asyncProcessor.getBookInfoAsync(epubFile);
    
    // 等待所有异步操作完成
    CompletableFuture.allOf(bookFuture, metadataFuture, infoFuture).join();
    
    // 获取结果
    EpubBook book = bookFuture.get();
    Metadata metadata = metadataFuture.get();
    EpubReader.EpubInfo info = infoFuture.get();
    
} finally {
    asyncProcessor.shutdown();
}
```

## 流式处理

对于大型 EPUB 文件，可以使用流式处理来优化内存使用：

```java
// 流式处理所有章节
EpubReader.fromFile(epubFile)
    .streamChapters((chapter, inputStream) -> {
        try {
            System.out.println("处理章节: " + chapter.getTitle());
            
            // 读取内容（示例：计算内容长度）
            byte[] buffer = new byte[8192];
            int bytesRead;
            int totalBytes = 0;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                totalBytes += bytesRead;
                // 处理内容块
            }
            
            System.out.println("章节大小: " + totalBytes + " 字节");
            
        } catch (IOException e) {
            System.err.println("处理章节失败: " + e.getMessage());
        }
    });

// 流式处理特定章节
EpubReader.fromFile(epubFile)
    .streamChapter("chapter1", inputStream -> {
        try {
            // 处理特定章节的内容流
            String content = readStreamContent(inputStream);
            System.out.println("第一章内容长度: " + content.length());
        } catch (IOException e) {
            System.err.println("读取章节失败: " + e.getMessage());
        }
    });
```

## 增强的API功能

### 增强的书籍对象

```java
// 使用增强的书籍对象
EpubBook book = EpubReader.fromFile(epubFile).parse();
EpubBookEnhanced enhancedBook = new EpubBookEnhanced(book, epubFile);

// 获取所有章节（包括嵌套章节）
List<EpubChapter> allChapters = enhancedBook.getAllChapters();

// 按标题查找章节
EpubChapter chapter = enhancedBook.findChapterByTitle("前言");

// 获取特定类型的资源
List<EpubResource> images = enhancedBook.getImageResources();
List<EpubResource> cssFiles = enhancedBook.getCssResources();

// 检查是否有封面
if (enhancedBook.hasCover()) {
    System.out.println("书籍有封面图片");
}
```

### 增强的元数据

```java
// 使用增强的元数据对象
Metadata metadata = EpubReader.fromFile(epubFile).parseMetadata();
MetadataEnhanced enhancedMetadata = new MetadataEnhanced(metadata);

// 获取解析后的日期
LocalDate date = enhancedMetadata.getParsedDate();
if (date != null) {
    System.out.println("出版日期: " + date);
}

// 检查可访问性特性
if (enhancedMetadata.hasAccessibilityFeatures()) {
    System.out.println("可访问性特性: " + enhancedMetadata.getAccessibilityFeatures());
}

// 获取元数据摘要
String summary = enhancedMetadata.getSummary();
System.out.println("元数据摘要: " + summary);
```

## 多种导航类型支持

EPUBime 支持多种导航类型，包括 NCX 和 NAV 格式：

```java
// 解析 NCX 导航（EPUB 2）
String ncxPath = EpubParser.getNcxPath(opfContent, "OEBPS/");
String ncxContent = EpubParser.readEpubContent(epubFile, ncxPath);
List<EpubChapter> ncxChapters = EpubParser.parseNcx(ncxContent);

// 解析 NAV 导航（EPUB 3）
String navPath = EpubParser.getNavPath(opfContent, "OEBPS/");
if (navPath != null) {
    String navContent = EpubParser.readEpubContent(epubFile, navPath);
    List<EpubChapter> navChapters = EpubParser.parseNav(navContent);
    
    // 解析其他导航类型
    List<EpubChapter> landmarks = EpubParser.parseNavByType(navContent, "landmarks");
    List<EpubChapter> pageList = EpubParser.parseNavByType(navContent, "page-list");
}
```

## 直接解析特定文件

可以直接解析 EPUB 文件中的特定内容：

```java
// 直接读取内部文件内容
String container = EpubParser.readEpubContent(epubFile, "META-INF/container.xml");
String opfContent = EpubParser.readEpubContent(epubFile, "OEBPS/content.opf");

// 解析元数据
Metadata metadata = EpubParser.parseMetadata(opfContent);

// 解析资源文件
List<EpubResource> resources = EpubParser.parseResources(opfContent, "OEBPS/", epubFile);
```

## 安全路径验证

EPUBime 提供了路径验证机制，防止目录遍历攻击：

```java
// 使用内置的路径验证器
try {
    String validatedPath = PathValidator.validatePath("OEBPS/chapter1.html");
} catch (EpubPathValidationException e) {
    // 处理路径验证异常
    System.err.println("路径验证失败: " + e.getMessage());
}
```

## 自定义 ZIP 处理

使用 `ZipFileManager` 进行高级 ZIP 操作：

```java
ZipFileManager zipManager = new ZipFileManager(epubFile);
List<String> allFiles = zipManager.listAllFiles();

// 获取特定文件内容
byte[] fileContent = zipManager.getFileContent("OEBPS/content.opf");

// 安全地关闭资源
zipManager.close();

// 使用 try-with-resources 语句确保资源被正确释放
try (ZipFileManager zipManager = new ZipFileManager(epubFile)) {
    // 执行 ZIP 操作
    String content = new String(zipManager.getFileContent("OEBPS/toc.ncx"));
} catch (EpubZipException e) {
    // 处理 ZIP 相关异常
}
```

## 批量操作

EPUBime 支持批量操作以提高性能：

```java
// 批量读取多个资源文件
List<String> filePaths = Arrays.asList("OEBPS/chapter1.html", "OEBPS/chapter2.html");
Map<String, byte[]> contents = zipManager.getMultipleFileContents(filePaths);

// 批量处理多个章节内容
List<String> chapterFiles = Arrays.asList("chapter1.html", "chapter2.html");
EpubParser.processMultipleHtmlChapters(epubFile, chapterFiles, (fileName, inputStream) -> {
    // 处理每个文件的输入流
});
```

## 内容处理示例

处理 HTML 章节内容的完整示例：

```java
// 使用 JSoup 解析章节内容
EpubParser.processHtmlChapterContent(epubFile, "chapter1.html", inputStream -> {
    Document doc = Jsoup.parse(inputStream, "UTF-8", "");
    
    // 提取文本内容
    String text = doc.text();
    
    // 提取特定元素
    Elements headings = doc.select("h1, h2, h3, h4, h5, h6");
    for (Element heading : headings) {
        System.out.println("标题: " + heading.text());
    }
    
    // 提取图片引用
    Elements images = doc.select("img");
    for (Element img : images) {
        String src = img.attr("src");
        System.out.println("图片引用: " + src);
    }
});
```

## 自定义异常处理

EPUBime 提供了完整的异常处理体系：

```java
try {
    EpubParser parser = new EpubParser(epubFile);
    EpubBook book = parser.parse();
} catch (EpubParseException e) {
    // 解析异常：处理 EPUB 文件解析过程中的错误
    System.err.println("解析错误: " + e.getMessage());
    System.err.println("文件: " + e.getFileName());
    System.err.println("路径: " + e.getPath());
} catch (EpubFormatException e) {
    // 格式异常：处理 EPUB 格式不符合规范的情况
    System.err.println("格式错误: " + e.getMessage());
} catch (EpubPathValidationException e) {
    // 路径验证异常：处理路径验证错误
    System.err.println("路径验证错误: " + e.getMessage());
} catch (EpubResourceException e) {
    // 资源异常：处理资源文件访问错误
    System.err.println("资源错误: " + e.getMessage());
} catch (EpubZipException e) {
    // ZIP 异常：处理 ZIP 文件操作错误
    System.err.println("ZIP 错误: " + e.getMessage());
}
```

## 批量处理

EPUBime 支持批量处理多个 EPUB 文件：

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    // 多个 EPUB 文件
    List<File> epubFiles = Arrays.asList(
        new File("book1.epub"),
        new File("book2.epub"),
        new File("book3.epub")
    );
    
    // 并行处理多个书籍
    asyncProcessor.processMultipleBooksAsync(epubFiles, book -> {
        System.out.println("处理书籍: " + book.getMetadata().getTitle());
        System.out.println("章节数: " + book.getChapters().size());
        return book;
    }).thenAccept(books -> {
        System.out.println("完成处理 " + books.size() + " 本书籍");
    }).join();
    
    // 获取所有书籍的章节数
    List<CompletableFuture<Integer>> chapterCountFutures = epubFiles.stream()
        .map(asyncProcessor::getChapterCountAsync)
        .collect(Collectors.toList());
    
    CompletableFuture.allOf(chapterCountFutures.toArray(new CompletableFuture[0]))
        .thenRun(() -> {
            List<Integer> chapterCounts = chapterCountFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
            
            System.out.println("各书籍章节数: " + chapterCounts);
        })
        .join();
        
} finally {
    asyncProcessor.shutdown();
}
```