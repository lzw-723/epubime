---
outline: deep
---

# 实际应用示例

本节提供了 EPUBime 库在实际应用中的完整示例，展示如何构建图书管理器、内容提取器、格式转换器等实用工具。

## 图书管理器

### 1. 简单的图书目录管理器

```java
import fun.lzwi.epubime.api.*;
import fun.lzwi.epubime.epub.*;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class BookLibraryManager {
    private final Map<String, EpubReader.EpubInfo> library = new HashMap<>();

    /**
     * 添加书籍到图书馆
     */
    public void addBook(File epubFile) {
        try {
            EpubReader.EpubInfo info = EpubReader.fromFile(epubFile).getInfo();
            library.put(epubFile.getAbsolutePath(), info);
            System.out.println("已添加书籍: " + info.getTitle());
        } catch (Exception e) {
            System.err.println("添加书籍失败: " + epubFile.getName() + " - " + e.getMessage());
        }
    }

    /**
     * 按作者搜索书籍
     */
    public List<EpubReader.EpubInfo> findBooksByAuthor(String author) {
        return library.values().stream()
                .filter(info -> info.getAuthor() != null &&
                               info.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * 按标题搜索书籍
     */
    public List<EpubReader.EpubInfo> findBooksByTitle(String title) {
        return library.values().stream()
                .filter(info -> info.getTitle() != null &&
                               info.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * 获取图书馆统计信息
     */
    public LibraryStats getStats() {
        int totalBooks = library.size();
        long totalSize = library.values().stream()
                .mapToLong(EpubReader.EpubInfo::getFileSize)
                .sum();
        int totalChapters = library.values().stream()
                .mapToInt(EpubReader.EpubInfo::getChapterCount)
                .sum();

        return new LibraryStats(totalBooks, totalSize, totalChapters);
    }

    /**
     * 导出图书馆目录到 CSV
     */
    public void exportToCSV(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("标题,作者,语言,章节数,文件大小");
            for (EpubReader.EpubInfo info : library.values()) {
                writer.printf("\"%s\",\"%s\",\"%s\",%d,%d%n",
                    info.getTitle(), info.getAuthor(), info.getLanguage(),
                    info.getChapterCount(), info.getFileSize());
            }
            System.out.println("目录已导出到: " + filename);
        } catch (IOException e) {
            System.err.println("导出失败: " + e.getMessage());
        }
    }

    public static class LibraryStats {
        public final int totalBooks;
        public final long totalSize;
        public final int totalChapters;

        public LibraryStats(int totalBooks, long totalSize, int totalChapters) {
            this.totalBooks = totalBooks;
            this.totalSize = totalSize;
            this.totalChapters = totalChapters;
        }

        @Override
        public String toString() {
            return String.format("图书馆统计: %d 本书, 总大小: %.2f MB, 总章节数: %d",
                totalBooks, totalSize / (1024.0 * 1024.0), totalChapters);
        }
    }
}

// 使用示例
public class LibraryExample {
    public static void main(String[] args) {
        BookLibraryManager library = new BookLibraryManager();

        // 添加多本书籍
        File booksDir = new File("books");
        if (booksDir.exists() && booksDir.isDirectory()) {
            for (File file : booksDir.listFiles((dir, name) -> name.endsWith(".epub"))) {
                library.addBook(file);
            }
        }

        // 搜索和统计
        List<EpubReader.EpubInfo> tolstoyBooks = library.findBooksByAuthor("托尔斯泰");
        System.out.println("托尔斯泰的书籍: " + tolstoyBooks.size());

        BookLibraryManager.LibraryStats stats = library.getStats();
        System.out.println(stats);

        // 导出目录
        library.exportToCSV("library_catalog.csv");
    }
}
```

### 2. 高级图书分析器

```java
import fun.lzwi.epubime.api.*;
import fun.lzwi.epubime.epub.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AdvancedBookAnalyzer {
    private final AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

    /**
     * 分析书籍的详细统计信息
     */
    public CompletableFuture<BookAnalysis> analyzeBook(File epubFile) {
        return asyncProcessor.parseBookAsync(epubFile)
                .thenApply(book -> {
                    try {
                        EpubBookEnhanced enhanced = new EpubBookEnhanced(book, epubFile);

                        // 基本信息
                        BookAnalysis analysis = new BookAnalysis();
                        analysis.title = enhanced.getTitle();
                        analysis.author = enhanced.getAuthor();
                        analysis.language = enhanced.getLanguage();
                        analysis.totalChapters = enhanced.getChapterCount();

                        // 资源统计
                        analysis.imageCount = enhanced.getImageResources().size();
                        analysis.cssCount = enhanced.getCssResources().size();
                        analysis.fontCount = enhanced.getFontResources().size();

                        // 内容分析
                        analyzeContent(enhanced, analysis);

                        return analysis;

                    } catch (Exception e) {
                        throw new RuntimeException("分析失败: " + epubFile.getName(), e);
                    }
                });
    }

    /**
     * 分析书籍内容
     */
    private void analyzeContent(EpubBookEnhanced book, BookAnalysis analysis) {
        List<EpubChapter> chapters = book.getAllChapters();
        analysis.wordCount = 0;
        analysis.paragraphCount = 0;
        analysis.headingCount = 0;
        analysis.linkCount = 0;

        for (EpubChapter chapter : chapters) {
            try {
                book.processChapterContent(chapter, inputStream -> {
                    try {
                        String content = readStreamToString(inputStream);
                        Document doc = Jsoup.parse(content);

                        // 统计字数
                        String text = doc.text();
                        analysis.wordCount += text.split("\\s+").length;

                        // 统计段落
                        Elements paragraphs = doc.select("p");
                        analysis.paragraphCount += paragraphs.size();

                        // 统计标题
                        Elements headings = doc.select("h1, h2, h3, h4, h5, h6");
                        analysis.headingCount += headings.size();

                        // 统计链接
                        Elements links = doc.select("a[href]");
                        analysis.linkCount += links.size();

                        // 提取关键词
                        extractKeywords(text, analysis);

                    } catch (IOException e) {
                        System.err.println("处理章节内容失败: " + chapter.getTitle());
                    }
                });
            } catch (Exception e) {
                System.err.println("处理章节失败: " + chapter.getTitle());
            }
        }
    }

    /**
     * 提取关键词
     */
    private void extractKeywords(String text, BookAnalysis analysis) {
        // 简单的关键词提取（可以扩展为更复杂的算法）
        String[] words = text.toLowerCase().split("\\W+");
        Map<String, Integer> wordFreq = new HashMap<>();

        for (String word : words) {
            if (word.length() > 3) { // 只统计长度大于3的词
                wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
            }
        }

        // 取出现频率最高的10个词作为关键词
        analysis.keywords = wordFreq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 批量分析多本书籍
     */
    public CompletableFuture<List<BookAnalysis>> analyzeMultipleBooks(List<File> epubFiles) {
        List<CompletableFuture<BookAnalysis>> futures = epubFiles.stream()
                .map(this::analyzeBook)
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    /**
     * 生成分析报告
     */
    public void generateReport(List<BookAnalysis> analyses, String outputFile) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("EPUB书籍分析报告");
            writer.println("生成时间: " + new Date());
            writer.println("========================================");

            for (BookAnalysis analysis : analyses) {
                writer.println("\n书名: " + analysis.title);
                writer.println("作者: " + analysis.author);
                writer.println("语言: " + analysis.language);
                writer.println("章节数: " + analysis.totalChapters);
                writer.println("字数: " + analysis.wordCount);
                writer.println("段落数: " + analysis.paragraphCount);
                writer.println("标题数: " + analysis.headingCount);
                writer.println("链接数: " + analysis.linkCount);
                writer.println("图片数: " + analysis.imageCount);
                writer.println("CSS文件数: " + analysis.cssCount);
                writer.println("字体文件数: " + analysis.fontCount);
                writer.println("关键词: " + String.join(", ", analysis.keywords));
                writer.println("----------------------------------------");
            }

            // 汇总统计
            int totalBooks = analyses.size();
            int totalChapters = analyses.stream().mapToInt(a -> a.totalChapters).sum();
            int totalWords = analyses.stream().mapToInt(a -> a.wordCount).sum();
            int totalImages = analyses.stream().mapToInt(a -> a.imageCount).sum();

            writer.println("\n汇总统计:");
            writer.println("总书籍数: " + totalBooks);
            writer.println("总章节数: " + totalChapters);
            writer.println("总字数: " + totalWords);
            writer.println("平均每本书字数: " + (totalWords / totalBooks));
            writer.println("总图片数: " + totalImages);

        } catch (IOException e) {
            System.err.println("生成报告失败: " + e.getMessage());
        }
    }

    private String readStreamToString(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    public void shutdown() {
        asyncProcessor.shutdown();
    }

    public static class BookAnalysis {
        public String title;
        public String author;
        public String language;
        public int totalChapters;
        public int wordCount;
        public int paragraphCount;
        public int headingCount;
        public int linkCount;
        public int imageCount;
        public int cssCount;
        public int fontCount;
        public List<String> keywords = new ArrayList<>();
    }
}

// 使用示例
public class AnalyzerExample {
    public static void main(String[] args) {
        AdvancedBookAnalyzer analyzer = new AdvancedBookAnalyzer();

        try {
            List<File> epubFiles = Arrays.asList(
                new File("book1.epub"),
                new File("book2.epub")
            );

            // 批量分析
            analyzer.analyzeMultipleBooks(epubFiles)
                    .thenAccept(analyses -> {
                        // 生成报告
                        analyzer.generateReport(analyses, "book_analysis_report.txt");

                        // 打印摘要
                        System.out.println("分析完成，共处理 " + analyses.size() + " 本书");
                        for (BookAnalysis analysis : analyses) {
                            System.out.printf("《%s》: %d 字, %d 章, %d 张图片%n",
                                analysis.title, analysis.wordCount,
                                analysis.totalChapters, analysis.imageCount);
                        }
                    })
                    .join();

        } finally {
            analyzer.shutdown();
        }
    }
}
```

## 内容提取器

### 3. 智能文本提取器

```java
import fun.lzwi.epubime.api.*;
import fun.lzwi.epubime.epub.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class SmartContentExtractor {
    private final AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

    /**
     * 提取书籍的纯文本内容
     */
    public CompletableFuture<String> extractPlainText(File epubFile) {
        return asyncProcessor.parseBookAsync(epubFile)
                .thenApply(book -> {
                    StringBuilder text = new StringBuilder();
                    EpubBookEnhanced enhanced = new EpubBookEnhanced(book, epubFile);

                    text.append("书名: ").append(enhanced.getTitle()).append("\n");
                    text.append("作者: ").append(enhanced.getAuthor()).append("\n\n");

                    for (EpubChapter chapter : enhanced.getAllChapters()) {
                        try {
                            enhanced.processChapterContent(chapter, inputStream -> {
                                try {
                                    String content = readStreamToString(inputStream);
                                    Document doc = Jsoup.parse(content);

                                    // 提取章节标题
                                    Elements headings = doc.select("h1, h2, h3, h4, h5, h6");
                                    if (!headings.isEmpty()) {
                                        text.append("\n").append(headings.first().text()).append("\n\n");
                                    }

                                    // 提取段落文本
                                    Elements paragraphs = doc.select("p");
                                    for (Element p : paragraphs) {
                                        String paraText = cleanText(p.text());
                                        if (!paraText.trim().isEmpty()) {
                                            text.append(paraText).append("\n\n");
                                        }
                                    }

                                } catch (IOException e) {
                                    System.err.println("处理章节失败: " + chapter.getTitle());
                                }
                            });
                        } catch (Exception e) {
                            System.err.println("提取章节失败: " + chapter.getTitle());
                        }
                    }

                    return text.toString();
                });
    }

    /**
     * 提取结构化内容（保持章节层次）
     */
    public CompletableFuture<BookStructure> extractStructuredContent(File epubFile) {
        return asyncProcessor.parseBookAsync(epubFile)
                .thenApply(book -> {
                    BookStructure structure = new BookStructure();
                    EpubBookEnhanced enhanced = new EpubBookEnhanced(book, epubFile);

                    structure.title = enhanced.getTitle();
                    structure.author = enhanced.getAuthor();
                    structure.language = enhanced.getLanguage();

                    // 递归处理章节
                    processChaptersRecursively(enhanced, book.getChapters(), structure.chapters, 0);

                    return structure;
                });
    }

    private void processChaptersRecursively(EpubBookEnhanced book, List<EpubChapter> chapters,
                                          List<ChapterContent> result, int level) {
        for (EpubChapter chapter : chapters) {
            ChapterContent content = new ChapterContent();
            content.title = chapter.getTitle();
            content.level = level;
            content.href = chapter.getContent();

            // 提取章节内容
            try {
                book.processChapterContent(chapter, inputStream -> {
                    try {
                        String htmlContent = readStreamToString(inputStream);
                        Document doc = Jsoup.parse(htmlContent);

                        // 提取纯文本
                        content.text = doc.text();

                        // 提取段落
                        Elements paragraphs = doc.select("p");
                        content.paragraphs = paragraphs.stream()
                                .map(Element::text)
                                .filter(text -> !text.trim().isEmpty())
                                .collect(Collectors.toList());

                        // 提取图片引用
                        Elements images = doc.select("img");
                        content.images = images.stream()
                                .map(img -> img.attr("src"))
                                .collect(Collectors.toList());

                        // 提取链接
                        Elements links = doc.select("a[href]");
                        content.links = links.stream()
                                .map(link -> new Link(link.text(), link.attr("href")))
                                .collect(Collectors.toList());

                    } catch (IOException e) {
                        content.text = "内容提取失败";
                    }
                });
            } catch (Exception e) {
                content.text = "章节处理失败";
            }

            result.add(content);

            // 处理子章节
            if (chapter.hasChildren()) {
                processChaptersRecursively(book, chapter.getChildren(), result, level + 1);
            }
        }
    }

    /**
     * 提取特定类型的内容
     */
    public CompletableFuture<ContentByType> extractContentByType(File epubFile, ContentType type) {
        return asyncProcessor.parseBookAsync(epubFile)
                .thenApply(book -> {
                    ContentByType result = new ContentByType();
                    EpubBookEnhanced enhanced = new EpubBookEnhanced(book, epubFile);

                    result.title = enhanced.getTitle();

                    for (EpubChapter chapter : enhanced.getAllChapters()) {
                        try {
                            enhanced.processChapterContent(chapter, inputStream -> {
                                try {
                                    String content = readStreamToString(inputStream);
                                    Document doc = Jsoup.parse(content);

                                    switch (type) {
                                        case HEADINGS:
                                            Elements headings = doc.select("h1, h2, h3, h4, h5, h6");
                                            result.content.addAll(headings.stream()
                                                    .map(Element::text)
                                                    .collect(Collectors.toList()));
                                            break;

                                        case LINKS:
                                            Elements links = doc.select("a[href]");
                                            result.content.addAll(links.stream()
                                                    .map(link -> link.attr("href"))
                                                    .collect(Collectors.toList()));
                                            break;

                                        case IMAGES:
                                            Elements images = doc.select("img");
                                            result.content.addAll(images.stream()
                                                    .map(img -> img.attr("src"))
                                                    .collect(Collectors.toList()));
                                            break;

                                        case TABLES:
                                            Elements tables = doc.select("table");
                                            for (Element table : tables) {
                                                result.content.add(table.text());
                                            }
                                            break;

                                        case CODE_BLOCKS:
                                            Elements codeBlocks = doc.select("pre, code");
                                            result.content.addAll(codeBlocks.stream()
                                                    .map(Element::text)
                                                    .collect(Collectors.toList()));
                                            break;
                                    }

                                } catch (IOException e) {
                                    // 忽略处理错误
                                }
                            });
                        } catch (Exception e) {
                            // 忽略处理错误
                        }
                    }

                    return result;
                });
    }

    /**
     * 搜索书籍内容
     */
    public CompletableFuture<SearchResults> searchContent(File epubFile, String query, boolean caseSensitive) {
        return asyncProcessor.parseBookAsync(epubFile)
                .thenApply(book -> {
                    SearchResults results = new SearchResults();
                    EpubBookEnhanced enhanced = new EpubBookEnhanced(book, epubFile);

                    results.bookTitle = enhanced.getTitle();
                    results.query = query;

                    Pattern pattern = Pattern.compile(caseSensitive ? query : "(?i)" + query);

                    for (EpubChapter chapter : enhanced.getAllChapters()) {
                        try {
                            enhanced.processChapterContent(chapter, inputStream -> {
                                try {
                                    String content = readStreamToString(inputStream);
                                    Document doc = Jsoup.parse(content);
                                    String text = doc.text();

                                    // 查找匹配
                                    java.util.regex.Matcher matcher = pattern.matcher(text);
                                    while (matcher.find()) {
                                        SearchResult result = new SearchResult();
                                        result.chapterTitle = chapter.getTitle();
                                        result.matchText = extractContext(text, matcher.start(), matcher.end());
                                        result.position = matcher.start();
                                        results.matches.add(result);
                                    }

                                } catch (IOException e) {
                                    // 忽略处理错误
                                }
                            });
                        } catch (Exception e) {
                            // 忽略处理错误
                        }
                    }

                    return results;
                });
    }

    private String extractContext(String text, int start, int end) {
        int contextStart = Math.max(0, start - 50);
        int contextEnd = Math.min(text.length(), end + 50);
        String context = text.substring(contextStart, contextEnd);

        if (contextStart > 0) context = "..." + context;
        if (contextEnd < text.length()) context = context + "...";

        return context;
    }

    private String cleanText(String text) {
        // 清理多余的空白字符
        return text.replaceAll("\\s+", " ").trim();
    }

    private String readStreamToString(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    public void shutdown() {
        asyncProcessor.shutdown();
    }

    // 数据类
    public enum ContentType {
        HEADINGS, LINKS, IMAGES, TABLES, CODE_BLOCKS
    }

    public static class BookStructure {
        public String title;
        public String author;
        public String language;
        public List<ChapterContent> chapters = new ArrayList<>();
    }

    public static class ChapterContent {
        public String title;
        public int level;
        public String href;
        public String text;
        public List<String> paragraphs = new ArrayList<>();
        public List<String> images = new ArrayList<>();
        public List<Link> links = new ArrayList<>();
    }

    public static class Link {
        public final String text;
        public final String href;

        public Link(String text, String href) {
            this.text = text;
            this.href = href;
        }
    }

    public static class ContentByType {
        public String title;
        public List<String> content = new ArrayList<>();
    }

    public static class SearchResults {
        public String bookTitle;
        public String query;
        public List<SearchResult> matches = new ArrayList<>();
    }

    public static class SearchResult {
        public String chapterTitle;
        public String matchText;
        public int position;
    }
}

// 使用示例
public class ExtractorExample {
    public static void main(String[] args) {
        SmartContentExtractor extractor = new SmartContentExtractor();

        try {
            File epubFile = new File("sample.epub");

            // 提取纯文本
            extractor.extractPlainText(epubFile)
                    .thenAccept(text -> {
                        try (PrintWriter writer = new PrintWriter("book_text.txt")) {
                            writer.print(text);
                            System.out.println("纯文本已提取到 book_text.txt");
                        } catch (IOException e) {
                            System.err.println("保存文本失败: " + e.getMessage());
                        }
                    })
                    .join();

            // 提取结构化内容
            extractor.extractStructuredContent(epubFile)
                    .thenAccept(structure -> {
                        System.out.println("书名: " + structure.title);
                        System.out.println("章节数: " + structure.chapters.size());

                        for (SmartContentExtractor.ChapterContent chapter : structure.chapters) {
                            System.out.println("  ".repeat(chapter.level) + chapter.title +
                                             " (" + chapter.paragraphs.size() + " 段)");
                        }
                    })
                    .join();

            // 搜索内容
            extractor.searchContent(epubFile, "重要", false)
                    .thenAccept(results -> {
                        System.out.println("在《" + results.bookTitle + "》中找到 " +
                                         results.matches.size() + " 处匹配 \"" + results.query + "\"");

                        for (int i = 0; i < Math.min(5, results.matches.size()); i++) {
                            SmartContentExtractor.SearchResult match = results.matches.get(i);
                            System.out.println("  " + match.chapterTitle + ": " + match.matchText);
                        }
                    })
                    .join();

        } finally {
            extractor.shutdown();
        }
    }
}
```

## 格式转换器

### 4. EPUB 到其他格式的转换器

```java
import fun.lzwi.epubime.api.*;
import fun.lzwi.epubime.epub.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class EpubConverter {
    private final AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();

    /**
     * 转换为 Markdown 格式
     */
    public CompletableFuture<Void> convertToMarkdown(File epubFile, File outputDir) {
        return asyncProcessor.parseBookAsync(epubFile)
                .thenCompose(book -> {
                    EpubBookEnhanced enhanced = new EpubBookEnhanced(book, epubFile);
                    String bookTitle = enhanced.getTitle().replaceAll("[\\\\/:*?\"<>|]", "_");

                    Path bookDir = outputDir.toPath().resolve(bookTitle);
                    try {
                        Files.createDirectories(bookDir);
                    } catch (IOException e) {
                        throw new RuntimeException("创建输出目录失败", e);
                    }

                    List<CompletableFuture<Void>> futures = new ArrayList<>();

                    // 转换每个章节
                    for (EpubChapter chapter : enhanced.getAllChapters()) {
                        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                            try {
                                enhanced.processChapterContent(chapter, inputStream -> {
                                    try {
                                        String htmlContent = readStreamToString(inputStream);
                                        String markdown = convertHtmlToMarkdown(htmlContent, chapter.getTitle());

                                        String filename = generateMarkdownFilename(chapter);
                                        Path outputFile = bookDir.resolve(filename);

                                        Files.write(outputFile, markdown.getBytes("UTF-8"));
                                        System.out.println("已转换: " + filename);

                                    } catch (IOException e) {
                                        System.err.println("转换章节失败: " + chapter.getTitle());
                                    }
                                });
                            } catch (Exception e) {
                                System.err.println("处理章节失败: " + chapter.getTitle());
                            }
                        });

                        futures.add(future);
                    }

                    // 复制资源文件
                    futures.add(copyResources(enhanced, bookDir));

                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                });
    }

    private String convertHtmlToMarkdown(String htmlContent, String chapterTitle) {
        StringBuilder markdown = new StringBuilder();

        // 添加章节标题
        if (chapterTitle != null && !chapterTitle.trim().isEmpty()) {
            markdown.append("# ").append(chapterTitle).append("\n\n");
        }

        Document doc = Jsoup.parse(htmlContent);

        // 转换标题
        Elements headings = doc.select("h1, h2, h3, h4, h5, h6");
        for (Element heading : headings) {
            int level = Integer.parseInt(heading.tagName().substring(1));
            markdown.append("#".repeat(level)).append(" ").append(heading.text()).append("\n\n");
        }

        // 转换段落
        Elements paragraphs = doc.select("p");
        for (Element p : paragraphs) {
            markdown.append(p.text()).append("\n\n");
        }

        // 转换列表
        Elements lists = doc.select("ul, ol");
        for (Element list : lists) {
            Elements items = list.select("li");
            for (Element item : items) {
                markdown.append("- ").append(item.text()).append("\n");
            }
            markdown.append("\n");
        }

        // 转换链接
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String text = link.text();
            String href = link.attr("href");
            markdown.append("[").append(text).append("](").append(href).append(")");
        }

        // 转换图片
        Elements images = doc.select("img");
        for (Element img : images) {
            String src = img.attr("src");
            String alt = img.attr("alt");
            markdown.append("![").append(alt).append("](").append(src).append(")\n\n");
        }

        return markdown.toString();
    }

    private String generateMarkdownFilename(EpubChapter chapter) {
        String title = chapter.getTitle();
        if (title == null || title.trim().isEmpty()) {
            title = "chapter";
        }

        // 清理文件名
        title = title.replaceAll("[\\\\/:*?\"<>|]", "_");
        title = title.replaceAll("\\s+", "_");

        return title + ".md";
    }

    private CompletableFuture<Void> copyResources(EpubBookEnhanced book, Path bookDir) {
        return CompletableFuture.runAsync(() -> {
            try {
                Path resourcesDir = bookDir.resolve("resources");
                Files.createDirectories(resourcesDir);

                List<EpubResource> resources = new ArrayList<>();
                resources.addAll(book.getImageResources());
                resources.addAll(book.getCssResources());
                resources.addAll(book.getFontResources());

                for (EpubResource resource : resources) {
                    try {
                        byte[] data = resource.getData();
                        if (data != null) {
                            String filename = Paths.get(resource.getHref()).getFileName().toString();
                            Path resourceFile = resourcesDir.resolve(filename);
                            Files.write(resourceFile, data);
                        }
                    } catch (Exception e) {
                        System.err.println("复制资源失败: " + resource.getHref());
                    }
                }

            } catch (IOException e) {
                System.err.println("创建资源目录失败");
            }
        });
    }

    /**
     * 转换为单文件 HTML
     */
    public CompletableFuture<Void> convertToSingleHtml(File epubFile, File outputFile) {
        return asyncProcessor.parseBookAsync(epubFile)
                .thenAccept(book -> {
                    try {
                        EpubBookEnhanced enhanced = new EpubBookEnhanced(book, epubFile);

                        StringBuilder html = new StringBuilder();
                        html.append("<!DOCTYPE html>\n");
                        html.append("<html>\n<head>\n");
                        html.append("<meta charset=\"UTF-8\">\n");
                        html.append("<title>").append(enhanced.getTitle()).append("</title>\n");
                        html.append("<style>\n");
                        html.append("body { font-family: Arial, sans-serif; margin: 40px; }\n");
                        html.append("h1 { color: #333; border-bottom: 2px solid #333; padding-bottom: 10px; }\n");
                        html.append("h2 { color: #666; margin-top: 30px; }\n");
                        html.append("p { line-height: 1.6; margin-bottom: 15px; }\n");
                        html.append("</style>\n");
                        html.append("</head>\n<body>\n");

                        html.append("<h1>").append(enhanced.getTitle()).append("</h1>\n");
                        html.append("<p><strong>作者:</strong> ").append(enhanced.getAuthor()).append("</p>\n");
                        html.append("<p><strong>语言:</strong> ").append(enhanced.getLanguage()).append("</p>\n");
                        html.append("<hr>\n");

                        // 处理所有章节
                        for (EpubChapter chapter : enhanced.getAllChapters()) {
                            try {
                                enhanced.processChapterContent(chapter, inputStream -> {
                                    try {
                                        String content = readStreamToString(inputStream);
                                        Document doc = Jsoup.parse(content);

                                        // 添加章节标题
                                        if (chapter.getTitle() != null) {
                                            html.append("<h2>").append(chapter.getTitle()).append("</h2>\n");
                                        }

                                        // 添加章节内容
                                        Elements paragraphs = doc.select("p");
                                        for (Element p : paragraphs) {
                                            html.append("<p>").append(p.html()).append("</p>\n");
                                        }

                                    } catch (IOException e) {
                                        html.append("<p><em>章节内容加载失败</em></p>\n");
                                    }
                                });
                            } catch (Exception e) {
                                html.append("<p><em>处理章节失败: ").append(chapter.getTitle()).append("</em></p>\n");
                            }
                        }

                        html.append("</body>\n</html>");

                        Files.write(outputFile.toPath(), html.toString().getBytes("UTF-8"));
                        System.out.println("HTML 文件已生成: " + outputFile.getName());

                    } catch (Exception e) {
                        System.err.println("转换失败: " + e.getMessage());
                    }
                });
    }

    /**
     * 转换为纯文本格式
     */
    public CompletableFuture<Void> convertToText(File epubFile, File outputFile) {
        return asyncProcessor.parseBookAsync(epubFile)
                .thenAccept(book -> {
                    try {
                        EpubBookEnhanced enhanced = new EpubBookEnhanced(book, epubFile);

                        StringBuilder text = new StringBuilder();
                        text.append(enhanced.getTitle()).append("\n");
                        text.append("作者: ").append(enhanced.getAuthor()).append("\n");
                        text.append("语言: ").append(enhanced.getLanguage()).append("\n");
                        text.append("=".repeat(50)).append("\n\n");

                        for (EpubChapter chapter : enhanced.getAllChapters()) {
                            try {
                                enhanced.processChapterContent(chapter, inputStream -> {
                                    try {
                                        String content = readStreamToString(inputStream);
                                        Document doc = Jsoup.parse(content);

                                        // 添加章节标题
                                        if (chapter.getTitle() != null) {
                                            text.append(chapter.getTitle()).append("\n");
                                            text.append("-".repeat(Math.min(chapter.getTitle().length(), 30))).append("\n\n");
                                        }

                                        // 添加段落内容
                                        Elements paragraphs = doc.select("p");
                                        for (Element p : paragraphs) {
                                            text.append(p.text()).append("\n\n");
                                        }

                                        text.append("\n");

                                    } catch (IOException e) {
                                        text.append("[章节内容加载失败]\n\n");
                                    }
                                });
                            } catch (Exception e) {
                                text.append("[处理章节失败: ").append(chapter.getTitle()).append("]\n\n");
                            }
                        }

                        Files.write(outputFile.toPath(), text.toString().getBytes("UTF-8"));
                        System.out.println("文本文件已生成: " + outputFile.getName());

                    } catch (Exception e) {
                        System.err.println("转换失败: " + e.getMessage());
                    }
                });
    }

    private String readStreamToString(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    public void shutdown() {
        asyncProcessor.shutdown();
    }
}

// 使用示例
public class ConverterExample {
    public static void main(String[] args) {
        EpubConverter converter = new EpubConverter();

        try {
            File epubFile = new File("sample.epub");
            File outputDir = new File("output");

            // 转换为 Markdown
            converter.convertToMarkdown(epubFile, outputDir)
                    .thenRun(() -> System.out.println("Markdown 转换完成"))
                    .join();

            // 转换为单文件 HTML
            File htmlFile = new File("output/book.html");
            converter.convertToSingleHtml(epubFile, htmlFile)
                    .thenRun(() -> System.out.println("HTML 转换完成"))
                    .join();

            // 转换为纯文本
            File textFile = new File("output/book.txt");
            converter.convertToText(epubFile, textFile)
                    .thenRun(() -> System.out.println("文本转换完成"))
                    .join();

        } finally {
            converter.shutdown();
        }
    }
}
```

这些示例展示了如何使用 EPUBime 库构建实际的应用程序。通过这些例子，您可以了解如何组合使用库的各种功能来解决实际问题。</content>
<parameter name="filePath">docs/practical-examples.md