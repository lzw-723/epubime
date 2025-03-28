package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class EpubParserTest {

    @Test
    public void getRootFilePath() {
        String containerContent =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><container version=\"1.0\" " + "xmlns" + "=\"urn:oasis" +
                        ":names:tc:opendocument:xmlns:container\"><rootfiles><rootfile " + "full-path=\"OEBPS" +
                        "/content.opf\" media-type=\"application/oebps-package+xml\"/></rootfiles" + "></container>";
        String rootFilePath = EpubParser.getRootFilePath(containerContent);
        assertEquals("OEBPS/content.opf", rootFilePath);
    }

    @Test
    public void readEpubContent() throws EpubParseException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        String content = EpubParser.readEpubContent(epubFile, "mimetype");
        assertNotNull(content);
        assertEquals("application/epub+zip", content);
    }

    @Test
    public void parseMetadata() throws EpubParseException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        String optContent = EpubParser.readEpubContent(epubFile, "OEBPS/book.opf");
        Metadata metadata = EpubParser.parseMetadata(optContent);
        assertNotNull(metadata);
        assertEquals("坟", metadata.getTitle());
        assertEquals("鲁迅", metadata.getCreator());
        assertEquals("zh", metadata.getLanguage());
        assertEquals("传硕公版书", metadata.getPublisher());
        assertEquals("2022-12-06T13:14:44.000000+00:00", metadata.getDate());
        assertEquals("https://www.7sbook.com/ebook/254.html", metadata.getIdentifier());
        assertArrayEquals(new String[]{"论文", "论文集"}, metadata.getSubjects().toArray());
        assertEquals("《坟》是鲁迅的论文集，收录鲁迅在1907年~1925" +
                "年间所写的论文二十三篇。包括《人之历史》、《文化偏至论》、《摩罗诗力说》、《娜拉走后怎样》、《说胡须》、《论照相之类》、《论他妈的》、《从胡须说到牙齿》等。",
                metadata.getDescription());
        assertEquals("本书的版权和许可信息。", metadata.getRights());
        //        assertEquals("text", metadata.getType());
        //        assertEquals("application/epub+zip", metadata.getFormat());
        assertEquals("https://www.7sbook.com/", metadata.getSource());
    }

    @Test
    public void getNcxPath() throws EpubParseException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        String optContent = EpubParser.readEpubContent(epubFile, "OEBPS/book.opf");
        String tocPath = EpubParser.getNcxPath(optContent, "");
        assertEquals("book.ncx", tocPath);
    }

    @Test
    public void getRootFileDir() {
        String rootFileDir = EpubParser.getRootFileDir("OEBPS/content.opf");
        assertEquals("OEBPS/", rootFileDir);
    }

    @Test
    public void parseNcx() throws EpubParseException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        String ncxContent = EpubParser.readEpubContent(epubFile, "OEBPS/book.ncx");
        List<EpubChapter> ncx = EpubParser.parseNcx(ncxContent);
        assertNotNull(ncx);
        assertEquals(28, ncx.size());
    }

    @Test
    public void parseResources() throws EpubParseException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        String opfContent = EpubParser.readEpubContent(epubFile, "OEBPS/book.opf");
        List<EpubResource> resources = EpubParser.parseResources(opfContent, "OEBPS/", epubFile);
        assertNotNull(resources);
        assertEquals(35, resources.size());
    }

    @Test
    public void parse() throws EpubParseException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubBook book = new EpubParser(epubFile).parse();
        assertNotNull(book);
        assertEquals("坟", book.getMetadata().getTitle());
        assertEquals("鲁迅", book.getMetadata().getCreator());
    }

    @Test
    public void getNavPath() {
        String opfContent = "<manifest><item properties=\"nav\" href=\"nav.xhtml\"></item></manifest>";
        String opfDir = "";
        String navPath = EpubParser.getNavPath(opfContent, opfDir);
        assertEquals("nav.xhtml", navPath);
    }

    @Test
    public void parseNav() {
        String navContent = "<nav><ol><li><a href=\"chapter1.xhtml\">Chapter 1</a></li><li><a href=\"chapter2" +
                ".xhtml\">Chapter 2</a></li></ol></nav>";
        List<EpubChapter> chapters = EpubParser.parseNav(navContent);
        assertNotNull(chapters);
        assertEquals(2, chapters.size());
    }
}