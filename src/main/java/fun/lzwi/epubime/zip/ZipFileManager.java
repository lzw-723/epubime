package fun.lzwi.epubime.zip;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * ZIP文件句柄管理器
 * 提供ZIP文件句柄的重用和管理，优化延迟加载性能
 */
public class ZipFileManager {
    // 单例实例
    private static final ZipFileManager INSTANCE = new ZipFileManager();
    
    // 线程本地存储，每个线程维护自己的ZIP文件句柄
    private final ThreadLocal<ZipFileHolder> threadLocalZipFile = new ThreadLocal<>();
    
    private ZipFileManager() {}
    
    /**
     * 获取ZIP文件管理器实例
     * @return ZIP文件管理器实例
     */
    public static ZipFileManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * 获取ZIP文件句柄，支持重用
     * @param zipFile ZIP文件
     * @return ZIP文件句柄
     * @throws IOException
     */
    public ZipFile getZipFile(File zipFile) throws IOException {
        ZipFileHolder holder = threadLocalZipFile.get();
        if (holder != null && holder.isValid() && holder.getFile().equals(zipFile)) {
            // 重用现有的ZIP文件句柄
            holder.incrementUsage();
            return holder.getZipFile();
        } else {
            // 关闭旧的句柄（如果存在）
            if (holder != null) {
                holder.close();
            }
            
            // 创建新的ZIP文件句柄
            ZipFile newZipFile = new ZipFile(zipFile);
            holder = new ZipFileHolder(newZipFile, zipFile);
            threadLocalZipFile.set(holder);
            return newZipFile;
        }
    }
    
    /**
     * 显式关闭当前线程的ZIP文件句柄
     */
    public void closeCurrentZipFile() {
        ZipFileHolder holder = threadLocalZipFile.get();
        if (holder != null) {
            holder.close();
            threadLocalZipFile.remove();
        }
    }
    
    /**
     * 清理当前线程的所有资源
     */
    public void cleanup() {
        closeCurrentZipFile();
    }
    
    /**
     * ZIP文件句柄包装器
     */
    private static class ZipFileHolder implements Closeable {
        private ZipFile zipFile;
        private File file;
        private int usageCount;
        private boolean closed;
        
        public ZipFileHolder(ZipFile zipFile, File file) {
            this.zipFile = zipFile;
            this.file = file;
            this.usageCount = 1;
            this.closed = false;
        }
        
        public ZipFile getZipFile() {
            return zipFile;
        }
        
        public File getFile() {
            return file;
        }
        
        public void incrementUsage() {
            usageCount++;
        }
        
        public boolean isValid() {
            return !closed && zipFile != null;
        }
        
        @Override
        public void close() {
            if (!closed && zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    // 忽略关闭异常
                } finally {
                    zipFile = null;
                    closed = true;
                }
            }
        }
    }
}