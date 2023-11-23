package fun.lzwi.epubime.document;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import fun.lzwi.Utils;

public class PackageDocumentUtilsTest {

    private PackageDocument packageDocument;

    @Before
    public void setUp() throws FileNotFoundException, UnsupportedEncodingException {
        FileInputStream in = new FileInputStream(Utils.getFile("content.opf"));
        Node packageElement = PackageDocumentUtils.getPackageElement(in);
        packageDocument = PackageDocumentUtils.getPackageDocument(packageElement);
    }

    @Test
    public void testGetManifestSection() {
        assertEquals(3, packageDocument.getManifest().size());
    }

    @Test
    public void testGetMetaDataSection() {
        assertEquals(2, packageDocument.getMetaData().getMeta().size());
        // TODO: DC
    }

    @Test
    public void testGetPackageDocument() {
        assertEquals("BookId", packageDocument.getUniqueIdentifier());
    }

    @Test
    public void testGetSpineSection() {
        assertEquals(2, packageDocument.getSpine().size());
    }
}
