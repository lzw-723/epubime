package fun.lzwi.epubime.api.examples;

import fun.lzwi.epubime.api.AsyncEpubProcessor;
import fun.lzwi.epubime.api.EpubBookEnhanced;
import fun.lzwi.epubime.api.EpubReader;
import fun.lzwi.epubime.api.MetadataEnhanced;
import fun.lzwi.epubime.epub.EpubBook;
import fun.lzwi.epubime.epub.EpubChapter;
import fun.lzwi.epubime.epub.EpubResource;
import fun.lzwi.epubime.epub.Metadata;
import fun.lzwi.epubime.exception.SimpleEpubException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Examples demonstrating the optimized API usage
 */
public class EpubReaderExamples {
    
    /**
     * Basic usage example with fluent API
     */
    public static void basicUsageExample() {
        System.out.println("=== Basic Usage Example ===");
        
        try {
            // Simple parsing
            File epubFile = new File("sample.epub");
            
            EpubBook book = EpubReader.fromFile(epubFile)
                    .withCache(true)
                    .parse();
            
            // Access metadata
            Metadata metadata = book.getMetadata();
            System.out.println("Title: " + metadata.getTitle());
            System.out.println("Author: " + metadata.getCreator());
            System.out.println("Language: " + metadata.getLanguage());
            
            // Access chapters
            List<EpubChapter> chapters = book.getChapters();
            System.out.println("Number of chapters: " + chapters.size());
            
            for (EpubChapter chapter : chapters) {
                System.out.println("Chapter: " + chapter.getTitle());
            }
            
        } catch (SimpleEpubException e) {
            System.err.println("Failed to parse EPUB: " + e.getMessage());
        }
    }
    
    /**
     * Enhanced API usage example
     */
    public static void enhancedApiExample() {
        System.out.println("\n=== Enhanced API Example ===");
        
        try {
            File epubFile = new File("sample.epub");
            
            // Parse and get enhanced book
            EpubBook book = EpubReader.fromFile(epubFile).parse();
            EpubBookEnhanced enhancedBook = new EpubBookEnhanced(book, epubFile);
            
            // Use enhanced methods
            System.out.println("Book Info:");
            System.out.println(enhancedBook.getBookInfo());
            
            // Find chapters by title
            EpubChapter chapter = enhancedBook.findChapterByTitle("Introduction");
            if (chapter != null) {
                System.out.println("Found chapter: " + chapter.getTitle());
            }
            
            // Get all chapters (including nested)
            List<EpubChapter> allChapters = enhancedBook.getAllChapters();
            System.out.println("Total chapters (including nested): " + allChapters.size());
            
            // Get specific resource types
            List<EpubResource> images = enhancedBook.getImageResources();
            System.out.println("Image resources: " + images.size());
            
            List<EpubResource> cssFiles = enhancedBook.getCssResources();
            System.out.println("CSS resources: " + cssFiles.size());
            
            // Check for cover
            if (enhancedBook.hasCover()) {
                System.out.println("Book has cover image");
            }
            
        } catch (SimpleEpubException e) {
            System.err.println("Failed to process EPUB: " + e.getMessage());
        }
    }
    
    /**
     * Enhanced metadata example
     */
    public static void enhancedMetadataExample() {
        System.out.println("\n=== Enhanced Metadata Example ===");
        
        try {
            File epubFile = new File("sample.epub");
            
            // Parse metadata
            Metadata metadata = EpubReader.fromFile(epubFile).parseMetadata();
            MetadataEnhanced enhancedMetadata = new MetadataEnhanced(metadata);
            
            // Use enhanced metadata methods
            System.out.println("Title: " + enhancedMetadata.getTitle());
            System.out.println("Author: " + enhancedMetadata.getAuthor());
            System.out.println("Language: " + enhancedMetadata.getLanguage());
            
            // Get parsed date
            java.time.LocalDate date = enhancedMetadata.getParsedDate();
            if (date != null) {
                System.out.println("Parsed date: " + date);
            }
            
            // Check for accessibility features
            if (enhancedMetadata.hasAccessibilityFeatures()) {
                System.out.println("Accessibility features: " + 
                        enhancedMetadata.getAccessibilityFeatures());
            }
            
            // Get summary
            System.out.println("\nMetadata Summary:");
            System.out.println(enhancedMetadata.getSummary());
            
        } catch (SimpleEpubException e) {
            System.err.println("Failed to parse metadata: " + e.getMessage());
        }
    }
    
    /**
     * Stream processing example
     */
    public static void streamProcessingExample() {
        System.out.println("\n=== Stream Processing Example ===");
        
        try {
            File epubFile = new File("large-book.epub");
            
            // Stream process chapters without loading everything into memory
            EpubReader.fromFile(epubFile)
                    .streamChapters((chapter, inputStream) -> {
                        try {
                            // Process chapter content stream
                            System.out.println("Processing chapter: " + chapter.getTitle());
                            
                            // Read content in chunks
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            int totalBytes = 0;
                            
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                totalBytes += bytesRead;
                                // Process chunk here if needed
                            }
                            
                            System.out.println("Chapter size: " + totalBytes + " bytes");
                            
                        } catch (IOException e) {
                            System.err.println("Error processing chapter: " + e.getMessage());
                        }
                    });
            
            // Stream specific chapter
            EpubReader.fromFile(epubFile)
                    .streamChapter("chapter1", inputStream -> {
                        try {
                            // Read entire chapter content
                            String content = readStreamContent(inputStream);
                            System.out.println("Chapter 1 content length: " + content.length());
                        } catch (IOException e) {
                            System.err.println("Error reading chapter: " + e.getMessage());
                        }
                    });
            
        } catch (SimpleEpubException e) {
            System.err.println("Failed to stream process EPUB: " + e.getMessage());
        }
    }
    
    /**
     * Async processing example
     */
    public static void asyncProcessingExample() {
        System.out.println("\n=== Async Processing Example ===");
        
        AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();
        
        try {
            File epubFile = new File("sample.epub");
            
            // Parse book asynchronously
            CompletableFuture<EpubBook> bookFuture = asyncProcessor.parseBookAsync(epubFile);
            
            // Parse metadata asynchronously
            CompletableFuture<Metadata> metadataFuture = asyncProcessor.parseMetadataAsync(epubFile);
            
            // Get book info asynchronously
            CompletableFuture<EpubReader.EpubInfo> infoFuture = asyncProcessor.getBookInfoAsync(epubFile);
            
            // Process all async results when ready
            CompletableFuture.allOf(bookFuture, metadataFuture, infoFuture)
                    .thenRun(() -> {
                        try {
                            EpubBook book = bookFuture.get();
                            Metadata metadata = metadataFuture.get();
                            EpubReader.EpubInfo info = infoFuture.get();
                            
                            System.out.println("Async processing complete:");
                            System.out.println("Book title: " + metadata.getTitle());
                            System.out.println("Book info: " + info);
                            System.out.println("Chapters: " + book.getChapters().size());
                            
                        } catch (Exception e) {
                            System.err.println("Error in async processing: " + e.getMessage());
                        }
                    })
                    .join(); // Wait for completion
            
            // Process chapters asynchronously
            asyncProcessor.processChaptersAsync(epubFile, (chapter, inputStream) -> {
                try {
                    System.out.println("Async processing chapter: " + chapter.getTitle());
                    // Process chapter content
                    inputStream.close();
                } catch (IOException e) {
                    System.err.println("Error processing chapter: " + e.getMessage());
                }
            }).join();
            
            // Validate EPUB asynchronously
            asyncProcessor.validateAsync(epubFile)
                    .thenAccept(isValid -> {
                        System.out.println("EPUB is " + (isValid ? "valid" : "invalid"));
                    })
                    .join();
            
        } finally {
            asyncProcessor.shutdown();
        }
    }
    
    /**
     * Resource processing example
     */
    public static void resourceProcessingExample() {
        System.out.println("\n=== Resource Processing Example ===");
        
        try {
            File epubFile = new File("sample.epub");
            
            // Process all resources
            EpubReader.fromFile(epubFile)
                    .withParallelProcessing(true)
                    .processResources(resource -> {
                        System.out.println("Resource: " + resource.getId() + 
                                         " (" + resource.getType() + ")");
                        return null;
                    });
            
            // Get specific resources
            EpubBook book = EpubReader.fromFile(epubFile).parse();
            
            // Get cover
            EpubResource cover = book.getCover();
            if (cover != null) {
                System.out.println("Cover: " + cover.getId() + " (" + cover.getType() + ")");
                byte[] coverData = cover.getData();
                if (coverData != null) {
                    System.out.println("Cover size: " + coverData.length + " bytes");
                }
            }
            
            // Get resources by type
            List<EpubResource> images = book.getResources().stream()
                    .filter(r -> r.getType() != null && r.getType().startsWith("image/"))
                    .collect(Collectors.toList());
            
            System.out.println("Images found: " + images.size());
            
            // Save resources to files
            for (EpubResource image : images) {
                try {
                    String fileName = "extracted_" + image.getId().replaceAll("[^a-zA-Z0-9.]", "_");
                    Path outputPath = Paths.get(fileName);
                    
                    byte[] data = image.getData();
                    if (data != null) {
                        Files.write(outputPath, data);
                        System.out.println("Saved: " + outputPath);
                    }
                } catch (IOException e) {
                    System.err.println("Failed to save image: " + e.getMessage());
                }
            }
            
        } catch (SimpleEpubException e) {
            System.err.println("Failed to process resources: " + e.getMessage());
        }
    }
    
    /**
     * Quick info example
     */
    public static void quickInfoExample() {
        System.out.println("\n=== Quick Info Example ===");
        
        try {
            File epubFile = new File("sample.epub");
            
            // Get quick info without full parsing
            EpubReader.EpubInfo info = EpubReader.fromFile(epubFile).getInfo();
            
            System.out.println("Quick EPUB Info:");
            System.out.println("Title: " + info.getTitle());
            System.out.println("Author: " + info.getAuthor());
            System.out.println("Language: " + info.getLanguage());
            System.out.println("Chapters: " + info.getChapterCount());
            System.out.println("File size: " + info.getFileSize() + " bytes");
            
            // Validate EPUB
            boolean isValid = EpubReader.fromFile(epubFile).isValid();
            System.out.println("Valid EPUB: " + isValid);
            
        } catch (SimpleEpubException e) {
            System.err.println("Failed to get EPUB info: " + e.getMessage());
        }
    }
    
    /**
     * Read all content from input stream (Java 8 compatible)
     */
    private static String readStreamContent(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            content.append(new String(buffer, 0, bytesRead, "UTF-8"));
        }
        return content.toString();
    }
    
    /**
     * Batch processing example
     */
    public static void batchProcessingExample() {
        System.out.println("\n=== Batch Processing Example ===");
        
        AsyncEpubProcessor asyncProcessor = new AsyncEpubProcessor();
        
        try {
            // List of EPUB files to process
            List<File> epubFiles = Arrays.asList(
                    new File("book1.epub"),
                    new File("book2.epub"),
                    new File("book3.epub")
            );
            
            // Process multiple books in parallel
            asyncProcessor.processMultipleBooksAsync(epubFiles, book -> {
                // Process each book
                System.out.println("Processing: " + book.getMetadata().getTitle());
                return book;
            }).thenAccept(books -> {
                System.out.println("Processed " + books.size() + " books");
            }).join();
            
            // Get chapter counts for all books
            List<CompletableFuture<Integer>> chapterCountFutures = epubFiles.stream()
                    .map(asyncProcessor::getChapterCountAsync)
                    .collect(java.util.stream.Collectors.toList());
            
            CompletableFuture.allOf(chapterCountFutures.toArray(new CompletableFuture[0]))
                    .thenRun(() -> {
                        List<Integer> chapterCounts = chapterCountFutures.stream()
                                .map(CompletableFuture::join)
                                .collect(java.util.stream.Collectors.toList());
                        
                        System.out.println("Chapter counts: " + chapterCounts);
                    })
                    .join();
            
        } finally {
            asyncProcessor.shutdown();
        }
    }
    
    public static void main(String[] args) {
        // Run all examples
        basicUsageExample();
        enhancedApiExample();
        enhancedMetadataExample();
        streamProcessingExample();
        asyncProcessingExample();
        resourceProcessingExample();
        quickInfoExample();
        batchProcessingExample();
    }
}
