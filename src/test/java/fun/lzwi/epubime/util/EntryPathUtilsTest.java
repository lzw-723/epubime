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
    public void testParent() throws IOException {
        assertEquals("base", EntryPathUtils.parent("/base/index.html"));
        assertEquals("base", EntryPathUtils.parent("base/index.html"));
        assertEquals("", EntryPathUtils.parent("/index.html"));
        assertEquals("", EntryPathUtils.parent("index.html"));
    }
}
