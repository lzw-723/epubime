package fun.lzwi.epubime.api;

/**
 * EPUB读取器配置类
 * 负责管理EpubReader的配置选项，遵循单一职责原则
 *
 * 不可变设计：每个withXxx()方法返回新实例，支持安全共享
 */
public class EpubReaderConfig {
    private final boolean useCache;
    private final boolean lazyLoading;
    private final boolean parallelProcessing;

    /**
     * 默认构造函数，使用默认配置
     */
    public EpubReaderConfig() {
        this(true, false, false);
    }

    /**
     * 复制构造函数
     * @param other 要复制的配置
     */
    public EpubReaderConfig(EpubReaderConfig other) {
        this(other.useCache, other.lazyLoading, other.parallelProcessing);
    }

    /**
     * 私有构造函数
     */
    private EpubReaderConfig(boolean useCache, boolean lazyLoading, boolean parallelProcessing) {
        this.useCache = useCache;
        this.lazyLoading = lazyLoading;
        this.parallelProcessing = parallelProcessing;
    }

    /**
     * 设置是否使用缓存
     * @param useCache 是否使用缓存
     * @return 新的配置对象，包含更新的值
     */
    public EpubReaderConfig withCache(boolean useCache) {
        return new EpubReaderConfig(useCache, this.lazyLoading, this.parallelProcessing);
    }

    /**
     * 设置是否使用延迟加载
     * @param lazyLoading 是否使用延迟加载
     * @return 新的配置对象，包含更新的值
     */
    public EpubReaderConfig withLazyLoading(boolean lazyLoading) {
        return new EpubReaderConfig(this.useCache, lazyLoading, this.parallelProcessing);
    }

    /**
     * 设置是否使用并行处理
     * @param parallelProcessing 是否使用并行处理
     * @return 新的配置对象，包含更新的值
     */
    public EpubReaderConfig withParallelProcessing(boolean parallelProcessing) {
        return new EpubReaderConfig(this.useCache, this.lazyLoading, parallelProcessing);
    }

    // Getter方法
    public boolean isUseCache() { return useCache; }
    public boolean isLazyLoading() { return lazyLoading; }
    public boolean isParallelProcessing() { return parallelProcessing; }
}
