package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.epub.EpubFileReader;
import fun.lzwi.epubime.epub.Metadata;
import fun.lzwi.epubime.parser.MetadataParser;
import fun.lzwi.epubime.zip.ZipFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Performance benchmark tests for EPUBime library
 * Measures the performance of EPUB parser in different scenarios
 * 
 * 改进：
 * 1. 添加@BeforeEach确保状态隔离
 * 2. 多次运行取平均值
 * 3. 添加统计分析（标准差、中位数）
 */
public class PerformanceBenchmarkTest {

    private static final int WARMUP_RUNS = 3;
    private static final int MEASUREMENT_RUNS = 5;
    
    // Records execution time for each operation
    private final Map<String, List<Long>> benchmarkResults = new HashMap<>();

    /**
     * 在每次测试前清除EPUBime的缓存，确保测试公平性
     */
    @BeforeEach
    public void setUp() {
        clearEpubimeCaches();
        benchmarkResults.clear();
    }

    /**
     * Tests performance with different size EPUB files
     */
    @Test
    public void testDifferentSizeEpubPerformance() throws Exception {

        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");

        EpubFileReader fileReader = new EpubFileReader(epubFile);

        // Warmup
        for (int i = 0; i < WARMUP_RUNS; i++) {
            fileReader.readContent("mimetype");
            clearEpubimeCaches();
        }

        // Test small file performance (only read mimetype)
        List<Long> mimetypeDurations = new ArrayList<>();
        for (int i = 0; i < MEASUREMENT_RUNS; i++) {
            clearEpubimeCaches();
            long startTime = System.nanoTime();
            String mimetype = fileReader.readContent("mimetype");
            long endTime = System.nanoTime();
            mimetypeDurations.add(endTime - startTime);
            assertNotNull(mimetype);
        }

        // Test medium file performance (read OPF file)
        List<Long> opfDurations = new ArrayList<>();
        for (int i = 0; i < MEASUREMENT_RUNS; i++) {
            clearEpubimeCaches();
            long startTime = System.nanoTime();
            String opfContent = fileReader.readContent("OEBPS/book.opf");
            long endTime = System.nanoTime();
            opfDurations.add(endTime - startTime);
            assertNotNull(opfContent);
        }

        // Test large file performance (read NCX file)
        List<Long> ncxDurations = new ArrayList<>();
        for (int i = 0; i < MEASUREMENT_RUNS; i++) {
            clearEpubimeCaches();
            long startTime = System.nanoTime();
            String ncxContent = fileReader.readContent("OEBPS/book.ncx");
            long endTime = System.nanoTime();
            ncxDurations.add(endTime - startTime);
            assertNotNull(ncxContent);
        }

        // Statistical analysis
        System.out.println("\n=== File Read Performance ===");
        System.out.printf("Mimetype: %.2f ms ± %.2f ms (median: %.2f ms)%n",
                         calculateAverage(mimetypeDurations) / 1_000_000.0,
                         calculateStdDev(mimetypeDurations) / 1_000_000.0,
                         calculateMedian(mimetypeDurations) / 1_000_000.0);
        System.out.printf("OPF: %.2f ms ± %.2f ms (median: %.2f ms)%n",
                         calculateAverage(opfDurations) / 1_000_000.0,
                         calculateStdDev(opfDurations) / 1_000_000.0,
                         calculateMedian(opfDurations) / 1_000_000.0);
        System.out.printf("NCX: %.2f ms ± %.2f ms (median: %.2f ms)%n",
                         calculateAverage(ncxDurations) / 1_000_000.0,
                         calculateStdDev(ncxDurations) / 1_000_000.0,
                         calculateMedian(ncxDurations) / 1_000_000.0);

        benchmarkResults.put("read_small_file", mimetypeDurations);
        benchmarkResults.put("read_medium_file", opfDurations);
        benchmarkResults.put("read_large_file", ncxDurations);
    }

    /**
     * Displays all benchmark results
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