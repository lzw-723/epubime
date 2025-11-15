# EpubReaderConfig

`EpubReaderConfig` 是 EPUBime 库中用于配置 `EpubReader` 行为的配置类。该类允许用户自定义解析选项，如缓存、延迟加载和并行处理等。

## 类定义

```java
public class EpubReaderConfig
```

## 构造方法

### EpubReaderConfig()
默认构造方法，使用默认配置选项。

### EpubReaderConfig(EpubReaderConfig other)
复制构造方法，从另一个配置对象创建新的配置对象。

**参数:**
- `other`: 要复制的配置对象

## 方法

### withCache(boolean useCache)
设置是否使用缓存。

**参数:**
- `useCache`: 是否启用缓存

**返回值:**
- `EpubReaderConfig`: 返回当前配置对象，用于方法链调用

### withLazyLoading(boolean lazyLoading)
设置是否使用延迟加载。

**参数:**
- `lazyLoading`: 是否启用延迟加载

**返回值:**
- `EpubReaderConfig`: 返回当前配置对象，用于方法链调用

### withParallelProcessing(boolean parallelProcessing)
设置是否使用并行处理。

**参数:**
- `parallelProcessing`: 是否启用并行处理

**返回值:**
- `EpubReaderConfig`: 返回当前配置对象，用于方法链调用

### isUseCache()
检查是否启用了缓存。

**返回值:**
- `boolean`: 如果启用了缓存返回 true，否则返回 false

### isLazyLoading()
检查是否启用了延迟加载。

**返回值:**
- `boolean`: 如果启用了延迟加载返回 true，否则返回 false

### isParallelProcessing()
检查是否启用了并行处理。

**返回值:**
- `boolean`: 如果启用了并行处理返回 true，否则返回 false

## 配置选项说明

- **缓存 (Cache)**: 启用缓存可以避免重复解析相同的内容，提高性能。默认启用。
- **延迟加载 (Lazy Loading)**: 启用延迟加载可以优化内存使用，仅在需要时加载资源。默认禁用。
- **并行处理 (Parallel Processing)**: 启用并行处理可以同时处理多个资源，提高处理效率。默认禁用。

## 使用示例

```java
// 创建默认配置
EpubReaderConfig config = new EpubReaderConfig();

// 使用方法链配置多个选项
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)              // 启用缓存
    .withLazyLoading(false)       // 禁用延迟加载
    .withParallelProcessing(true); // 启用并行处理

// 使用配置对象
EpubBook book = EpubReader.fromFile(epubFile, config).parse();

// 检查配置选项
System.out.println("使用缓存: " + config.isUseCache());
System.out.println("使用延迟加载: " + config.isLazyLoading());
System.out.println("使用并行处理: " + config.isParallelProcessing());
```

## 性能建议

- 对于需要多次访问相同 EPUB 文件的场景，建议启用缓存以提高性能。
- 对于大文件或内存受限的环境，建议启用延迟加载以优化内存使用。
- 对于包含大量资源文件的 EPUB 文件，建议启用并行处理以提高处理效率。