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