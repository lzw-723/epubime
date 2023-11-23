package fun.lzwi.epubime.document.section;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import fun.lzwi.Utils;
import fun.lzwi.epubime.document.PackageDocumentReader;
import fun.lzwi.epubime.document.section.element.ManifestItem;

public class ManifestTest {

    private Manifest manifest;

    @Before
    public void setUp() throws FileNotFoundException, UnsupportedEncodingException {
        InputStream opf = new FileInputStream(Utils.getFile("content.opf"));
        manifest = new PackageDocumentReader(opf).read().getManifest();

    }

    @Test
    public void testSize() {
        assertEquals(3, manifest.getItems().size());
    }

    @Test
    public void testGetItemById() {
        ManifestItem item = manifest.getItemById("sgc-nav.css");
        assertEquals("text/css", item.getMediaType());
    }
}
