package fun.lzwi.epubime.api;

import fun.lzwi.epubime.epub.EpubBook;
import fun.lzwi.epubime.epub.EpubChapter;
import fun.lzwi.epubime.epub.EpubResource;
import fun.lzwi.epubime.epub.Metadata;
import fun.lzwi.epubime.epub.EpubBookProcessor;
import fun.lzwi.epubime.epub.EpubStreamProcessor;
import fun.lzwi.epubime.exception.BaseEpubException;
import fun.lzwi.epubime.exception.EpubResourceException;
import fun.lzwi.epubime.exception.EpubPathValidationException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Enhanced EpubBook with fluent API methods and improved usability
 * Provides convenient methods for common operations
 */
public class EpubBookEnhanced {
    private final EpubBook book;
    private final File epubFile;
    
    public EpubBookEnhanced(EpubBook book, File epubFile) {
        this.book = new EpubBook(book);
        this.epubFile = epubFile;
    }
    
    /**
     * Get metadata with null-safe access
     * @return metadata, never null
     */
    public Metadata getMetadata() {
        return book.getMetadata();
    }
    
    /**
     * Get the title with fallback to empty string
     * @return book title
     */
    public String getTitle() {
        return Optional.ofNullable(book.getMetadata().getTitle()).orElse("");
    }
    
    /**
     * Get the author with fallback to empty string
     * @return book author
     */
    public String getAuthor() {
        return Optional.ofNullable(book.getMetadata().getCreator()).orElse("");
    }
    
    /**
     * Get the language with fallback to empty string
     * @return book language
     */
    public String getLanguage() {
        return Optional.ofNullable(book.getMetadata().getLanguage()).orElse("");
    }
    
    /**
     * Get all chapters as a flat list (including nested chapters)
     * @return flat list of all chapters
     */
    public List<EpubChapter> getAllChapters() {
        return flattenChapters(book.getChapters());
    }
    
    /**
     * Find a chapter by title (case-insensitive)
     * @param title the chapter title to search for
     * @return the chapter, or null if not found
     */
    public EpubChapter findChapterByTitle(String title) {
        return findChapterByTitleRecursive(book.getChapters(), title);
    }
    
    /**
     * Find chapters by content path pattern
     * @param pattern the pattern to match in the content path
     * @return list of matching chapters
     */
    public List<EpubChapter> findChaptersByContentPattern(String pattern) {
        return getAllChapters().stream()
                .filter(chapter -> chapter.getContent() != null && 
                                 chapter.getContent().contains(pattern))
                .collect(Collectors.toList());
    }
    
    /**
     * Get chapters by navigation type
     * @param type the navigation type ("ncx", "nav", "landmarks", "page-list")
     * @return list of chapters for the specified navigation type
     */
    public List<EpubChapter> getChaptersByType(String type) {
        switch (type.toLowerCase()) {
            case "ncx":
                return book.getNcx();
            case "nav":
                return book.getNav();
            case "landmarks":
                return book.getLandmarks();
            case "page-list":
                return book.getPageList();
            default:
                return book.getChapters();
        }
    }
    
    /**
     * Get the first chapter
     * @return the first chapter, or null if no chapters
     */
    public EpubChapter getFirstChapter() {
        List<EpubChapter> chapters = book.getChapters();
        return chapters.isEmpty() ? null : chapters.get(0);
    }
    
    /**
     * Get the last chapter
     * @return the last chapter, or null if no chapters
     */
    public EpubChapter getLastChapter() {
        List<EpubChapter> chapters = book.getChapters();
        return chapters.isEmpty() ? null : chapters.get(chapters.size() - 1);
    }
    
    /**
     * Get chapter by index
     * @param index the chapter index (0-based)
     * @return the chapter, or null if index is invalid
     */
    public EpubChapter getChapter(int index) {
        List<EpubChapter> chapters = book.getChapters();
        if (index >= 0 && index < chapters.size()) {
            return chapters.get(index);
        }
        return null;
    }
    
    /**
     * Get total chapter count (including nested chapters)
     * @return total number of chapters
     */
    public int getChapterCount() {
        return getAllChapters().size();
    }
    
    /**
     * Get resources by type
     * @param mimeType the MIME type to filter by
     * @return list of resources with the specified type
     */
    public List<EpubResource> getResourcesByType(String mimeType) {
        return book.getResources().stream()
                .filter(resource -> mimeType.equals(resource.getType()))
                .collect(Collectors.toList());
    }
    
    /**
     * Get image resources
     * @return list of image resources
     */
    public List<EpubResource> getImageResources() {
        return book.getResources().stream()
                .filter(resource -> {
                    String type = resource.getType();
                    return type != null && type.startsWith("image/");
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get CSS resources
     * @return list of CSS resources
     */
    public List<EpubResource> getCssResources() {
        return getResourcesByType("text/css");
    }
    
    /**
     * Get JavaScript resources
     * @return list of JavaScript resources
     */
    public List<EpubResource> getJsResources() {
        return getResourcesByType("application/javascript");
    }
    
    /**
     * Get the cover resource with fallback
     * @return the cover resource, or null if not found
     */
    public EpubResource getCover() {
        return EpubBookProcessor.getCover(book);
    }

    /**
     * Check if the book has a cover
     * @return true if cover exists
     */
    public boolean hasCover() {
        return EpubBookProcessor.getCover(book) != null;
    }
    
    /**
     * Process a chapter content with stream processing
     * @param chapter the chapter to process
     * @param processor the content processor
     * @throws BaseEpubException if processing fails
     */
    public void processChapterContent(EpubChapter chapter, Consumer<InputStream> processor) 
            throws BaseEpubException {
        if (chapter == null || chapter.getContent() == null) {
            throw new EpubResourceException("Invalid chapter or chapter content", 
                    epubFile.getName(), "unknown", null);
        }
        
        try {
            // Use the streaming processor
            EpubStreamProcessor streamProcessor = new EpubStreamProcessor(epubFile);
            streamProcessor.processHtmlChapter(chapter.getContent(), processor);
        } catch (BaseEpubException | EpubPathValidationException e) {
            throw new EpubResourceException("Failed to process chapter content",
                    epubFile.getName(), chapter.getContent(), e);
        }
    }
    
    /**
     * Get chapter content as string
     * @param chapter the chapter
     * @return chapter content as string, or null if failed
     */
    public String getChapterContentAsString(EpubChapter chapter) {
        if (chapter == null || chapter.getContent() == null) {
            return null;
        }
        
        try {
            StringBuilder content = new StringBuilder();
            processChapterContent(chapter, inputStream -> {
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        content.append(new String(buffer, 0, bytesRead, "UTF-8"));
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read chapter content", e);
                }
            });
            return content.toString();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Load all resource data into memory
     * @throws IOException if loading fails
     */
    public void loadAllResources() throws IOException {
        EpubBookProcessor.loadAllResourceData(book);
    }
    
    /**
     * Get the underlying EpubBook instance
     * @return a copy of the original EpubBook
     */
    public EpubBook getOriginalBook() {
        return new EpubBook(book);
    }
    
    /**
     * Get basic book information
     * @return formatted book information
     */
    public String getBookInfo() {
        return String.format("Title: %s%nAuthor: %s%nLanguage: %s%nChapters: %d%nResources: %d",
                getTitle(),
                getAuthor(),
                getLanguage(),
                getChapterCount(),
                book.getResources().size());
    }
    
    /**
     * Flatten nested chapter structure into a single list
     * @param chapters the chapter list to flatten
     * @return flattened list of all chapters
     */
    private List<EpubChapter> flattenChapters(List<EpubChapter> chapters) {
        List<EpubChapter> result = new java.util.ArrayList<>();
        for (EpubChapter chapter : chapters) {
            result.add(chapter);
            if (chapter.hasChildren()) {
                result.addAll(flattenChapters(chapter.getChildren()));
            }
        }
        return result;
    }
    
    /**
     * Find chapter by title recursively (case-insensitive)
     * @param chapters the chapters to search
     * @param title the title to search for
     * @return the found chapter, or null
     */
    private EpubChapter findChapterByTitleRecursive(List<EpubChapter> chapters, String title) {
        for (EpubChapter chapter : chapters) {
            if (chapter.getTitle() != null && 
                chapter.getTitle().equalsIgnoreCase(title)) {
                return chapter;
            }
            if (chapter.hasChildren()) {
                EpubChapter found = findChapterByTitleRecursive(chapter.getChildren(), title);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}