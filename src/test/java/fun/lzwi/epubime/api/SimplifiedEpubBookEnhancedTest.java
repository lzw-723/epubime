package fun.lzwi.epubime.api;

import fun.lzwi.epubime.epub.EpubBook;
import fun.lzwi.epubime.epub.EpubChapter;
import fun.lzwi.epubime.epub.EpubResource;
import fun.lzwi.epubime.epub.Metadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SimplifiedEpubBookEnhancedTest {

    private EpubBookEnhanced enhancedBook;
    private File testFile;

    @BeforeEach
    public void setUp() {
        // Create a simple test book
        Metadata metadata = new Metadata();
        metadata.setTitle("Test Book");
        metadata.setCreator("Test Author");
        metadata.setLanguage("en");
        
        EpubBook book = new EpubBook();
        book.setMetadata(metadata);
        
        testFile = new File("test.epub");
        enhancedBook = new EpubBookEnhanced(book, testFile);
    }

    @Test
    public void testBasicProperties() {
        assertEquals("Test Book", enhancedBook.getTitle());
        assertEquals("Test Author", enhancedBook.getAuthor());
        assertEquals("en", enhancedBook.getLanguage());
    }

    @Test
    public void testGetMetadata() {
        Metadata metadata = enhancedBook.getMetadata();
        assertNotNull(metadata);
        assertEquals("Test Book", metadata.getTitle());
    }

    @Test
    public void testGetAllChapters() {
        // Test with empty chapters
        List<EpubChapter> allChapters = enhancedBook.getAllChapters();
        assertNotNull(allChapters);
        assertEquals(0, allChapters.size());
    }

    @Test
    public void testFindChapterByTitle() {
        // Test with no chapters
        assertNull(enhancedBook.findChapterByTitle("Non-existent"));
    }

    @Test
    public void testFindChaptersByContentPattern() {
        // Test with no chapters
        List<EpubChapter> results = enhancedBook.findChaptersByContentPattern("test");
        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    public void testGetChaptersByType() {
        // Test different types
        assertNotNull(enhancedBook.getChaptersByType("ncx"));
        assertNotNull(enhancedBook.getChaptersByType("nav"));
        assertNotNull(enhancedBook.getChaptersByType("landmarks"));
        assertNotNull(enhancedBook.getChaptersByType("page-list"));
        assertNotNull(enhancedBook.getChaptersByType("unknown"));
    }

    @Test
    public void testGetFirstAndLastChapter() {
        assertNull(enhancedBook.getFirstChapter());
        assertNull(enhancedBook.getLastChapter());
    }

    @Test
    public void testGetChapter() {
        assertNull(enhancedBook.getChapter(0));
        assertNull(enhancedBook.getChapter(-1));
        assertNull(enhancedBook.getChapter(1));
    }

    @Test
    public void testGetChapterCount() {
        assertEquals(0, enhancedBook.getChapterCount());
    }

    @Test
    public void testGetResourcesByType() {
        // Test with no resources
        List<EpubResource> cssResources = enhancedBook.getResourcesByType("text/css");
        assertNotNull(cssResources);
        assertEquals(0, cssResources.size());
    }

    @Test
    public void testGetImageResources() {
        List<EpubResource> imageResources = enhancedBook.getImageResources();
        assertNotNull(imageResources);
        assertEquals(0, imageResources.size());
    }

    @Test
    public void testGetCssResources() {
        List<EpubResource> cssResources = enhancedBook.getCssResources();
        assertNotNull(cssResources);
        assertEquals(0, cssResources.size());
    }

    @Test
    public void testGetJsResources() {
        List<EpubResource> jsResources = enhancedBook.getJsResources();
        assertNotNull(jsResources);
        assertEquals(0, jsResources.size());
    }

    @Test
    public void testGetCover() {
        assertNull(enhancedBook.getCover());
    }

    @Test
    public void testHasCover() {
        assertFalse(enhancedBook.hasCover());
    }

    @Test
    public void testGetOriginalBook() {
        EpubBook originalBook = enhancedBook.getOriginalBook();
        assertNotNull(originalBook);
        assertNotNull(originalBook.getMetadata());
        assertEquals("Test Book", originalBook.getMetadata().getTitle());
    }

    @Test
    public void testGetBookInfo() {
        String bookInfo = enhancedBook.getBookInfo();
        assertNotNull(bookInfo);
        assertTrue(bookInfo.contains("Title: Test Book"));
        assertTrue(bookInfo.contains("Author: Test Author"));
        assertTrue(bookInfo.contains("Language: en"));
        assertTrue(bookInfo.contains("Chapters: 0"));
        assertTrue(bookInfo.contains("Resources: 0"));
    }

    @Test
    public void testNullHandling() {
        // Test with null metadata values
        Metadata nullMetadata = new Metadata();
        EpubBook nullBook = new EpubBook();
        nullBook.setMetadata(nullMetadata);
        
        EpubBookEnhanced nullEnhanced = new EpubBookEnhanced(nullBook, testFile);
        
        assertEquals("", nullEnhanced.getTitle());
        assertEquals("", nullEnhanced.getAuthor());
        assertEquals("", nullEnhanced.getLanguage());
    }
}