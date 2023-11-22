package fun.lzwi.epubime.document.section;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import fun.lzwi.Utils;
import fun.lzwi.epubime.document.PackageDocument;
import fun.lzwi.epubime.document.PackageDocumentReader;

public class MetaDataTest {

    private MetaData metaData;

    @Before
    public void setUp() throws UnsupportedEncodingException, IOException {
        PackageDocument packageDocument = new PackageDocumentReader(Files.newInputStream(Utils.getFile("content.opf").toPath())).read();
        metaData = packageDocument.getMetaData();
    }

    @Test
    public void testGetContributors() {
        assertEquals(metaData.getDc().getContributors().size(), 0);
    }

    @Test
    public void testGetCoverages() {
        assertEquals(metaData.getDc().getCoverages().size(), 0);
    }

    @Test
    public void testGetCreators() {
        assertEquals(metaData.getDc().getCreators().size(), 0);
    }

    @Test
    public void testGetDates() {
        assertEquals(metaData.getDc().getDates().size(), 0);
    }

    @Test
    public void testGetDescriptions() {
        assertEquals(metaData.getDc().getDescriptions().size(), 0);
    }

    @Test
    public void testGetFormats() {
        assertEquals(metaData.getDc().getFormats().size(), 0);
    }

    @Test
    public void testGetIdentifiers() {
        assertEquals(metaData.getDc().getIdentifiers().size(), 1);
    }

    @Test
    public void testGetLanguages() {
        assertEquals(metaData.getDc().getLanguages().size(), 1);
    }

    @Test
    public void testGetPublishers() {
        assertEquals(metaData.getDc().getPublishers().size(), 0);
    }

    @Test
    public void testGetRelations() {
        assertEquals(metaData.getDc().getRelations().size(), 0);
    }

    @Test
    public void testGetRights() {
        assertEquals(metaData.getDc().getRights().size(), 0);
    }

    @Test
    public void testGetSources() {
        assertEquals(metaData.getDc().getSources().size(), 0);
    }

    @Test
    public void testGetSubjects() {
        assertEquals(metaData.getDc().getSubjects().size(), 0);
    }

    @Test
    public void testGetTitles() {
        assertEquals(metaData.getDc().getTitles().size(), 1);
    }

    @Test
    public void testGetTypes() {
        assertEquals(metaData.getDc().getTypes().size(), 0);
    }

    @Test
    public void testClone() {
        MetaData metaData = new MetaData();
        assertNotEquals(metaData, metaData.clone());
    }

    @Test
    public void testGetMeta() {
        int size = metaData.getMeta().size();
        assertEquals(2, size);
    }
    @Test
    public void testGetMeta2() {
        assertEquals("1.9.30", metaData.getMeta().get("Sigil version"));
    }

}
