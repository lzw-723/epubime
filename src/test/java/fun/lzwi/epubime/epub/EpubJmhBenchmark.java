package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.epub.EpubBook;
import fun.lzwi.epubime.epub.EpubChapter;
import fun.lzwi.epubime.epub.EpubResource;
import fun.lzwi.epubime.epub.Metadata;
import fun.lzwi.epubime.zip.ZipFileManager;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Professional benchmarks for EPUBime library performance testing
 * Provides accurate microbenchmarking with proper warmup and measurement
 */
public class EpubJmhBenchmark {

    private File epubFile;
    private EpubParser epubimeParser;
    private EpubFileReader epubimeFileReader;

    public EpubJmhBenchmark() throws Exception {
        epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        epubimeParser = new EpubParser(epubFile);
        epubimeFileReader = new EpubFileReader(epubFile);
        clearCaches();
    }

    private void clearCaches() {
        EpubCacheManager.getInstance().clearAllCaches();
        ZipFileManager.getInstance().cleanup();
    }

    public EpubBook benchmarkEpubimeParse() throws Exception {
        clearCaches();
        return epubimeParser.parse();
    }

    public EpubBook benchmarkEpubimeParseAndAccess() throws Exception {
        clearCaches();
        EpubBook book = epubimeParser.parse();

        // 模拟实际使用场景：获取元数据
        Metadata metadata = book.getMetadata();
        String title = metadata.getTitle();
        String author = metadata.getCreator();

        // 获取章节列表
        List<EpubChapter> chapters = book.getChapters();

        // 获取资源列表
        List<EpubResource> resources = book.getResources();

        return book;
    }

    public Object benchmarkEpubimeFullWorkflow() throws Exception {
        clearCaches();
        EpubBook book = epubimeParser.parse();

        // 完整的实际使用工作流
        Metadata metadata = book.getMetadata();
        List<EpubChapter> chapters = book.getChapters();
        List<EpubResource> resources = book.getResources();

        // 获取封面
        EpubResource cover = null;
        try {
            cover = fun.lzwi.epubime.epub.EpubBookProcessor.getCover(book);
        } catch (Exception e) {
            // 忽略封面获取错误
        }

        // 读取第一个章节的内容（如果存在）
        String firstChapterContent = null;
        if (!chapters.isEmpty()) {
            EpubChapter firstChapter = chapters.get(0);
            if (firstChapter.getContent() != null) {
                firstChapterContent = epubimeFileReader.readContent(firstChapter.getContent());
            }
        }

        // 返回结果对象用于验证
        return new Object[] {metadata, chapters, resources, cover, firstChapterContent};
    }

    public String benchmarkEpubimeReadMimetype() throws Exception {
        clearCaches();
        return epubimeFileReader.readContent("mimetype");
    }

    public String benchmarkEpubimeReadOpf() throws Exception {
        clearCaches();
        return epubimeFileReader.readContent("OEBPS/book.opf");
    }

    public String benchmarkEpubimeReadNcx() throws Exception {
        clearCaches();
        return epubimeFileReader.readContent("OEBPS/book.ncx");
    }

    public Book benchmarkEpublibParse() throws Exception {
        clearCaches();
        try (InputStream inputStream = new FileInputStream(epubFile)) {
            EpubReader epubReader = new EpubReader();
            return epubReader.readEpub(inputStream);
        }
    }

    public Book benchmarkEpublibParseAndAccess() throws Exception {
        clearCaches();
        try (InputStream inputStream = new FileInputStream(epubFile)) {
            EpubReader epubReader = new EpubReader();
            Book book = epubReader.readEpub(inputStream);

            // 模拟实际使用场景
            nl.siegmann.epublib.domain.Metadata metadata = book.getMetadata();
            String title = metadata.getFirstTitle();
            List<nl.siegmann.epublib.domain.Author> authors = metadata.getAuthors();

            // 获取资源
            java.util.Collection<nl.siegmann.epublib.domain.Resource> resources = book.getResources().getAll();

            // 获取目录
            nl.siegmann.epublib.domain.TableOfContents toc = book.getTableOfContents();

            return book;
        }
    }

    public static void main(String[] args) throws Exception {
        EpubJmhBenchmark benchmark = new EpubJmhBenchmark();

        System.out.println("=== EPUBime Professional Benchmarks ===\n");

        // Warmup
        System.out.println("Warming up...");
        for (int i = 0; i < 5; i++) {
            benchmark.benchmarkEpubimeParse();
            benchmark.benchmarkEpublibParse();
            benchmark.benchmarkEpubimeParseAndAccess();
            benchmark.benchmarkEpublibParseAndAccess();
        }
        System.out.println("Warmup complete.\n");

        // Benchmark 1: Simple parsing only
        System.out.println("=== Benchmark 1: Simple Parsing Performance ===");
        benchmark.runParsingBenchmark();

        // Benchmark 2: Parse + Access (simulating real usage)
        System.out.println("\n=== Benchmark 2: Parse + Access Performance (Real Usage) ===");
        benchmark.runParseAndAccessBenchmark();

        // Benchmark 3: Full workflow (complete user scenario)
        System.out.println("\n=== Benchmark 3: Full Workflow Performance ===");
        benchmark.runFullWorkflowBenchmark();

        // Benchmark 4: File reading performance
        System.out.println("\n=== Benchmark 4: File Reading Performance ===");
        benchmark.runFileReadingBenchmark();

        System.out.println("\n=== All Benchmarks Complete ===");
    }

    private void runParsingBenchmark() throws Exception {
        // Benchmark EPUBime parsing
        System.out.println("Benchmarking EPUBime parsing performance:");
        long[] epubimeTimes = new long[10];
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            benchmarkEpubimeParse();
            long end = System.nanoTime();
            epubimeTimes[i] = end - start;
            System.out.printf("Run %d: %.2f ms%n", i + 1, epubimeTimes[i] / 1_000_000.0);
        }

        double epubimeAvg = java.util.Arrays.stream(epubimeTimes).average().orElse(0) / 1_000_000.0;
        System.out.printf("EPUBime average parsing time: %.2f ms%n%n", epubimeAvg);

        // Benchmark epublib parsing
        System.out.println("Benchmarking epublib parsing performance:");
        long[] epublibTimes = new long[10];
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            benchmarkEpublibParse();
            long end = System.nanoTime();
            epublibTimes[i] = end - start;
            System.out.printf("Run %d: %.2f ms%n", i + 1, epublibTimes[i] / 1_000_000.0);
        }

        double epublibAvg = java.util.Arrays.stream(epublibTimes).average().orElse(0) / 1_000_000.0;
        System.out.printf("epublib average parsing time: %.2f ms%n%n", epublibAvg);

        // Comparison
        printComparison("Simple Parsing", epubimeAvg, epublibAvg);
    }

    private void runParseAndAccessBenchmark() throws Exception {
        // Benchmark EPUBime parse + access
        System.out.println("Benchmarking EPUBime parse + access performance:");
        long[] epubimeTimes = new long[10];
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            benchmarkEpubimeParseAndAccess();
            long end = System.nanoTime();
            epubimeTimes[i] = end - start;
            System.out.printf("Run %d: %.2f ms%n", i + 1, epubimeTimes[i] / 1_000_000.0);
        }

        double epubimeAvg = java.util.Arrays.stream(epubimeTimes).average().orElse(0) / 1_000_000.0;
        System.out.printf("EPUBime average parse+access time: %.2f ms%n%n", epubimeAvg);

        // Benchmark epublib parse + access
        System.out.println("Benchmarking epublib parse + access performance:");
        long[] epublibTimes = new long[10];
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            benchmarkEpublibParseAndAccess();
            long end = System.nanoTime();
            epublibTimes[i] = end - start;
            System.out.printf("Run %d: %.2f ms%n", i + 1, epublibTimes[i] / 1_000_000.0);
        }

        double epublibAvg = java.util.Arrays.stream(epublibTimes).average().orElse(0) / 1_000_000.0;
        System.out.printf("epublib average parse+access time: %.2f ms%n%n", epublibAvg);

        // Comparison
        printComparison("Parse + Access", epubimeAvg, epublibAvg);
    }

    private void runFullWorkflowBenchmark() throws Exception {
        System.out.println("Benchmarking EPUBime full workflow performance:");
        long[] workflowTimes = new long[10];
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            Object result = benchmarkEpubimeFullWorkflow();
            long end = System.nanoTime();
            workflowTimes[i] = end - start;
            System.out.printf("Run %d: %.2f ms%n", i + 1, workflowTimes[i] / 1_000_000.0);
        }

        double workflowAvg = java.util.Arrays.stream(workflowTimes).average().orElse(0) / 1_000_000.0;
        System.out.printf("EPUBime average full workflow time: %.2f ms%n", workflowAvg);
        System.out.println("(Includes: parse + metadata access + chapters + resources + cover + first chapter content)");
    }

    private void runFileReadingBenchmark() throws Exception {
        // Mimetype
        long[] mimetypeTimes = new long[10];
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            benchmarkEpubimeReadMimetype();
            long end = System.nanoTime();
            mimetypeTimes[i] = end - start;
        }
        double mimetypeAvg = java.util.Arrays.stream(mimetypeTimes).average().orElse(0) / 1_000_000.0;
        System.out.printf("Reading mimetype: %.4f ms%n", mimetypeAvg);

        // OPF
        long[] opfTimes = new long[10];
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            benchmarkEpubimeReadOpf();
            long end = System.nanoTime();
            opfTimes[i] = end - start;
        }
        double opfAvg = java.util.Arrays.stream(opfTimes).average().orElse(0) / 1_000_000.0;
        System.out.printf("Reading OPF file: %.4f ms%n", opfAvg);

        // NCX
        long[] ncxTimes = new long[10];
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            benchmarkEpubimeReadNcx();
            long end = System.nanoTime();
            ncxTimes[i] = end - start;
        }
        double ncxAvg = java.util.Arrays.stream(ncxTimes).average().orElse(0) / 1_000_000.0;
        System.out.printf("Reading NCX file: %.4f ms%n", ncxAvg);
    }

    private void printComparison(String testName, double epubimeTime, double epublibTime) {
        System.out.println("=== " + testName + " Performance Comparison ===");
        System.out.printf("EPUBime: %.2f ms%n", epubimeTime);
        System.out.printf("epublib: %.2f ms%n", epublibTime);
        double ratio = epublibTime > 0 ? epubimeTime / epublibTime : 0;
        System.out.printf("Performance ratio (EPUBime/epublib): %.2f%n", ratio);
        double improvement = epublibTime > 0 ? (epublibTime - epubimeTime) / epublibTime * 100 : 0;
        System.out.printf("EPUBime is %.1f%% faster%n", improvement);
        System.out.println();
    }
}