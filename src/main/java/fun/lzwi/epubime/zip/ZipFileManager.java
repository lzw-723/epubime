package fun.lzwi.epubime.zip;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipFile;

/**
 * ZIP文件句柄管理器 - 使用LRU句柄池优化
 * 
 * 改进:
 * - 使用全局LRU池替代ThreadLocal，支持跨线程重用
 * - 限制最大句柄数，防止资源泄漏
 * - 引用计数管理，自动清理未使用句柄
 * - 线程安全设计
 */
public class ZipFileManager {

    // 单例实例
    private static final ZipFileManager INSTANCE = new ZipFileManager();
    
    // 最大缓存句柄数（LRU淘汰）
    private static final int MAX_CACHED_HANDLES = 50;

    // LRU句柄池 - 使用LinkedHashMap实现LRU
    private final LinkedHashMap<File, ZipFileHolder> handlePool;
    
    // 池锁 - 保护并发访问
    private final Object poolLock = new Object();

    /**
     * 私有构造函数，防止外部实例化
     */
    private ZipFileManager() {
        // 使用LinkedHashMap实现LRU池
        this.handlePool = new LinkedHashMap<File, ZipFileHolder>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<File, ZipFileHolder> eldest) {
                // 当池大小超过限制时，移除最久未使用的句柄
                if (size() > MAX_CACHED_HANDLES) {
                    // 只移除引用计数为0的句柄
                    ZipFileHolder holder = eldest.getValue();
                    if (holder.getUsageCount() <= 0) {
                        holder.close();
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * 获取ZIP文件管理器实例
     * @return ZIP文件管理器实例
     */
    public static ZipFileManager getInstance() {
        return INSTANCE;
    }

    /**
     * 获取ZIP文件句柄，支持跨线程重用
     * 
     * @param zipFile ZIP文件
     * @return ZIP文件句柄
     * @throws IOException IO异常
     */
    public ZipFile getZipFile(File zipFile) throws IOException {
        if (zipFile == null) {
            throw new IllegalArgumentException("ZIP file cannot be null");
        }
        
        synchronized (poolLock) {
            ZipFileHolder holder = handlePool.get(zipFile);
            
            if (holder != null && holder.isValid()) {
                // 重用现有的ZIP文件句柄
                holder.incrementUsage();
                return holder.getZipFile();
            }
            
            // 创建新的ZIP文件句柄
            ZipFile newZipFile = new ZipFile(zipFile);
            holder = new ZipFileHolder(newZipFile, zipFile);
            holder.incrementUsage(); // 初始使用计数为1
            
            handlePool.put(zipFile, holder);
            
            return newZipFile;
        }
    }

    /**
     * 释放ZIP文件句柄，减少引用计数
     * 当引用计数归零时，标记为可回收
     * 
     * @param zipFile 要释放的ZIP文件
     */
    public void releaseZipFile(File zipFile) {
        if (zipFile == null) {
            return;
        }
        
        synchronized (poolLock) {
            ZipFileHolder holder = handlePool.get(zipFile);
            if (holder != null) {
                holder.decrementUsage();
            }
        }
    }
    
    /**
     * 释放ZIP文件句柄（通过句柄对象）
     * 
     * @param zipFile ZIP文件句柄
     */
    public void releaseZipFile(ZipFile zipFile) {
        if (zipFile == null) {
            return;
        }
        
        synchronized (poolLock) {
            // 查找对应的holder
            for (ZipFileHolder holder : handlePool.values()) {
                if (holder.getZipFile() == zipFile) {
                    holder.decrementUsage();
                    break;
                }
            }
        }
    }
    
    /**
     * 释放当前线程的ZIP文件句柄（向后兼容）
     * @deprecated 请使用 releaseZipFile(File) 或 releaseZipFile(ZipFile)
     */
    @Deprecated
    public void releaseZipFile() {
        // 为了向后兼容，不做任何操作
    }

    /**
     * 显式关闭指定文件的ZIP句柄
     * 
     * @param zipFile ZIP文件
     */
    public void closeZipFile(File zipFile) {
        if (zipFile == null) {
            return;
        }
        
        synchronized (poolLock) {
            ZipFileHolder holder = handlePool.remove(zipFile);
            if (holder != null) {
                holder.forceClose();
            }
        }
    }

    /**
     * 显式关闭当前线程的ZIP文件句柄（向后兼容）
     * @deprecated 请使用 closeZipFile(File) 或 cleanup()
     */
    @Deprecated
    public void closeCurrentZipFile() {
        // 由于现在是全局池，这个方法不再有意义
        // 为了向后兼容，不做任何操作
    }

    /**
     * 清理所有缓存的ZIP句柄
     */
    public void cleanup() {
        synchronized (poolLock) {
            for (ZipFileHolder holder : handlePool.values()) {
                holder.forceClose();
            }
            handlePool.clear();
        }
    }

    /**
     * 获取当前缓存的句柄数
     * 
     * @return 缓存句柄数
     */
    public int getCachedHandleCount() {
        synchronized (poolLock) {
            return handlePool.size();
        }
    }

    /**
     * 清理无效的缓存（文件不存在或已损坏）
     */
    public void cleanupInvalidCaches() {
        synchronized (poolLock) {
            Iterator<Map.Entry<File, ZipFileHolder>> iterator = handlePool.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<File, ZipFileHolder> entry = iterator.next();
                File file = entry.getKey();
                ZipFileHolder holder = entry.getValue();
                
                // 如果文件不存在或句柄已关闭，移除
                if (!file.exists() || !holder.isValid()) {
                    holder.forceClose();
                    iterator.remove();
                }
            }
        }
    }

    /**
     * ZIP文件句柄包装器
     */
    private static class ZipFileHolder implements Closeable {
        private ZipFile zipFile;
        private final File file;
        private final AtomicInteger usageCount; // 使用原子计数器以确保线程安全
        private volatile boolean closed; // 使用volatile确保多线程可见性

        /**
         * 构造函数
         * @param zipFile ZIP文件句柄
         * @param file 对应的文件
         */
        public ZipFileHolder(ZipFile zipFile, File file) {
            this.zipFile = zipFile;
            this.file = file;
            this.usageCount = new AtomicInteger(0);
            this.closed = false;
        }

        /**
         * 获取ZIP文件句柄
         * @return ZIP文件句柄
         */
        public ZipFile getZipFile() {
            return zipFile;
        }

        /**
         * 获取对应的文件
         * @return 对应的文件
         */
        public File getFile() {
            return file;
        }

        /**
         * 获取使用计数
         * @return 使用计数
         */
        public int getUsageCount() {
            return usageCount.get();
        }

        /**
         * 增加使用计数
         */
        public void incrementUsage() {
            usageCount.incrementAndGet();
        }

        /**
         * 减少使用计数，当计数归零时标记为可回收
         */
        public void decrementUsage() {
            int count = usageCount.decrementAndGet();
            if (count <= 0) {
                // 不立即关闭，让LRU池决定何时清理
                // 这样可以避免短时间内重新打开同一个文件
            }
        }

        /**
         * 强制关闭ZIP文件，忽略引用计数
         */
        public void forceClose() {
            close();
        }

        /**
         * 检查句柄是否有效
         * @return 如果有效返回true，否则返回false
         */
        public boolean isValid() {
            return !closed && zipFile != null;
        }

        @Override
        public void close() {
            synchronized (this) {
                if (!closed && zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (IOException e) {
                        // 忽略关闭异常
                    } finally {
                        zipFile = null;
                        closed = true;
                        usageCount.set(0);
                    }
                }
            }
        }
    }
}
