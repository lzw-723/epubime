package fun.lzwi.epubime.util;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import fun.lzwi.Utils;
import fun.lzwi.epubime.document.NavigationDocument;

public class NavigationDocumentUtilsTest {
    private InputStream doc;

    @Before
    public void setUp() throws FileNotFoundException {
        doc = new FileInputStream(Utils.getFile("nav.xhtml"));
    }

    @Test
    public void testGetBody() {
        Node body = NavigationDocumentUtils.getBody(doc);
        assertNotNull(body);
    }

    @Test
    public void testGetDocument() {
        Node body = NavigationDocumentUtils.getBody(doc);
        NavigationDocument document = NavigationDocumentUtils.getDocument(body);
        assertNotNull(document);
    }
}
