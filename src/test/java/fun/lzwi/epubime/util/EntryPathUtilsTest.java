package fun.lzwi.epubime.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class EntryPathUtilsTest {
    @Test
    public void testParse() throws IOException {
        // url for zip file's entry
        assertEquals("index.html", EntryPathUtils.parse(null, "index.html"));
        assertEquals("index.html", EntryPathUtils.parse("", "/index.html"));
        assertEquals("index.html", EntryPathUtils.parse(".", "index.html"));
        assertEquals("index.html", EntryPathUtils.parse("/", "index.html"));

        assertEquals("base/index.html", EntryPathUtils.parse("base", "./index.html"));
        assertEquals("index.html", EntryPathUtils.parse("base", "/index.html"));
        assertEquals("index.html", EntryPathUtils.parse("base", "../index.html"));
        assertEquals("index.html", EntryPathUtils.parse("base/page", "../../index.html"));

        assertEquals("base/index.html", EntryPathUtils.parse("/base", "./index.html"));
        assertEquals("index.html", EntryPathUtils.parse("/base", "/index.html"));
        assertEquals("index.html", EntryPathUtils.parse("/base", "../index.html"));
        assertEquals("index.html", EntryPathUtils.parse("/base/page", "../../index.html"));

        assertEquals("base/index.html", EntryPathUtils.parse("/base/", "./index.html"));
        assertEquals("index.html", EntryPathUtils.parse("/base/", "/index.html"));
        assertEquals("index.html", EntryPathUtils.parse("/base/", "../index.html"));
        assertEquals("index.html", EntryPathUtils.parse("/base/page/", "../../index.html"));
    }

    @Test
    public void testNoEN() throws IOException {

        assertEquals("目录/文件.html", EntryPathUtils.parse("/目录/", "./文件.html"));
        assertEquals("文件.html", EntryPathUtils.parse("/base/", "/文件.html"));
        assertEquals("文件.html", EntryPathUtils.parse("/目录/", "../文件.html"));
        assertEquals("文件.html", EntryPathUtils.parse("/目录/子目录/", "../../文件.html"));

    }
    @Test
    public void testUrlEncoded() throws IOException {

        assertEquals("目录/文件.html", EntryPathUtils.parse("/%E7%9B%AE%E5%BD%95/", "./%E6%96%87%E4%BB%B6.html"));
        assertEquals("文件.html", EntryPathUtils.parse("/%E7%9B%AE%E5%BD%95/", "/%E6%96%87%E4%BB%B6.html"));
        assertEquals("文件.html", EntryPathUtils.parse("/%E7%9B%AE%E5%BD%95/", "../%E6%96%87%E4%BB%B6.html"));
        assertEquals("文件.html", EntryPathUtils.parse("/%E7%9B%AE%E5%BD%95/%E5%AD%90%E7%9B%AE%E5%BD%95/", "../../%E6%96%87%E4%BB%B6.html"));

    }

    @Test
    public void testParent() throws IOException {
        assertEquals("base", EntryPathUtils.parent("/base/index.html"));
        assertEquals("base", EntryPathUtils.parent("base/index.html"));
        assertEquals("", EntryPathUtils.parent("/index.html"));
        assertEquals("", EntryPathUtils.parent("index.html"));
    }

    @Test
    public void testHash() throws IOException {
        assertEquals("base/index.html", EntryPathUtils.parse("base", "./index.html#toc"));
        assertEquals("index.html", EntryPathUtils.parse("base", "/index.html#toc"));
        assertEquals("index.html", EntryPathUtils.parse("base", "../index.html#toc"));
        assertEquals("index.html", EntryPathUtils.parse("base/page", "../../index.html#toc"));

        assertEquals("toc", EntryPathUtils.hash("./index.html#toc"));
        assertEquals("toc", EntryPathUtils.hash("/index.html#toc"));
        assertEquals("toc", EntryPathUtils.hash("../index.html#toc"));
        assertEquals("toc", EntryPathUtils.hash("../../index.html#toc"));

    }
}
