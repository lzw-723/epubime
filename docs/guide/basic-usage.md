# 基础用法

## 解析 EPUB 文件

使用 `EpubParser` 类来解析 EPUB 文件：

```java
File epubFile = new File("path/to/your/book.epub");
EpubParser parser = new EpubParser(epubFile);
EpubBook book = parser.parse();
```

## 获取元数据

通过 `Metadata` 对象获取书籍的元数据信息：

```java
Metadata metadata = book.getMetadata();
String title = metadata.getTitle();
String creator = metadata.getCreator();
String language = metadata.getLanguage();
String publisher = metadata.getPublisher();
String identifier = metadata.getIdentifier();
Date date = metadata.getDate();
String description = metadata.getDescription();
List<String> subjects = metadata.getSubjects();
```

## 处理章节

获取书籍的章节结构：

```java
List<EpubChapter> chapters = book.getChapters();
for (EpubChapter chapter : chapters) {
    System.out.println("章节标题: " + chapter.getTitle());
    System.out.println("内容文件路径: " + chapter.getContent());
    
    // 处理嵌套章节
    if (chapter.hasChildren()) {
        for (EpubChapter child : chapter.getChildren()) {
            System.out.println("子章节: " + child.getTitle());
        }
    }
}
```

## 访问资源文件

获取书籍中的资源文件（如图片、CSS 等）：

```java
List<EpubResource> resources = book.getResources();
for (EpubResource resource : resources) {
    String href = resource.getHref();
    String mediaType = resource.getMediaType();
    byte[] data = resource.getData();
    
    // 保存资源文件到磁盘
    resource.saveToFile("output/" + href);
}
```

## 获取封面

直接获取书籍封面：

```java
EpubResource cover = book.getCover();
if (cover != null) {
    byte[] coverData = cover.getData();
    // 保存封面图片
    cover.saveToFile("cover.jpg");
}
```

## 使用缓存

EPUBime 提供了智能缓存机制，避免重复解析相同文件：

```java
// 第一次解析会缓存结果
EpubBook book1 = parser.parse();

// 后续调用将使用缓存的结果
EpubBook book2 = parser.parse();

// 强制重新解析（跳过缓存）
EpubBook book3 = parser.parseWithoutCache();
```

## 错误处理

EPUBime 提供了完整的异常处理体系：

```java
try {
    EpubParser parser = new EpubParser(epubFile);
    EpubBook book = parser.parse();

    // 处理书籍内容
    System.out.println("成功解析: " + book.getMetadata().getTitle());

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

} catch (Exception e) {
    // 其他异常
    System.err.println("未知错误: " + e.getMessage());
    e.printStackTrace();
}
```

## 资源管理

正确管理资源以避免内存泄漏：

```java
// 使用 try-with-resources 语句（推荐）
try (ZipFileManager zipManager = new ZipFileManager(epubFile)) {
    List<String> files = zipManager.listAllFiles();
    System.out.println("EPUB 文件包含 " + files.size() + " 个文件");

    // 获取特定文件内容
    byte[] content = zipManager.getFileContent("OEBPS/content.opf");
    if (content != null) {
        String opfContent = new String(content, "UTF-8");
        System.out.println("OPF 文件大小: " + content.length + " 字节");
    }

} catch (EpubZipException e) {
    System.err.println("ZIP 操作失败: " + e.getMessage());
}

// 手动资源管理
ZipFileManager zipManager = null;
try {
    zipManager = new ZipFileManager(epubFile);
    // 执行操作
} finally {
    if (zipManager != null) {
        zipManager.close();
    }
}
```

## 流式处理大文件

对于大型 EPUB 文件，可以使用流式处理避免内存问题：

```java
// 处理单个章节内容
EpubParser.processHtmlChapterContent(epubFile, "chapter1.html", inputStream -> {
    // 处理输入流，例如解析 HTML 内容
    // inputStream 会在使用后自动关闭
});

// 批量处理多个章节
List<String> chapterFiles = Arrays.asList("chapter1.html", "chapter2.html");
EpubParser.processMultipleHtmlChapters(epubFile, chapterFiles, (fileName, inputStream) -> {
    // 处理每个文件的输入流
});
```