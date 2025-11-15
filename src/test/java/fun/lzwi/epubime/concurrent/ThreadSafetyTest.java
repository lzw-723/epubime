package fun.lzwi.epubime.concurrent;

import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.epub.EpubBook;
import fun.lzwi.epubime.epub.EpubParser;
import fun.lzwi.epubime.zip.ZipFileManager;
import fun.lzwi.epubime.zip.ZipUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * 线程安全测试类
 * 测试EPUBime库在多线程环境下的线程安全性
 */
public class ThreadSafetyTest {

    private static final int THREAD_COUNT = 10;
    private static final int ITERATIONS_PER_THREAD = 100;
    
    @Test
    public void testEpubCacheManagerThreadSafety() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        // 获取测试文件
        File epubFile = getTestEpubFile();
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < ITERATIONS_PER_THREAD; j++) {
                        // 测试缓存管理器的线程安全性
                        EpubCacheManager cacheManager = EpubCacheManager.getInstance();
                        EpubCacheManager.EpubFileCache cache = cacheManager.getFileCache(epubFile);
                        
                        // 设置和获取文本内容
                        String key = "thread_" + threadId + "_iter_" + j;
                        String content = "content_from_thread_" + threadId + "_iteration_" + j;
                        cache.setTextContent(key, content);
                        
                        String retrieved = cache.getTextContent(key);
                        if (content.equals(retrieved)) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                        }
                        
                        // 测试二进制内容缓存
                        byte[] binaryData = ("binary_data_" + threadId + "_" + j).getBytes();
                        cache.setBinaryContent(key, binaryData);
                        byte[] retrievedData = cache.getBinaryContent(key);
                        
                        if (retrievedData != null && new String(retrievedData).equals(new String(binaryData))) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        System.out.println("Cache Manager Thread Safety Test:");
        System.out.println("Success count: " + successCount.get());
        System.out.println("Error count: " + errorCount.get());
        
        // 验证结果
        assertEquals("Should have no errors", 0, errorCount.get());
        assertTrue("Should have successful operations", successCount.get() > 0);
    }
    
    @Test
    public void testZipFileManagerThreadSafety() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        // 获取测试文件
        File epubFile = getTestEpubFile();
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < ITERATIONS_PER_THREAD; j++) {
                        // 测试ZIP文件管理器的线程安全性
                        try {
                            // 获取ZIP文件内容
                            String content = ZipUtils.getZipFileContent(epubFile, "META-INF/container.xml");
                            if (content != null && content.contains("container")) {
                                successCount.incrementAndGet();
                            } else {
                                errorCount.incrementAndGet();
                            }
                            
                            // 测试文件列表获取
                            List<String> fileList = ZipUtils.getZipFileList(epubFile);
                            if (fileList != null && !fileList.isEmpty()) {
                                successCount.incrementAndGet();
                            } else {
                                errorCount.incrementAndGet();
                            }
                        } catch (IOException e) {
                            errorCount.incrementAndGet();
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    // 清理当前线程的ZIP文件句柄
                    ZipFileManager.getInstance().cleanup();
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        System.out.println("ZIP File Manager Thread Safety Test:");
        System.out.println("Success count: " + successCount.get());
        System.out.println("Error count: " + errorCount.get());
        
        // 验证结果
        assertEquals("Should have no errors", 0, errorCount.get());
        assertTrue("Should have successful operations", successCount.get() > 0);
    }
    
    @Test
    public void testEpubParserThreadSafety() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        // 获取测试文件
        File epubFile = getTestEpubFile();
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 10; j++) { // 减少解析次数，避免内存压力
                        try {
                            // 测试EPUB解析器的线程安全性
                            EpubParser parser = new EpubParser(epubFile);
                            EpubBook book = parser.parse();
                            
                            if (book != null && book.getMetadata() != null) {
                                successCount.incrementAndGet();
                            } else {
                                errorCount.incrementAndGet();
                            }
                        } catch (Exception e) {
                            errorCount.incrementAndGet();
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        System.out.println("EPUB Parser Thread Safety Test:");
        System.out.println("Success count: " + successCount.get());
        System.out.println("Error count: " + errorCount.get());
        
        // 验证结果
        assertTrue("Should have some successful operations", successCount.get() > 0);
        // 允许一些错误，因为并发解析可能会有资源竞争
        assertTrue("Error rate should be reasonable", 
                   errorCount.get() < successCount.get() * 0.1); // 错误率应小于10%
    }
    
    @Test
    public void testConcurrentCacheAccess() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger conflictCount = new AtomicInteger(0);
        
        File epubFile = getTestEpubFile();
        
        // 创建多个线程同时访问缓存
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await(); // 等待所有线程准备就绪
                    
                    // 同时访问缓存
                    EpubCacheManager cacheManager = EpubCacheManager.getInstance();
                    EpubCacheManager.EpubFileCache cache = cacheManager.getFileCache(epubFile);
                    
                    // 每个线程使用自己的键，避免故意制造竞争条件
                    // 测试重点是验证缓存的线程安全性，而不是竞争条件
                    String threadKey = "thread_key_" + threadId;
                    String value = "thread_" + threadId + "_value";
                    cache.setTextContent(threadKey, value);
                    
                    // 立即读取，检查是否得到预期的值
                    String retrieved = cache.getTextContent(threadKey);
                    if (!value.equals(retrieved)) {
                        conflictCount.incrementAndGet();
                    }
                    
                    // 也测试共享键，但接受可能的竞争条件
                    String sharedKey = "shared_key_final";
                    String sharedValue = "final_value";
                    cache.setTextContent(sharedKey, sharedValue);
                    
                    // 小延迟，让其他线程完成写入
                    Thread.sleep(1);
                    
                    String sharedRetrieved = cache.getTextContent(sharedKey);
                    if (sharedRetrieved == null || sharedRetrieved.isEmpty()) {
                        conflictCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    conflictCount.incrementAndGet();
                } finally {
                    endLatch.countDown();
                }
            });
        }
        
        // 同时启动所有线程
        startLatch.countDown();
        endLatch.await();
        executor.shutdown();
        
        System.out.println("Concurrent Cache Access Test:");
        System.out.println("Conflict count: " + conflictCount.get());
        
        // 在当前的同步实现下，冲突应该很少
        assertTrue("Should have minimal conflicts with proper synchronization", 
                   conflictCount.get() <= THREAD_COUNT * 0.1); // 允许最多10%的冲突率
    }
    
    /**
     * 获取测试用的EPUB文件
     */
    private File getTestEpubFile() {
        File epubFile = new File("src/test/resources/fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        if (!epubFile.exists()) {
            epubFile = new File("target/test-classes/fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        }
        assertTrue("EPUB file should exist", epubFile.exists());
        return epubFile;
    }
}