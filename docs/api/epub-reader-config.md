# API 参考

## EpubReaderConfig

`EpubReaderConfig` 是 EpubReader 的配置类，遵循单一职责原则，专门负责管理 EpubReader 的配置选项。

### 创建实例

```java
// 使用默认配置
public EpubReaderConfig()

// 复制现有配置
public EpubReaderConfig(EpubReaderConfig other)
```

### 配置方法

#### withCache()
```java
public EpubReaderConfig withCache(boolean useCache)
```
启用或禁用缓存机制。

参数:
- `useCache`: 是否使用缓存（默认：true）

返回:
- `EpubReaderConfig`: this，用于方法链式调用

#### withLazyLoading()
```java
public EpubReaderConfig withLazyLoading(boolean lazyLoading)
```
启用或禁用延迟加载。

参数:
- `lazyLoading`: 是否使用延迟加载（默认：false）

返回:
- `EpubReaderConfig`: this，用于方法链式调用

#### withParallelProcessing()
```java
public EpubReaderConfig withParallelProcessing(boolean parallelProcessing)
```
启用或禁用并行处理。

参数:
- `parallelProcessing`: 是否使用并行处理（默认：false）

返回:
- `EpubReaderConfig`: this，用于方法链式调用

### 获取配置值

```java
public boolean isUseCache()
public boolean isLazyLoading()
public boolean isParallelProcessing()
```

### 使用示例

```java
// 创建自定义配置
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)
    .withLazyLoading(false)
    .withParallelProcessing(true);

// 使用配置创建 EpubReader
EpubBook book = EpubReader.fromFile(epubFile, config).parse();

// 复制和修改配置
EpubReaderConfig newConfig = new EpubReaderConfig(config)
    .withCache(false);
```</content>
<parameter name="filePath">docs/api/epub-reader-config.md