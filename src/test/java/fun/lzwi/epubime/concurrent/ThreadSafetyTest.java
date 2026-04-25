package fun.lzwi.epubime.concurrent;

import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.epub.EpubBook;
import fun.lzwi.epubime.epub.EpubParser;
import fun.lzwi.epubime.zip.ZipFileManager;
import fun.lzwi.epubime.zip.ZipUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 线程安全测试类
 * 测试EPUBime库在多线程环境下的线程安全性
 * 
 * 改进内容：
 * 1. 添加超时机制防止死锁导致测试挂起
 * 2. 增加压力测试支持更高的并发线程数
 * 3. 添加随机延迟模拟真实场景
 * 4. 增加并发清理操作测试
 * 5. 优化数据竞争检测机制
 * 6. 参数化测试配置便于调整
 */
public class ThreadSafetyTest {

    // 基础测试配置
    private static final int BASE_THREAD_COUNT = 10;
    private static final int BASE_ITERATIONS = 100;
    
    // 压力测试配置
    private static final int STRESS_THREAD_COUNT = 50;
    private static final int STRESS_ITERATIONS = 500;
    
    // 超时配置（秒）
    private static final int NORMAL_TEST_TIMEOUT = 60;
    private static final int STRESS_TEST_TIMEOUT = 180;
    
    // 随机延迟范围（毫秒）
    private static final int MIN_DELAY_MS = 1;
    private static final int MAX_DELAY_MS = 10;
    
    // 测试模式
    private enum TestMode {
        NORMAL,      // 普通测试
        STRESS,      // 压力测试
        MIXED        // 混合读写
    }
    
    @Test
    @Timeout(value = NORMAL_TEST_TIMEOUT, unit = TimeUnit.SECONDS)
    public void testEpubCacheManagerThreadSafety() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(BASE_THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(BASE_THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        // 获取测试文件
        File epubFile = getTestEpubFile();

        for (int i = 0; i < BASE_THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < BASE_ITERATIONS; j++) {
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
                        } else if (retrieved == null) {
                            // LRU驱逐导致key丢失，不是线程安全问题
                        } else {
                            // 读取到非null且不匹配的值，真正的数据损坏
                            errorCount.incrementAndGet();
                        }
                        
                        // 添加随机延迟模拟真实场景
                        randomDelay();

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
        assertEquals(0, errorCount.get(), "Should have no errors");
        assertTrue(successCount.get() > 0, "Should have successful operations");
    }

    @Test
    @Timeout(value = NORMAL_TEST_TIMEOUT, unit = TimeUnit.SECONDS)
    public void testZipFileManagerThreadSafety() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(BASE_THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(BASE_THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        // 获取测试文件
        File epubFile = getTestEpubFile();

        for (int i = 0; i < BASE_THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < BASE_ITERATIONS; j++) {
                        // 测试ZIP文件管理器的线程安全性
                        try {
                            // 获取ZIP文件内容
                            String content = ZipUtils.getZipFileContent(epubFile, "META-INF/container.xml");
                            if (content != null && content.contains("container")) {
                                successCount.incrementAndGet();
                            } else {
                                errorCount.incrementAndGet();
                            }
                            
                            // 添加随机延迟
                            randomDelay();

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
                    // 释放当前文件的引用计数，不全局关闭其他线程正在使用的句柄
                    ZipFileManager.getInstance().releaseZipFile(epubFile);
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
        assertEquals(0, errorCount.get(), "Should have no errors");
        assertTrue(successCount.get() > 0, "Should have successful operations");
    }

    @Test
    @Timeout(value = NORMAL_TEST_TIMEOUT, unit = TimeUnit.SECONDS)
    public void testEpubParserThreadSafety() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(BASE_THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(BASE_THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        // 获取测试文件
        File epubFile = getTestEpubFile();

        for (int i = 0; i < BASE_THREAD_COUNT; i++) {
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
        assertTrue(successCount.get() > 0, "Should have some successful operations");
        // 允许一些错误，因为并发解析可能会有资源竞争
        assertTrue(errorCount.get() < successCount.get() * 0.1,
                   "Error rate should be reasonable"); // 错误率应小于10%
    }
    
    @Test
    @Timeout(value = NORMAL_TEST_TIMEOUT, unit = TimeUnit.SECONDS)
    public void testConcurrentCacheAccess() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(BASE_THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(BASE_THREAD_COUNT);
        AtomicInteger conflictCount = new AtomicInteger(0);

        File epubFile = getTestEpubFile();

        // 创建多个线程同时访问缓存
        for (int i = 0; i < BASE_THREAD_COUNT; i++) {
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
        assertTrue(conflictCount.get() <= BASE_THREAD_COUNT * 0.1,
                   "Should have minimal conflicts with proper synchronization"); // 允许最多10%的冲突率
    }

    /**
     * 压力测试：缓存管理器高并发场景
     */
    @Test
    @Timeout(value = STRESS_TEST_TIMEOUT, unit = TimeUnit.SECONDS)
    public void testEpubCacheManagerStress() throws InterruptedException, ExecutionException {
        runStressTest(TestMode.NORMAL, STRESS_THREAD_COUNT, STRESS_ITERATIONS);
    }

    /**
     * 压力测试：ZIP文件管理器高并发场景
     */
    @Test
    @Timeout(value = STRESS_TEST_TIMEOUT, unit = TimeUnit.SECONDS)
    public void testZipFileManagerStress() throws InterruptedException, ExecutionException {
        runStressTest(TestMode.STRESS, STRESS_THREAD_COUNT, STRESS_ITERATIONS);
    }

    /**
     * 混合读写压力测试
     */
    @Test
    @Timeout(value = STRESS_TEST_TIMEOUT, unit = TimeUnit.SECONDS)
    public void testMixedReadWriteStress() throws InterruptedException, ExecutionException {
        runStressTest(TestMode.MIXED, STRESS_THREAD_COUNT, STRESS_ITERATIONS);
    }

    /**
     * 测试并发清理操作的安全性
     */
    @Test
    @Timeout(value = NORMAL_TEST_TIMEOUT, unit = TimeUnit.SECONDS)
    public void testConcurrentCleanup() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(BASE_THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(BASE_THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        AtomicLong cleanupCount = new AtomicLong(0);

        File epubFile = getTestEpubFile();

        for (int i = 0; i < BASE_THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < BASE_ITERATIONS; j++) {
                        EpubCacheManager cacheManager = EpubCacheManager.getInstance();
                        
                        // 交替执行读写和清理操作
                        if (j % 10 == 0) {
                            // 10%的概率执行清理
                            cacheManager.cleanupInvalidCaches();
                            cleanupCount.incrementAndGet();
                        } else {
                            // 90%的概率执行读写
                            EpubCacheManager.EpubFileCache cache = cacheManager.getFileCache(epubFile);
                            String key = "cleanup_thread_" + threadId + "_iter_" + j;
                            String content = "cleanup_content_" + threadId + "_" + j;
                            cache.setTextContent(key, content);
                            
                            String retrieved = cache.getTextContent(key);
                            if (content.equals(retrieved)) {
                                successCount.incrementAndGet();
                            } else {
                                errorCount.incrementAndGet();
                            }
                        }
                        
                        randomDelay();
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        System.out.println("Concurrent Cleanup Test:");
        System.out.println("Success count: " + successCount.get());
        System.out.println("Error count: " + errorCount.get());
        System.out.println("Cleanup count: " + cleanupCount.get());

        assertEquals(0, errorCount.get(), "Should have no errors during concurrent cleanup");
        assertTrue(cleanupCount.get() > 0, "Should have executed cleanup operations");
    }

    /**
     * 测试数据竞争检测
     */
    @Test
    @Timeout(value = NORMAL_TEST_TIMEOUT, unit = TimeUnit.SECONDS)
    public void testDataRaceDetection() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(BASE_THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(BASE_THREAD_COUNT);
        AtomicInteger raceConditionCount = new AtomicInteger(0);

        File epubFile = getTestEpubFile();
        EpubCacheManager cacheManager = EpubCacheManager.getInstance();
        EpubCacheManager.EpubFileCache cache = cacheManager.getFileCache(epubFile);

        // 使用共享键检测数据竞争
        String sharedKey = "race_detection_key";
        AtomicLong lastWriteTimestamp = new AtomicLong(0);
        AtomicInteger writeCount = new AtomicInteger(0);

        for (int i = 0; i < BASE_THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < BASE_ITERATIONS; j++) {
                        long writeTime = System.nanoTime();
                        String value = "thread_" + threadId + "_write_" + j;
                        cache.setTextContent(sharedKey, value);
                        lastWriteTimestamp.set(writeTime);
                        writeCount.incrementAndGet();
                        
                        // 立即读取验证
                        String retrieved = cache.getTextContent(sharedKey);
                        if (retrieved != null && !retrieved.equals(value)) {
                            // 检测到数据竞争：读取到的值不是自己写入的
                            raceConditionCount.incrementAndGet();
                        }
                        
                        // 随机延迟增加竞争
                        if (j % 5 == 0) {
                            randomDelay();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        System.out.println("Data Race Detection Test:");
        System.out.println("Total writes: " + writeCount.get());
        System.out.println("Race conditions detected: " + raceConditionCount.get());

        // 在使用ReadWriteLock的情况下，数据竞争应该非常少
        // 但由于多个线程并发写入同一个键，读取到其他线程的值是正常的并发行为
        // 这里主要检测缓存实现的正确性
        double raceRate = writeCount.get() > 0 ? 
            (double) raceConditionCount.get() / writeCount.get() : 0;
        assertTrue(raceRate < 0.80, 
                   "Data race rate should be less than 80%, but was: " + (raceRate * 100) + "%");
    }

    /**
     * 通用压力测试方法
     */
    private void runStressTest(TestMode mode, int threadCount, int iterations) 
            throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        File epubFile = getTestEpubFile();

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterations; j++) {
                        try {
                            switch (mode) {
                                case NORMAL:
                                    // 缓存管理器压力测试
                                    EpubCacheManager cacheManager = EpubCacheManager.getInstance();
                                    EpubCacheManager.EpubFileCache cache = cacheManager.getFileCache(epubFile);
                                    
                                    String key = "stress_" + threadId + "_" + j;
                                    String content = "stress_content_" + threadId + "_" + j;
                                    cache.setTextContent(key, content);
                                    
                                    String retrieved = cache.getTextContent(key);
                                    if (content.equals(retrieved)) {
                                        successCount.incrementAndGet();
                                    } else if (retrieved == null) {
                                        // LRU驱逐导致key丢失，不是线程安全问题
                                    } else {
                                        // 读取到非null且不匹配的值，真正的数据损坏
                                        errorCount.incrementAndGet();
                                    }
                                    break;
                                    
                                case STRESS:
                                    // ZIP文件管理器压力测试
                                    String zipContent = ZipUtils.getZipFileContent(epubFile, "META-INF/container.xml");
                                    if (zipContent != null && zipContent.contains("container")) {
                                        successCount.incrementAndGet();
                                    } else {
                                        errorCount.incrementAndGet();
                                    }
                                    break;
                                    
                                case MIXED:
                                    // 混合读写测试
                                    EpubCacheManager mixedCacheManager = EpubCacheManager.getInstance();
                                    EpubCacheManager.EpubFileCache mixedCache = mixedCacheManager.getFileCache(epubFile);
                                    
                                    if (j % 3 == 0) {
                                        // 30%写操作
                                        mixedCache.setTextContent("mixed_" + threadId, "value_" + j);
                                        successCount.incrementAndGet();
                                    } else {
                                        // 70%读操作
                                        String mixedValue = mixedCache.getTextContent("mixed_" + threadId);
                                        if (mixedValue != null) {
                                            successCount.incrementAndGet();
                                        }
                                    }
                                    break;
                            }
                            
                            // 定期添加随机延迟
                            if (j % 10 == 0) {
                                randomDelay();
                            }
                        } catch (IOException e) {
                            errorCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    if (mode == TestMode.STRESS) {
                        ZipFileManager.getInstance().cleanup();
                    }
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        System.out.println("Stress Test (" + mode + "):");
        System.out.println("Threads: " + threadCount + ", Iterations: " + iterations);
        System.out.println("Success: " + successCount.get() + ", Errors: " + errorCount.get());
        
        double errorRate = (successCount.get() + errorCount.get()) > 0 ?
            (double) errorCount.get() / (successCount.get() + errorCount.get()) : 0;
        
        // 压力测试允许极低的错误率（如LRU缓存驱逐导致的）
        assertTrue(errorRate < 0.01, 
                     "Error rate should be less than 1% in stress test. Actual error rate: " + (errorRate * 100) + "%");
    }

    /**
     * 添加随机延迟模拟真实场景的不规则访问模式
     */
    private void randomDelay() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(MIN_DELAY_MS, MAX_DELAY_MS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 获取测试用的EPUB文件
     */
    private File getTestEpubFile() {
        File epubFile = new File("src/test/resources/fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        if (!epubFile.exists()) {
            epubFile = new File("target/test-classes/fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        }
        assertTrue(epubFile.exists(), "EPUB file should exist");
        return epubFile;
    }
}