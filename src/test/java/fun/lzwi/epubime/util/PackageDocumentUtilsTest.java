package fun.lzwi.epubime.util;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import fun.lzwi.Utils;
import fun.lzwi.epubime.document.PackageDocument;
import fun.lzwi.epubime.document.PackageDocumentUtils;

public class PackageDocumentUtilsTest {
    private InputStream opf;

    @Before
    public void setUp() throws FileNotFoundException {
        opf = new FileInputStream(Utils.getFile("content.opf"));
    }

    @Test
    public void testGetPackageDocument() {
        Node packageElement = PackageDocumentUtils.getPackageElement(opf);
        PackageDocument packageDocument = PackageDocumentUtils.getPackageDocument(packageElement);
        assertNotNull(packageDocument);
    }

    @Test
    public void testGetPackageElement() {
        Node packageElement = PackageDocumentUtils.getPackageElement(opf);
        assertNotNull(packageElement);
    }
}
