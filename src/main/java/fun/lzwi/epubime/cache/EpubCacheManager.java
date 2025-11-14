package fun.lzwi.epubime.cache;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EPUB解析缓存管理器
 * 提供对ZIP文件内容、解析结果等的缓存，避免重复解析相同内容
 */
public class EpubCacheManager {
    // 单例实例
    private static final EpubCacheManager INSTANCE = new EpubCacheManager();
    
    // 每个EPUB文件的缓存，使用WeakHashMap避免内存泄漏
    private final Map<File, EpubFileCache> fileCaches = new WeakHashMap<>();
    
    private EpubCacheManager() {}
    
    /**
     * 获取缓存管理器实例
     * @return 缓存管理器实例
     */
    public static EpubCacheManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * 获取指定EPUB文件的缓存
     * @param epubFile EPUB文件
     * @return 文件缓存
     */
    public EpubFileCache getFileCache(File epubFile) {
        synchronized (fileCaches) {
            EpubFileCache cache = fileCaches.get(epubFile);
            if (cache == null) {
                cache = new EpubFileCache();
                fileCaches.put(epubFile, cache);
            }
            return cache;
        }
    }
    
    /**
     * 清除指定EPUB文件的缓存
     * @param epubFile EPUB文件
     */
    public void clearFileCache(File epubFile) {
        synchronized (fileCaches) {
            fileCaches.remove(epubFile);
        }
    }
    
    /**
     * 清除所有缓存
     */
    public void clearAllCaches() {
        synchronized (fileCaches) {
            fileCaches.clear();
        }
    }
    
    /**
     * 单个EPUB文件的缓存
     */
    public static class EpubFileCache {
        // ZIP文件内容缓存 (文件路径 -> 文件内容)
        private final Map<String, String> textContentCache = new ConcurrentHashMap<>();
        private final Map<String, byte[]> binaryContentCache = new ConcurrentHashMap<>();
        
        // 解析结果缓存
        private final Map<String, Object> parsedResultCache = new ConcurrentHashMap<>();
        
        /**
         * 获取文本内容缓存
         * @return 文本内容缓存
         */
        public Map<String, String> getTextContentCache() {
            return textContentCache;
        }
        
        /**
         * 获取二进制内容缓存
         * @return 二进制内容缓存
         */
        public Map<String, byte[]> getBinaryContentCache() {
            return binaryContentCache;
        }
        
        /**
         * 获取解析结果缓存
         * @return 解析结果缓存
         */
        public Map<String, Object> getParsedResultCache() {
            return parsedResultCache;
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