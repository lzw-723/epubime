# EpubBook

`EpubBook` 类代表一个解析后的 EPUB 书籍，遵循单一职责原则，专门作为数据模型存储书籍信息。业务逻辑操作请使用相应的处理器类。

## 构造函数

```java
public EpubBook()
```
创建空的 EpubBook 实例。

```java
public EpubBook(EpubBook other)
```
复制构造函数，用于缓存。

参数:
- `other`: 要复制的 EpubBook 实例

## 主要方法

### 元数据操作

#### getMetadata()
```java
public Metadata getMetadata()
```
获取书籍元数据副本。

返回:
- `Metadata`: 书籍元数据对象副本

#### setMetadata()
```java
public void setMetadata(Metadata metadata)
```
设置书籍元数据。

参数:
- `metadata`: 元数据对象

### 章节操作

#### getNcx()
```java
public List<EpubChapter> getNcx()
```
获取 NCX 格式的目录章节列表。

返回:
- `List<EpubChapter>`: NCX 目录列表（不可修改）

#### setNcx()
```java
public void setNcx(List<EpubChapter> ncx)
```
设置 NCX 格式的目录章节列表。

参数:
- `ncx`: NCX 目录列表

#### getNav()
```java
public List<EpubChapter> getNav()
```
获取 NAV 格式的目录章节列表。

返回:
- `List<EpubChapter>`: NAV 目录列表（不可修改）

#### setNav()
```java
public void setNav(List<EpubChapter> nav)
```
设置 NAV 格式的目录章节列表。

参数:
- `nav`: NAV 目录列表

#### getLandmarks()
```java
public List<EpubChapter> getLandmarks()
```
获取地标导航章节列表。

返回:
- `List<EpubChapter>`: 地标导航列表（不可修改）

#### setLandmarks()
```java
public void setLandmarks(List<EpubChapter> landmarks)
```
设置地标导航章节列表。

参数:
- `landmarks`: 地标导航列表

#### getPageList()
```java
public List<EpubChapter> getPageList()
```
获取页面列表导航章节列表。

返回:
- `List<EpubChapter>`: 页面列表导航列表（不可修改）

#### setPageList()
```java
public void setPageList(List<EpubChapter> pageList)
```
设置页面列表导航章节列表。

参数:
- `pageList`: 页面列表导航列表

#### getChapters()
```java
public List<EpubChapter> getChapters()
```
获取主要章节列表，优先使用 NAV 目录，如果 NAV 为空则使用 NCX 目录。

返回:
- `List<EpubChapter>`: 章节列表（不可修改）

### 资源操作

#### getResources()
```java
public List<EpubResource> getResources()
```
获取资源文件列表。

返回:
- `List<EpubResource>`: 资源列表（不可修改）

#### setResources()
```java
public void setResources(List<EpubResource> resources)
```
设置资源文件列表。

参数:
- `resources`: 资源列表

## 相关处理器类

由于遵循单一职责原则，EpubBook 只负责数据存储。业务逻辑操作请使用以下处理器类：

### EpubBookProcessor
处理书籍相关的业务逻辑：
- `getCover(EpubBook book)` - 获取封面资源
- `getResource(EpubBook book, String resourceId)` - 获取特定资源
- `getResourceWithFallback(EpubBook book, String resourceId)` - 获取资源并应用回退机制
- `loadAllResourceData(EpubBook book)` - 批量加载资源数据

### EpubStreamProcessor
处理流式操作：
- `processBookChapters(EpubBook book, BiConsumer<EpubChapter, InputStream> processor)` - 流式处理书籍章节

## 使用示例

```java
// 创建 EpubBook 实例
EpubBook book = new EpubBook();

// 设置元数据
Metadata metadata = new Metadata();
metadata.addTitle("示例书籍");
book.setMetadata(metadata);

// 设置章节
List<EpubChapter> chapters = new ArrayList<>();
// ... 添加章节
book.setNcx(chapters);

// 设置资源
List<EpubResource> resources = new ArrayList<>();
// ... 添加资源
book.setResources(resources);

// 使用处理器类进行业务操作
EpubResource cover = EpubBookProcessor.getCover(book);
EpubResource resource = EpubBookProcessor.getResource(book, "image1");
```