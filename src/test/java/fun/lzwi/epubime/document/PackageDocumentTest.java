package fun.lzwi.epubime.document;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import fun.lzwi.Utils;

public class PackageDocumentTest {
    PackageDocument packageDocument;

    @Before
    public void setUp() throws UnsupportedEncodingException, IOException {
        packageDocument = new PackageDocumentReader(Files.newInputStream(Utils.getFile("content.opf").toPath())).read();
    }

    @Test
    public void testClone() {
        assertEquals(packageDocument.toString(), packageDocument.clone().toString());
    }

    @Test
    public void testGetManifest() {
        assertEquals(packageDocument.getManifest().getItems().size(), 3);
    }

    @Test
    public void testGetMetaData() {
        assertEquals(packageDocument.getMetaData().getMeta().size(), 2);
    }

    @Test
    public void testGetSpine() {
        assertEquals(packageDocument.getSpine().getItemRefs().size(), 2);
    }
}
