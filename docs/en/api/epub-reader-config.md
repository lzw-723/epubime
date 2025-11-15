# EpubReaderConfig

`EpubReaderConfig` is a configuration class in the EPUBime library used to configure the behavior of `EpubReader`. This class allows users to customize parsing options such as caching, lazy loading, and parallel processing.

## Class Definition

```java
public class EpubReaderConfig
```

## Constructors

### EpubReaderConfig()
Default constructor that uses default configuration options.

### EpubReaderConfig(EpubReaderConfig other)
Copy constructor that creates a new configuration object from another configuration object.

**Parameters:**
- `other`: The configuration object to copy

## Methods

### withCache(boolean useCache)
Sets whether to use caching.

**Parameters:**
- `useCache`: Whether to enable caching

**Returns:**
- `EpubReaderConfig`: Returns the current configuration object for method chaining

### withLazyLoading(boolean lazyLoading)
Sets whether to use lazy loading.

**Parameters:**
- `lazyLoading`: Whether to enable lazy loading

**Returns:**
- `EpubReaderConfig`: Returns the current configuration object for method chaining

### withParallelProcessing(boolean parallelProcessing)
Sets whether to use parallel processing.

**Parameters:**
- `parallelProcessing`: Whether to enable parallel processing

**Returns:**
- `EpubReaderConfig`: Returns the current configuration object for method chaining

### isUseCache()
Checks if caching is enabled.

**Returns:**
- `boolean`: True if caching is enabled, false otherwise

### isLazyLoading()
Checks if lazy loading is enabled.

**Returns:**
- `boolean`: True if lazy loading is enabled, false otherwise

### isParallelProcessing()
Checks if parallel processing is enabled.

**Returns:**
- `boolean`: True if parallel processing is enabled, false otherwise

## Configuration Options Explanation

- **Cache**: Enabling caching can avoid re-parsing the same content, improving performance. Enabled by default.
- **Lazy Loading**: Enabling lazy loading can optimize memory usage by loading resources only when needed. Disabled by default.
- **Parallel Processing**: Enabling parallel processing can process multiple resources simultaneously, improving efficiency. Disabled by default.

## Usage Examples

```java
// Create default configuration
EpubReaderConfig config = new EpubReaderConfig();

// Configure multiple options using method chaining
EpubReaderConfig config = new EpubReaderConfig()
    .withCache(true)              // Enable caching
    .withLazyLoading(false)       // Disable lazy loading
    .withParallelProcessing(true); // Enable parallel processing

// Use configuration object
EpubBook book = EpubReader.fromFile(epubFile, config).parse();

// Check configuration options
System.out.println("Using cache: " + config.isUseCache());
System.out.println("Using lazy loading: " + config.isLazyLoading());
System.out.println("Using parallel processing: " + config.isParallelProcessing());
```

## Performance Recommendations

- For scenarios where the same EPUB file needs to be accessed multiple times, it is recommended to enable caching to improve performance.
- For large files or memory-constrained environments, it is recommended to enable lazy loading to optimize memory usage.
- For EPUB files containing a large number of resource files, it is recommended to enable parallel processing to improve processing efficiency.