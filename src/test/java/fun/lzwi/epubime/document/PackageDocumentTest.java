package fun.lzwi.epubime.document;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PackageDocumentTest {
    @Test
    public void testClone() {
        PackageDocument packageDocument = new PackageDocument();
        assertEquals(packageDocument.toString(), packageDocument.clone().toString());
    }
}
