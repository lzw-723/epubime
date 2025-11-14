# EpubResource

`EpubResource` 类代表 EPUB 书籍中的一个资源文件，如图片、CSS、字体等。

## 构造函数

```java
public EpubResource()
```
创建一个新的 EpubResource 实例。

```java
public EpubResource(String href, String mediaType)
```
创建一个新的 EpubResource 实例。

参数:
- `href`: 资源的 href 属性
- `mediaType`: 资源的媒体类型

```java
public EpubResource(String id, String href, String mediaType, byte[] data)
```
创建一个新的 EpubResource 实例。

参数:
- `id`: 资源 ID
- `href`: 资源的 href 属性
- `mediaType`: 资源的媒体类型
- `data`: 资源数据

## 主要方法

### getId()
```java
public String getId()
```
获取资源 ID。

返回:
- `String`: 资源 ID

### setId()
```java
public void setId(String id)
```
设置资源 ID。

参数:
- `id`: 资源 ID

### getHref()
```java
public String getHref()
```
获取资源的 href 属性。

返回:
- `String`: 资源的 href 属性

### setHref()
```java
public void setHref(String href)
```
设置资源的 href 属性。

参数:
- `href`: 资源的 href 属性

### getMediaType()
```java
public String getMediaType()
```
获取资源的媒体类型。

返回:
- `String`: 资源的媒体类型（如 "image/jpeg"、"text/css"）

### setMediaType()
```java
public void setMediaType(String mediaType)
```
设置资源的媒体类型。

参数:
- `mediaType`: 资源的媒体类型

### getData()
```java
public byte[] getData()
```
获取资源数据。

返回:
- `byte[]`: 资源数据

### setData()
```java
public void setData(byte[] data)
```
设置资源数据。

参数:
- `data`: 资源数据

### getFileName()
```java
public String getFileName()
```
获取资源文件名（从 href 中提取）。

返回:
- `String`: 资源文件名

### getExtension()
```java
public String getExtension()
```
获取资源文件扩展名。

返回:
- `String`: 资源文件扩展名（如 "jpg"、"css"）

### getSize()
```java
public long getSize()
```
获取资源大小（字节数）。

返回:
- `long`: 资源大小

### isImage()
```java
public boolean isImage()
```
检查资源是否为图像。

返回:
- `boolean`: 如果资源是图像则返回 true，否则返回 false

### isStylesheet()
```java
public boolean isStylesheet()
```
检查资源是否为样式表。

返回:
- `boolean`: 如果资源是样式表则返回 true，否则返回 false

### isScript()
```java
public boolean isScript()
```
检查资源是否为脚本文件。

返回:
- `boolean`: 如果资源是脚本文件则返回 true，否则返回 false

### isFont()
```java
public boolean isFont()
```
检查资源是否为字体文件。

返回:
- `boolean`: 如果资源是字体文件则返回 true，否则返回 false

### isAudio()
```java
public boolean isAudio()
```
检查资源是否为音频文件。

返回:
- `boolean`: 如果资源是音频文件则返回 true，否则返回 false

### isVideo()
```java
public boolean isVideo()
```
检查资源是否为视频文件。

返回:
- `boolean`: 如果资源是视频文件则返回 true，否则返回 false

### saveToFile()
```java
public void saveToFile(String filePath) throws EpubResourceException
```
将资源保存到文件。

参数:
- `filePath`: 目标文件路径

抛出:
- `EpubResourceException`: 保存过程中发生错误

### saveToDirectory()
```java
public void saveToDirectory(String directoryPath) throws EpubResourceException
```
将资源保存到指定目录。

参数:
- `directoryPath`: 目标目录路径

抛出:
- `EpubResourceException`: 保存过程中发生错误

### toInputStream()
```java
public InputStream toInputStream()
```
将资源数据转换为输入流。

返回:
- `InputStream`: 资源数据的输入流

### toBufferedImage()
```java
public BufferedImage toBufferedImage() throws EpubResourceException
```
将图像资源转换为 BufferedImage 对象。

返回:
- `BufferedImage`: 图像的 BufferedImage 对象

抛出:
- `EpubResourceException`: 转换过程中发生错误（如果资源不是有效图像）

### toCSSDocument()
```java
public String toCSSDocument() throws EpubResourceException
```
将 CSS 资源转换为字符串。

返回:
- `String`: CSS 内容字符串

抛出:
- `EpubResourceException`: 如果资源不是 CSS 文件

### getMimeType()
```java
public String getMimeType()
```
获取资源的 MIME 类型。

返回:
- `String`: MIME 类型

### isBinary()
```java
public boolean isBinary()
```
检查资源是否为二进制文件。

返回:
- `boolean`: 如果资源是二进制文件则返回 true，否则返回 false

### getTextContent()
```java
public String getTextContent() throws EpubResourceException
```
获取文本资源的内容。

返回:
- `String`: 文本内容

抛出:
- `EpubResourceException`: 如果资源不是文本文件或读取失败

### updateContent()
```java
public void updateContent(String newContent) throws EpubResourceException
```
更新文本资源的内容。

参数:
- `newContent`: 新的文本内容

抛出:
- `EpubResourceException`: 如果资源不是文本文件或更新失败

### getAbsolutePath()
```java
public String getAbsolutePath()
```
获取资源的绝对路径。

返回:
- `String`: 资源的绝对路径

### getRelativePath()
```java
public String getRelativePath(String basePath)
```
获取资源相对于指定基础路径的路径。

参数:
- `basePath`: 基础路径

返回:
- `String`: 相对路径

### isValid()
```java
public boolean isValid()
```
检查资源是否有效（有有效的 href 和媒体类型）。

返回:
- `boolean`: 如果资源有效则返回 true，否则返回 false

### toString()
```java
public String toString()
```
返回资源的字符串表示。

返回:
- `String`: 资源的字符串表示