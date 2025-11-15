# API 参考

## EpubFileReader

`EpubFileReader` 是专门负责读取 EPUB 文件内容的类，遵循单一职责原则，提供安全的文件读取功能。

### 构造函数

```java
public EpubFileReader(File epubFile)
```
创建文件读取器实例。

参数:
- `epubFile`: EPUB 文件对象

抛出:
- `IllegalArgumentException`: 如果文件参数为 null

### 文件读取方法

#### readContent()
```java
public String readContent(String path) throws EpubZipException, EpubPathValidationException
```
读取 EPUB 文件中指定路径的内容。

参数:
- `path`: 文件路径

返回:
- `String`: 文件内容

抛出:
- `EpubZipException`: ZIP 文件读取异常
- `EpubPathValidationException`: 路径验证异常

#### processHtmlChapterContent()
```java
public void processHtmlChapterContent(String htmlFileName, Consumer<InputStream> processor)
    throws EpubZipException, EpubPathValidationException
```
流式处理 HTML 章节内容。

参数:
- `htmlFileName`: HTML 文件名
- `processor`: 内容处理器，接收 InputStream

抛出:
- `EpubZipException`: ZIP 文件处理异常
- `EpubPathValidationException`: 路径验证异常

#### processMultipleHtmlChapters()
```java
public void processMultipleHtmlChapters(List<String> htmlFileNames,
    BiConsumer<String, InputStream> processor)
    throws EpubZipException, EpubPathValidationException
```
流式处理多个 HTML 章节内容。

参数:
- `htmlFileNames`: HTML 文件名列表
- `processor`: 内容处理器，接收文件名和 InputStream

抛出:
- `EpubZipException`: ZIP 文件处理异常
- `EpubPathValidationException`: 路径验证异常

## 安全特性

- **路径验证**: 自动验证文件路径，防止目录遍历攻击
- **ZIP 安全**: 使用安全的 ZIP 文件处理机制
- **异常处理**: 提供详细的异常信息用于调试

## 使用示例

```java
File epubFile = new File("book.epub");
EpubFileReader reader = new EpubFileReader(epubFile);

// 读取文本内容
try {
    String mimetype = reader.readContent("mimetype");
    System.out.println("MIME 类型: " + mimetype);

    String container = reader.readContent("META-INF/container.xml");
    System.out.println("容器内容: " + container);
} catch (EpubZipException | EpubPathValidationException e) {
    System.err.println("读取文件失败: " + e.getMessage());
}

// 流式处理 HTML 内容
reader.processHtmlChapterContent("chapter1.xhtml", inputStream -> {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
        String line;
        while ((line = br.readLine()) != null) {
            // 处理每一行
            System.out.println(line);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
});

// 批量处理多个文件
List<String> files = Arrays.asList("chapter1.xhtml", "chapter2.xhtml");
reader.processMultipleHtmlChapters(files, (fileName, inputStream) -> {
    System.out.println("处理文件: " + fileName);
    // 处理文件内容
});
```

## 注意事项

- 所有路径都会经过安全验证
- InputStream 在处理器执行完毕后自动关闭
- 支持 ZIP 文件中的所有压缩格式
- 提供详细的错误信息用于问题诊断