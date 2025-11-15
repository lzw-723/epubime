---
outline: deep
---

# API 使用示例

本节提供了 EPUBime 库的各种使用示例，涵盖从基础用法到高级功能的完整示例。

## 基础示例

### 1. 使用 Fluent API 解析 EPUB 文件

```java
import fun.lzwi.epubime.api.*;
import fun.lzwi.epubime.epub.*;

File epubFile = new File("sample.epub");

// 使用 Fluent API 进行链式调用
EpubBook book = EpubReader.fromFile(epubFile)
    .withCache(true)              // 启用缓存
    .withLazyLoading(true)        // 启用延迟加载
    .withParallelProcessing(true) // 启用并行处理
    .parse();

// 获取元数据
Metadata metadata = book.getMetadata();
System.out.println("标题: " + metadata.getTitle());
System.out.println("作者: " + metadata.getCreator());
System.out.println("语言: " + metadata.getLanguage());

// 获取章节列表
List<EpubChapter> chapters = book.getChapters();
System.out.println("章节数: " + chapters.size());

for (EpubChapter chapter : chapters) {
    System.out.println("章节: " + chapter.getTitle());
}
```

### 2. 快速获取书籍信息

```java
// 无需完整解析，快速获取基本信息
EpubReader.EpubInfo info = EpubReader.fromFile(epubFile).getInfo();

System.out.println("标题: " + info.getTitle());
System.out.println("作者: " + info.getAuthor());
System.out.println("语言: " + info.getLanguage());
System.out.println("章节数: " + info.getChapterCount());
System.out.println("文件大小: " + info.getFileSize() + " 字节");

// 验证 EPUB 文件有效性
boolean isValid = EpubReader.fromFile(epubFile).isValid();
System.out.println("文件有效: " + isValid);
```

### 3. 只解析特定内容

```java
// 只解析元数据
Metadata metadata = EpubReader.fromFile(epubFile).parseMetadata();
System.out.println("仅元数据 - 标题: " + metadata.getTitle());

// 只解析目录
List<EpubChapter> chapters = EpubReader.fromFile(epubFile).parseTableOfContents();
System.out.println("仅目录 - 章节数: " + chapters.size());
```

## 流式处理示例

### 4. 流式处理章节内容

```java
// 流式处理所有章节（适合大文件）
EpubReader.fromFile(epubFile)
    .streamChapters((chapter, inputStream) -> {
        try {
            System.out.println("处理章节: " + chapter.getTitle());
            
            // 读取内容流（示例：计算大小）
            byte[] buffer = new byte[8192];
            int bytesRead;
            int totalBytes = 0;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                totalBytes += bytesRead;
            }
            
            System.out.println("章节大小: " + totalBytes + " 字节");
            
        } catch (IOException e) {
            System.err.println("处理章节失败: " + e.getMessage());
        }
    });
```

### 5. 流式处理特定章节

```java
// 流式处理特定章节
EpubReader.fromFile(epubFile)
    .streamChapter("chapter1", inputStream -> {
        try {
            // 读取完整内容
            String content = readStreamContent(inputStream);
            System.out.println("第一章内容长度: " + content.length());
            
            // 或者使用 JSoup 解析 HTML
            Document doc = Jsoup.parse(content);
            String text = doc.text();
            System.out.println("第一章文本长度: " + text.length());
            
        } catch (IOException e) {
            System.err.println("读取章节失败: " + e.getMessage());
        }
    });

// 辅助方法
private static String readStreamContent(InputStream inputStream) throws IOException {
    StringBuilder content = new StringBuilder();
    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = inputStream.read(buffer)) != -1) {
        content.append(new String(buffer, 0, bytesRead, "UTF-8"));
    }
    return content.toString();
}
```

## 异步处理示例

### 6. 基本异步解析

```java
import fun.lzwi.epubime.api.AsyncEpubProcessor;
import java.util.concurrent.CompletableFuture;

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
    
    System.out.println("异步解析完成:");
    System.out.println("标题: " + metadata.getTitle());
    System.out.println("章节数: " + book.getChapters().size());
    System.out.println("文件大小: " + info.getFileSize());
    
} finally {
    asyncProcessor.shutdown();
}
```

### 7. 异步章节处理

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    // 异步处理所有章节
    CompletableFuture<Void> processingFuture = asyncProcessor.processChaptersAsync(
        epubFile,
        (chapter, inputStream) -> {
            try {
                System.out.println("异步处理章节: " + chapter.getTitle());
                
                // 处理章节内容
                byte[] buffer = new byte[1024];
                int bytesRead;
                int totalBytes = 0;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    totalBytes += bytesRead;
                }
                
                System.out.println("章节 " + chapter.getTitle() + " 大小: " + totalBytes + " 字节");
                
            } catch (IOException e) {
                System.err.println("处理章节失败: " + e.getMessage());
            }
        }
    );
    
    // 等待处理完成
    processingFuture.join();
    System.out.println("异步章节处理完成");
    
} finally {
    asyncProcessor.shutdown();
}
```

### 8. 异步验证和增强对象

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    // 异步验证
    asyncProcessor.validateAsync(epubFile)
        .thenAccept(isValid -> {
            System.out.println("EPUB 验证结果: " + (isValid ? "有效" : "无效"));
        })
        .join();
    
    // 异步加载增强书籍对象
    CompletableFuture<EpubBookEnhanced> enhancedBookFuture = 
        asyncProcessor.loadEnhancedBookAsync(epubFile);
    
    EpubBookEnhanced enhancedBook = enhancedBookFuture.get();
    
    System.out.println("增强书籍信息:");
    System.out.println("标题: " + enhancedBook.getTitle());
    System.out.println("总章节数: " + enhancedBook.getChapterCount());
    System.out.println("图片资源数: " + enhancedBook.getImageCount());
    
    // 异步加载增强元数据
    CompletableFuture<MetadataEnhanced> enhancedMetadataFuture = 
        asyncProcessor.loadEnhancedMetadataAsync(epubFile);
    
    MetadataEnhanced enhancedMetadata = enhancedMetadataFuture.get();
    
    System.out.println("增强元数据信息:");
    System.out.println("摘要: " + enhancedMetadata.getSummary());
    
    if (enhancedMetadata.hasAccessibilityFeatures()) {
        System.out.println("可访问性特性: " + enhancedMetadata.getAccessibilityFeatures());
    }
    
} finally {
    asyncProcessor.shutdown();
}
```

## 增强功能示例

### 9. 使用增强书籍对象

```java
// 创建增强书籍对象
EpubBook book = EpubReader.fromFile(epubFile).parse();
EpubBookEnhanced enhancedBook = new EpubBookEnhanced(book, epubFile);

// 获取增强信息
System.out.println("书籍信息:");
System.out.println(enhancedBook.getBookInfo());

// 查找章节
EpubChapter chapter = enhancedBook.findChapterByTitle("前言");
if (chapter != null) {
    System.out.println("找到章节: " + chapter.getTitle());
}

// 获取所有章节（包括嵌套）
List<EpubChapter> allChapters = enhancedBook.getAllChapters();
System.out.println("总章节数: " + allChapters.size());

// 获取特定类型资源
List<EpubResource> images = enhancedBook.getImageResources();
List<EpubResource> cssFiles = enhancedBook.getCssResources();
List<EpubResource> fonts = enhancedBook.getFontResources();

System.out.println("图片资源: " + images.size());
System.out.println("CSS 文件: " + cssFiles.size());
System.out.println("字体文件: " + fonts.size());

// 检查封面
if (enhancedBook.hasCover()) {
    System.out.println("书籍有封面");
    EpubResource cover = enhancedBook.getCoverImage();
    if (cover != null && cover.getData() != null) {
        System.out.println("封面大小: " + cover.getData().length + " 字节");
    }
}
```

### 10. 使用增强元数据

```java
// 创建增强元数据对象
Metadata metadata = EpubReader.fromFile(epubFile).parseMetadata();
MetadataEnhanced enhancedMetadata = new MetadataEnhanced(metadata);

// 获取基础信息
System.out.println("标题: " + enhancedMetadata.getTitle());
System.out.println("作者: " + enhancedMetadata.getAuthor());
System.out.println("语言: " + enhancedMetadata.getLanguage());

// 获取解析后的日期
LocalDate date = enhancedMetadata.getParsedDate();
if (date != null) {
    System.out.println("出版日期: " + date);
}

// 获取标识符
String isbn = enhancedMetadata.getISBN();
if (isbn != null) {
    System.out.println("ISBN: " + isbn);
}

String uuid = enhancedMetadata.getUUID();
if (uuid != null) {
    System.out.println("UUID: " + uuid);
}

// 获取主题和关键词
List<String> subjects = enhancedMetadata.getSubjects();
if (!subjects.isEmpty()) {
    System.out.println("主题: " + subjects);
}

List<String> keywords = enhancedMetadata.getKeywords();
if (!keywords.isEmpty()) {
    System.out.println("关键词: " + keywords);
}

// 检查可访问性
if (enhancedMetadata.hasAccessibilityFeatures()) {
    System.out.println("可访问性特性: " + enhancedMetadata.getAccessibilityFeatures());
    System.out.println("可访问性摘要: " + enhancedMetadata.getAccessibilitySummary());
}

// 获取摘要信息
String summary = enhancedMetadata.getSummary();
System.out.println("元数据摘要: " + summary);

// 检查各种属性
System.out.println("有封面: " + enhancedMetadata.hasCover());
System.out.println("有描述: " + enhancedMetadata.hasDescription());
System.out.println("有主题: " + enhancedMetadata.hasSubjects());
System.out.println("有版权信息: " + enhancedMetadata.hasRights());
System.out.println("有出版商: " + enhancedMetadata.hasPublisher());
```

## 批量处理示例

### 11. 批量处理多个 EPUB 文件

```java
AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

try {
    // 多个 EPUB 文件
    List<File> epubFiles = Arrays.asList(
        new File("book1.epub"),
        new File("book2.epub"),
        new File("book3.epub")
    );
    
    // 批量处理多个书籍
    asyncProcessor.processMultipleBooksAsync(epubFiles, book -> {
        System.out.println("处理书籍: " + book.getMetadata().getTitle());
        System.out.println("  章节数: " + book.getChapters().size());
        System.out.println("  资源数: " + book.getResources().size());
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

## 资源处理示例

### 12. 处理资源文件

```java
// 获取特定资源
EpubResource resource = EpubReader.fromFile(epubFile)
    .getResource("image1");

if (resource != null) {
    System.out.println("资源 ID: " + resource.getId());
    System.out.println("资源类型: " + resource.getType());
    System.out.println("资源路径: " + resource.getHref());
    
    byte[] data = resource.getData();
    if (data != null) {
        System.out.println("资源大小: " + data.length + " 字节");
        
        // 保存到文件
        try (FileOutputStream fos = new FileOutputStream("extracted_" + resource.getId())) {
            fos.write(data);
            System.out.println("资源已保存");
        }
    }
}

// 并行处理所有资源
EpubReader.fromFile(epubFile)
    .withParallelProcessing(true)
    .processResources(resource -> {
        System.out.println("处理资源: " + resource.getId() + 
                         " (" + resource.getType() + ")");
        return null;
    });
```

## 错误处理示例

### 13. 完整的错误处理

```java
try {
    // 使用 Fluent API 并处理各种异常
    EpubBook book = EpubReader.fromFile(epubFile)
        .withCache(true)
        .parse();
        
} catch (EpubParseException e) {
    // 解析异常
    System.err.println("解析错误: " + e.getMessage());
    System.err.println("文件: " + e.getFileName());
    System.err.println("路径: " + e.getPath());
    
} catch (EpubFormatException e) {
    // 格式异常
    System.err.println("格式错误: " + e.getMessage());
    
} catch (EpubPathValidationException e) {
    // 路径验证异常
    System.err.println("路径验证错误: " + e.getMessage());
    
} catch (EpubResourceException e) {
    // 资源异常
    System.err.println("资源错误: " + e.getMessage());
    
} catch (EpubZipException e) {
    // ZIP 异常
    System.err.println("ZIP 错误: " + e.getMessage());
    
} catch (Exception e) {
    // 其他异常
    System.err.println("未知错误: " + e.getMessage());
    e.printStackTrace();
}
```

这些示例涵盖了 EPUBime 库的主要功能和用法。根据您的具体需求，可以组合使用这些功能来构建强大的 EPUB 处理应用。