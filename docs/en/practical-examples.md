---
outline: deep
---

# Practical Application Examples

This section provides complete examples of EPUBime library in real applications, demonstrating how to build book managers, content extractors, and format converters.

## Book Library Manager

### 1. Simple Book Catalog Manager

```java
import fun.lzwi.epubime.api.*;
import fun.lzwi.epubime.epub.*;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class BookLibraryManager {
    private final Map<String, EpubReader.EpubInfo> library = new HashMap<>();

    /**
     * Add a book to the library
     */
    public void addBook(File epubFile) {
        try {
            EpubReader.EpubInfo info = EpubReader.fromFile(epubFile).getInfo();
            library.put(epubFile.getAbsolutePath(), info);
            System.out.println("Added book: " + info.getTitle());
        } catch (Exception e) {
            System.err.println("Failed to add book: " + epubFile.getName() + " - " + e.getMessage());
        }
    }

    /**
     * Search books by author
     */
    public List<EpubReader.EpubInfo> findBooksByAuthor(String author) {
        return library.values().stream()
                .filter(info -> info.getAuthor() != null &&
                               info.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Search books by title
     */
    public List<EpubReader.EpubInfo> findBooksByTitle(String title) {
        return library.values().stream()
                .filter(info -> info.getTitle() != null &&
                               info.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Get library statistics
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
     * Export library catalog to CSV
     */
    public void exportToCSV(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Title,Author,Language,Chapter Count,File Size");
            for (EpubReader.EpubInfo info : library.values()) {
                writer.printf("\"%s\",\"%s\",\"%s\",%d,%d%n",
                    info.getTitle(), info.getAuthor(), info.getLanguage(),
                    info.getChapterCount(), info.getFileSize());
            }
            System.out.println("Catalog exported to: " + filename);
        } catch (IOException e) {
            System.err.println("Export failed: " + e.getMessage());
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
            return String.format("Library Statistics: %d books, Total size: %.2f MB, Total chapters: %d",
                totalBooks, totalSize / (1024.0 * 1024.0), totalChapters);
        }
    }
}

// Usage example
public class LibraryExample {
    public static void main(String[] args) {
        BookLibraryManager library = new BookLibraryManager();

        // Add multiple books
        File booksDir = new File("books");
        if (booksDir.exists() && booksDir.isDirectory()) {
            for (File file : booksDir.listFiles((dir, name) -> name.endsWith(".epub"))) {
                library.addBook(file);
            }
        }

        // Search and statistics
        List<EpubReader.EpubInfo> tolstoyBooks = library.findBooksByAuthor("Tolstoy");
        System.out.println("Tolstoy's books: " + tolstoyBooks.size());

        BookLibraryManager.LibraryStats stats = library.getStats();
        System.out.println(stats);

        // Export catalog
        library.exportToCSV("library_catalog.csv");
    }
}
```

### 2. Advanced Book Analyzer

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
     * Analyze detailed book statistics
     */
    public CompletableFuture<BookAnalysis> analyzeBook(File epubFile) {
        return asyncProcessor.parseBookAsync(epubFile)
                .thenApply(book -> {
                    try {
                        EpubBookEnhanced enhanced = new EpubBookEnhanced(book, epubFile);

                        // Basic information
                        BookAnalysis analysis = new BookAnalysis();
                        analysis.title = enhanced.getTitle();
                        analysis.author = enhanced.getAuthor();
                        analysis.language = enhanced.getLanguage();
                        analysis.totalChapters = enhanced.getChapterCount();

                        // Resource statistics
                        analysis.imageCount = enhanced.getImageResources().size();
                        analysis.cssCount = enhanced.getCssResources().size();
                        analysis.fontCount = enhanced.getFontResources().size();

                        // Content analysis
                        analyzeContent(enhanced, analysis);

                        return analysis;

                    } catch (Exception e) {
                        throw new RuntimeException("Analysis failed: " + epubFile.getName(), e);
                    }
                });
    }

    /**
     * Analyze book content
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

                        // Count words
                        String text = doc.text();
                        analysis.wordCount += text.split("\\s+").length;

                        // Count paragraphs
                        Elements paragraphs = doc.select("p");
                        analysis.paragraphCount += paragraphs.size();

                        // Count headings
                        Elements headings = doc.select("h1, h2, h3, h4, h5, h6");
                        analysis.headingCount += headings.size();

                        // Count links
                        Elements links = doc.select("a[href]");
                        analysis.linkCount += links.size();

                        // Extract keywords
                        extractKeywords(text, analysis);

                    } catch (IOException e) {
                        System.err.println("Failed to process chapter content: " + chapter.getTitle());
                    }
                });
            } catch (Exception e) {
                System.err.println("Failed to process chapter: " + chapter.getTitle());
            }
        }
    }

    /**
     * Extract keywords
     */
    private void extractKeywords(String text, BookAnalysis analysis) {
        // Simple keyword extraction (can be extended to more complex algorithms)
        String[] words = text.toLowerCase().split("\\W+");
        Map<String, Integer> wordFreq = new HashMap<>();

        for (String word : words) {
            if (word.length() > 3) { // Only count words longer than 3 characters
                wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
            }
        }

        // Take the top 10 most frequent words as keywords
        analysis.keywords = wordFreq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Batch analyze multiple books
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
     * Generate analysis report
     */
    public void generateReport(List<BookAnalysis> analyses, String outputFile) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("EPUB Book Analysis Report");
            writer.println("Generated at: " + new Date());
            writer.println("========================================");

            for (BookAnalysis analysis : analyses) {
                writer.println("\nTitle: " + analysis.title);
                writer.println("Author: " + analysis.author);
                writer.println("Language: " + analysis.language);
                writer.println("Total Chapters: " + analysis.totalChapters);
                writer.println("Word Count: " + analysis.wordCount);
                writer.println("Paragraph Count: " + analysis.paragraphCount);
                writer.println("Heading Count: " + analysis.headingCount);
                writer.println("Link Count: " + analysis.linkCount);
                writer.println("Image Count: " + analysis.imageCount);
                writer.println("CSS Files: " + analysis.cssCount);
                writer.println("Font Files: " + analysis.fontCount);
                writer.println("Keywords: " + String.join(", ", analysis.keywords));
                writer.println("----------------------------------------");
            }

            // Summary statistics
            int totalBooks = analyses.size();
            int totalChapters = analyses.stream().mapToInt(a -> a.totalChapters).sum();
            int totalWords = analyses.stream().mapToInt(a -> a.wordCount).sum();
            int totalImages = analyses.stream().mapToInt(a -> a.imageCount).sum();

            writer.println("\nSummary Statistics:");
            writer.println("Total Books: " + totalBooks);
            writer.println("Total Chapters: " + totalChapters);
            writer.println("Total Words: " + totalWords);
            writer.println("Average Words per Book: " + (totalWords / totalBooks));
            writer.println("Total Images: " + totalImages);

        } catch (IOException e) {
            System.err.println("Failed to generate report: " + e.getMessage());
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

// Usage example
public class AnalyzerExample {
    public static void main(String[] args) {
        AdvancedBookAnalyzer analyzer = new AdvancedBookAnalyzer();

        try {
            List<File> epubFiles = Arrays.asList(
                new File("book1.epub"),
                new File("book2.epub")
            );

            // Batch analysis
            analyzer.analyzeMultipleBooks(epubFiles)
                    .thenAccept(analyses -> {
                        // Generate report
                        analyzer.generateReport(analyses, "book_analysis_report.txt");

                        // Print summary
                        System.out.println("Analysis completed, processed " + analyses.size() + " books");
                        for (BookAnalysis analysis : analyses) {
                            System.out.printf("'%s': %d words, %d chapters, %d images%n",
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

## Content Extractor

### 3. Smart Text Extractor

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
     * Extract plain text content from book
     */
    public CompletableFuture<String> extractPlainText(File epubFile) {
        return asyncProcessor.parseBookAsync(epubFile)
                .thenApply(book -> {
                    StringBuilder text = new StringBuilder();
                    EpubBookEnhanced enhanced = new EpubBookEnhanced(book, epubFile);

                    text.append("Title: ").append(enhanced.getTitle()).append("\n");
                    text.append("Author: ").append(enhanced.getAuthor()).append("\n\n");

                    for (EpubChapter chapter : enhanced.getAllChapters()) {
                        try {
                            enhanced.processChapterContent(chapter, inputStream -> {
                                try {
                                    String content = readStreamToString(inputStream);
                                    Document doc = Jsoup.parse(content);

                                    // Extract chapter title
                                    Elements headings = doc.select("h1, h2, h3, h4, h5, h6");
                                    if (!headings.isEmpty()) {
                                        text.append("\n").append(headings.first().text()).append("\n\n");
                                    }

                                    // Extract paragraph text
                                    Elements paragraphs = doc.select("p");
                                    for (Element p : paragraphs) {
                                        String paraText = cleanText(p.text());
                                        if (!paraText.trim().isEmpty()) {
                                            text.append(paraText).append("\n\n");
                                        }
                                    }

                                } catch (IOException e) {
                                    System.err.println("Failed to process chapter content: " + chapter.getTitle());
                                }
                            });
                        } catch (Exception e) {
                            System.err.println("Failed to extract chapter: " + chapter.getTitle());
                        }
                    }

                    return text.toString();
                });
    }

    /**
     * Extract structured content (preserving chapter hierarchy)
     */
    public CompletableFuture<BookStructure> extractStructuredContent(File epubFile) {
        return asyncProcessor.parseBookAsync(epubFile)
                .thenApply(book -> {
                    BookStructure structure = new BookStructure();
                    EpubBookEnhanced enhanced = new EpubBookEnhanced(book, epubFile);

                    structure.title = enhanced.getTitle();
                    structure.author = enhanced.getAuthor();
                    structure.language = enhanced.getLanguage();

                    // Recursively process chapters
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

            // Extract chapter content
            try {
                book.processChapterContent(chapter, inputStream -> {
                    try {
                        String htmlContent = readStreamToString(inputStream);
                        Document doc = Jsoup.parse(htmlContent);

                        // Extract plain text
                        content.text = doc.text();

                        // Extract paragraphs
                        Elements paragraphs = doc.select("p");
                        content.paragraphs = paragraphs.stream()
                                .map(Element::text)
                                .filter(text -> !text.trim().isEmpty())
                                .collect(Collectors.toList());

                        // Extract image references
                        Elements images = doc.select("img");
                        content.images = images.stream()
                                .map(img -> img.attr("src"))
                                .collect(Collectors.toList());

                        // Extract links
                        Elements links = doc.select("a[href]");
                        content.links = links.stream()
                                .map(link -> new Link(link.text(), link.attr("href")))
                                .collect(Collectors.toList());

                    } catch (IOException e) {
                        content.text = "Content extraction failed";
                    }
                });
            } catch (Exception e) {
                content.text = "Chapter processing failed";
            }

            result.add(content);

            // Process child chapters
            if (chapter.hasChildren()) {
                processChaptersRecursively(book, chapter.getChildren(), result, level + 1);
            }
        }
    }

    /**
     * Extract specific types of content
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
                                    // Ignore processing errors
                                }
                            });
                        } catch (Exception e) {
                            // Ignore processing errors
                        }
                    }

                    return result;
                });
    }

    /**
     * Search book content
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

                                    // Find matches
                                    java.util.regex.Matcher matcher = pattern.matcher(text);
                                    while (matcher.find()) {
                                        SearchResult result = new SearchResult();
                                        result.chapterTitle = chapter.getTitle();
                                        result.matchText = extractContext(text, matcher.start(), matcher.end());
                                        result.position = matcher.start();
                                        results.matches.add(result);
                                    }

                                } catch (IOException e) {
                                    // Ignore processing errors
                                }
                            });
                        } catch (Exception e) {
                            // Ignore processing errors
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
        // Clean extra whitespace
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

    // Data classes
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

// Usage example
public class ExtractorExample {
    public static void main(String[] args) {
        SmartContentExtractor extractor = new SmartContentExtractor();

        try {
            File epubFile = new File("sample.epub");

            // Extract plain text
            extractor.extractPlainText(epubFile)
                    .thenAccept(text -> {
                        try (PrintWriter writer = new PrintWriter("book_text.txt")) {
                            writer.print(text);
                            System.out.println("Plain text extracted to book_text.txt");
                        } catch (IOException e) {
                            System.err.println("Failed to save text: " + e.getMessage());
                        }
                    })
                    .join();

            // Extract structured content
            extractor.extractStructuredContent(epubFile)
                    .thenAccept(structure -> {
                        System.out.println("Title: " + structure.title);
                        System.out.println("Total chapters: " + structure.chapters.size());

                        for (SmartContentExtractor.ChapterContent chapter : structure.chapters) {
                            System.out.println("  ".repeat(chapter.level) + chapter.title +
                                             " (" + chapter.paragraphs.size() + " paragraphs)");
                        }
                    })
                    .join();

            // Search content
            extractor.searchContent(epubFile, "important", false)
                    .thenAccept(results -> {
                        System.out.println("Found " + results.matches.size() + " matches for \"" +
                                         results.query + "\" in '" + results.bookTitle + "'");

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

## Format Converter

### 4. EPUB to Other Formats Converter

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
     * Convert to Markdown format
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
                        throw new RuntimeException("Failed to create output directory", e);
                    }

                    List<CompletableFuture<Void>> futures = new ArrayList<>();

                    // Convert each chapter
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
                                        System.out.println("Converted: " + filename);

                                    } catch (IOException e) {
                                        System.err.println("Failed to convert chapter: " + chapter.getTitle());
                                    }
                                });
                            } catch (Exception e) {
                                System.err.println("Failed to process chapter: " + chapter.getTitle());
                            }
                        });

                        futures.add(future);
                    }

                    // Copy resources
                    futures.add(copyResources(enhanced, bookDir));

                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                });
    }

    private String convertHtmlToMarkdown(String htmlContent, String chapterTitle) {
        StringBuilder markdown = new StringBuilder();

        // Add chapter title
        if (chapterTitle != null && !chapterTitle.trim().isEmpty()) {
            markdown.append("# ").append(chapterTitle).append("\n\n");
        }

        Document doc = Jsoup.parse(htmlContent);

        // Convert headings
        Elements headings = doc.select("h1, h2, h3, h4, h5, h6");
        for (Element heading : headings) {
            int level = Integer.parseInt(heading.tagName().substring(1));
            markdown.append("#".repeat(level)).append(" ").append(heading.text()).append("\n\n");
        }

        // Convert paragraphs
        Elements paragraphs = doc.select("p");
        for (Element p : paragraphs) {
            markdown.append(p.text()).append("\n\n");
        }

        // Convert lists
        Elements lists = doc.select("ul, ol");
        for (Element list : lists) {
            Elements items = list.select("li");
            for (Element item : items) {
                markdown.append("- ").append(item.text()).append("\n");
            }
            markdown.append("\n");
        }

        // Convert links
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String text = link.text();
            String href = link.attr("href");
            markdown.append("[").append(text).append("](").append(href).append(")");
        }

        // Convert images
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

        // Clean filename
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
                        System.err.println("Failed to copy resource: " + resource.getHref());
                    }
                }

            } catch (IOException e) {
                System.err.println("Failed to create resources directory");
            }
        });
    }

    /**
     * Convert to single HTML file
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
                        html.append("<p><strong>Author:</strong> ").append(enhanced.getAuthor()).append("</p>\n");
                        html.append("<p><strong>Language:</strong> ").append(enhanced.getLanguage()).append("</p>\n");
                        html.append("<hr>\n");

                        // Process all chapters
                        for (EpubChapter chapter : enhanced.getAllChapters()) {
                            try {
                                enhanced.processChapterContent(chapter, inputStream -> {
                                    try {
                                        String content = readStreamToString(inputStream);
                                        Document doc = Jsoup.parse(content);

                                        // Add chapter title
                                        if (chapter.getTitle() != null) {
                                            html.append("<h2>").append(chapter.getTitle()).append("</h2>\n");
                                        }

                                        // Add chapter content
                                        Elements paragraphs = doc.select("p");
                                        for (Element p : paragraphs) {
                                            html.append("<p>").append(p.html()).append("</p>\n");
                                        }

                                    } catch (IOException e) {
                                        html.append("<p><em>Chapter content failed to load</em></p>\n");
                                    }
                                });
                            } catch (Exception e) {
                                html.append("<p><em>Failed to process chapter: ").append(chapter.getTitle()).append("</em></p>\n");
                            }
                        }

                        html.append("</body>\n</html>");

                        Files.write(outputFile.toPath(), html.toString().getBytes("UTF-8"));
                        System.out.println("HTML file generated: " + outputFile.getName());

                    } catch (Exception e) {
                        System.err.println("Conversion failed: " + e.getMessage());
                    }
                });
    }

    /**
     * Convert to plain text format
     */
    public CompletableFuture<Void> convertToText(File epubFile, File outputFile) {
        return asyncProcessor.parseBookAsync(epubFile)
                .thenAccept(book -> {
                    try {
                        EpubBookEnhanced enhanced = new EpubBookEnhanced(book, epubFile);

                        StringBuilder text = new StringBuilder();
                        text.append(enhanced.getTitle()).append("\n");
                        text.append("Author: ").append(enhanced.getAuthor()).append("\n");
                        text.append("Language: ").append(enhanced.getLanguage()).append("\n");
                        text.append("=".repeat(50)).append("\n\n");

                        for (EpubChapter chapter : enhanced.getAllChapters()) {
                            try {
                                enhanced.processChapterContent(chapter, inputStream -> {
                                    try {
                                        String content = readStreamToString(inputStream);
                                        Document doc = Jsoup.parse(content);

                                        // Add chapter title
                                        if (chapter.getTitle() != null) {
                                            text.append(chapter.getTitle()).append("\n");
                                            text.append("-".repeat(Math.min(chapter.getTitle().length(), 30))).append("\n\n");
                                        }

                                        // Add paragraph content
                                        Elements paragraphs = doc.select("p");
                                        for (Element p : paragraphs) {
                                            text.append(p.text()).append("\n\n");
                                        }

                                    } catch (IOException e) {
                                        text.append("[Chapter content failed to load]\n\n");
                                    }
                                });
                            } catch (Exception e) {
                                text.append("[Failed to process chapter: ").append(chapter.getTitle()).append("]\n\n");
                            }
                        }

                        Files.write(outputFile.toPath(), text.toString().getBytes("UTF-8"));
                        System.out.println("Text file generated: " + outputFile.getName());

                    } catch (Exception e) {
                        System.err.println("Conversion failed: " + e.getMessage());
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

// Usage example
public class ConverterExample {
    public static void main(String[] args) {
        EpubConverter converter = new EpubConverter();

        try {
            File epubFile = new File("sample.epub");
            File outputDir = new File("output");

            // Convert to Markdown
            converter.convertToMarkdown(epubFile, outputDir)
                    .thenRun(() -> System.out.println("Markdown conversion completed"))
                    .join();

            // Convert to single HTML
            File htmlFile = new File("output/book.html");
            converter.convertToSingleHtml(epubFile, htmlFile)
                    .thenRun(() -> System.out.println("HTML conversion completed"))
                    .join();

            // Convert to plain text
            File textFile = new File("output/book.txt");
            converter.convertToText(epubFile, textFile)
                    .thenRun(() -> System.out.println("Text conversion completed"))
                    .join();

        } finally {
            converter.shutdown();
        }
    }
}
```

These examples demonstrate how to use the EPUBime library to build real applications. Through these examples, you can understand how to combine various functions of the library to solve practical problems.