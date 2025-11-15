# API 参考

## EpubStreamProcessor

`EpubStreamProcessor` 是专门处理 EPUB 流式操作的类，遵循单一职责原则，避免将整个文件加载到内存中。

### 构造函数

```java
public EpubStreamProcessor(File epubFile)
```
创建流处理器实例。

参数:
- `epubFile`: EPUB 文件对象

抛出:
- `IllegalArgumentException`: 如果文件参数为 null

### 章节流处理

#### processHtmlChapter()
```java
public void processHtmlChapter(String htmlFileName, Consumer<InputStream> processor)
    throws BaseEpubException, EpubPathValidationException
```
流式处理单个 HTML 章节内容。

参数:
- `htmlFileName`: HTML 文件名
- `processor`: 内容处理器，接收 InputStream

抛出:
- `BaseEpubException`: 处理异常
- `EpubPathValidationException`: 路径验证异常

#### processMultipleHtmlChapters()
```java
public void processMultipleHtmlChapters(List<String> htmlFileNames,
    BiConsumer<String, InputStream> processor)
    throws BaseEpubException, EpubPathValidationException
```
流式处理多个 HTML 章节内容。

参数:
- `htmlFileNames`: HTML 文件名列表
- `processor`: 内容处理器，接收文件名和 InputStream

抛出:
- `BaseEpubException`: 处理异常
- `EpubPathValidationException`: 路径验证异常

#### processBookChapters()
```java
public void processBookChapters(EpubBook book, BiConsumer<EpubChapter, InputStream> processor)
    throws BaseEpubException
```
流式处理书籍的所有章节内容。

参数:
- `book`: EpubBook 实例
- `processor`: 内容处理器，接收 EpubChapter 和 InputStream

抛出:
- `BaseEpubException`: 处理异常

## 使用示例

```java
File epubFile = new File("book.epub");
EpubStreamProcessor processor = new EpubStreamProcessor(epubFile);

// 处理单个章节
processor.processHtmlChapter("chapter1.xhtml", inputStream -> {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        String line;
        while ((line = reader.readLine()) != null) {
            // 处理每一行内容
            System.out.println(line);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
});

// 处理多个章节
List<String> chapterFiles = Arrays.asList("chapter1.xhtml", "chapter2.xhtml");
processor.processMultipleHtmlChapters(chapterFiles, (fileName, inputStream) -> {
    System.out.println("处理文件: " + fileName);
    // 处理文件内容
});

// 处理整本书的章节
EpubBook book = EpubReader.fromFile(epubFile).parse();
processor.processBookChapters(book, (chapter, inputStream) -> {
    System.out.println("处理章节: " + chapter.getTitle());
    // 处理章节内容
});
```

## 注意事项

- 流式处理可以有效减少内存使用，适合处理大型 EPUB 文件
- InputStream 在处理器函数执行完毕后会自动关闭
- 路径验证会防止目录遍历攻击
- 异常处理应该包含 BaseEpubException 和 EpubPathValidationException