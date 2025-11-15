# API Reference

## EpubReaderConfig

`EpubReaderConfig` is the configuration class for EpubReader, following the Single Responsibility Principle, dedicated to managing EpubReader configuration options.

### Creating Instances

```java
// Use default configuration
public EpubReaderConfig()

// Copy existing configuration
public EpubReaderConfig(EpubReaderConfig other)
```

### Configuration Methods

#### withCache()
```java
public EpubReaderConfig withCache(boolean useCache)
```
Enable or disable caching mechanism.

Parameters:
- `useCache`: Whether to use caching (default: true)

Returns:
- `EpubReaderConfig`: this, for method chaining

#### withLazyLoading()
```java
public EpubReaderConfig withLazyLoading(boolean lazyLoading)
```
Enable or disable lazy loading.

Parameters:
- `lazyLoading`: Whether to use lazy loading (default: false)

Returns:
- `EpubReaderConfig`: this, for method chaining

#### withParallelProcessing()
```java
public EpubReaderConfig withParallelProcessing(boolean parallelProcessing)
```
Enable or disable parallel processing.

Parameters:
- `parallelProcessing`: Whether to use parallel processing (default: false)

Returns:
- `EpubReaderConfig`: this, for method chaining

### Getting Configuration Values

```java
public boolean isUseCache()
public boolean isLazyLoading()
public boolean isParallelProcessing()
```

### Usage Example

```java
// Create custom configuration
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)
    .withLazyLoading(false)
    .withParallelProcessing(true);

// Use configuration to create EpubReader
EpubBook book = EpubReader.fromFile(epubFile, config).parse();

// Copy and modify configuration
EpubReaderConfig newConfig = new EpubReaderConfig(config)
    .withCache(false);
```</content>
<parameter name="filePath">docs/en/api/epub-reader-config.md