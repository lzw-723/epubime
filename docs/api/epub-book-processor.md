# API 参考

## EpubBookProcessor

`EpubBookProcessor` 是处理 EpubBook 相关业务逻辑的专用类，遵循单一职责原则，专门负责书籍数据的处理和操作。

### 封面处理

#### getCover()
```java
public static EpubResource getCover(EpubBook book)
```
获取书籍的封面资源。

参数:
- `book`: EpubBook 实例

返回:
- `EpubResource`: 封面资源对象，如果未找到则返回 null

#### 封面查找逻辑
1. 优先查找 `properties="cover-image"` 的资源
2. 如果未找到，则使用元数据的 cover 字段回退查找
3. 自动应用资源回退机制

### 资源处理

#### getResource()
```java
public static EpubResource getResource(EpubBook book, String resourceId)
```
根据资源 ID 获取资源。

参数:
- `book`: EpubBook 实例
- `resourceId`: 资源 ID

返回:
- `EpubResource`: 找到的资源对象，如果未找到则返回 null

#### getResourceWithFallback()
```java
public static EpubResource getResourceWithFallback(EpubBook book, String resourceId)
```
根据资源 ID 获取资源，并自动应用回退机制。

参数:
- `book`: EpubBook 实例
- `resourceId`: 资源 ID

返回:
- `EpubResource`: 应用回退机制后的资源对象，如果未找到则返回 null

### 数据加载

#### loadAllResourceData()
```java
public static void loadAllResourceData(EpubBook book) throws IOException
```
批量加载所有资源的数据到内存中。

参数:
- `book`: EpubBook 实例

抛出:
- `IOException`: 文件读取异常

## 使用示例

```java
// 获取封面资源
EpubResource cover = EpubBookProcessor.getCover(book);
if (cover != null) {
    System.out.println("封面: " + cover.getHref());
}

// 获取特定资源
EpubResource image = EpubBookProcessor.getResource(book, "image1");
if (image != null) {
    System.out.println("图片类型: " + image.getType());
}

// 获取资源并应用回退机制
EpubResource resource = EpubBookProcessor.getResourceWithFallback(book, "fallback-resource");

// 批量加载资源数据
try {
    EpubBookProcessor.loadAllResourceData(book);
    System.out.println("所有资源数据已加载");
} catch (IOException e) {
    System.err.println("加载资源数据失败: " + e.getMessage());
}
```</content>
<parameter name="filePath">docs/api/epub-book-processor.md