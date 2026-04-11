package fun.lzwi.epubime.zip;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * ZIP Bomb 防护工具类
 * 
 * <p>ZIP Bomb 是一种恶意压缩文件，通过极小的压缩比（如 1GB 内容压缩到几 KB）
 * 来消耗解压系统的内存和 CPU。本类提供多重防护机制：</p>
 * 
 * <ul>
 *   <li>文件大小限制：限制解压后的最大文件大小</li>
 *   <li>压缩比检查：检测异常的压缩比例</li>
 *   <li>条目数量限制：限制 ZIP 文件中的最大条目数</li>
 *   <li>流式解压监控：在读取过程中实时检查解压大小</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>
 * // 验证 ZIP 文件安全性
 * ZipBombProtection.validateZipFileSafety(zipFile);
 * 
 * // 安全地读取 ZIP 条目
 * try (SafeZipInputStream is = ZipBombProtection.openSafeStream(zipFile, entryName)) {
 *     byte[] data = is.readAllBytes();
 * }
 * </pre>
 * 
 * @see ZipFile
 * @see ZipEntry
 */
public class ZipBombProtection {
    
    // 默认配置常量
    /**
     * 单个条目解压后的最大大小：100 MB
     */
    public static final long DEFAULT_MAX_ENTRY_SIZE = 100L * 1024 * 1024;
    
    /**
     * 最大压缩比：1000:1
     * 如果压缩比超过此值，则视为 ZIP Bomb 攻击
     */
    public static final double DEFAULT_MAX_COMPRESSION_RATIO = 1000.0;
    
    /**
     * ZIP 文件中的最大条目数：10,000
     */
    public static final int DEFAULT_MAX_ENTRY_COUNT = 10_000;
    
    /**
     * ZIP 文件的最大总大小：500 MB
     */
    public static final long DEFAULT_MAX_ZIP_SIZE = 500L * 1024 * 1024;
    
    /**
     * 默认安全配置
     */
    private static final ZipSafetyConfig DEFAULT_CONFIG = new ZipSafetyConfig(
        DEFAULT_MAX_ENTRY_SIZE,
        DEFAULT_MAX_COMPRESSION_RATIO,
        DEFAULT_MAX_ENTRY_COUNT,
        DEFAULT_MAX_ZIP_SIZE
    );

    /**
     * ZIP 安全配置
     */
    public static class ZipSafetyConfig {
        private final long maxEntrySize;
        private final double maxCompressionRatio;
        private final int maxEntryCount;
        private final long maxZipSize;

        @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", 
                           justification = "Parameter validation - object is immutable after construction")
        public ZipSafetyConfig(long maxEntrySize, double maxCompressionRatio,
                              int maxEntryCount, long maxZipSize) {
            if (maxEntrySize <= 0) {
                throw new IllegalArgumentException("maxEntrySize must be positive");
            }
            if (maxCompressionRatio <= 0) {
                throw new IllegalArgumentException("maxCompressionRatio must be positive");
            }
            if (maxEntryCount <= 0) {
                throw new IllegalArgumentException("maxEntryCount must be positive");
            }
            if (maxZipSize <= 0) {
                throw new IllegalArgumentException("maxZipSize must be positive");
            }
            
            this.maxEntrySize = maxEntrySize;
            this.maxCompressionRatio = maxCompressionRatio;
            this.maxEntryCount = maxEntryCount;
            this.maxZipSize = maxZipSize;
        }

        public long getMaxEntrySize() {
            return maxEntrySize;
        }

        public double getMaxCompressionRatio() {
            return maxCompressionRatio;
        }

        public int getMaxEntryCount() {
            return maxEntryCount;
        }

        public long getMaxZipSize() {
            return maxZipSize;
        }

        /**
         * 获取默认配置
         */
        public static ZipSafetyConfig getDefault() {
            return DEFAULT_CONFIG;
        }
    }

    /**
     * ZIP Bomb 异常
     */
    public static class ZipBombException extends IOException {
        private final String reason;
        private final long expectedSize;
        private final long actualSize;

        public ZipBombException(String message, String reason) {
            super(message);
            this.reason = reason;
            this.expectedSize = -1;
            this.actualSize = -1;
        }

        public ZipBombException(String message, String reason, long expectedSize, long actualSize) {
            super(message);
            this.reason = reason;
            this.expectedSize = expectedSize;
            this.actualSize = actualSize;
        }

        public String getReason() {
            return reason;
        }

        public long getExpectedSize() {
            return expectedSize;
        }

        public long getActualSize() {
            return actualSize;
        }
    }

    /**
     * 验证 ZIP 文件的安全性
     * 
     * <p>此方法检查：</p>
     * <ul>
     *   <li>ZIP 文件总大小是否超过限制</li>
     *   <li>ZIP 条目数量是否超过限制</li>
     *   <li>是否存在压缩比异常的条目</li>
     *   <li>是否存在解压后过大的条目</li>
     * </ul>
     * 
     * @param zipFile ZIP 文件
     * @throws ZipBombException 如果发现潜在的安全问题
     * @throws IOException 如果读取 ZIP 文件失败
     */
    public static void validateZipFileSafety(ZipFile zipFile) throws ZipBombException, IOException {
        validateZipFileSafety(zipFile, DEFAULT_CONFIG);
    }

    /**
     * 验证 ZIP 文件的安全性（使用自定义配置）
     * 
     * @param zipFile ZIP 文件
     * @param config 安全配置
     * @throws ZipBombException 如果发现潜在的安全问题
     * @throws IOException 如果读取 ZIP 文件失败
     */
    public static void validateZipFileSafety(ZipFile zipFile, ZipSafetyConfig config) 
            throws ZipBombException, IOException {
        if (zipFile == null) {
            throw new IllegalArgumentException("zipFile cannot be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }

        // 检查 ZIP 文件大小
        java.io.File file = new java.io.File(zipFile.getName());
        if (file.exists()) {
            long actualSize = file.length();
            if (actualSize > config.getMaxZipSize()) {
                throw new ZipBombException(
                    String.format("ZIP file size too large: %d bytes (max: %d bytes)",
                                actualSize, config.getMaxZipSize()),
                    "ZIP_FILE_TOO_LARGE",
                    config.getMaxZipSize(),
                    actualSize
                );
            }
        }

        // 检查条目数量和每个条目的大小
        int entryCount = 0;
        java.util.Enumeration<? extends ZipEntry> entries = zipFile.entries();
        
        while (entries.hasMoreElements()) {
            entryCount++;
            
            // 检查条目数量限制
            if (entryCount > config.getMaxEntryCount()) {
                throw new ZipBombException(
                    String.format("Too many entries in ZIP file: %d (max: %d)", 
                                entryCount, config.getMaxEntryCount()),
                    "TOO_MANY_ENTRIES",
                    config.getMaxEntryCount(),
                    entryCount
                );
            }
            
            ZipEntry entry = entries.nextElement();
            
            // 跳过目录条目
            if (entry.isDirectory()) {
                continue;
            }
            
            // 检查解压后大小
            long uncompressedSize = entry.getSize();
            if (uncompressedSize > config.getMaxEntrySize()) {
                throw new ZipBombException(
                    String.format("ZIP entry too large after decompression: %d bytes (max: %d bytes)", 
                                uncompressedSize, config.getMaxEntrySize()),
                    "ENTRY_TOO_LARGE",
                    config.getMaxEntrySize(),
                    uncompressedSize
                );
            }
            
            // 检查压缩比
            long compressedSize = entry.getCompressedSize();
            if (compressedSize > 0 && uncompressedSize > 0) {
                double compressionRatio = (double) uncompressedSize / compressedSize;
                if (compressionRatio > config.getMaxCompressionRatio()) {
                    throw new ZipBombException(
                        String.format("Suspicious compression ratio: %.2f:1 (max: %.2f:1) for entry: %s", 
                                    compressionRatio, config.getMaxCompressionRatio(), entry.getName()),
                        "HIGH_COMPRESSION_RATIO",
                        (long) (compressedSize * config.getMaxCompressionRatio()),
                        uncompressedSize
                    );
                }
            }
        }
    }

    /**
     * 安全地打开 ZIP 条目输入流
     * 
     * <p>此方法返回一个受监控的输入流，在读取过程中会：</p>
     * <ul>
     *   <li>限制读取的总字节数</li>
     *   <li>实时检查解压进度</li>
     *   <li>超过限制时自动抛出异常</li>
     * </ul>
     * 
     * @param zipFile ZIP 文件
     * @param entryName 条目名称
     * @return 安全的输入流
     * @throws ZipBombException 如果条目不安全
     * @throws IOException 如果读取失败
     */
    public static SafeZipInputStream openSafeStream(ZipFile zipFile, String entryName) 
            throws ZipBombException, IOException {
        return openSafeStream(zipFile, entryName, DEFAULT_CONFIG);
    }

    /**
     * 安全地打开 ZIP 条目输入流（使用自定义配置）
     * 
     * @param zipFile ZIP 文件
     * @param entryName 条目名称
     * @param config 安全配置
     * @return 安全的输入流
     * @throws ZipBombException 如果条目不安全
     * @throws IOException 如果读取失败
     */
    public static SafeZipInputStream openSafeStream(ZipFile zipFile, String entryName, 
                                                    ZipSafetyConfig config) 
            throws ZipBombException, IOException {
        if (zipFile == null) {
            throw new IllegalArgumentException("zipFile cannot be null");
        }
        if (entryName == null || entryName.isEmpty()) {
            throw new IllegalArgumentException("entryName cannot be null or empty");
        }
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }

        ZipEntry entry = zipFile.getEntry(entryName);
        if (entry == null) {
            throw new IOException("ZIP entry not found: " + entryName);
        }

        // 验证条目安全性
        validateZipEntry(entry, config);

        InputStream inputStream = zipFile.getInputStream(entry);
        return new SafeZipInputStream(inputStream, entry, config);
    }

    /**
     * 验证单个 ZIP 条目的安全性
     * 
     * @param entry ZIP 条目
     * @param config 安全配置
     * @throws ZipBombException 如果条目不安全
     */
    public static void validateZipEntry(ZipEntry entry, ZipSafetyConfig config) 
            throws ZipBombException {
        if (entry == null) {
            throw new IllegalArgumentException("entry cannot be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }

        // 跳过目录
        if (entry.isDirectory()) {
            return;
        }

        // 检查解压后大小
        long uncompressedSize = entry.getSize();
        if (uncompressedSize > config.getMaxEntrySize()) {
            throw new ZipBombException(
                String.format("ZIP entry too large after decompression: %d bytes (max: %d bytes)", 
                            uncompressedSize, config.getMaxEntrySize()),
                "ENTRY_TOO_LARGE",
                config.getMaxEntrySize(),
                uncompressedSize
            );
        }

        // 检查压缩比
        long compressedSize = entry.getCompressedSize();
        if (compressedSize > 0 && uncompressedSize > 0) {
            double compressionRatio = (double) uncompressedSize / compressedSize;
            if (compressionRatio > config.getMaxCompressionRatio()) {
                throw new ZipBombException(
                    String.format("Suspicious compression ratio: %.2f:1 (max: %.2f:1)", 
                                compressionRatio, config.getMaxCompressionRatio()),
                    "HIGH_COMPRESSION_RATIO",
                    (long) (compressedSize * config.getMaxCompressionRatio()),
                    uncompressedSize
                );
            }
        }
    }

    /**
     * 安全的 ZIP 输入流
     *
     * <p>此输入流在读取过程中会监控已读取的字节数，
     * 如果超过配置的最大限制，将抛出 ZipBombException。</p>
     */
    public static class SafeZipInputStream extends InputStream {
        private final InputStream delegate;
        private final ZipEntry entry;
        private final ZipSafetyConfig config;
        private final AtomicLong bytesRead = new AtomicLong(0);
        private volatile boolean closed = false;

        SafeZipInputStream(InputStream delegate, ZipEntry entry, ZipSafetyConfig config) {
            this.delegate = delegate;
            this.entry = entry;
            this.config = config;
        }

        @Override
        public synchronized int read() throws IOException {
            if (closed) {
                throw new IOException("Stream is closed");
            }

            int b = delegate.read();
            if (b != -1) {
                bytesRead.incrementAndGet();
                checkSizeLimit();
            }
            return b;
        }

        @Override
        public synchronized int read(byte[] b) throws IOException {
            if (closed) {
                throw new IOException("Stream is closed");
            }

            int bytes = delegate.read(b);
            if (bytes > 0) {
                bytesRead.addAndGet(bytes);
                checkSizeLimit();
            }
            return bytes;
        }

        @Override
        public synchronized int read(byte[] b, int off, int len) throws IOException {
            if (closed) {
                throw new IOException("Stream is closed");
            }

            int bytes = delegate.read(b, off, len);
            if (bytes > 0) {
                bytesRead.addAndGet(bytes);
                checkSizeLimit();
            }
            return bytes;
        }

        @Override
        public synchronized long skip(long n) throws IOException {
            if (closed) {
                throw new IOException("Stream is closed");
            }

            long skipped = delegate.skip(n);
            if (skipped > 0) {
                bytesRead.addAndGet(skipped);
                checkSizeLimit();
            }
            return skipped;
        }

        @Override
        public int available() throws IOException {
            if (closed) {
                return 0;
            }
            return delegate.available();
        }

        @Override
        public synchronized void close() throws IOException {
            if (!closed) {
                delegate.close();
                closed = true;
            }
        }

        @Override
        public synchronized void mark(int readlimit) {
            if (!closed) {
                delegate.mark(readlimit);
            }
        }

        @Override
        public synchronized void reset() throws IOException {
            if (closed) {
                throw new IOException("Stream is closed");
            }
            delegate.reset();
        }

        @Override
        public boolean markSupported() {
            return !closed && delegate.markSupported();
        }

        /**
         * 检查是否超过大小限制
         *
         * @throws ZipBombException 如果超过限制
         */
        private void checkSizeLimit() throws ZipBombException {
            if (bytesRead.get() > config.getMaxEntrySize()) {
                throw new ZipBombException(
                    String.format("Decompressed size exceeds limit: %d bytes (max: %d bytes) for entry: %s",
                                bytesRead.get(), config.getMaxEntrySize(), entry.getName()),
                    "DECOMPRESSION_SIZE_EXCEEDED",
                    config.getMaxEntrySize(),
                    bytesRead.get()
                );
            }
        }

        /**
         * 获取已读取的字节数
         *
         * @return 已读取的字节数
         */
        public long getBytesRead() {
            return bytesRead.get();
        }

        /**
         * 获取 ZIP 条目信息
         * 注意：返回的是内部引用，调用者不应修改此对象
         *
         * @return ZIP 条目
         */
        @SuppressFBWarnings(value = "EI_EXPOSE_REP", 
                           justification = "ZipEntry is immutable in practice - only used for reading metadata")
        public ZipEntry getEntry() {
            return entry;
        }
    }
}
