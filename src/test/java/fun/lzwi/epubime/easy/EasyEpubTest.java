package fun.lzwi.epubime.easy;

import fun.lzwi.Utils;
import fun.lzwi.epubime.BytesResourceReader;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class EasyEpubTest {
    private EasyEpub book;

    @Before
    public void setUp() throws ParserConfigurationException, SAXException, IOException {
        book = new EasyEpub(Utils.getFile("《坟》鲁迅.epub"));
    }

    @Test
    public void testGetTitle() {
        assertEquals("坟", book.getTitle());
    }

    @Test
    public void testGetAuthor() {
        assertEquals("鲁迅", book.getAuthor());
    }

    @Test
    public void testGetCover() {
        assertEquals("OEBPS/images/Cover.jpg", book.getCover());
    }

    @Test
    public void testGetModified() {
        assertEquals("2022-12-06T13:14:44Z", book.getModified());
    }

    @Test
    public void testGetIdentifier() {
        assertEquals("https://www.7sbook.com/ebook/254.html", book.getIdentifier());
    }

    @Test
    public void testGetLanguage() {
        assertEquals("zh", book.getLanguage());
    }

    @Test
    public void testGetResource() throws IOException {
        assertEquals(16007, new BytesResourceReader().read(book.getResource("OEBPS/images/Cover.jpg")).length);
    }

    @Test
    public void testGetDate() {
        assertEquals("2022-12-06T13:14:44.000000+00:00", book.getDate());
    }

    @Test
    public void testGetDescription() {
        assertEquals("《坟》是鲁迅的论文集，收录鲁迅在1907年~1925年间所写的论文二十三篇。包括《人之历史》、《文化偏至论》、《摩罗诗力说》、《娜拉走后怎样》、《说胡须》、《论照相之类》、《论他妈的》、《从胡须说到牙齿》等。", book.getDescription());

    }


    @Test
    public void testGetResources() {
        assertEquals(35, book.getResources().size());
    }

    @Test
    public void testGetResources2() {
        assertEquals("application/x-dtbncx+xml", book.getResources().get(0).getType());
        assertEquals("OEBPS/book.ncx", book.getResources().get(0).getHref());
    }


    @Test
    public void testGetContent() {
        assertEquals(28, book.getContent().size());
    }

    @Test
    public void testGetContentLabel() {
        assertEquals("论“他妈的！”", book.getContent().get(20).getLabel());
    }

    @Test
    public void testGetContentHref() {
        assertEquals("OEBPS/Chapter19100.html", book.getContent().get(20).getHref());
    }
}
