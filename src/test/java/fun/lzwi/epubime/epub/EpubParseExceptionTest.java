package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.exception.EpubParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EpubParseExceptionTest {

    @Test
    public void testEpubParseException() {
        EpubParseException exception = new EpubParseException("Test Exception");
        assertEquals("[9001: Unknown error occurred] Test Exception", exception.getMessage());
    }
}