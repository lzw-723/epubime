package fun.lzwi.epubime.api;

/**
 * EPUB读取器配置类
 * 负责管理EpubReader的配置选项，遵循单一职责原则
 */
public class EpubReaderConfig {
    private boolean useCache = true;
    private boolean lazyLoading = false;
    private boolean parallelProcessing = false;

    /**
     * 默认构造函数，使用默认配置
     */
    public EpubReaderConfig() {}

    /**
     * 复制构造函数
     * @param other 要复制的配置
     */
    public EpubReaderConfig(EpubReaderConfig other) {
        this.useCache = other.useCache;
        this.lazyLoading = other.lazyLoading;
        this.parallelProcessing = other.parallelProcessing;
    }

    /**
     * 设置是否使用缓存
     * @param useCache 是否使用缓存
     * @return this 配置对象，用于方法链
     */
    public EpubReaderConfig withCache(boolean useCache) {
        this.useCache = useCache;
        return this;
    }

    /**
     * 设置是否使用延迟加载
     * @param lazyLoading 是否使用延迟加载
     * @return this 配置对象，用于方法链
     */
    public EpubReaderConfig withLazyLoading(boolean lazyLoading) {
        this.lazyLoading = lazyLoading;
        return this;
    }

    /**
     * 设置是否使用并行处理
     * @param parallelProcessing 是否使用并行处理
     * @return this 配置对象，用于方法链
     */
    public EpubReaderConfig withParallelProcessing(boolean parallelProcessing) {
        this.parallelProcessing = parallelProcessing;
        return this;
    }

    // Getter方法
    public boolean isUseCache() { return useCache; }
    public boolean isLazyLoading() { return lazyLoading; }
    public boolean isParallelProcessing() { return parallelProcessing; }
}