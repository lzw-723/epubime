package fun.lzwi.epubime.document;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import fun.lzwi.Utils;

public class PackageDocumentReaderTest {
    PackageDocument packageDocument;

    @Before
    public void setUp() throws UnsupportedEncodingException, IOException {
        packageDocument = new PackageDocumentReader(Files.newInputStream(Utils.getFile("content.opf").toPath()), "entry/href").read();
    }

    @Test
    public void testClone() {
        assertEquals(packageDocument.toString(), packageDocument.clone().toString());
    }

    @Test
    public void testGetManifest() {
        assertEquals(3,packageDocument.getManifest().getItems().size());
    }

    @Test
    public void testGetMetaData() {
        assertEquals(2,packageDocument.getMetaData().getMeta().size());
    }

    @Test
    public void testGetSpine() {
        assertEquals(2,packageDocument.getSpine().getItemRefs().size());
    }
    @Test
    public void testGetHref() {
        assertEquals("entry/href",packageDocument.getHref());
    }
}
