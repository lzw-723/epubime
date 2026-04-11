package fun.lzwi.epubime.api;

import fun.lzwi.epubime.epub.EpubBook;
import fun.lzwi.epubime.epub.EpubChapter;
import fun.lzwi.epubime.epub.EpubResource;
import fun.lzwi.epubime.epub.Metadata;
import fun.lzwi.epubime.exception.BaseEpubException;
import fun.lzwi.epubime.exception.EpubPathValidationException;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Asynchronous EPUB processor for non-blocking operations.
 * Implements AutoCloseable to ensure proper resource cleanup.
 * Provides async versions of common EPUB processing tasks.
 */
public class AsyncEpubProcessor implements AutoCloseable {
    private final ExecutorService executor;
    private final boolean ownsExecutor;

    /**
     * Default thread factory for EPUB processing threads.
     */
    private static class EpubThreadFactory implements ThreadFactory {
        private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "epubime-async-" + THREAD_NUMBER.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    }

    /**
     * Create with default executor (bounded thread pool, max 8 threads).
     * Uses daemon threads that won't prevent JVM shutdown.
     */
    public AsyncEpubProcessor() {
        this.executor = new ThreadPoolExecutor(
                0, 8,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new EpubThreadFactory()
        );
        this.ownsExecutor = true;
    }

    /**
     * Create with custom executor.
     * @param executor the executor to use for async operations
     */
    public AsyncEpubProcessor(ExecutorService executor) {
        this.executor = executor;
        this.ownsExecutor = false;
    }

    /**
     * Parse EPUB file asynchronously
     * @param epubFile the EPUB file to parse
     * @return CompletableFuture containing the parsed EpubBook
     */
    public CompletableFuture<EpubBook> parseBookAsync(File epubFile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return EpubReader.fromFile(epubFile).parse();
            } catch (BaseEpubException | java.io.IOException | EpubPathValidationException e) {
                throw new RuntimeException("Failed to parse EPUB", e);
            }
        }, executor);
    }

    /**
     * Parse an EPUB file asynchronously
     * @param epubFile the EPUB file to parse
     * @param useCache whether to use caching
     * @param lazyLoading whether to use lazy loading
     * @return CompletableFuture containing the parsed EpubBook
     */
    public CompletableFuture<EpubBook> parseBookAsync(File epubFile, boolean useCache, boolean lazyLoading) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                EpubReaderConfig config = new EpubReaderConfig()
                        .withCache(useCache)
                        .withLazyLoading(lazyLoading);
                return EpubReader.fromFile(epubFile, config).parse();
            } catch (BaseEpubException | java.io.IOException | EpubPathValidationException e) {
                throw new RuntimeException("Failed to parse EPUB", e);
            }
        }, executor);
    }

    /**
     * Read metadata asynchronously
     * @param epubFile the EPUB file to read
     * @return CompletableFuture containing the metadata
     */
    public CompletableFuture<Metadata> readMetadataAsync(File epubFile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return EpubReader.fromFile(epubFile).readMetadata();
            } catch (BaseEpubException | java.io.IOException | EpubPathValidationException e) {
                throw new RuntimeException("Failed to read metadata", e);
            }
        }, executor);
    }

    /**
     * Read table of contents asynchronously
     * @param epubFile the EPUB file to read
     * @return CompletableFuture containing the chapters
     */
    public CompletableFuture<List<EpubChapter>> readTableOfContentsAsync(File epubFile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return EpubReader.fromFile(epubFile).readTableOfContents();
            } catch (BaseEpubException | java.io.IOException | EpubPathValidationException e) {
                throw new RuntimeException("Failed to read table of contents", e);
            }
        }, executor);
    }

    /**
     * Get EPUB information asynchronously
     * @param epubFile the EPUB file to analyze
     * @return CompletableFuture containing basic EPUB information
     */
    public CompletableFuture<EpubReader.EpubInfo> getBookInfoAsync(File epubFile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return EpubReader.fromFile(epubFile).getInfo();
            } catch (BaseEpubException | java.io.IOException | EpubPathValidationException e) {
                throw new RuntimeException("Failed to get book info", e);
            }
        }, executor);
    }

    /**
     * Process chapters asynchronously with stream processing
     * @param epubFile the EPUB file to process
     * @param processor the processor for each chapter
     * @return CompletableFuture that completes when all chapters are processed
     */
    public CompletableFuture<Void> processChaptersAsync(File epubFile, BiConsumer<EpubChapter, InputStream> processor) {
        return CompletableFuture.runAsync(() -> {
            try {
                EpubReader.fromFile(epubFile).streamChapters(processor);
            } catch (BaseEpubException | java.io.IOException | EpubPathValidationException e) {
                throw new RuntimeException("Failed to process chapters", e);
            }
        }, executor);
    }

    /**
     * Process a specific chapter asynchronously
     * @param epubFile the EPUB file
     * @param chapterId the chapter ID to process
     * @param processor the processor for the chapter content
     * @return CompletableFuture that completes when the chapter is processed
     */
    public CompletableFuture<Void> processChapterAsync(File epubFile, String chapterId, Consumer<InputStream> processor) {
        return CompletableFuture.runAsync(() -> {
            try {
                EpubReader.fromFile(epubFile).streamChapter(chapterId, processor);
            } catch (BaseEpubException | EpubPathValidationException | java.io.IOException e) {
                throw new RuntimeException("Failed to process chapter: " + chapterId, e);
            }
        }, executor);
    }

    /**
     * Process all resources asynchronously
     * @param epubFile the EPUB file
     * @param processor function to process each resource
     * @return CompletableFuture that completes when processing is done
     */
    public CompletableFuture<Void> processResourcesAsync(File epubFile, Function<EpubResource, Void> processor) {
        return CompletableFuture.runAsync(() -> {
            try {
                EpubReaderConfig config = new EpubReaderConfig().withParallelProcessing(true);
                EpubReader.fromFile(epubFile, config).processResources(processor);
            } catch (BaseEpubException | java.io.IOException | EpubPathValidationException e) {
                throw new RuntimeException("Failed to process resources", e);
            }
        }, executor);
    }

    /**
     * Get cover resource asynchronously
     * @param epubFile the EPUB file
     * @return CompletableFuture containing the cover resource, or null if not found
     */
    public CompletableFuture<EpubResource> getCoverAsync(File epubFile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return EpubReader.fromFile(epubFile).getCover();
            } catch (BaseEpubException | java.io.IOException | EpubPathValidationException e) {
                throw new RuntimeException("Failed to get cover", e);
            }
        }, executor);
    }

    /**
     * Get a specific resource asynchronously
     * @param epubFile the EPUB file
     * @param resourceId the resource ID
     * @return CompletableFuture containing the resource, or null if not found
     */
    public CompletableFuture<EpubResource> getResourceAsync(File epubFile, String resourceId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return EpubReader.fromFile(epubFile).getResource(resourceId);
            } catch (BaseEpubException | java.io.IOException | EpubPathValidationException e) {
                throw new RuntimeException("Failed to get resource: " + resourceId, e);
            }
        }, executor);
    }

    /**
     * Validate EPUB file asynchronously
     * @param epubFile the EPUB file to validate
     * @return CompletableFuture containing validation result
     */
    public CompletableFuture<Boolean> validateAsync(File epubFile) {
        return CompletableFuture.supplyAsync(() ->
            EpubReader.fromFile(epubFile).isValid(), executor
        );
    }

    /**
     * Load enhanced book asynchronously
     * @param epubFile the EPUB file
     * @return CompletableFuture containing the enhanced book
     */
    public CompletableFuture<EpubBookEnhanced> loadEnhancedBookAsync(File epubFile) {
        return parseBookAsync(epubFile)
                .thenApply(book -> new EpubBookEnhanced(book, epubFile));
    }

    /**
     * Load enhanced metadata asynchronously
     * @param epubFile the EPUB file
     * @return CompletableFuture containing the enhanced metadata
     */
    public CompletableFuture<MetadataEnhanced> loadEnhancedMetadataAsync(File epubFile) {
        return readMetadataAsync(epubFile)
                .thenApply(MetadataEnhanced::new);
    }

    /**
     * Process multiple EPUB files in parallel
     * @param epubFiles the EPUB files to process
     * @param processor the processor for each book
     * @return CompletableFuture containing list of processed books
     */
    public CompletableFuture<List<EpubBook>> processMultipleBooksAsync(List<File> epubFiles,
                                                                      Function<EpubBook, EpubBook> processor) {
        if (epubFiles == null || epubFiles.isEmpty()) {
            return CompletableFuture.completedFuture(new java.util.ArrayList<>());
        }
        List<CompletableFuture<EpubBook>> futures = epubFiles.stream()
                .map(file -> parseBookAsync(file).thenApply(processor))
                .collect(java.util.stream.Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(java.util.stream.Collectors.toList()));
    }

    /**
     * Get chapter count asynchronously
     * @param epubFile the EPUB file
     * @return CompletableFuture containing the chapter count
     */
    public CompletableFuture<Integer> getChapterCountAsync(File epubFile) {
        return readTableOfContentsAsync(epubFile)
                .thenApply(List::size);
    }

    /**
     * Get resource count asynchronously
     * @param epubFile the EPUB file
     * @return CompletableFuture containing the resource count
     */
    public CompletableFuture<Integer> getResourceCountAsync(File epubFile) {
        return parseBookAsync(epubFile)
                .thenApply(book -> book.getResources().size());
    }

    /**
     * Shutdown the processor and release resources.
     * Only shuts down the executor if this instance owns it (created via default constructor).
     */
    public void shutdown() {
        if (ownsExecutor) {
            executor.shutdown();
        }
    }

    @Override
    public void close() {
        shutdown();
    }
}
