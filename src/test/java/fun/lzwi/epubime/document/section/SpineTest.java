package fun.lzwi.epubime.document.section;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import fun.lzwi.Utils;
import fun.lzwi.epubime.document.PackageDocumentReader;

public class SpineTest {

    private Spine spine;

    @Before
    public void setUp() throws FileNotFoundException, UnsupportedEncodingException {
        InputStream opf = new FileInputStream(Utils.getFile("content.opf"));
        spine = new PackageDocumentReader(opf).read().getSpine();
    }

    @Test
    public void testClone() {
        assertNotEquals(spine, spine.clone());
    }

    @Test
    public void testGetId() {
        assertEquals(null, spine.getId());
    }

    @Test
    public void testGetItemRefs() {
        assertEquals(2, spine.getItemRefs().size());
    }

    @Test
    public void testSize() {
        assertEquals(2, spine.size());
    }

}
