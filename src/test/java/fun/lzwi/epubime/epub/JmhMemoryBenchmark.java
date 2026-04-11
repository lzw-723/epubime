package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.zip.ZipFileManager;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.concurrent.TimeUnit;

/**
 * 专业的JMH基准测试：内存使用对比
 * 
 * 使用更准确的内存测量方法：
 * - 使用Runtime.totalMemory()和Runtime.freeMemory()
 * - 强制GC并等待稳定
 * - 多次测量取中位数
 * 
 * 运行方式：
 * 1. mvn clean package
 * 2. java -jar target/benchmarks.jar JmhMemoryBenchmark
 */
@State(Scope.Thread)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgsPrepend = {"-Xmx1g", "-Xms1g"})
public class JmhMemoryBenchmark {

    private File epubFile;
    private EpubParser epubimeParser;
    private EpubReader epublibReader;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        epubimeParser = new EpubParser(epubFile);
        epublibReader = new EpubReader();
        clearCaches();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        clearCaches();
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @Measurement(iterations = 10)
    public void epubimeMemoryUsage(Blackhole bh) throws Exception {
        clearCaches();
        forceGC();
        
        long beforeMem = getUsedMemory();
        EpubBook book = epubimeParser.parse();
        long afterMem = getUsedMemory();
        
        long memoryDiff = afterMem - beforeMem;
        bh.consume(book);
        bh.consume(memoryDiff);
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @Measurement(iterations = 10)
    public void epublibMemoryUsage(Blackhole bh) throws Exception {
        clearCaches();
        forceGC();
        
        long beforeMem = getUsedMemory();
        try (InputStream is = new FileInputStream(epubFile)) {
            Book book = epublibReader.readEpub(is);
            long afterMem = getUsedMemory();
            
            long memoryDiff = afterMem - beforeMem;
            bh.consume(book);
            bh.consume(memoryDiff);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @Measurement(iterations = 10)
    public void cacheMemoryEfficiency(Blackhole bh) throws Exception {
        clearCaches();
        forceGC();
        
        long beforeMem = getUsedMemory();
        EpubBook book1 = epubimeParser.parse();
        long afterFirstParse = getUsedMemory();
        
        EpubBook book2 = epubimeParser.parse();
        long afterSecondParse = getUsedMemory();
        
        bh.consume(book1);
        bh.consume(book2);
        bh.consume(afterFirstParse - beforeMem);
        bh.consume(afterSecondParse - afterFirstParse);
    }

    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private void forceGC() {
        for (int i = 0; i < 3; i++) {
            System.gc();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void clearCaches() {
        EpubCacheManager.getInstance().clearAllCaches();
        ZipFileManager.getInstance().cleanup();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhMemoryBenchmark.class.getSimpleName())
                .result("jmh-memory-benchmark-results.json")
                .resultFormat(ResultFormatType.JSON)
                .build();

        new Runner(opt).run();
    }
}
