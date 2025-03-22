package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class EpubBookTest {

    @Test
    public void getCover() throws EpubParseException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubBook book = new EpubParser(epubFile).parse();
        EpubResource cover = book.getCover();
        assertNotNull(cover);
        assertEquals("OEBPS/images/Cover.jpg", cover.getHref());
    }
}