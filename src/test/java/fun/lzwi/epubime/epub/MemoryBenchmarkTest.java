package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.epub.EpubFileReader;
import fun.lzwi.epubime.epub.Metadata;
import fun.lzwi.epubime.parser.MetadataParser;
import fun.lzwi.epubime.zip.ZipFileManager;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Memory benchmark tests for EPUBime library
 * Measures memory usage in different scenarios
 */
public class MemoryBenchmarkTest {

    // Records memory usage for each operation
    private final Map<String, Long> memoryResults = new HashMap<>();

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
     * Measures memory usage before and after an operation
     */
    private long measureMemoryUsage(Runnable operation) {
        // Force garbage collection to get more accurate measurements
        System.gc();
        try {
            Thread.sleep(100); // Wait for GC to complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        operation.run();

        // Force garbage collection again
        System.gc();
        try {
            Thread.sleep(100); // Wait for GC to complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        return afterMemory - beforeMemory;
    }

    /**
     * Tests memory usage of parsing EPUB files
     */
    @Test
    public void testParseMemoryUsage() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");

        long memoryUsed = measureMemoryUsage(() -> {
            try {
                EpubBook book = new EpubParser(epubFile).parse();
                assertNotNull(book);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("Memory used for parsing EPUB file: " + memoryUsed / 1024.0 / 1024.0 + " MB");
        memoryResults.put("parse_full_epub_memory", memoryUsed);

        // Memory threshold check (reasonable limit for a small EPUB file)
        assertTrue(memoryUsed < 50 * 1024 * 1024L, "Memory usage exceeds 50 MB, may indicate memory leak"); // 50 MB
    }

    /**
     * Tests memory usage of reading EPUB content
     */
    @Test
    public void testReadEpubContentMemoryUsage() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");

        // 清除EPUBime缓存，确保测试公平性
        clearEpubimeCaches();

        long memoryUsed = measureMemoryUsage(() -> {
            try {
                EpubFileReader fileReader = new EpubFileReader(epubFile);
                String content = fileReader.readContent("mimetype");
                assertNotNull(content);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("Memory used for reading EPUB content: " + memoryUsed / 1024.0 / 1024.0 + " MB");
        memoryResults.put("read_epub_content_memory", memoryUsed);

        // Memory threshold check
        assertTrue(memoryUsed < 10 * 1024 * 1024L, "Memory usage exceeds 10 MB, may indicate memory leak"); // 10 MB
    }

    /**
     * Tests memory usage of parsing metadata
     */
    @Test
    public void testParseMetadataMemoryUsage() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");

        // 清除EPUBime缓存，确保测试公平性
        clearEpubimeCaches();

        EpubFileReader fileReader = new EpubFileReader(epubFile);
        String opfContent = fileReader.readContent("OEBPS/book.opf");

        long memoryUsed = measureMemoryUsage(() -> {
            try {
                MetadataParser metadataParser = new MetadataParser();
                Metadata metadata = metadataParser.parseMetadata(opfContent);
                assertNotNull(metadata);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("Memory used for parsing metadata: " + memoryUsed / 1024.0 / 1024.0 + " MB");
        memoryResults.put("parse_metadata_memory", memoryUsed);

        // Memory threshold check
        assertTrue(memoryUsed < 5 * 1024 * 1024L, "Memory usage exceeds 5 MB, may indicate memory leak"); // 5 MB
    }

    /**
     * Tests memory usage of the cache mechanism
     */
    @Test
    public void testCacheMemoryUsage() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");

        // 清除EPUBime缓存，确保测试公平性
        clearEpubimeCaches();

        EpubParser parser = new EpubParser(epubFile);

        // First parse
        long memoryUsedFirst = measureMemoryUsage(() -> {
            try {
                EpubBook book1 = parser.parse();
                assertNotNull(book1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Second parse (should use cache)
        long memoryUsedSecond = measureMemoryUsage(() -> {
            try {
                EpubBook book2 = parser.parse();
                assertNotNull(book2);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("Memory used for first parse: " + memoryUsedFirst / 1024.0 / 1024.0 + " MB");
        System.out.println("Memory used for cached parse: " + memoryUsedSecond / 1024.0 / 1024.0 + " MB");

        memoryResults.put("first_parse_memory", memoryUsedFirst);
        memoryResults.put("cached_parse_memory", memoryUsedSecond);

        // Cache should use less memory on second parse
        if (memoryUsedFirst > 0) {
            assertTrue(memoryUsedSecond <= memoryUsedFirst,
                       "Cached parse should use less or equal memory than first parse");
        }
    }

    /**
     * Tests memory usage comparison between EPUBime and epublib
     */
    @Test
    public void testMemoryUsageComparison() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");

        // 清除EPUBime缓存，确保测试公平性
        clearEpubimeCaches();

        // Test EPUBime memory usage
        long epubimeMemoryUsed = measureMemoryUsage(() -> {
            try {
                EpubBook epubimeBook = new EpubParser(epubFile).parse();
                assertNotNull(epubimeBook);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("EPUBime memory used for parsing: " + epubimeMemoryUsed / 1024.0 / 1024.0 + " MB");
        memoryResults.put("epubime_parse_memory", epubimeMemoryUsed);

        // 清除EPUBime缓存，确保对epublib的测试公平性
        clearEpubimeCaches();

        // Test epublib memory usage
        long epublibMemoryUsed = measureMemoryUsage(() -> {
            try {
                InputStream epublibInputStream = new FileInputStream(epubFile);
                EpubReader epubReader = new EpubReader();
                Book epublibBook = epubReader.readEpub(epublibInputStream);
                assertNotNull(epublibBook);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("epublib memory used for parsing: " + epublibMemoryUsed / 1024.0 / 1024.0 + " MB");
        memoryResults.put("epublib_parse_memory", epublibMemoryUsed);

        // Compare memory usage
        System.out.println("Memory comparison - EPUBime vs epublib: " +
                           (epubimeMemoryUsed < epublibMemoryUsed ? "EPUBime uses less memory" : "epublib uses less memory"));
        System.out.println("Memory difference: " + Math.abs(epubimeMemoryUsed - epublibMemoryUsed) / 1024.0 / 1024.0 + " MB");
    }

    /**
     * Displays all memory benchmark results
     */
    public void printMemoryBenchmarkResults() {
        System.out.println("\n=== Memory Benchmark Results ===");
        for (Map.Entry<String, Long> entry : memoryResults.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() / 1024.0 / 1024.0 + " MB");
        }
        System.out.println("========================\n");
    }
}