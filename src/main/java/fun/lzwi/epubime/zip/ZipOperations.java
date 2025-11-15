package fun.lzwi.epubime.zip;

import fun.lzwi.epubime.cache.EpubCacheManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * ZIP操作工具类
 * 提供统一的ZIP文件操作方法，消除重复代码
 */
public class ZipOperations {
    
    // 使用常量避免重复计算
    public static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();
    public static final int BUFFER_SIZE = 8192;
    public static final int INITIAL_STRING_BUILDER_CAPACITY = 4096;
    
    /**
     * 验证文件路径安全性
     * 
     * @param fileName 文件名
     * @throws IOException 如果路径不安全
     */
    public static void validatePathSafety(String fileName) throws IOException {
        if (!PathValidator.isPathSafe("", fileName)) {
            throw new IOException("Invalid file path: " + fileName);
        }
    }
    
    /**
     * 验证多个文件路径安全性
     * 
     * @param fileNames 文件名列表
     * @throws IOException 如果有任何路径不安全
     */
    public static void validateMultiplePathsSafety(java.util.List<String> fileNames) throws IOException {
        for (String fileName : fileNames) {
            validatePathSafety(fileName);
        }
    }
    
    /**
     * 获取ZIP文件句柄
     * 
     * @param zipFile ZIP文件
     * @return ZIP文件句柄
     * @throws IOException IO异常
     */
    public static ZipFile getZipFile(File zipFile) throws IOException {
        return ZipFileManager.getInstance().getZipFile(zipFile);
    }
    
    /**
     * 释放ZIP文件句柄
     */
    public static void releaseZipFile() {
        ZipFileManager.getInstance().releaseZipFile();
    }
    
    /**
     * 获取ZIP条目
     * 
     * @param zipFile ZIP文件句柄
     * @param entryName 条目名称
     * @return ZIP条目，如果不存在返回null
     */
    public static ZipEntry getZipEntry(ZipFile zipFile, String entryName) {
        return zipFile.getEntry(entryName);
    }
    
    /**
     * 从缓存获取文本内容
     * 
     * @param zipFile ZIP文件
     * @param fileName 文件名
     * @return 缓存的文本内容，如果不存在返回null
     */
    public static String getCachedTextContent(File zipFile, String fileName) {
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(zipFile);
        return cache.getTextContent(fileName);
    }
    
    /**
     * 从缓存获取二进制内容
     * 
     * @param zipFile ZIP文件
     * @param fileName 文件名
     * @return 缓存的二进制内容，如果不存在返回null
     */
    public static byte[] getCachedBinaryContent(File zipFile, String fileName) {
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(zipFile);
        return cache.getBinaryContent(fileName);
    }
    
    /**
     * 缓存文本内容
     * 
     * @param zipFile ZIP文件
     * @param fileName 文件名
     * @param content 文本内容
     */
    public static void cacheTextContent(File zipFile, String fileName, String content) {
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(zipFile);
        cache.setTextContent(fileName, content);
    }
    
    /**
     * 缓存二进制内容
     * 
     * @param zipFile ZIP文件
     * @param fileName 文件名
     * @param data 二进制数据
     */
    public static void cacheBinaryContent(File zipFile, String fileName, byte[] data) {
        EpubCacheManager.EpubFileCache cache = EpubCacheManager.getInstance().getFileCache(zipFile);
        cache.setBinaryContent(fileName, data.clone());
    }
    
    /**
     * 从输入流读取文本内容
     * 
     * @param inputStream 输入流
     * @return 文本内容
     * @throws IOException IO异常
     */
    public static String readTextContent(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, DEFAULT_CHARSET))) {
            StringBuilder content = new StringBuilder(INITIAL_STRING_BUILDER_CAPACITY);
            char[] buffer = new char[BUFFER_SIZE];
            int charsRead;
            
            while ((charsRead = reader.read(buffer)) != -1) {
                content.append(buffer, 0, charsRead);
            }
            
            return content.toString();
        }
    }
    
    /**
     * 从输入流读取二进制内容
     * 
     * @param inputStream 输入流
     * @param expectedSize 预期大小（如果已知）
     * @return 二进制数据
     * @throws IOException IO异常
     */
    public static byte[] readBinaryContent(InputStream inputStream, long expectedSize) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(
                expectedSize > 0 && expectedSize < Integer.MAX_VALUE ? (int) expectedSize : BUFFER_SIZE)) {
            
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            
            return out.toByteArray();
        }
    }
    
    /**
     * 处理ZIP文件内容
     * 
     * @param zipFile ZIP文件
     * @param fileName 文件名
     * @param processor 内容处理器
     * @throws IOException IO异常
     */
    public static void processZipContent(File zipFile, String fileName, 
                                       java.util.function.Consumer<InputStream> processor) throws IOException {
        validatePathSafety(fileName);
        
        ZipFile zip = getZipFile(zipFile);
        ZipEntry entry = getZipEntry(zip, fileName);
        
        if (entry == null) {
            releaseZipFile();
            return;
        }

        try (InputStream in = zip.getInputStream(entry)) {
            processor.accept(in);
        } finally {
            releaseZipFile();
        }
    }
    
    /**
     * 获取ZIP文件输入流
     * 
     * @param zipFile ZIP文件
     * @param fileName 文件名
     * @return 输入流，如果不存在返回null
     * @throws IOException IO异常
     */
    public static InputStream getZipInputStream(File zipFile, String fileName) throws IOException {
        validatePathSafety(fileName);
        
        ZipFile zip = getZipFile(zipFile);
        ZipEntry entry = getZipEntry(zip, fileName);
        
        if (entry == null) {
            return null;
        }

        return zip.getInputStream(entry);
    }
}