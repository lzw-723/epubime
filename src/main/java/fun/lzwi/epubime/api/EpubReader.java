package fun.lzwi.epubime.api;

import fun.lzwi.epubime.epub.EpubBook;
import fun.lzwi.epubime.epub.EpubChapter;
import fun.lzwi.epubime.epub.EpubResource;
import fun.lzwi.epubime.epub.Metadata;
import fun.lzwi.epubime.epub.EpubBookProcessor;
import fun.lzwi.epubime.epub.EpubStreamProcessor;
import fun.lzwi.epubime.exception.BaseEpubException;
import fun.lzwi.epubime.exception.EpubPathValidationException;
import fun.lzwi.epubime.epub.EpubParser;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Fluent API for EPUB reading operations
 * Provides a modern, chainable interface for EPUB file processing
 * 
 * Example usage:
 * <pre>
 * EpubBook book = EpubReader.fromFile(new File("book.epub"))
 *     .withCache(true)
 *     .withLazyLoading(true)
 *     .parse();
 * 
 * // Stream processing
 * EpubReader.fromFile(file)
 *     .streamChapters((chapter, content) -> {
 *         System.out.println("Processing: " + chapter.getTitle());
 *         // Process content stream
 *     });
 * </pre>
 */
public class EpubReader {
    private final File epubFile;
    private final EpubReaderConfig config;

    private EpubReader(File epubFile, EpubReaderConfig config) {
        if (epubFile == null) {
            throw new IllegalArgumentException("EPUB file cannot be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        this.epubFile = epubFile;
        this.config = config;
    }
    
    /**
     * Create an EpubReader from a File with default config
     * @param epubFile the EPUB file
     * @return a new EpubReader instance
     */
    public static EpubReader fromFile(File epubFile) {
        return new EpubReader(epubFile, new EpubReaderConfig());
    }

    /**
     * Create an EpubReader from a file path with default config
     * @param filePath the path to the EPUB file
     * @return a new EpubReader instance
     */
    public static EpubReader fromFile(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        return new EpubReader(new File(filePath), new EpubReaderConfig());
    }

    /**
     * Create an EpubReader from a File with custom config
     * @param epubFile the EPUB file
     * @param config the configuration
     * @return a new EpubReader instance
     */
    public static EpubReader fromFile(File epubFile, EpubReaderConfig config) {
        return new EpubReader(epubFile, config);
    }
    

    
    /**
     * 解析EPUB文件
     * @return 解析后的EPUB书籍对象
     * @throws BaseEpubException 解析异常
     */
    public EpubBook parse() throws BaseEpubException, java.io.IOException, EpubPathValidationException {
        EpubParser parser = new EpubParser(epubFile);

        EpubBook book;
        if (config.isUseCache()) {
            book = parser.parse();
        } else {
            book = parser.parseWithoutCache();
        }

        // Ensure all resources have a reference to the EPUB file for streaming
        if (!book.getResources().isEmpty()) {
            for (EpubResource resource : book.getResources()) {
                if (resource.getEpubFile() == null) {
                    resource.setEpubFile(epubFile);
                }
            }
        }

        return book;
    }
    
    /**
     * Parse the metadata from the EPUB file
     * @return the metadata
     * @throws BaseEpubException if parsing fails
     */
    public Metadata parseMetadata() throws BaseEpubException, java.io.IOException, EpubPathValidationException {
        // For now, we'll parse the full book and return just the metadata
        // In a future optimization, this could parse only the metadata section
        return parse().getMetadata();
    }
    
    /**
     * Parse only the table of contents from the EPUB file
     * @return the list of chapters
     * @throws BaseEpubException if parsing fails
     */
    public List<EpubChapter> parseTableOfContents() throws BaseEpubException, java.io.IOException, EpubPathValidationException {
        return parse().getChapters();
    }
    
    /**
     * Stream process chapters without loading entire content into memory
     * @param processor a consumer that processes each chapter and its content stream
     * @throws BaseEpubException if processing fails
     */
    public void streamChapters(BiConsumer<EpubChapter, InputStream> processor) throws BaseEpubException, java.io.IOException, EpubPathValidationException {
        EpubBook book = parse();
        EpubStreamProcessor streamProcessor = new EpubStreamProcessor(epubFile);
        streamProcessor.processBookChapters(book, processor);
    }
    
    /**
     * Stream process a specific chapter
     * @param chapterId the ID of the chapter to process
     * @param processor a consumer that processes the chapter content stream
     * @throws BaseEpubException if processing fails
     * @throws EpubPathValidationException if path validation fails
     */
    public void streamChapter(String chapterId, Consumer<InputStream> processor) throws BaseEpubException, EpubPathValidationException, java.io.IOException {
        EpubBook book = parse();

        // Find the chapter by ID
        EpubChapter targetChapter = findChapterById(book.getChapters(), chapterId);
        if (targetChapter == null) {
            throw new BaseEpubException("Chapter not found: " + chapterId);
        }

        // Stream the chapter content
        EpubStreamProcessor streamProcessor = new EpubStreamProcessor(epubFile);
        streamProcessor.processHtmlChapter(targetChapter.getContent(), processor);
    }
    
    /**
     * Process all resources with a custom function
     * @param processor a function that processes each resource
     * @throws BaseEpubException if processing fails
     */
    public void processResources(Function<EpubResource, Void> processor) throws BaseEpubException, java.io.IOException, EpubPathValidationException {
        EpubBook book = parse();
        List<EpubResource> resources = book.getResources();

        if (config.isParallelProcessing()) {
            resources.parallelStream().forEach(processor::apply);
        } else {
            resources.forEach(processor::apply);
        }
    }
    
    /**
     * Get a specific resource by ID
     * @param resourceId the resource ID
     * @return the resource, or null if not found
     * @throws BaseEpubException if parsing fails
     */
    public EpubResource getResource(String resourceId) throws BaseEpubException, java.io.IOException, EpubPathValidationException {
        EpubBook book = parse();
        return EpubBookProcessor.getResource(book, resourceId);
    }
    
    /**
     * Get the cover image resource
     * @return the cover resource, or null if not found
     * @throws BaseEpubException if parsing fails
     */
    public EpubResource getCover() throws BaseEpubException, java.io.IOException, EpubPathValidationException {
        EpubBook book = parse();
        return EpubBookProcessor.getCover(book);
    }
    
    /**
     * Check if the EPUB file is valid
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        try {
            parseMetadata();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get information about the EPUB file without full parsing
     * @return basic information about the EPUB
     * @throws BaseEpubException if parsing fails
     */
    public EpubInfo getInfo() throws BaseEpubException, java.io.IOException, EpubPathValidationException {
        Metadata metadata = parseMetadata();
        List<EpubChapter> chapters = parseTableOfContents();
        
        return new EpubInfo(
            metadata.getTitle(),
            metadata.getCreator(),
            metadata.getLanguage(),
            chapters.size(),
            epubFile.length()
        );
    }
    
    private EpubChapter findChapterById(List<EpubChapter> chapters, String chapterId) {
        for (EpubChapter chapter : chapters) {
            if (chapterId.equals(chapter.getId())) {
                return chapter;
            }
            // Search in children
            if (chapter.hasChildren()) {
                EpubChapter found = findChapterById(chapter.getChildren(), chapterId);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
    
    /**
     * Basic information about an EPUB file
     */
    public static class EpubInfo {
        private final String title;
        private final String author;
        private final String language;
        private final int chapterCount;
        private final long fileSize;
        
        public EpubInfo(String title, String author, String language, int chapterCount, long fileSize) {
            this.title = title;
            this.author = author;
            this.language = language;
            this.chapterCount = chapterCount;
            this.fileSize = fileSize;
        }
        
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public String getLanguage() { return language; }
        public int getChapterCount() { return chapterCount; }
        public long getFileSize() { return fileSize; }
        
        @Override
        public String toString() {
            return String.format("EpubInfo{title='%s', author='%s', language='%s', chapters=%d, size=%d bytes}",
                    title, author, language, chapterCount, fileSize);
        }
    }
}