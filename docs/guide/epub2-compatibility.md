# EPUB2 兼容性

EPUBime 库完全支持 EPUB 2 格式的解析和处理，提供了强大的向后兼容性。EPUB2 是早期的 EPUB 标准，与 EPUB 3 在结构和元数据处理方面存在一些差异。

## 支持的 EPUB2 特性

### 1. 元数据解析

EPUBime 能够正确解析 EPUB2 格式的元数据，包括：

- **标题和作者**: 解析 `<title>`, `<creator>` 等基本元素
- **语言和标识符**: 解析 `<language>`, `<identifier>` 等元素
- **出版信息**: 解析 `<publisher>`, `<date>`, `<description>` 等元素
- **主题和权利**: 解析 `<subject>`, `<rights>`, `<contributor>` 等元素
- **封面信息**: 通过 `<meta name="cover" content="cover-id"/>` 标签解析封面

EPUB2 元数据解析支持多种格式：

```xml
<!-- 无命名空间格式 -->
<package version="2.0">
  <metadata>
    <title>EPUB2 Book Without Namespaces</title>
    <creator>Author Name</creator>
    <language>en-US</language>
    <identifier>urn:uuid:test-123</identifier>
  </metadata>
</package>

<!-- 混合命名空间格式 -->
<package version="2.0">
  <metadata>
    <dc:title xmlns:dc="http://purl.org/dc/elements/1.1/">EPUB2 Book</dc:title>
    <creator>Author Without Namespace</creator>
    <dc:language>zh-CN</dc:language>
  </metadata>
</package>
```

### 2. 导航解析 (NCX)

EPUB2 使用 NCX (Navigation Control file for XML applications) 文件进行导航，EPUBime 提供了完整的 NCX 解析支持：

- **基本导航点**: 解析 `navPoint` 元素及其 `playOrder` 属性
- **嵌套结构**: 支持嵌套的 `navPoint` 结构，表示章节和子章节
- **导航标签**: 解析 `navLabel` 和 `content` 元素
- **元数据**: 解析 NCX 文件中的元信息

NCX 文件示例：

```xml
<ncx version="2005-1">
  <navMap>
    <navPoint id="navpoint-1" playOrder="1">
      <navLabel>
        <text>Chapter 1</text>
      </navLabel>
      <content src="chapter1.html"/>
    </navPoint>
    <navPoint id="navpoint-2" playOrder="2">
      <navLabel>
        <text>Chapter 2</text>
      </navLabel>
      <content src="chapter2.html"/>
      <navPoint id="navpoint-2-1" playOrder="3">
        <navLabel>
          <text>Section 2.1</text>
        </navLabel>
        <content src="chapter2.html#section1"/>
      </navPoint>
    </navPoint>
  </navMap>
</ncx>
```

### 3. 资源解析

EPUB2 的资源管理通过 OPF (Open Packaging Format) 文件的 `manifest` 和 `spine` 元素实现：

- **清单解析**: 解析 `manifest` 中的所有资源项目
- **内容顺序**: 解析 `spine` 中的内容顺序
- **封面检测**: 通过 `meta` 标签检测封面资源
- **NCX 路径**: 提取 NCX 文件路径

OPF 文件示例：

```xml
<package version="2.0">
  <metadata>
    <title>Test Book</title>
    <identifier>test-id</identifier>
  </metadata>
  <manifest>
    <item id="chapter1" href="chapter1.html" media-type="application/xhtml+xml"/>
    <item id="cover" href="cover.jpg" media-type="image/jpeg"/>
    <item id="stylesheet" href="style.css" media-type="text/css"/>
    <item id="ncx" href="toc.ncx" media-type="application/x-dtbncx+xml"/>
  </manifest>
  <spine toc="ncx">
    <itemref idref="chapter1"/>
  </spine>
</package>
```

## 使用示例

### 解析 EPUB2 文件

```java
import fun.lzwi.epubime.api.*;
import fun.lzwi.epubime.epub.*;

File epub2File = new File("epub2-book.epub");
EpubBook book = EpubReader.fromFile(epub2File).parse();

// 获取元数据
Metadata metadata = book.getMetadata();
System.out.println("标题: " + metadata.getTitle());
System.out.println("作者: " + metadata.getCreator());

// 获取章节（来自 NCX）
List<EpubChapter> chapters = book.getNcx(); // EPUB2 章节通常来自 NCX
for (EpubChapter chapter : chapters) {
    System.out.println("章节: " + chapter.getTitle());
    System.out.println("内容: " + chapter.getContent());
    
    // 处理嵌套章节
    if (chapter.hasChildren()) {
        for (EpubChapter subChapter : chapter.getChildren()) {
            System.out.println("  子章节: " + subChapter.getTitle());
        }
    }
}

// 获取资源
List<EpubResource> resources = book.getResources();
System.out.println("资源数量: " + resources.size());
```

### 检查 EPUB 版本

```java
// EPUBime 会自动检测并处理不同版本的 EPUB 文件
EpubBook book = EpubReader.fromFile(epub2File).parse();

// 使用增强功能获取更多信息
EpubBookEnhanced enhancedBook = new EpubBookEnhanced(book, epub2File);
List<EpubChapter> ncxChapters = enhancedBook.getChaptersByType("ncx");

System.out.println("NCX 章节数量: " + ncxChapters.size());
```

## 兼容性保证

### 自动版本检测
EPUBime 会自动检测 EPUB 文件的版本，并使用相应的解析策略：
- 如果是 EPUB2 文件，使用 NCX 导航解析
- 如果是 EPUB3 文件，使用 NAV 导航解析
- 支持混合命名空间和无命名空间的元数据格式

### 错误处理
EPUB2 文件可能存在格式不规范的情况，EPUBime 提供了强大的错误处理机制：
- 容忍缺失的命名空间声明
- 处理不完整的元数据
- 提供详细的错误信息和恢复建议

### 性能优化
- 智能缓存机制，避免重复解析
- 流式处理，优化内存使用
- 针对 EPUB2 格式的专门优化

## 注意事项

1. **导航差异**: EPUB2 使用 NCX，EPUB3 使用 NAV，EPUBime 会自动选择合适的解析方法
2. **元数据格式**: EPUB2 元数据格式相对简单，但可能存在命名空间使用不一致的情况
3. **资源引用**: EPUB2 的资源引用通常直接在 OPF 文件中定义
4. **向后兼容**: 所有 EPUBime 的 API 都支持 EPUB2 文件，无需特殊配置

EPUBime 的 EPUB2 兼容性确保了对大量现有 EPUB2 文件的支持，同时保持了与 EPUB3 的兼容性，为用户提供统一的 API 体验。