package fun.lzwi.epubime.zip;

import fun.lzwi.epubime.cache.EpubCacheManager;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

/**
 * ZIP工具类
 * 提供对ZIP文件的读取、流式处理等操作，支持缓存和ZIP文件句柄重用
 */
public class ZipUtils {
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * 获取ZIP文件中的文件列表
     * @param zipFile ZIP文件
     * @return 文件名列表
     * @throws IOException IO异常
     */
    public static List<String> getZipFileList(File zipFile) throws IOException {
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        try {
            return zip.stream().map(ZipEntry::getName).collect(java.util.stream.Collectors.toList());
        } finally {
            // 释放ZIP文件句柄，减少引用计数
            ZipFileManager.getInstance().releaseZipFile();
        }
    }

    /**
     * 获取ZIP文件中的文本内容
     * @param zipFile ZIP文件
     * @param fileName 文件名
     * @return 文件内容，如果不存在则返回null
     * @throws IOException IO异常
     */
    public static String getZipFileContent(File zipFile, String fileName) throws IOException {
        // Try to get from cache
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(zipFile);
        String cachedContent = cache.getTextContent(fileName);
        if (cachedContent != null) {
            return cachedContent;
        }
        
        // Cache miss, read from ZIP file
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        ZipEntry entry = zip.getEntry(fileName);
        if (entry == null) {
            // 释放ZIP文件句柄，减少引用计数
            ZipFileManager.getInstance().releaseZipFile();
            return null;
        }
        try (InputStream in = zip.getInputStream(entry); BufferedReader reader =
                new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET))) {
            String content = reader.lines().collect(java.util.stream.Collectors.joining(System.lineSeparator()));
            // Cache result
            cache.setTextContent(fileName, content);
            return content;
        } finally {
            // 释放ZIP文件句柄，减少引用计数
        ZipFileManager.getInstance().releaseZipFile();
        }
    }

    /**
     * 获取ZIP文件中的字节数组内容
     * @param zipFile ZIP文件
     * @param fileName 文件名
     * @return 文件内容字节数组，如果不存在则返回null
     * @throws IOException IO异常
     */
    public static byte[] getZipFileBytes(File zipFile, String fileName) throws IOException {
        // Try to get from cache
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(zipFile);
        byte[] cachedData = cache.getBinaryContent(fileName);
        if (cachedData != null) {
            return cachedData.clone(); // Return clone to avoid modifying cache data
        }
        
        // Cache miss, read from ZIP file
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        ZipEntry entry = zip.getEntry(fileName);
        if (entry == null) {
            return null;
        }
        try (InputStream in = zip.getInputStream(entry);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            byte[] data = out.toByteArray();
            // Cache result
            cache.setBinaryContent(fileName, data.clone());
            return data;
        } finally {
            // Note: We don't close the ZIP file because it may be reused
        }
    }

    /**
     * 流式处理ZIP文件中的内容以避免将整个文件加载到内存中
     * @param zipFile ZIP文件
     * @param fileName 要处理的文件名
     * @param processor 消费者函数，用于处理输入流
     * @throws IOException IO异常
     */
    public static void processZipFileContent(File zipFile, String fileName, Consumer<InputStream> processor) throws IOException {
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
            // Note: We don't close the ZIP file because it may be reused
        }
    }

    /**
     * 获取ZIP文件中指定文件的输入流，用于流式处理
     * @param zipFile ZIP文件
     * @param fileName 要获取的文件名
     * @return 输入流，调用者需要负责关闭
     * @throws IOException IO异常
     */
    public static InputStream getZipFileInputStream(File zipFile, String fileName) throws IOException {
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        ZipEntry entry = zip.getEntry(fileName);
        if (entry == null) {
            // Note: We can't close zip here because it may be reused
            return null;
        }
        return new ZipFileInputStream(zip, zip.getInputStream(entry));
    }

    /**
     * 使用单个ZIP文件句柄批量读取多个文件内容
     * @param zipFile ZIP文件
     * @param fileNames 要读取的文件名列表
     * @return 文件名到内容的映射
     * @throws IOException IO异常
     */
    public static java.util.Map<String, String> getMultipleZipFileContents(File zipFile, List<String> fileNames) throws IOException {
        java.util.Map<String, String> contents = new java.util.HashMap<>();
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        for (String fileName : fileNames) {
            ZipEntry entry = zip.getEntry(fileName);
            if (entry != null) {
                try (InputStream in = zip.getInputStream(entry); BufferedReader reader =
                        new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET))) {
                    String content = reader.lines().collect(java.util.stream.Collectors.joining(System.lineSeparator()));
                    contents.put(fileName, content);
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
     * 使用单个ZIP文件句柄批量读取多个文件的字节数组
     * @param zipFile ZIP文件
     * @param fileNames 要读取的文件名列表
     * @return 文件名到字节数组的映射
     * @throws IOException IO异常
     */
    public static java.util.Map<String, byte[]> getMultipleZipFileBytes(File zipFile, List<String> fileNames) throws IOException {
        java.util.Map<String, byte[]> contents = new java.util.HashMap<>();
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        for (String fileName : fileNames) {
            ZipEntry entry = zip.getEntry(fileName);
            if (entry != null) {
                try (InputStream in = zip.getInputStream(entry);
                     ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[8192];
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
        // Note: We don't close the ZIP file because it may be reused
        return contents;
    }
    
    /**
     * 流式处理HTML内容以避免将整个文件加载到内存中
     * @param zipFile ZIP文件
     * @param htmlFileName HTML文件名
     * @param processor 消费者函数，用于处理HTML内容
     * @throws IOException IO异常
     */
    public static void processHtmlContent(File zipFile, String htmlFileName, Consumer<InputStream> processor) throws IOException {
        processZipFileContent(zipFile, htmlFileName, processor);
    }

    /**
     * 批量流式处理多个HTML文件内容
     * @param zipFile ZIP文件
     * @param htmlFileNames HTML文件名列表
     * @param processor 消费者函数，用于处理每个HTML内容
     * @throws IOException IO异常
     */
    public static void processMultipleHtmlContents(File zipFile, List<String> htmlFileNames, 
                                                   BiConsumer<String, InputStream> processor) throws IOException {
        ZipFile zip = ZipFileManager.getInstance().getZipFile(zipFile);
        for (String fileName : htmlFileNames) {
            ZipEntry entry = zip.getEntry(fileName);
            if (entry != null) {
                try (InputStream in = zip.getInputStream(entry)) {
                    processor.accept(fileName, in);
                } finally {
                    // 释放ZIP文件句柄，减少引用计数
                }
            }
        }
        // 释放ZIP文件句柄，减少引用计数
        ZipFileManager.getInstance().releaseZipFile();
    }

    /**
     * 自定义输入流，自动释放ZIP文件句柄引用
     */
    private static class ZipFileInputStream extends InputStream {
        private final InputStream inputStream;

        public ZipFileInputStream(ZipFile zipFile, InputStream inputStream) {
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
                // 在流关闭时释放ZIP文件句柄引用
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