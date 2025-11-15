package fun.lzwi.epubime.api;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import fun.lzwi.epubime.epub.EpubBook;
import fun.lzwi.epubime.epub.EpubChapter;
import fun.lzwi.epubime.epub.EpubResource;
import fun.lzwi.epubime.epub.Metadata;
import fun.lzwi.epubime.exception.BaseEpubException;
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
    private boolean useCache = true;
    @SuppressFBWarnings("URF_UNREAD_FIELD") // TODO: Implement lazy loading functionality
    private boolean lazyLoading = false;
    private boolean parallelProcessing = false;
    
    private EpubReader(File epubFile) {
        if (epubFile == null) {
            throw new IllegalArgumentException("EPUB file cannot be null");
        }
        this.epubFile = epubFile;
    }
    
    /**
     * Create an EpubReader from a File
     * @param epubFile the EPUB file
     * @return a new EpubReader instance
     */
    public static EpubReader fromFile(File epubFile) {
        return new EpubReader(epubFile);
    }
    
    /**
     * Create an EpubReader from a file path
     * @param filePath the path to the EPUB file
     * @return a new EpubReader instance
     */
    public static EpubReader fromFile(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        return new EpubReader(new File(filePath));
    }
    
    /**
     * Enable or disable caching
     * @param useCache whether to use caching
     * @return this reader for method chaining
     */
    public EpubReader withCache(boolean useCache) {
        this.useCache = useCache;
        return this;
    }
    
    /**
     * Enable or disable lazy loading of resources
     * @param lazyLoading whether to use lazy loading
     * @return this reader for method chaining
     */
    public EpubReader withLazyLoading(boolean lazyLoading) {
        this.lazyLoading = lazyLoading;
        return this;
    }
    
    /**
     * Enable or disable parallel processing for multiple resources
     * @param parallelProcessing whether to use parallel processing
     * @return this reader for method chaining
     */
    public EpubReader withParallelProcessing(boolean parallelProcessing) {
        this.parallelProcessing = parallelProcessing;
        return this;
    }
    
    /**
     * Parse the EPUB file and return an EpubBook
     * @return the parsed EpubBook
     * @throws BaseEpubException if parsing fails
     */
    public EpubBook parse() throws BaseEpubException {
        EpubParser parser = new EpubParser(epubFile);
        
        EpubBook book;
        if (useCache) {
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
     * Parse only the metadata from the EPUB file
     * @return the metadata
     * @throws BaseEpubException if parsing fails
     */
    public Metadata parseMetadata() throws BaseEpubException {
        // For now, we'll parse the full book and return just the metadata
        // In a future optimization, this could parse only the metadata section
        return parse().getMetadata();
    }
    
    /**
     * Parse only the table of contents from the EPUB file
     * @return the list of chapters
     * @throws BaseEpubException if parsing fails
     */
    public List<EpubChapter> parseTableOfContents() throws BaseEpubException {
        return parse().getChapters();
    }
    
    /**
     * Stream process chapters without loading entire content into memory
     * @param processor a consumer that processes each chapter and its content stream
     * @throws BaseEpubException if processing fails
     */
    public void streamChapters(BiConsumer<EpubChapter, InputStream> processor) throws BaseEpubException {
        EpubBook book = parse();
        
        // Ensure the book has a reference to the EPUB file for streaming
        if (!book.getResources().isEmpty()) {
            for (EpubResource resource : book.getResources()) {
                if (resource.getEpubFile() == null) {
                    resource.setEpubFile(epubFile);
                }
            }
        }
        
        book.processHtmlChapters(processor);
    }
    
    /**
     * Stream process a specific chapter
     * @param chapterId the ID of the chapter to process
     * @param processor a consumer that processes the chapter content stream
     * @throws BaseEpubException if processing fails
     */
    public void streamChapter(String chapterId, Consumer<InputStream> processor) throws BaseEpubException {
        EpubBook book = parse();
        
        // Find the chapter by ID
        EpubChapter targetChapter = findChapterById(book.getChapters(), chapterId);
        if (targetChapter == null) {
            throw new BaseEpubException("Chapter not found: " + chapterId);
        }
        
        // Stream the chapter content
        book.processHtmlChapters((chapter, inputStream) -> {
            if (chapter.getId().equals(chapterId)) {
                processor.accept(inputStream);
            }
        });
    }
    
    /**
     * Process all resources with a custom function
     * @param processor a function that processes each resource
     * @throws BaseEpubException if processing fails
     */
    public void processResources(Function<EpubResource, Void> processor) throws BaseEpubException {
        EpubBook book = parse();
        List<EpubResource> resources = book.getResources();
        
        if (parallelProcessing) {
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
    public EpubResource getResource(String resourceId) throws BaseEpubException {
        EpubBook book = parse();
        return book.getResource(resourceId);
    }
    
    /**
     * Get the cover image resource
     * @return the cover resource, or null if not found
     * @throws BaseEpubException if parsing fails
     */
    public EpubResource getCover() throws BaseEpubException {
        EpubBook book = parse();
        return book.getCover();
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
    public EpubInfo getInfo() throws BaseEpubException {
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