package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.exception.EpubParseException;

import static org.junit.Assert.*;

public class EpubParseExceptionTest {

    @org.junit.Test
    public void testEpubParseException() {
        EpubParseException exception = new EpubParseException("Test Exception");
        assertEquals("[9001: Unknown error occurred] Test Exception", exception.getMessage());
    }
}