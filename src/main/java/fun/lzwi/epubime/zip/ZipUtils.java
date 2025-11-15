package fun.lzwi.epubime.zip;

import java.io.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * ZIP工具类
 * 提供读取ZIP文件、流式处理等操作，支持缓存和ZIP文件句柄复用
 * 
 * 重构后使用ZipOperations工具类消除重复代码
 */
public class ZipUtils {
    
    /**
     * 获取ZIP文件中的文件列表
     *
     * @param zipFile ZIP文件
     * @return 文件名列表
     * @throws IOException IO异常
     */
    public static List<String> getZipFileList(File zipFile) throws IOException {
        ZipFile zip = ZipOperations.getZipFile(zipFile);
        try {
            // 使用并行流提高性能（对于大文件）
            return zip.stream()
                     .map(ZipEntry::getName)
                     .collect(java.util.stream.Collectors.toList());
        } finally {
            // 释放ZIP文件句柄，减少引用计数
            ZipOperations.releaseZipFile();
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
        // 尝试从缓存获取
        String cachedContent = ZipOperations.getCachedTextContent(zipFile, fileName);
        
        if (cachedContent != null) {
            return cachedContent;
        }

        // 缓存未命中，从ZIP文件读取
        ZipFile zip = ZipOperations.getZipFile(zipFile);
        ZipEntry entry = ZipOperations.getZipEntry(zip, fileName);
        
        if (entry == null) {
            // 释放ZIP文件句柄，减少引用计数
            ZipOperations.releaseZipFile();
            return null;
        }

        try (InputStream in = zip.getInputStream(entry)) {
            String result = ZipOperations.readTextContent(in);
            // 缓存结果
            ZipOperations.cacheTextContent(zipFile, fileName, result);
            return result;
        } finally {
            // 释放ZIP文件句柄，减少引用计数
            ZipOperations.releaseZipFile();
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
        // 尝试从缓存获取
        byte[] cachedData = ZipOperations.getCachedBinaryContent(zipFile, fileName);
        
        if (cachedData != null) {
            return cachedData.clone(); // 返回克隆避免修改缓存数据
        }

        // 缓存未命中，从ZIP文件读取
        ZipFile zip = ZipOperations.getZipFile(zipFile);
        ZipEntry entry = ZipOperations.getZipEntry(zip, fileName);
        
        if (entry == null) {
            return null;
        }

        try (InputStream in = zip.getInputStream(entry)) {
            byte[] data = ZipOperations.readBinaryContent(in, entry.getSize());
            // 缓存结果
            ZipOperations.cacheBinaryContent(zipFile, fileName, data.clone());
            return data;
        }
        // 注意：这里不关闭ZIP文件，因为它可能被复用
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
        ZipOperations.processZipContent(zipFile, fileName, processor);
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
        return ZipOperations.getZipInputStream(zipFile, fileName);
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
        ZipOperations.validateMultiplePathsSafety(fileNames);

        java.util.Map<String, String> contents = new java.util.HashMap<>((int) (fileNames.size() / 0.75f) + 1);
        ZipFile zip = ZipOperations.getZipFile(zipFile);
        
        // 优化：预分配StringBuilder，避免重复创建
        StringBuilder contentBuilder = new StringBuilder(ZipOperations.INITIAL_STRING_BUILDER_CAPACITY);
        char[] buffer = new char[ZipOperations.BUFFER_SIZE];
        
        for (String fileName : fileNames) {
            ZipEntry entry = ZipOperations.getZipEntry(zip, fileName);
            if (entry != null) {
                try (InputStream in = zip.getInputStream(entry); 
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in, ZipOperations.DEFAULT_CHARSET))) {
                    
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
        ZipOperations.releaseZipFile();
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
        ZipOperations.validateMultiplePathsSafety(fileNames);

        java.util.Map<String, byte[]> contents = new java.util.HashMap<>((int) (fileNames.size() / 0.75f) + 1);
        ZipFile zip = ZipOperations.getZipFile(zipFile);
        
        byte[] buffer = new byte[ZipOperations.BUFFER_SIZE];
        
        for (String fileName : fileNames) {
            ZipEntry entry = ZipOperations.getZipEntry(zip, fileName);
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
        ZipOperations.validateMultiplePathsSafety(htmlFileNames);

        ZipFile zip = ZipOperations.getZipFile(zipFile);
        try {
            for (String fileName : htmlFileNames) {
                ZipEntry entry = ZipOperations.getZipEntry(zip, fileName);
                if (entry != null) {
                    try (InputStream in = zip.getInputStream(entry)) {
                        processor.accept(fileName, in);
                    }
                }
            }
        } finally {
            // 释放ZIP文件句柄，减少引用计数
            ZipOperations.releaseZipFile();
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
                ZipOperations.releaseZipFile();
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