# MetadataEnhanced

`MetadataEnhanced` 是 EPUBime 库中的增强元数据对象，提供了更多便利方法和类型安全的访问方式。该类是对基础 `Metadata` 类的包装，提供了更丰富的功能和更简单的 API。

## 类定义

```java
public class MetadataEnhanced
```

## 构造方法

### MetadataEnhanced(Metadata metadata)
创建一个增强的元数据对象。

**参数:**
- `metadata`: 基础 Metadata 对象

## 方法

### getTitle()
获取主要标题。

**返回值:**
- `String`: 主要标题，如果没有则返回空字符串

### getTitles()
获取所有标题。

**返回值:**
- `List<String>`: 所有标题的列表

### getAuthor()
获取主要作者/创建者。

**返回值:**
- `String`: 主要作者，如果没有则返回空字符串

### getAuthors()
获取所有作者/创建者。

**返回值:**
- `List<String>`: 所有作者的列表

### getLanguage()
获取主要语言。

**返回值:**
- `String`: 主要语言，如果没有则返回空字符串

### getLanguages()
获取所有语言。

**返回值:**
- `List<String>`: 所有语言的列表

### getPublisher()
获取主要出版商。

**返回值:**
- `String`: 主要出版商，如果没有则返回空字符串

### getPublishers()
获取所有出版商。

**返回值:**
- `List<String>`: 所有出版商的列表

### getIdentifier()
获取主要标识符。

**返回值:**
- `String`: 主要标识符，如果没有则返回空字符串

### getIdentifiers()
获取所有标识符。

**返回值:**
- `List<String>`: 所有标识符的列表

### getDescription()
获取主要描述。

**返回值:**
- `String`: 主要描述，如果没有则返回空字符串

### getDescriptions()
获取所有描述。

**返回值:**
- `List<String>`: 所有描述的列表

### getDate()
获取主要日期。

**返回值:**
- `String`: 主要日期，如果没有则返回空字符串

### getDates()
获取所有日期。

**返回值:**
- `List<String>`: 所有日期的列表

### getParsedDate()
获取解析后的日期为 LocalDate 对象。

**返回值:**
- `LocalDate`: 解析后的日期，如果解析失败则返回 null

### getRights()
获取权利信息。

**返回值:**
- `String`: 权利信息，如果没有则返回空字符串

### getRightsList()
获取所有权利信息。

**返回值:**
- `List<String>`: 所有权利信息的列表

### getSource()
获取来源。

**返回值:**
- `String`: 来源，如果没有则返回空字符串

### getSources()
获取所有来源。

**返回值:**
- `List<String>`: 所有来源的列表

### getSubject()
获取主要主题/类型。

**返回值:**
- `String`: 主要主题，如果没有则返回空字符串

### getSubjects()
获取所有主题/类型。

**返回值:**
- `List<String>`: 所有主题的列表

### getType()
获取主要类型。

**返回值:**
- `String`: 主要类型，如果没有则返回空字符串

### getTypes()
获取所有类型。

**返回值:**
- `List<String>`: 所有类型的列表

### getFormat()
获取主要格式。

**返回值:**
- `String`: 主要格式，如果没有则返回空字符串

### getFormats()
获取所有格式。

**返回值:**
- `List<String>`: 所有格式的列表

### getCoverId()
获取封面 ID。

**返回值:**
- `String`: 封面 ID，如果没有则返回空字符串

### getModified()
获取修改日期。

**返回值:**
- `String`: 修改日期，如果没有则返回空字符串

### getUniqueIdentifier()
获取唯一标识符。

**返回值:**
- `String`: 唯一标识符，如果没有则返回空字符串

### getAccessibilityFeatures()
获取可访问性特性。

**返回值:**
- `List<String>`: 可访问性特性的列表

### getAccessibilityHazards()
获取可访问性危害。

**返回值:**
- `List<String>`: 可访问性危害的列表

### getAccessibilitySummary()
获取可访问性摘要。

**返回值:**
- `String`: 可访问性摘要，如果没有则返回空字符串

### getLayout()
获取布局属性。

**返回值:**
- `String`: 布局属性，如果没有则返回空字符串

### getOrientation()
获取方向属性。

**返回值:**
- `String`: 方向属性，如果没有则返回空字符串

### getSpread()
获取展开属性。

**返回值:**
- `String`: 展开属性，如果没有则返回空字符串

### getViewport()
获取视口属性。

**返回值:**
- `String`: 视口属性，如果没有则返回空字符串

### getMedia()
获取媒体属性。

**返回值:**
- `String`: 媒体属性，如果没有则返回空字符串

### getFlow()
获取流属性。

**返回值:**
- `String`: 流属性，如果没有则返回空字符串

### isAlignXCenter()
检查是否启用了 align-x-center。

**返回值:**
- `boolean`: 如果启用了 align-x-center 返回 true

### hasCover()
检查书籍是否有指定的封面。

**返回值:**
- `boolean`: 如果指定了封面返回 true

### hasAccessibilityFeatures()
检查书籍是否有可访问性特性。

**返回值:**
- `boolean`: 如果有可访问性特性返回 true

### hasAccessibilityHazards()
检查书籍是否有可访问性危害。

**返回值:**
- `boolean`: 如果指定了可访问性危害返回 true

### hasDescription()
检查书籍是否有描述。

**返回值:**
- `boolean`: 如果有描述返回 true

### hasSubjects()
检查书籍是否有主题/类型。

**返回值:**
- `boolean`: 如果有主题返回 true

### getSummary()
获取元数据的格式化摘要。

**返回值:**
- `String`: 格式化的元数据摘要

### getOriginalMetadata()
获取底层的 Metadata 实例。

**返回值:**
- `Metadata`: 原始 Metadata 的副本

## 使用示例

```java
// 解析 EPUB 并获取增强元数据
EpubBook book = EpubReader.fromFile(new File("book.epub")).parse();
MetadataEnhanced metadata = new MetadataEnhanced(book.getMetadata());

// 获取基本元数据信息
System.out.println("标题: " + metadata.getTitle());
System.out.println("作者: " + metadata.getAuthor());
System.out.println("语言: " + metadata.getLanguage());
System.out.println("出版商: " + metadata.getPublisher());
System.out.println("日期: " + metadata.getDate());

// 获取所有元数据项
List<String> allTitles = metadata.getTitles();
List<String> allAuthors = metadata.getAuthors();
List<String> allSubjects = metadata.getSubjects();

// 获取可访问性信息
if (metadata.hasAccessibilityFeatures()) {
    List<String> features = metadata.getAccessibilityFeatures();
    System.out.println("可访问性特性: " + String.join(", ", features));
}

// 获取解析后的日期
LocalDate parsedDate = metadata.getParsedDate();
if (parsedDate != null) {
    System.out.println("解析后的日期: " + parsedDate);
}

// 获取元数据摘要
System.out.println("元数据摘要:");
System.out.println(metadata.getSummary());
```

## 注意事项

- `MetadataEnhanced` 是对 `Metadata` 的包装，不会修改原始对象。
- 提供了更丰富的查询方法，简化了常见操作。
- 对于可能返回 null 的方法，提供了合理的默认值（如空字符串）。
- 提供了日期解析功能，支持多种常见的日期格式。
- 包含了 EPUB 3.3 规范中的可访问性元数据支持。