package fun.lzwi.epubime.cache;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EPUB解析缓存管理器
 * 提供对ZIP文件内容、解析结果等的缓存，避免重复解析相同内容
 *
 * 改进：
 * - 文件缓存使用 LRU 策略限制条目数，防止内存泄漏
 * - 提供自动清理不存在文件的机制
 */
public class EpubCacheManager {
    // Maximum number of cached EPUB files (LRU eviction)
    private static final int MAX_CACHED_FILES = 50;
    // Maximum number of text entries per file
    private static final int MAX_TEXT_ENTRIES = 100;
    // Maximum number of binary entries per file
    private static final int MAX_BINARY_ENTRIES = 50;
    // Maximum number of parsed result entries per file
    private static final int MAX_PARSED_ENTRIES = 10;

    // 每个EPUB文件的缓存，使用 LinkedHashMap 实现 LRU 策略
    private final Map<File, EpubFileCache> fileCaches =
            Collections.synchronizedMap(new LruMap<>(MAX_CACHED_FILES));

    /**
     * 私有构造函数，防止外部实例化
     */
    private EpubCacheManager() {}

    /**
     * 静态内部类实现延迟初始化的单例模式
     */
    private static class SingletonHolder {
        private static final EpubCacheManager INSTANCE = new EpubCacheManager();
    }

    /**
     * 获取缓存管理器实例
     * @return 缓存管理器实例
     */
    public static EpubCacheManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取指定EPUB文件的缓存
     * @param epubFile EPUB文件
     * @return 文件缓存
     */
    public EpubFileCache getFileCache(File epubFile) {
        return fileCaches.computeIfAbsent(epubFile, k -> new EpubFileCache());
    }

    /**
     * 清除指定EPUB文件的缓存
     * @param epubFile EPUB文件
     */
    public void clearFileCache(File epubFile) {
        fileCaches.remove(epubFile);
    }

    /**
     * 清除所有缓存
     */
    public void clearAllCaches() {
        fileCaches.clear();
    }

    /**
     * 清理不存在文件的缓存，避免内存泄漏
     * 建议定期调用此方法
     */
    public void cleanupInvalidCaches() {
        synchronized (fileCaches) {
            fileCaches.entrySet().removeIf(entry -> !entry.getKey().exists());
        }
    }

    /**
     * LRU LinkedHashMap implementation for automatic eviction.
     */
    private static class LruMap<K, V> extends LinkedHashMap<K, V> {
        private final int maxSize;

        LruMap(int maxSize) {
            super(maxSize + 1, 0.75f, true);
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > maxSize;
        }
    }

    /**
     * 单个EPUB文件的缓存
     */
    public static class EpubFileCache {
        // ZIP文件内容缓存 (文件路径 -> 文件内容), LRU-limited
        private final Map<String, String> textContentCache =
                Collections.synchronizedMap(new LruMap<>(MAX_TEXT_ENTRIES));
        private final Map<String, byte[]> binaryContentCache =
                Collections.synchronizedMap(new LruMap<>(MAX_BINARY_ENTRIES));

        // 解析结果缓存, LRU-limited
        private final Map<String, Object> parsedResultCache =
                Collections.synchronizedMap(new LruMap<>(MAX_PARSED_ENTRIES));

        /**
         * 获取文本内容缓存
         * @return 文本内容缓存的不可修改视图
         */
        public Map<String, String> getTextContentCache() {
            return Collections.unmodifiableMap(textContentCache);
        }

        /**
         * 获取指定键的文本内容
         * @param key 键
         * @return 文本内容
         */
        public String getTextContent(String key) {
            return textContentCache.get(key);
        }

        /**
         * 设置文本内容缓存
         * @param key 键
         * @param content 内容
         */
        public void setTextContent(String key, String content) {
            if (key != null) {
                if (content != null) {
                    textContentCache.put(key, content);
                } else {
                    textContentCache.remove(key);
                }
            }
        }

        /**
         * 获取二进制内容缓存
         * @return 二进制内容缓存的不可修改视图
         */
        public Map<String, byte[]> getBinaryContentCache() {
            return Collections.unmodifiableMap(binaryContentCache);
        }

        /**
         * 获取指定键的二进制内容
         * @param key 键
         * @return 二进制内容
         */
        public byte[] getBinaryContent(String key) {
            byte[] data = binaryContentCache.get(key);
            return data != null ? data.clone() : null;
        }

        /**
         * 设置二进制内容缓存
         * @param key 键
         * @param data 数据
         */
        public void setBinaryContent(String key, byte[] data) {
            if (key != null) {
                if (data != null) {
                    binaryContentCache.put(key, data.clone());
                } else {
                    binaryContentCache.remove(key);
                }
            }
        }

        /**
         * 获取解析结果缓存
         * @return 解析结果缓存的不可修改视图
         */
        public Map<String, Object> getParsedResultCache() {
            return Collections.unmodifiableMap(parsedResultCache);
        }

        /**
         * 获取指定键的解析结果
         * @param key 键
         * @return 解析结果
         */
        public Object getParsedResult(String key) {
            return parsedResultCache.get(key);
        }

        /**
         * 设置解析结果缓存
         * @param key 键
         * @param result 结果
         */
        public void setParsedResult(String key, Object result) {
            if (key != null) {
                if (result != null) {
                    parsedResultCache.put(key, result);
                } else {
                    parsedResultCache.remove(key);
                }
            }
        }

        /**
         * 清除该文件的所有缓存
         */
        public void clear() {
            textContentCache.clear();
            binaryContentCache.clear();
            parsedResultCache.clear();
        }
    }
}
