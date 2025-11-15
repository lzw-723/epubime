package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.exception.EpubFormatException;
import fun.lzwi.epubime.exception.EpubParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnhancedEpubFormatExceptionTest {

    @Test
    public void testMissingMimetype() {
        String fileName = "test.epub";
        String filePath = "books/test.epub";

        EpubFormatException exception = EpubFormatException.missingMimetype(fileName, filePath);

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Missing required mimetype file"),
                  "Message should contain mimetype text");
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        assertEquals("mimetypeValidation", exception.getOperation());
        assertEquals(EpubParseException.ErrorCode.EPUB_MISSING_MIMETYPE, exception.getErrorCode());
        assertEquals("请确保EPUB文件包含mimetype文件", exception.getRecoverySuggestion());
    }

    @Test
    public void testInvalidContainer() {
        String fileName = "invalid.epub";
        String filePath = "books/invalid.epub";
        String details = "Missing rootfile element";

        EpubFormatException exception = EpubFormatException.invalidContainer(fileName, filePath, details);

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Invalid container.xml: " + details),
                  "Message should contain details");
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        assertEquals("containerValidation", exception.getOperation());
        assertEquals(EpubParseException.ErrorCode.EPUB_INVALID_CONTAINER, exception.getErrorCode());
        assertEquals("请检查META-INF/container.xml文件格式", exception.getRecoverySuggestion());
    }

    @Test
    public void testInvalidOpf() {
        String fileName = "broken.epub";
        String filePath = "books/broken.epub";
        String opfPath = "OEBPS/content.opf";
        String details = "Missing package element";

        EpubFormatException exception = EpubFormatException.invalidOpf(fileName, filePath, opfPath, details);

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Invalid OPF file: " + details),
                  "Message should contain details");
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        assertEquals("opfValidation", exception.getOperation());
        assertEquals(EpubParseException.ErrorCode.EPUB_INVALID_OPF, exception.getErrorCode());
        assertEquals("请检查OPF文件的XML格式和结构", exception.getRecoverySuggestion());
    }

    @Test
    public void testInvalidNcx() {
        String fileName = "no-nav.epub";
        String filePath = "books/no-nav.epub";
        String ncxPath = "OEBPS/toc.ncx";
        String details = "Missing navMap element";

        EpubFormatException exception = EpubFormatException.invalidNcx(fileName, filePath, ncxPath, details);

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Invalid NCX file: " + details),
                  "Message should contain details");
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        assertEquals("ncxValidation", exception.getOperation());
        assertEquals(EpubParseException.ErrorCode.EPUB_INVALID_NCX, exception.getErrorCode());
        assertEquals("请检查NCX文件的XML格式和结构", exception.getRecoverySuggestion());
    }

    @Test
    public void testInvalidNav() {
        String fileName = "epub3.epub";
        String filePath = "books/epub3.epub";
        String navPath = "OEBPS/nav.xhtml";
        String details = "Missing toc nav element";

        EpubFormatException exception = EpubFormatException.invalidNav(fileName, filePath, navPath, details);

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Invalid NAV file: " + details),
                  "Message should contain details");
        assertEquals(fileName, exception.getFileName());
        assertEquals(filePath, exception.getFilePath());
        assertEquals("navValidation", exception.getOperation());
        assertEquals(EpubParseException.ErrorCode.EPUB_INVALID_NAV, exception.getErrorCode());
        assertEquals("请检查NAV文件的XML格式和结构", exception.getRecoverySuggestion());
    }

    @Test
    public void testStaticFactoryMethodsWithNullValues() {
        // Test with null values to ensure robustness
        EpubFormatException exception1 = EpubFormatException.missingMimetype(null, null);
        assertNotNull(exception1, "Exception should not be null");
        // 消息格式包含错误码和详细信息
        assertTrue(exception1.getMessage().contains("Missing required mimetype file"),
                  "Message should contain expected text");

        EpubFormatException exception2 = EpubFormatException.invalidContainer(null, null, null);
        assertNotNull(exception2, "Exception should not be null");
        assertTrue(exception2.getMessage().contains("Invalid container.xml:"),
                  "Message should handle null details");

        EpubFormatException exception3 = EpubFormatException.invalidOpf(null, null, null, null);
        assertNotNull(exception3, "Exception should not be null");
        assertTrue(exception3.getMessage().contains("Invalid OPF file:"),
                  "Message should handle null details");

        EpubFormatException exception4 = EpubFormatException.invalidNcx(null, null, null, null);
        assertNotNull(exception4, "Exception should not be null");
        assertTrue(exception4.getMessage().contains("Invalid NCX file:"),
                  "Message should handle null details");

        EpubFormatException exception5 = EpubFormatException.invalidNav(null, null, null, null);
        assertNotNull(exception5, "Exception should not be null");
        assertTrue(exception5.getMessage().contains("Invalid NAV file:"),
                  "Message should handle null details");
    }

    @Test
    public void testStaticFactoryMethodsWithEmptyStrings() {
        // Test with empty strings
        EpubFormatException exception1 = EpubFormatException.missingMimetype("", "");
        assertNotNull(exception1, "Exception should not be null");
        assertTrue(exception1.getMessage().contains("Missing required mimetype file"),
                  "Message should contain expected text");

        EpubFormatException exception2 = EpubFormatException.invalidContainer("", "", "");
        assertNotNull(exception2, "Exception should not be null");
        assertTrue(exception2.getMessage().contains("Invalid container.xml:"),
                  "Message should handle empty details");
    }

    @Test
    public void testErrorCodes() {
        // Verify that each static factory method uses the correct error code
        EpubFormatException mimetypeException = EpubFormatException.missingMimetype("test", "test");
        assertEquals(EpubParseException.ErrorCode.EPUB_MISSING_MIMETYPE, 
                    mimetypeException.getErrorCode());

        EpubFormatException containerException = EpubFormatException.invalidContainer("test", "test", "test");
        assertEquals(EpubParseException.ErrorCode.EPUB_INVALID_CONTAINER, 
                    containerException.getErrorCode());

        EpubFormatException opfException = EpubFormatException.invalidOpf("test", "test", "test", "test");
        assertEquals(EpubParseException.ErrorCode.EPUB_INVALID_OPF, 
                    opfException.getErrorCode());

        EpubFormatException ncxException = EpubFormatException.invalidNcx("test", "test", "test", "test");
        assertEquals(EpubParseException.ErrorCode.EPUB_INVALID_NCX, 
                    ncxException.getErrorCode());

        EpubFormatException navException = EpubFormatException.invalidNav("test", "test", "test", "test");
        assertEquals(EpubParseException.ErrorCode.EPUB_INVALID_NAV, 
                    navException.getErrorCode());
    }

    @Test
    public void testRecoverySuggestions() {
        // Verify that each static factory method provides appropriate recovery suggestions
        EpubFormatException mimetypeException = EpubFormatException.missingMimetype("test", "test");
        assertEquals("请确保EPUB文件包含mimetype文件", 
                    mimetypeException.getRecoverySuggestion());

        EpubFormatException containerException = EpubFormatException.invalidContainer("test", "test", "test");
        assertEquals("请检查META-INF/container.xml文件格式", 
                    containerException.getRecoverySuggestion());

        EpubFormatException opfException = EpubFormatException.invalidOpf("test", "test", "test", "test");
        assertEquals("请检查OPF文件的XML格式和结构", 
                    opfException.getRecoverySuggestion());

        EpubFormatException ncxException = EpubFormatException.invalidNcx("test", "test", "test", "test");
        assertEquals("请检查NCX文件的XML格式和结构", 
                    ncxException.getRecoverySuggestion());

        EpubFormatException navException = EpubFormatException.invalidNav("test", "test", "test", "test");
        assertEquals("请检查NAV文件的XML格式和结构", 
                    navException.getRecoverySuggestion());
    }

    @Test
    public void testOperations() {
        // Verify that each static factory method sets the correct operation
        EpubFormatException mimetypeException = EpubFormatException.missingMimetype("test", "test");
        assertEquals("mimetypeValidation", mimetypeException.getOperation());

        EpubFormatException containerException = EpubFormatException.invalidContainer("test", "test", "test");
        assertEquals("containerValidation", containerException.getOperation());

        EpubFormatException opfException = EpubFormatException.invalidOpf("test", "test", "test", "test");
        assertEquals("opfValidation", opfException.getOperation());

        EpubFormatException ncxException = EpubFormatException.invalidNcx("test", "test", "test", "test");
        assertEquals("ncxValidation", ncxException.getOperation());

        EpubFormatException navException = EpubFormatException.invalidNav("test", "test", "test", "test");
        assertEquals("navValidation", navException.getOperation());
    }
}