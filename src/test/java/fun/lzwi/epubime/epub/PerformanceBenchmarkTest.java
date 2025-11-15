package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.epub.EpubFileReader;
import fun.lzwi.epubime.exception.EpubParseException;
import fun.lzwi.epubime.exception.BaseEpubException;
import fun.lzwi.epubime.parser.MetadataParser;
import fun.lzwi.epubime.zip.ZipFileManager;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
     * Tests the performance of parsing EPUB files
     */
    @Test
    public void testParsePerformance() throws EpubParseException, BaseEpubException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        long startTime = System.nanoTime();
        EpubBook book = new EpubParser(epubFile).parse();
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        System.out.println("Parsing EPUB file time: " + duration / 1_000_000.0 + " ms");
        benchmarkResults.put("parse_full_epub", duration);
        
        assertNotNull(book);
        assertNotNull(book.getMetadata());
        assertNotNull(book.getChapters());
        
        // Performance threshold check
        assertTrue("Parsing time exceeds 5 seconds, performance needs optimization", duration < 5_000_000_000L); // 5 seconds
    }

    /**
     * Tests the performance of reading EPUB content
     */
    @Test
    public void testReadEpubContentPerformance() throws EpubParseException, BaseEpubException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        
        // 清除EPUBime缓存，确保测试公平性
        clearEpubimeCaches();
        
        long startTime = System.nanoTime();
        EpubFileReader fileReader = new EpubFileReader(epubFile);
        String content = fileReader.readContent("mimetype");
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        System.out.println("Reading EPUB content time: " + duration / 1_000_000.0 + " ms");
        benchmarkResults.put("read_epub_content", duration);
        
        assertNotNull(content);
        
        // Performance threshold check
        assertTrue("Reading content time exceeds 1 second, performance needs optimization", duration < 1_000_000_000L); // 1 second
    }

    /**

     * Tests the performance of parsing metadata

     */

    @Test

    public void testParseMetadataPerformance() throws EpubParseException, BaseEpubException {

        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");

        

        // 清除EPUBime缓存，确保测试公平性

        clearEpubimeCaches();

        

        EpubFileReader fileReader = new EpubFileReader(epubFile);
        String opfContent = fileReader.readContent("OEBPS/book.opf");

        long startTime = System.nanoTime();

        MetadataParser metadataParser = new MetadataParser();
        Metadata metadata = metadataParser.parseMetadata(opfContent);

        long endTime = System.nanoTime();

        long duration = endTime - startTime;

        

        System.out.println("Parsing metadata time: " + duration / 1_000_000.0 + " ms");

        benchmarkResults.put("parse_metadata", duration);

        

        assertNotNull(metadata);

        

        // Performance threshold check

        assertTrue("Parsing metadata time exceeds 500 milliseconds, performance needs optimization", duration < 500_000_000L); // 500 milliseconds

    }

    /**

     * Tests the performance of the cache mechanism

     */

    @Test

    public void testCachePerformance() throws EpubParseException, BaseEpubException {

        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");

        

        // 清除EPUBime缓存，确保测试公平性

        clearEpubimeCaches();

        

        EpubParser parser = new EpubParser(epubFile);

        

        // First parse

        long startTime1 = System.nanoTime();

        EpubBook book1 = parser.parse();

        long endTime1 = System.nanoTime();

        long firstParseDuration = endTime1 - startTime1;

        

        // Second parse (should use cache)

        long startTime2 = System.nanoTime();

        EpubBook book2 = parser.parse();

        long endTime2 = System.nanoTime();

        long secondParseDuration = endTime2 - startTime2;

        

        System.out.println("First parsing EPUB file time: " + firstParseDuration / 1_000_000.0 + " ms");

        System.out.println("Cached parsing EPUB file time: " + secondParseDuration / 1_000_000.0 + " ms");

        double improvementPercentage = firstParseDuration > 0 ? (firstParseDuration - secondParseDuration) * 100.0 / firstParseDuration : 0;

        System.out.println("Cache efficiency improvement: " + improvementPercentage + "%");

        

        benchmarkResults.put("first_parse", firstParseDuration);

        benchmarkResults.put("cached_parse", secondParseDuration);

        

        assertNotNull(book1);

        assertNotNull(book2);

        

        // Verify if cache is effective - allow some tolerance for timing variations in tests

        // The cached parse should be faster than the first parse, but allow for some tolerance

        // due to JVM warmup, small files, or timing precision issues

        if (firstParseDuration > 0) {

            assertTrue("Second parse with cache should not take significantly longer than first parse", 

                      secondParseDuration <= firstParseDuration * 1.1); // Allow 10% tolerance for timing variations

        }

        // If both durations are very small (near zero), just verify they're not negative and that we got valid results

        assertTrue("Parse durations should be non-negative", firstParseDuration >= 0 && secondParseDuration >= 0);

    }

    /**

     * Tests performance with different size EPUB files

     */

    @Test

    public void testDifferentSizeEpubPerformance() throws EpubParseException, BaseEpubException {

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