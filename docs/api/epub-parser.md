# API 参考

## EpubParser

`EpubParser` 是 EPUBime 库的主要解析器类，负责解析 EPUB 文件并生成 `EpubBook` 对象。

### 构造函数

```java
public EpubParser(File epubFile)
```
创建一个新的 EPUB 解析器实例。

参数:
- `epubFile`: 要解析的 EPUB 文件

### 主要方法

#### parse()
```java
public EpubBook parse() throws EpubException
```
解析 EPUB 文件并返回 EpubBook 对象。如果已缓存结果，将返回缓存的解析结果。

返回:
- `EpubBook`: 解析出的 EPUB 书籍对象

抛出:
- `EpubException`: 解析过程中发生错误

#### parseWithoutCache()
```java
public EpubBook parseWithoutCache() throws EpubException
```
解析 EPUB 文件并返回 EpubBook 对象，跳过缓存。

返回:
- `EpubBook`: 解析出的 EPUB 书籍对象

抛出:
- `EpubException`: 解析过程中发生错误

#### readEpubContent()
```java
public static String readEpubContent(File epubFile, String entryPath) throws EpubException
```
直接读取 EPUB 文件中的特定内容。

参数:
- `epubFile`: EPUB 文件
- `entryPath`: 要读取的内部文件路径

返回:
- `String`: 文件内容

抛出:
- `EpubException`: 读取过程中发生错误

#### processHtmlChapterContent()
```java
public static void processHtmlChapterContent(File epubFile, String entryPath, ContentProcessor processor) throws EpubException
```
流式处理 HTML 章节内容。

参数:
- `epubFile`: EPUB 文件
- `entryPath`: 章节文件路径
- `processor`: 内容处理器

抛出:
- `EpubException`: 处理过程中发生错误

#### processMultipleHtmlChapters()
```java
public static void processMultipleHtmlChapters(File epubFile, List<String> entryPaths, ChapterContentProcessor processor) throws EpubException
```
批量流式处理多个 HTML 章节。

参数:
- `epubFile`: EPUB 文件
- `entryPaths`: 章节文件路径列表
- `processor`: 章节内容处理器

抛出:
- `EpubException`: 处理过程中发生错误

#### parseMetadata()
```java
public static Metadata parseMetadata(String opfContent) throws EpubException
```
解析 OPF 文件内容并提取元数据。

参数:
- `opfContent`: OPF 文件内容

返回:
- `Metadata`: 解析出的元数据对象

抛出:
- `EpubException`: 解析过程中发生错误

#### parseNcx()
```java
public static List<EpubChapter> parseNcx(String ncxContent) throws EpubException
```
解析 NCX 文件内容并提取章节信息。

参数:
- `ncxContent`: NCX 文件内容

返回:
- `List<EpubChapter>`: 解析出的章节列表

抛出:
- `EpubException`: 解析过程中发生错误

#### parseNav()
```java
public static List<EpubChapter> parseNav(String navContent) throws EpubException
```
解析 NAV 文件内容并提取章节信息。

参数:
- `navContent`: NAV 文件内容

返回:
- `List<EpubChapter>`: 解析出的章节列表

抛出:
- `EpubException`: 解析过程中发生错误

#### parseNavByType()
```java
public static List<EpubChapter> parseNavByType(String navContent, String type) throws EpubException
```
根据类型解析 NAV 文件内容。

参数:
- `navContent`: NAV 文件内容
- `type`: 导航类型（如 "landmarks", "page-list"）

返回:
- `List<EpubChapter>`: 解析出的章节列表

抛出:
- `EpubException`: 解析过程中发生错误

#### getNcxPath()
```java
public static String getNcxPath(String opfContent, String basePath) throws EpubException
```
从 OPF 文件中获取 NCX 文件路径。

参数:
- `opfContent`: OPF 文件内容
- `basePath`: 基础路径

返回:
- `String`: NCX 文件路径

抛出:
- `EpubException`: 解析过程中发生错误

#### getNavPath()
```java
public static String getNavPath(String opfContent, String basePath) throws EpubException
```
从 OPF 文件中获取 NAV 文件路径。

参数:
- `opfContent`: OPF 文件内容
- `basePath`: 基础路径

返回:
- `String`: NAV 文件路径，如果不存在则返回 null

抛出:
- `EpubException`: 解析过程中发生错误

#### parseResources()
```java
public static List<EpubResource> parseResources(String opfContent, String basePath, File epubFile) throws EpubException
```
解析 OPF 文件内容并提取所有资源文件。

参数:
- `opfContent`: OPF 文件内容
- `basePath`: 基础路径
- `epubFile`: EPUB 文件

返回:
- `List<EpubResource>`: 解析出的资源列表

抛出:
- `EpubException`: 解析过程中发生错误