package fun.lzwi.epubime.zip;

import fun.lzwi.epubime.cache.EpubCacheManager;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

public class ZipUtils {
    private static final String DEFAULT_CHARSET = "UTF-8";

    public static List<String> getZipFileList(File zipFile) throws IOException {
        try (ZipFile zip = new ZipFile(zipFile)) {
            return zip.stream().map(ZipEntry::getName).collect(java.util.stream.Collectors.toList());
        }
    }

    public static String getZipFileContent(File zipFile, String fileName) throws IOException {
        // 尝试从缓存获取
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(zipFile);
        String cachedContent = cache.getTextContentCache().get(fileName);
        if (cachedContent != null) {
            return cachedContent;
        }
        
        // 缓存未命中，从ZIP文件读取
        try (ZipFile zip = new ZipFile(zipFile)) {
            ZipEntry entry = zip.getEntry(fileName);
            if (entry == null) {
                return null;
            }
            try (InputStream in = zip.getInputStream(entry); BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET))) {
                String content = reader.lines().collect(java.util.stream.Collectors.joining(System.lineSeparator()));
                // 缓存结果
                cache.getTextContentCache().put(fileName, content);
                return content;
            }
        }
    }

    public static byte[] getZipFileBytes(File zipFile, String fileName) throws IOException {
        // 尝试从缓存获取
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(zipFile);
        byte[] cachedData = cache.getBinaryContentCache().get(fileName);
        if (cachedData != null) {
            return cachedData.clone(); // 返回克隆以避免修改缓存数据
        }
        
        // 缓存未命中，从ZIP文件读取
        try (ZipFile zip = new ZipFile(zipFile)) {
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
                // 缓存结果
                cache.getBinaryContentCache().put(fileName, data.clone());
                return data;
            }
        }
    }

    /**
     * 流式处理ZIP文件中的内容，避免将整个文件加载到内存中
     * @param zipFile ZIP文件
     * @param fileName 要处理的文件名
     * @param processor 处理输入流的消费者函数
     * @throws IOException
     */
    public static void processZipFileContent(File zipFile, String fileName, Consumer<InputStream> processor) throws IOException {
        try (ZipFile zip = new ZipFile(zipFile)) {
            ZipEntry entry = zip.getEntry(fileName);
            if (entry == null) {
                return;
            }
            try (InputStream in = zip.getInputStream(entry)) {
                processor.accept(in);
            }
        }
    }

    /**
     * 获取ZIP文件中指定文件的输入流，用于流式处理
     * @param zipFile ZIP文件
     * @param fileName 要获取的文件名
     * @return 输入流，调用方需要负责关闭
     * @throws IOException
     */
    public static InputStream getZipFileInputStream(File zipFile, String fileName) throws IOException {
        ZipFile zip = new ZipFile(zipFile);
        ZipEntry entry = zip.getEntry(fileName);
        if (entry == null) {
            zip.close();
            return null;
        }
        return new ZipFileInputStream(zip, zip.getInputStream(entry));
    }

    /**
     * 使用单个ZIP文件句柄批量读取多个文件内容
     * @param zipFile ZIP文件
     * @param fileNames 要读取的文件名列表
     * @return 文件名到内容的映射
     * @throws IOException
     */
    public static java.util.Map<String, String> getMultipleZipFileContents(File zipFile, List<String> fileNames) throws IOException {
        java.util.Map<String, String> contents = new java.util.HashMap<>();
        try (ZipFile zip = new ZipFile(zipFile)) {
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
        }
        return contents;
    }

    /**
     * 使用单个ZIP文件句柄批量读取多个文件的字节数组
     * @param zipFile ZIP文件
     * @param fileNames 要读取的文件名列表
     * @return 文件名到字节数组的映射
     * @throws IOException
     */
    public static java.util.Map<String, byte[]> getMultipleZipFileBytes(File zipFile, List<String> fileNames) throws IOException {
        java.util.Map<String, byte[]> contents = new java.util.HashMap<>();
        try (ZipFile zip = new ZipFile(zipFile)) {
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
        }
        return contents;
    }
    
    /**
     * 流式处理HTML内容，避免将整个文件加载到内存中
     * @param zipFile ZIP文件
     * @param htmlFileName HTML文件名
     * @param processor 处理HTML内容的消费者函数
     * @throws IOException
     */
    public static void processHtmlContent(File zipFile, String htmlFileName, Consumer<InputStream> processor) throws IOException {
        processZipFileContent(zipFile, htmlFileName, processor);
    }

    /**
     * 批量流式处理多个HTML文件内容
     * @param zipFile ZIP文件
     * @param htmlFileNames HTML文件名列表
     * @param processor 处理每个HTML内容的消费者函数
     * @throws IOException
     */
    public static void processMultipleHtmlContents(File zipFile, List<String> htmlFileNames, 
                                                   BiConsumer<String, InputStream> processor) throws IOException {
        try (ZipFile zip = new ZipFile(zipFile)) {
            for (String fileName : htmlFileNames) {
                ZipEntry entry = zip.getEntry(fileName);
                if (entry != null) {
                    try (InputStream in = zip.getInputStream(entry)) {
                        processor.accept(fileName, in);
                    }
                }
            }
        }
    }

    /**
     * 自定义输入流，自动关闭ZipFile
     */
    private static class ZipFileInputStream extends InputStream {
        private final ZipFile zipFile;
        private final InputStream inputStream;

        public ZipFileInputStream(ZipFile zipFile, InputStream inputStream) {
            this.zipFile = zipFile;
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
                zipFile.close();
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