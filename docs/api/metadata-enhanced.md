# API 参考

## MetadataEnhanced

`MetadataEnhanced` 是增强的元数据对象类，提供了更多便利方法和高级元数据操作功能。

### 构造函数

```java
public MetadataEnhanced(Metadata metadata)
```
创建增强的元数据对象。

参数:
- `metadata`: 基础 Metadata 对象

### 基础信息方法

#### getTitle()
```java
public String getTitle()
```
获取书籍标题。

返回:
- `String`: 书籍标题

#### getAuthor()
```java
public String getAuthor()
```
获取书籍作者。

返回:
- `String`: 书籍作者

#### getLanguage()
```java
public String getLanguage()
```
获取书籍语言。

返回:
- `String`: 语言代码

#### getPublisher()
```java
public String getPublisher()
```
获取出版商信息。

返回:
- `String`: 出版商名称

#### getDescription()
```java
public String getDescription()
```
获取书籍描述。

返回:
- `String`: 书籍描述

#### getRights()
```java
public String getRights()
```
获取版权信息。

返回:
- `String`: 版权信息

### 高级日期处理

#### getParsedDate()
```java
public LocalDate getParsedDate()
```
获取解析后的日期对象。

返回:
- `LocalDate`: 解析后的日期，如果解析失败则返回 null

#### getParsedPublicationDate()
```java
public LocalDate getParsedPublicationDate()
```
获取解析后的出版日期。

返回:
- `LocalDate`: 解析后的出版日期，如果解析失败则返回 null

#### getParsedModificationDate()
```java
public LocalDate getParsedModificationDate()
```
获取解析后的修改日期。

返回:
- `LocalDate`: 解析后的修改日期，如果解析失败则返回 null

### 标识符处理

#### getPrimaryIdentifier()
```java
public String getPrimaryIdentifier()
```
获取主要标识符。

返回:
- `String`: 主要标识符值

#### getISBN()
```java
public String getISBN()
```
获取 ISBN 标识符。

返回:
- `String`: ISBN 值，如果未找到则返回 null

#### getUUID()
```java
public String getUUID()
```
获取 UUID 标识符。

返回:
- `String`: UUID 值，如果未找到则返回 null

### 主题和分类

#### getSubjects()
```java
public List<String> getSubjects()
```
获取主题列表。

返回:
- `List<String>`: 主题列表

#### getSubjectList()
```java
public List<String> getSubjectList()
```
获取分类主题列表（别名方法）。

返回:
- `List<String>`: 主题列表

#### getKeywords()
```java
public List<String> getKeywords()
```
获取关键词列表。

返回:
- `List<String>`: 关键词列表

### 贡献者信息

#### getContributors()
```java
public List<String> getContributors()
```
获取贡献者列表。

返回:
- `List<String>`: 贡献者列表

#### getIllustrators()
```java
public List<String> getIllustrators()
```
获取插图作者列表。

返回:
- `List<String>`: 插图作者列表

#### getTranslators()
```java
public List<String> getTranslators()
```
获取翻译者列表。

返回:
- `List<String>`: 翻译者列表

#### getEditors()
```java
public List<String> getEditors()
```
获取编辑者列表。

返回:
- `List<String>`: 编辑者列表

### 可访问性特性

#### hasAccessibilityFeatures()
```java
public boolean hasAccessibilityFeatures()
```
检查是否有可访问性特性。

返回:
- `boolean`: true 如果有可访问性特性，false 否则

#### getAccessibilityFeatures()
```java
public List<String> getAccessibilityFeatures()
```
获取可访问性特性列表。

返回:
- `List<String>`: 可访问性特性列表

#### getAccessibilitySummary()
```java
public String getAccessibilitySummary()
```
获取可访问性摘要。

返回:
- `String`: 可访问性摘要

### 媒体覆盖和同步

#### hasMediaOverlay()
```java
public boolean hasMediaOverlay()
```
检查是否有媒体覆盖。

返回:
- `boolean`: true 如果有媒体覆盖，false 否则

#### getMediaOverlay()
```java
public String getMediaOverlay()
```
获取媒体覆盖信息。

返回:
- `String`: 媒体覆盖信息

### 版本和格式

#### getEpubVersion()
```java
public String getEpubVersion()
```
获取 EPUB 版本。

返回:
- `String`: EPUB 版本号

#### getFormat()
```java
public String getFormat()
```
获取格式信息。

返回:
- `String`: 格式信息

### 系列信息

#### getSeries()
```java
public String getSeries()
```
获取系列信息。

返回:
- `String`: 系列名称，如果未找到则返回 null

#### getSeriesIndex()
```java
public String getSeriesIndex()
```
获取系列中的索引。

返回:
- `String`: 系列索引，如果未找到则返回 null

### 检查和判断方法

#### hasCover()
```java
public boolean hasCover()
```
检查是否有封面。

返回:
- `boolean`: true 如果有封面，false 否则

#### hasDescription()
```java
public boolean hasDescription()
```
检查是否有描述。

返回:
- `boolean`: true 如果有描述，false 否则

#### hasSubjects()
```java
public boolean hasSubjects()
```
检查是否有主题。

返回:
- `boolean`: true 如果有主题，false 否则

#### hasRights()
```java
public boolean hasRights()
```
检查是否有版权信息。

返回:
- `boolean`: true 如果有版权信息，false 否则

#### hasPublisher()
```java
public boolean hasPublisher()
```
检查是否有出版商信息。

返回:
- `boolean`: true 如果有出版商，false 否则

### 摘要和统计

#### getSummary()
```java
public String getSummary()
```
获取元数据摘要信息。

返回:
- `String`: 格式化的元数据摘要

#### getDetailedSummary()
```java
public String getDetailedSummary()
```
获取详细的元数据摘要信息。

返回:
- `String`: 详细的元数据摘要

### 使用示例

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

// 获取主题
List<String> subjects = enhancedMetadata.getSubjects();
if (!subjects.isEmpty()) {
    System.out.println("主题: " + subjects);
}

// 检查可访问性
if (enhancedMetadata.hasAccessibilityFeatures()) {
    System.out.println("可访问性特性: " + enhancedMetadata.getAccessibilityFeatures());
}

// 获取摘要
String summary = enhancedMetadata.getSummary();
System.out.println("元数据摘要: " + summary);

// 检查各种属性
System.out.println("有封面: " + enhancedMetadata.hasCover());
System.out.println("有描述: " + enhancedMetadata.hasDescription());
System.out.println("有主题: " + enhancedMetadata.hasSubjects());
```