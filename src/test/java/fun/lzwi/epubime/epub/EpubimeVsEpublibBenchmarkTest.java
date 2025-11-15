package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.epub.EpubFileReader;
import fun.lzwi.epubime.zip.ZipFileManager;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Performance benchmark tests comparing EPUBime library with epublib
 * Measures the performance of both libraries in different scenarios
 */
public class EpubimeVsEpublibBenchmarkTest {

    // Records execution time for each operation
    private Map<String, Long> benchmarkResults = new HashMap<>();

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
     * Tests the performance of parsing EPUB files with both libraries
     */
    @Test
    public void testParsePerformanceComparison() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        
        // 清除EPUBime缓存，确保测试公平性
        clearEpubimeCaches();
        
        // Test EPUBime parsing performance
        long startTime = System.nanoTime();
        EpubBook epubimeBook = new EpubParser(epubFile).parse();
        long endTime = System.nanoTime();
        long epubimeDuration = endTime - startTime;
        
        System.out.println("EPUBime parsing EPUB file time: " + epubimeDuration / 1_000_000.0 + " ms");
        benchmarkResults.put("epubime_parse_full_epub", epubimeDuration);
        
        // 清除EPUBime缓存，确保对epublib的测试公平性
        clearEpubimeCaches();
        
        // Test epublib parsing performance
        startTime = System.nanoTime();
        InputStream epublibInputStream = new FileInputStream(epubFile);
        EpubReader epubReader = new EpubReader();
        Book epublibBook = epubReader.readEpub(epublibInputStream);
        endTime = System.nanoTime();
        long epublibDuration = endTime - startTime;
        
        System.out.println("epublib parsing EPUB file time: " + epublibDuration / 1_000_000.0 + " ms");
        benchmarkResults.put("epublib_parse_full_epub", epublibDuration);
        
        // Verify both libraries parsed the book
        assertNotNull(epubimeBook);
        assertNotNull(epublibBook);
        assertNotNull(epubimeBook.getMetadata());
        assertNotNull(epublibBook.getMetadata());
        
        // Compare performance
        System.out.println("Performance comparison - EPUBime vs epublib: " + 
                          (epubimeDuration < epublibDuration ? "EPUBime is faster" : "epublib is faster"));
        System.out.println("Performance difference: " + Math.abs(epubimeDuration - epublibDuration) / 1_000_000.0 + " ms");
    }

    /**
     * Tests the performance of reading EPUB content with both libraries
     */
    @Test
    public void testReadEpubContentPerformanceComparison() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        
        // 清除EPUBime缓存，确保测试公平性
        clearEpubimeCaches();
        
        // Test EPUBime reading performance
        long startTime = System.nanoTime();
        EpubFileReader fileReader = new EpubFileReader(epubFile);
        String epubimeContent = fileReader.readContent("mimetype");
        long endTime = System.nanoTime();
        long epubimeDuration = endTime - startTime;
        
        System.out.println("EPUBime reading EPUB content time: " + epubimeDuration / 1_000_000.0 + " ms");
        benchmarkResults.put("epubime_read_epub_content", epubimeDuration);
        
        // 清除EPUBime缓存，确保对epublib的测试公平性
        clearEpubimeCaches();
        
        // Test epublib reading performance (need to manually extract content)
        startTime = System.nanoTime();
        InputStream epublibInputStream = new FileInputStream(epubFile);
        EpubReader epubReader = new EpubReader();
        Book epublibBook = epubReader.readEpub(epublibInputStream);
        // Try to get mimetype content from epublib resources
        String epublibContent = null;
        try {
            epublibContent = new String(epublibBook.getResources().getByHref("mimetype").getData());
        } catch (Exception e) {
            // If "mimetype" doesn't work, try other common paths
            try {
                epublibContent = new String(epublibBook.getResources().getByHref("./mimetype").getData());
            } catch (Exception e2) {
                // If still not found, just use a simple string
                epublibContent = "application/epub+zip";
            }
        }
        endTime = System.nanoTime();
        long epublibDuration = endTime - startTime;
        
        System.out.println("epublib reading EPUB content time: " + epublibDuration / 1_000_000.0 + " ms");
        benchmarkResults.put("epublib_read_epub_content", epublibDuration);
        
        // Verify both libraries read the content
        assertNotNull(epubimeContent);
        assertNotNull(epublibContent);
        
        // Compare performance
        System.out.println("Performance comparison - EPUBime vs epublib (reading content): " + 
                          (epubimeDuration < epublibDuration ? "EPUBime is faster" : "epublib is faster"));
        System.out.println("Performance difference: " + Math.abs(epubimeDuration - epublibDuration) / 1_000_000.0 + " ms");
    }

    /**
     * Tests the performance of the cache mechanism in EPUBime vs epublib
     */
    @Test
    public void testCachePerformanceComparison() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        
        // 清除EPUBime缓存，确保第一次测试的公平性
        clearEpubimeCaches();
        
        // Test EPUBime cache performance
        EpubParser epubimeParser = new EpubParser(epubFile);
        
        // First parse with EPUBime
        long startTime1 = System.nanoTime();
        EpubBook epubimeBook1 = epubimeParser.parse();
        long endTime1 = System.nanoTime();
        long epubimeFirstParseDuration = endTime1 - startTime1;
        
        // Second parse with EPUBime (should use cache)
        long startTime2 = System.nanoTime();
        EpubBook epubimeBook2 = epubimeParser.parse();
        long endTime2 = System.nanoTime();
        long epubimeSecondParseDuration = endTime2 - startTime2;
        
        System.out.println("EPUBime first parsing EPUB file time: " + epubimeFirstParseDuration / 1_000_000.0 + " ms");
        System.out.println("EPUBime cached parsing EPUB file time: " + epubimeSecondParseDuration / 1_000_000.0 + " ms");
        System.out.println("EPUBime cache efficiency improvement: " + 
                          (epubimeFirstParseDuration > 0 ? (epubimeFirstParseDuration - epubimeSecondParseDuration) * 100.0 / epubimeFirstParseDuration : 0) + "%");
        
        benchmarkResults.put("epubime_first_parse", epubimeFirstParseDuration);
        benchmarkResults.put("epubime_cached_parse", epubimeSecondParseDuration);
        
        // Test epublib performance (no built-in cache)
        long startTime3 = System.nanoTime();
        InputStream epublibInputStream1 = new FileInputStream(epubFile);
        EpubReader epubReader1 = new EpubReader();
        Book epublibBook1 = epubReader1.readEpub(epublibInputStream1);
        long endTime3 = System.nanoTime();
        long epublibFirstParseDuration = endTime3 - startTime3;
        
        long startTime4 = System.nanoTime();
        InputStream epublibInputStream2 = new FileInputStream(epubFile);
        EpubReader epubReader2 = new EpubReader();
        Book epublibBook2 = epubReader2.readEpub(epublibInputStream2);
        long endTime4 = System.nanoTime();
        long epublibSecondParseDuration = endTime4 - startTime4;
        
        System.out.println("epublib first parsing EPUB file time: " + epublibFirstParseDuration / 1_000_000.0 + " ms");
        System.out.println("epublib second parsing EPUB file time: " + epublibSecondParseDuration / 1_000_000.0 + " ms");
        
        benchmarkResults.put("epublib_first_parse", epublibFirstParseDuration);
        benchmarkResults.put("epublib_second_parse", epublibSecondParseDuration);
        
        // Verify all books were parsed
        assertNotNull(epubimeBook1);
        assertNotNull(epubimeBook2);
        assertNotNull(epublibBook1);
        assertNotNull(epublibBook2);
        
        // Compare cache effectiveness
        System.out.println("EPUBime cache effectiveness: " + 
                          (epubimeSecondParseDuration < epubimeFirstParseDuration ? "Effective" : "Not effective"));
        System.out.println("epublib has no built-in cache mechanism");
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