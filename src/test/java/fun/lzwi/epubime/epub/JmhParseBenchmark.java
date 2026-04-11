package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.zip.ZipFileManager;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * 专业的JMH基准测试：解析性能对比
 * 
 * 使用JMH框架确保测试结果准确可靠：
 * - 自动JVM预热
 * - 多次迭代取平均值
 * - 防止死代码消除
 * - 统计学显著性检验
 * 
 * 运行方式：
 * 1. mvn clean package
 * 2. java -jar target/benchmarks.jar JmhParseBenchmark
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 2, jvmArgsPrepend = {"-Xmx512m", "-Xms512m"})
public class JmhParseBenchmark {

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
    public EpubBook epubimeParse(org.openjdk.jmh.infra.Blackhole bh) throws Exception {
        clearCaches();
        EpubBook book = epubimeParser.parse();
        bh.consume(book);
        return book;
    }

    @Benchmark
    public Book epublibParse(org.openjdk.jmh.infra.Blackhole bh) throws Exception {
        clearCaches();
        try (InputStream is = new FileInputStream(epubFile)) {
            Book book = epublibReader.readEpub(is);
            bh.consume(book);
            return book;
        }
    }

    @Benchmark
    public EpubBook epubimeParseWithCache(org.openjdk.jmh.infra.Blackhole bh) throws Exception {
        EpubBook book = epubimeParser.parse();
        bh.consume(book);
        return book;
    }

    private void clearCaches() {
        EpubCacheManager.getInstance().clearAllCaches();
        ZipFileManager.getInstance().cleanup();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhParseBenchmark.class.getSimpleName())
                .result("jmh-parse-benchmark-results.json")
                .resultFormat(ResultFormatType.JSON)
                .build();

        new Runner(opt).run();
    }
}
