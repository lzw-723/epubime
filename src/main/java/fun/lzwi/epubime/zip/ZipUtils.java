package fun.lzwi.epubime.zip;

import fun.lzwi.epubime.cache.EpubCacheManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * ZIP工具类
 * 提供读取ZIP文件、流式处理等操作，支持缓存和ZIP文件句柄复用
 */
public class ZipUtils {
    // 使用常量避免重复计算
    private static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();
    private static final int BUFFER_SIZE = 8192;
    private static final String LINE_SEPARATOR = System.lineSeparator();
    
    // 预分配的StringBuilder容量，避免动态扩容
    private static final int INITIAL_STRING_BUILDER_CAPACITY = 4096;

    /**
     * 获取ZIP文件中的文件列表
     *
     * @param zipFile ZIP文件
     * @return 文件名列表
     * @throws IOException IO异常
     */
    public static List<String> getZipFileList(File zipFile) throws IOException {
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        try {
            // 使用并行流提高性能（对于大文件）
            return zip.stream()
                     .map(ZipEntry::getName)
                     .collect(java.util.stream.Collectors.toList());
        } finally {
            // 释放ZIP文件句柄，减少引用计数
            ZipFileManager.getInstance().releaseZipFile();
        }
    }

    /**
     * 获取ZIP文件中的文本内容
     *
     * @param zipFile  ZIP文件
     * @param fileName 文件名
     * @return 文件内容，如果不存在返回null
     * @throws IOException IO异常
     */
    public static String getZipFileContent(File zipFile, String fileName) throws IOException {
        // 防止目录遍历攻击
        if (!PathValidator.isPathSafe("", fileName)) {
            throw new IOException("Invalid file path: " + fileName);
        }

        // 尝试从缓存获取
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(zipFile);
        String cachedContent = cache.getTextContent(fileName);
        
        if (cachedContent != null) {
            return cachedContent;
        }

        // 缓存未命中，从ZIP文件读取
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        ZipEntry entry = zip.getEntry(fileName);
        
        if (entry == null) {
            // 释放ZIP文件句柄，减少引用计数
            ZipFileManager.getInstance().releaseZipFile();
            return null;
        }

        try (InputStream in = zip.getInputStream(entry);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET))) {
            
            // 优化：预分配StringBuilder容量，避免动态扩容
            StringBuilder content = new StringBuilder(INITIAL_STRING_BUILDER_CAPACITY);
            char[] buffer = new char[BUFFER_SIZE];
            int charsRead;
            
            while ((charsRead = reader.read(buffer)) != -1) {
                content.append(buffer, 0, charsRead);
            }
            
            String result = content.toString();
            // 缓存结果
            cache.setTextContent(fileName, result);
            return result;
            
        } finally {
            // 释放ZIP文件句柄，减少引用计数
            ZipFileManager.getInstance().releaseZipFile();
        }
    }

    /**
     * 获取ZIP文件中的字节数组内容
     *
     * @param zipFile  ZIP文件
     * @param fileName 文件名
     * @return 文件内容字节数组，如果不存在返回null
     * @throws IOException IO异常
     */
    public static byte[] getZipFileBytes(File zipFile, String fileName) throws IOException {
        // 防止目录遍历攻击
        if (!PathValidator.isPathSafe("", fileName)) {
            throw new IOException("Invalid file path: " + fileName);
        }

        // 尝试从缓存获取
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(zipFile);
        byte[] cachedData = cache.getBinaryContent(fileName);
        
        if (cachedData != null) {
            return cachedData.clone(); // 返回克隆避免修改缓存数据
        }

        // 缓存未命中，从ZIP文件读取
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        ZipEntry entry = zip.getEntry(fileName);
        
        if (entry == null) {
            return null;
        }

        try (InputStream in = zip.getInputStream(entry);
             ByteArrayOutputStream out = new ByteArrayOutputStream((int) entry.getSize())) {
            
            // 优化：如果entry大小已知，直接预分配容量
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            
            byte[] data = out.toByteArray();
            // 缓存结果
            cache.setBinaryContent(fileName, data.clone());
            return data;
            
        } finally {
            // 注意：这里不关闭ZIP文件，因为它可能被复用
        }
    }

    /**
     * 流式处理ZIP文件中的内容，避免将整个文件加载到内存
     *
     * @param zipFile   ZIP文件
     * @param fileName  要处理的文件名
     * @param processor 处理输入流的消费者函数
     * @throws IOException IO异常
     */
    public static void processZipFileContent(File zipFile, String fileName, Consumer<InputStream> processor) throws IOException {
        // 防止目录遍历攻击
        if (!PathValidator.isPathSafe("", fileName)) {
            throw new IOException("Invalid file path: " + fileName);
        }

        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        ZipEntry entry = zip.getEntry(fileName);
        
        if (entry == null) {
            // 释放ZIP文件句柄，减少引用计数
            ZipFileManager.getInstance().releaseZipFile();
            return;
        }

        try (InputStream in = zip.getInputStream(entry)) {
            processor.accept(in);
        } finally {
            // 释放ZIP文件句柄，减少引用计数
            ZipFileManager.getInstance().releaseZipFile();
        }
    }

    /**
     * 获取ZIP文件中指定文件的输入流，用于流式处理
     *
     * @param zipFile  ZIP文件
     * @param fileName 要获取的文件名
     * @return 输入流，调用者需要关闭
     * @throws IOException IO异常
     */
    public static InputStream getZipFileInputStream(File zipFile, String fileName) throws IOException {
        // 防止目录遍历攻击
        if (!PathValidator.isPathSafe("", fileName)) {
            throw new IOException("Invalid file path: " + fileName);
        }

        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        ZipEntry entry = zip.getEntry(fileName);
        
        if (entry == null) {
            // 注意：这里不能关闭zip，因为它可能被复用
            return null;
        }

        return new ZipFileInputStream(zip.getInputStream(entry));
    }

    /**
     * 使用单个ZIP文件句柄批量读取多个文件内容
     *
     * @param zipFile   ZIP文件
     * @param fileNames 要读取的文件名列表
     * @return 文件名到内容的映射
     * @throws IOException IO异常
     */
    public static java.util.Map<String, String> getMultipleZipFileContents(File zipFile, List<String> fileNames) throws IOException {
        // 防止目录遍历攻击
        for (String fileName : fileNames) {
            if (!PathValidator.isPathSafe("", fileName)) {
                throw new IOException("Invalid file path: " + fileName);
            }
        }

        java.util.Map<String, String> contents = new java.util.HashMap<>((int) (fileNames.size() / 0.75f) + 1);
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        
        // 优化：预分配StringBuilder，避免重复创建
        StringBuilder contentBuilder = new StringBuilder(INITIAL_STRING_BUILDER_CAPACITY);
        char[] buffer = new char[BUFFER_SIZE];
        
        for (String fileName : fileNames) {
            ZipEntry entry = zip.getEntry(fileName);
            if (entry != null) {
                try (InputStream in = zip.getInputStream(entry); 
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET))) {
                    
                    contentBuilder.setLength(0); // 重置StringBuilder
                    int charsRead;
                    
                    while ((charsRead = reader.read(buffer)) != -1) {
                        contentBuilder.append(buffer, 0, charsRead);
                    }
                    
                    contents.put(fileName, contentBuilder.toString());
                }
            } else {
                contents.put(fileName, null);
            }
        }
        
        // 释放ZIP文件句柄，减少引用计数
        ZipFileManager.getInstance().releaseZipFile();
        return contents;
    }

    /**
     * 使用单个ZIP文件句柄批量读取多个文件字节数组
     *
     * @param zipFile   ZIP文件
     * @param fileNames 要读取的文件名列表
     * @return 文件名到字节数组的映射
     * @throws IOException IO异常
     */
    public static java.util.Map<String, byte[]> getMultipleZipFileBytes(File zipFile, List<String> fileNames) throws IOException {
        // 防止目录遍历攻击
        for (String fileName : fileNames) {
            if (!PathValidator.isPathSafe("", fileName)) {
                throw new IOException("Invalid file path: " + fileName);
            }
        }

        java.util.Map<String, byte[]> contents = new java.util.HashMap<>((int) (fileNames.size() / 0.75f) + 1);
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        
        byte[] buffer = new byte[BUFFER_SIZE];
        
        for (String fileName : fileNames) {
            ZipEntry entry = zip.getEntry(fileName);
            if (entry != null) {
                try (InputStream in = zip.getInputStream(entry); 
                     ByteArrayOutputStream out = new ByteArrayOutputStream((int) entry.getSize())) {
                    
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    
                    contents.put(fileName, out.toByteArray());
                }
            } else {
                contents.put(fileName, null);
            }
        }
        
        // 注意：这里不关闭ZIP文件，因为它可能被复用
        return contents;
    }

    /**
     * 流式处理HTML内容，避免将整个文件加载到内存
     *
     * @param zipFile      ZIP文件
     * @param htmlFileName HTML文件名
     * @param processor    处理HTML内容的消费者函数
     * @throws IOException IO异常
     */
    public static void processHtmlContent(File zipFile, String htmlFileName, Consumer<InputStream> processor) throws IOException {
        // 防止目录遍历攻击
        if (!PathValidator.isPathSafe("", htmlFileName)) {
            throw new IOException("Invalid file path: " + htmlFileName);
        }

        processZipFileContent(zipFile, htmlFileName, processor);
    }

    /**
     * 批量流式处理多个HTML文件内容
     *
     * @param zipFile       ZIP文件
     * @param htmlFileNames HTML文件名列表
     * @param processor     处理每个HTML内容的消费者函数
     * @throws IOException IO异常
     */
    public static void processMultipleHtmlContents(File zipFile, List<String> htmlFileNames, BiConsumer<String,
            InputStream> processor) throws IOException {
        // 防止目录遍历攻击
        for (String fileName : htmlFileNames) {
            if (!PathValidator.isPathSafe("", fileName)) {
                throw new IOException("Invalid file path: " + fileName);
            }
        }

        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        try {
            for (String fileName : htmlFileNames) {
                ZipEntry entry = zip.getEntry(fileName);
                if (entry != null) {
                    try (InputStream in = zip.getInputStream(entry)) {
                        processor.accept(fileName, in);
                    }
                }
            }
        } finally {
            // 释放ZIP文件句柄，减少引用计数
            ZipFileManager.getInstance().releaseZipFile();
        }
    }

    /**
     * 自定义输入流，自动释放ZIP文件句柄引用
     */
    private static class ZipFileInputStream extends InputStream {
        private final InputStream inputStream;

        public ZipFileInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return inputStream.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return inputStream.read(b, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            return inputStream.skip(n);
        }

        @Override
        public int available() throws IOException {
            return inputStream.available();
        }

        @Override
        public void close() throws IOException {
            try {
                inputStream.close();
            } finally {
                // 当流关闭时释放ZIP文件句柄引用
                ZipFileManager.getInstance().releaseZipFile();
            }
        }

        @Override
        public void mark(int readlimit) {
            inputStream.mark(readlimit);
        }

        @Override
        public void reset() throws IOException {
            inputStream.reset();
        }

        @Override
        public boolean markSupported() {
            return inputStream.markSupported();
        }
    }
}