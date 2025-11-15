package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.epub.EpubFileReader;
import fun.lzwi.epubime.epub.Metadata;
import fun.lzwi.epubime.parser.MetadataParser;
import fun.lzwi.epubime.zip.ZipFileManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Performance benchmark tests for EPUBime library
 * Measures the performance of EPUB parser in different scenarios
 */
public class PerformanceBenchmarkTest {

    // Records execution time for each operation
    private final Map<String, Long> benchmarkResults = new HashMap<>();

    /**
     * 在测试前清除EPUBime的缓存，确保测试公平性
     */
    private void clearEpubimeCaches() {
        // 清除EPUBime的缓存
        EpubCacheManager.getInstance().clearAllCaches();
        // 清理ZIP文件管理器的句柄
        ZipFileManager.getInstance().cleanup();
    }

    /**
     * Tests performance with different size EPUB files
     */
    @Test
    public void testDifferentSizeEpubPerformance() throws Exception {

        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");

        

        // 清除EPUBime缓存，确保测试公平性

        clearEpubimeCaches();

        

        EpubFileReader fileReader = new EpubFileReader(epubFile);

        // Test small file performance (only read mimetype)

        long startTime = System.nanoTime();

        String mimetype = fileReader.readContent("mimetype");

        long endTime = System.nanoTime();

        long mimetypeDuration = endTime - startTime;



        System.out.println("Reading small file (mimetype) time: " + mimetypeDuration / 1_000_000.0 + " ms");

        benchmarkResults.put("read_small_file", mimetypeDuration);

        // Test medium file performance (read OPF file)
        startTime = System.nanoTime();
        String opfContent = fileReader.readContent("OEBPS/book.opf");
        endTime = System.nanoTime();
        long opfDuration = endTime - startTime;

        System.out.println("Reading medium file (OPF) time: " + opfDuration / 1_000_000.0 + " ms");
        benchmarkResults.put("read_medium_file", opfDuration);

        // Test large file performance (read NCX file)
        startTime = System.nanoTime();
        String ncxContent = fileReader.readContent("OEBPS/book.ncx");
        endTime = System.nanoTime();
        long ncxDuration = endTime - startTime;
        
        System.out.println("Reading large file (NCX) time: " + ncxDuration / 1_000_000.0 + " ms");
        benchmarkResults.put("read_large_file", ncxDuration);
        
        assertNotNull(mimetype);
        assertNotNull(opfContent);
        assertNotNull(ncxContent);
    }

    /**
     * Displays all benchmark results
     */
    public void printBenchmarkResults() {
        System.out.println("\n=== Performance Benchmark Results ===");
        for (Map.Entry<String, Long> entry : benchmarkResults.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() / 1_000_000.0 + " ms");
        }
        System.out.println("========================\n");
    }
}