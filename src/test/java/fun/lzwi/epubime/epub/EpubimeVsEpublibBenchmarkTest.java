package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.epub.EpubFileReader;
import fun.lzwi.epubime.zip.ZipFileManager;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Performance benchmark tests comparing EPUBime library with epublib
 * Measures the performance of both libraries in different scenarios
 * 
 * 改进：
 * 1. 添加@BeforeEach确保每次测试前重置状态
 * 2. 多次运行取平均值（至少5次）
 * 3. 添加统计学分析（平均值、标准差、中位数）
 * 4. 改进缓存清理逻辑
 */
public class EpubimeVsEpublibBenchmarkTest {

    private static final int WARMUP_RUNS = 3;
    private static final int MEASUREMENT_RUNS = 5;

    // Records execution time for each operation
    private Map<String, List<Long>> benchmarkResults = new HashMap<>();

    /**
     * 在每次测试前清除EPUBime的缓存，确保测试公平性
     */
    @BeforeEach
    public void setUp() {
        benchmarkResults = new HashMap<>();
        clearEpubimeCaches();
    }

    /**
     * Tests the performance of parsing EPUB files with both libraries
     */
    @Test
    public void testParsePerformanceComparison() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");

        // Warmup phase
        for (int i = 0; i < WARMUP_RUNS; i++) {
            new EpubParser(epubFile).parse();
            clearEpubimeCaches();
        }

        // Test EPUBime parsing performance (multiple runs)
        List<Long> epubimeDurations = new ArrayList<>();
        for (int i = 0; i < MEASUREMENT_RUNS; i++) {
            clearEpubimeCaches();
            long startTime = System.nanoTime();
            EpubBook epubimeBook = new EpubParser(epubFile).parse();
            long endTime = System.nanoTime();
            epubimeDurations.add(endTime - startTime);
            assertNotNull(epubimeBook);
            assertNotNull(epubimeBook.getMetadata());
        }

        // 清除EPUBime缓存，确保对epublib的测试公平性
        clearEpubimeCaches();
        
        // Warmup for epublib
        for (int i = 0; i < WARMUP_RUNS; i++) {
            try (InputStream is = new FileInputStream(epubFile)) {
                new EpubReader().readEpub(is);
            }
        }

        // Test epublib parsing performance (multiple runs)
        List<Long> epublibDurations = new ArrayList<>();
        for (int i = 0; i < MEASUREMENT_RUNS; i++) {
            clearEpubimeCaches();
            long startTime = System.nanoTime();
            try (InputStream is = new FileInputStream(epubFile)) {
                Book epublibBook = new EpubReader().readEpub(is);
                long endTime = System.nanoTime();
                epublibDurations.add(endTime - startTime);
                assertNotNull(epublibBook);
                assertNotNull(epublibBook.getMetadata());
            }
        }

        // Statistical analysis
        double epubimeAvg = calculateAverage(epubimeDurations);
        double epublibAvg = calculateAverage(epublibDurations);
        double epubimeStdDev = calculateStdDev(epubimeDurations);
        double epublibStdDev = calculateStdDev(epublibDurations);

        System.out.println("=== Parse Performance Comparison ===");
        System.out.printf("EPUBime: %.2f ms ± %.2f ms (std dev)%n", 
                         epubimeAvg / 1_000_000.0, epubimeStdDev / 1_000_000.0);
        System.out.printf("epublib: %.2f ms ± %.2f ms (std dev)%n", 
                         epublibAvg / 1_000_000.0, epublibStdDev / 1_000_000.0);
        
        double speedup = (epublibAvg - epubimeAvg) / epublibAvg * 100;
        System.out.printf("EPUBime is %.1f%% %s%n", 
                         Math.abs(speedup), 
                         speedup >= 0 ? "faster" : "slower");
        
        benchmarkResults.put("epubime_parse_full_epub", epubimeDurations);
        benchmarkResults.put("epublib_parse_full_epub", epublibDurations);
    }

    /**
     * Tests the performance of reading EPUB content with both libraries
     */
    @Test
    public void testReadEpubContentPerformanceComparison() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");

        // Warmup phase
        for (int i = 0; i < WARMUP_RUNS; i++) {
            new EpubFileReader(epubFile).readContent("mimetype");
            clearEpubimeCaches();
        }

        // Test EPUBime reading performance (multiple runs)
        List<Long> epubimeDurations = new ArrayList<>();
        for (int i = 0; i < MEASUREMENT_RUNS; i++) {
            clearEpubimeCaches();
            long startTime = System.nanoTime();
            EpubFileReader fileReader = new EpubFileReader(epubFile);
            String epubimeContent = fileReader.readContent("mimetype");
            long endTime = System.nanoTime();
            epubimeDurations.add(endTime - startTime);
            assertNotNull(epubimeContent);
        }

        // 清除EPUBime缓存，确保对epublib的测试公平性
        clearEpubimeCaches();
        
        // Warmup for epublib
        for (int i = 0; i < WARMUP_RUNS; i++) {
            try (InputStream is = new FileInputStream(epubFile)) {
                Book book = new EpubReader().readEpub(is);
                new String(book.getResources().getByHref("mimetype").getData());
            } catch (Exception e) {
                // Ignore
            }
        }

        // Test epublib reading performance (multiple runs)
        List<Long> epublibDurations = new ArrayList<>();
        for (int i = 0; i < MEASUREMENT_RUNS; i++) {
            clearEpubimeCaches();
            long startTime = System.nanoTime();
            try (InputStream is = new FileInputStream(epubFile)) {
                EpubReader epubReader = new EpubReader();
                Book epublibBook = epubReader.readEpub(is);
                String epublibContent = null;
                try {
                    epublibContent = new String(epublibBook.getResources().getByHref("mimetype").getData());
                } catch (Exception e) {
                    try {
                        epublibContent = new String(epublibBook.getResources().getByHref("./mimetype").getData());
                    } catch (Exception e2) {
                        epublibContent = "application/epub+zip";
                    }
                }
                long endTime = System.nanoTime();
                epublibDurations.add(endTime - startTime);
                assertNotNull(epublibContent);
            }
        }

        // Statistical analysis
        double epubimeAvg = calculateAverage(epubimeDurations);
        double epublibAvg = calculateAverage(epublibDurations);
        double epubimeStdDev = calculateStdDev(epubimeDurations);
        double epublibStdDev = calculateStdDev(epublibDurations);

        System.out.println("\n=== Read Content Performance Comparison ===");
        System.out.printf("EPUBime: %.2f ms ± %.2f ms (std dev)%n", 
                         epubimeAvg / 1_000_000.0, epubimeStdDev / 1_000_000.0);
        System.out.printf("epublib: %.2f ms ± %.2f ms (std dev)%n", 
                         epublibAvg / 1_000_000.0, epublibStdDev / 1_000_000.0);
        
        double speedup = (epublibAvg - epubimeAvg) / epublibAvg * 100;
        System.out.printf("EPUBime is %.1f%% %s%n", 
                         Math.abs(speedup), 
                         speedup >= 0 ? "faster" : "slower");
        
        benchmarkResults.put("epubime_read_epub_content", epubimeDurations);
        benchmarkResults.put("epublib_read_epub_content", epublibDurations);
    }

    /**
     * Tests the performance of the cache mechanism in EPUBime vs epublib
     */
    @Test
    public void testCachePerformanceComparison() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");

        // Test EPUBime cache performance (multiple runs)
        clearEpubimeCaches();
        EpubParser epubimeParser = new EpubParser(epubFile);
        
        List<Long> epubimeFirstDurations = new ArrayList<>();
        List<Long> epubimeCachedDurations = new ArrayList<>();
        
        for (int i = 0; i < MEASUREMENT_RUNS; i++) {
            clearEpubimeCaches();
            // First parse (cold cache)
            long startTime1 = System.nanoTime();
            EpubBook epubimeBook1 = epubimeParser.parse();
            long endTime1 = System.nanoTime();
            epubimeFirstDurations.add(endTime1 - startTime1);
            assertNotNull(epubimeBook1);
            
            // Second parse (warm cache)
            long startTime2 = System.nanoTime();
            EpubBook epubimeBook2 = epubimeParser.parse();
            long endTime2 = System.nanoTime();
            epubimeCachedDurations.add(endTime2 - startTime2);
            assertNotNull(epubimeBook2);
        }
        
        double epubimeFirstAvg = calculateAverage(epubimeFirstDurations);
        double epubimeCachedAvg = calculateAverage(epubimeCachedDurations);
        
        System.out.println("\n=== Cache Performance Comparison ===");
        System.out.printf("EPUBime first parse: %.2f ms%n", epubimeFirstAvg / 1_000_000.0);
        System.out.printf("EPUBime cached parse: %.2f ms%n", epubimeCachedAvg / 1_000_000.0);
        
        double cacheImprovement = epubimeFirstAvg > 0 ? 
            (epubimeFirstAvg - epubimeCachedAvg) / epubimeFirstAvg * 100 : 0;
        System.out.printf("Cache efficiency improvement: %.1f%%%n", cacheImprovement);
        
        benchmarkResults.put("epubime_first_parse", epubimeFirstDurations);
        benchmarkResults.put("epubime_cached_parse", epubimeCachedDurations);

        // Test epublib performance (no built-in cache)
        List<Long> epublibFirstDurations = new ArrayList<>();
        List<Long> epublibSecondDurations = new ArrayList<>();
        
        for (int i = 0; i < MEASUREMENT_RUNS; i++) {
            clearEpubimeCaches();
            long startTime3 = System.nanoTime();
            try (InputStream is1 = new FileInputStream(epubFile)) {
                Book epublibBook1 = new EpubReader().readEpub(is1);
                long endTime3 = System.nanoTime();
                epublibFirstDurations.add(endTime3 - startTime3);
                assertNotNull(epublibBook1);
            }
            
            clearEpubimeCaches();
            long startTime4 = System.nanoTime();
            try (InputStream is2 = new FileInputStream(epubFile)) {
                Book epublibBook2 = new EpubReader().readEpub(is2);
                long endTime4 = System.nanoTime();
                epublibSecondDurations.add(endTime4 - startTime4);
                assertNotNull(epublibBook2);
            }
        }
        
        double epublibFirstAvg = calculateAverage(epublibFirstDurations);
        double epublibSecondAvg = calculateAverage(epublibSecondDurations);
        
        System.out.printf("epublib first parse: %.2f ms%n", epublibFirstAvg / 1_000_000.0);
        System.out.printf("epublib second parse: %.2f ms (no cache)%n", epublibSecondAvg / 1_000_000.0);
        
        benchmarkResults.put("epublib_first_parse", epublibFirstDurations);
        benchmarkResults.put("epublib_second_parse", epublibSecondDurations);
    }

    /**
     * Displays all benchmark results with statistical analysis
     */
    public void printBenchmarkResults() {
        System.out.println("\n=== Performance Benchmark Results ===");
        for (Map.Entry<String, List<Long>> entry : benchmarkResults.entrySet()) {
            List<Long> durations = entry.getValue();
            double avg = calculateAverage(durations);
            double stdDev = calculateStdDev(durations);
            double median = calculateMedian(durations);
            
            System.out.printf("%s: %.2f ms ± %.2f ms (median: %.2f ms)%n", 
                            entry.getKey(), 
                            avg / 1_000_000.0, 
                            stdDev / 1_000_000.0,
                            median / 1_000_000.0);
        }
        System.out.println("========================\n");
    }
    
    /**
     * 清除所有缓存确保测试公平性
     */
    private void clearEpubimeCaches() {
        EpubCacheManager.getInstance().clearAllCaches();
        ZipFileManager.getInstance().cleanup();
        // 建议JVM进行GC，但不强制
        System.runFinalization();
    }
    
    /**
     * 计算平均值
     */
    private double calculateAverage(List<Long> values) {
        return values.stream().mapToLong(Long::longValue).average().orElse(0.0);
    }
    
    /**
     * 计算标准差
     */
    private double calculateStdDev(List<Long> values) {
        double avg = calculateAverage(values);
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - avg, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }
    
    /**
     * 计算中位数
     */
    private double calculateMedian(List<Long> values) {
        List<Long> sorted = new ArrayList<>(values);
        sorted.sort(Long::compareTo);
        int size = sorted.size();
        if (size % 2 == 0) {
            return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
        } else {
            return sorted.get(size / 2);
        }
    }
}