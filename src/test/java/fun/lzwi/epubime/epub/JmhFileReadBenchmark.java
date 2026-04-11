package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import fun.lzwi.epubime.cache.EpubCacheManager;
import fun.lzwi.epubime.parser.MetadataParser;
import fun.lzwi.epubime.zip.ZipFileManager;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 专业的JMH基准测试：文件读取性能
 * 
 * 运行方式：
 * 1. mvn clean package
 * 2. java -jar target/benchmarks.jar JmhFileReadBenchmark
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 2, jvmArgsPrepend = {"-Xmx512m", "-Xms512m"})
public class JmhFileReadBenchmark {

    private File epubFile;
    private EpubFileReader fileReader;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        fileReader = new EpubFileReader(epubFile);
        clearCaches();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        clearCaches();
    }

    @Benchmark
    public String readMimetype(Blackhole bh) throws Exception {
        clearCaches();
        String content = fileReader.readContent("mimetype");
        bh.consume(content);
        return content;
    }

    @Benchmark
    public String readOpf(Blackhole bh) throws Exception {
        clearCaches();
        String content = fileReader.readContent("OEBPS/book.opf");
        bh.consume(content);
        return content;
    }

    @Benchmark
    public String readNcx(Blackhole bh) throws Exception {
        clearCaches();
        String content = fileReader.readContent("OEBPS/book.ncx");
        bh.consume(content);
        return content;
    }

    @Benchmark
    public Metadata parseMetadata(Blackhole bh) throws Exception {
        clearCaches();
        String opfContent = fileReader.readContent("OEBPS/book.opf");
        MetadataParser parser = new MetadataParser();
        Metadata metadata = parser.parseMetadata(opfContent, "3.0");
        bh.consume(metadata);
        return metadata;
    }

    @Benchmark
    public EpubBook parseFullBook(Blackhole bh) throws Exception {
        clearCaches();
        EpubParser parser = new EpubParser(epubFile);
        EpubBook book = parser.parse();
        
        bh.consume(book.getMetadata());
        bh.consume(book.getChapters());
        bh.consume(book.getResources());
        
        return book;
    }

    @Benchmark
    public List<EpubChapter> accessChapters(Blackhole bh) throws Exception {
        clearCaches();
        EpubParser parser = new EpubParser(epubFile);
        EpubBook book = parser.parse();
        List<EpubChapter> chapters = book.getChapters();
        bh.consume(chapters);
        return chapters;
    }

    private void clearCaches() {
        EpubCacheManager.getInstance().clearAllCaches();
        ZipFileManager.getInstance().cleanup();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhFileReadBenchmark.class.getSimpleName())
                .result("jmh-file-read-benchmark-results.json")
                .resultFormat(ResultFormatType.JSON)
                .build();

        new Runner(opt).run();
    }
}
