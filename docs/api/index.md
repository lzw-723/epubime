# API 参考

本节提供了 EPUBime 库的完整 API 参考文档。EPUBime 是一个纯 Java 库，用于解析 EPUB 文件格式，支持 EPUB 2 和 EPUB 3 格式。

## 核心类

- [EpubParser](/api/epub-parser) - EPUB 文件解析器，负责解析 EPUB 文件并生成 EpubBook 对象
- [EpubBook](/api/epub-book) - 代表一个解析后的 EPUB 书籍，包含元数据、章节和资源
- [Metadata](/api/metadata) - 代表 EPUB 书籍的元数据信息
- [EpubChapter](/api/epub-chapter) - 代表 EPUB 书籍中的一个章节
- [EpubResource](/api/epub-resource) - 代表 EPUB 书籍中的一个资源文件

## 异常处理

- [异常类](/api/exceptions) - EPUBime 的异常类体系，所有异常都继承自 EpubException

## 概述

EPUBime 的 API 设计简洁明了，主要流程如下：

1. 使用 `EpubParser` 解析 EPUB 文件
2. 获取 `EpubBook` 对象，其中包含元数据、章节和资源
3. 通过 `Metadata` 获取书籍元数据
4. 通过 `EpubChapter` 访问章节内容
5. 通过 `EpubResource` 访问资源文件

选择左侧导航栏中的类名查看详细 API 文档。