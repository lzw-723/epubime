package fun.lzwi.epubime.document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import fun.lzwi.epubime.document.section.Manifest;

public class PackageDocumentTest {
    PackageDocument packageDocument;

    @Before
    public void setUp() {
        packageDocument = new PackageDocument();
    }

    @Test
    public void testClone() {
        assertEquals(packageDocument.toString(), packageDocument.clone().toString());
    }

    @Test
    public void testGetManifest() {
        Manifest manifest = packageDocument.getManifest();
        assertNull(manifest);;
    }

    @Test
    public void testSetManifest() {
        Manifest manifest = new Manifest();
        packageDocument.setManifest(manifest);
        assertNotNull(packageDocument.getManifest());

    }
}
