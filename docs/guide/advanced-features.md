# 高级功能

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