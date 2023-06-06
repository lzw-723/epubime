package fun.lzwi.epubime.document.section;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MetaDataTest {

    private MetaData metaData;

    @Before
    public void setUp() {
        metaData = new MetaData();
    }

    @Test
    public void testGetContributors() {
        assertEquals(metaData.getContributors().size(), 0);
    }

    @Test
    public void testGetCoverages() {
        assertEquals(metaData.getCoverages().size(), 0);
    }

    @Test
    public void testGetCreators() {
        assertEquals(metaData.getCreators().size(), 0);
    }

    @Test
    public void testGetDates() {
        assertEquals(metaData.getDates().size(), 0);
    }

    @Test
    public void testGetDescriptions() {
        assertEquals(metaData.getDescriptions().size(), 0);
    }

    @Test
    public void testGetFormats() {
        assertEquals(metaData.getFormats().size(), 0);
    }

    @Test
    public void testGetIdentifiers() {
        assertEquals(metaData.getIdentifiers().size(), 0);
    }

    @Test
    public void testGetLanguages() {
        assertEquals(metaData.getLanguages().size(), 0);
    }

    @Test
    public void testGetPublishers() {
        assertEquals(metaData.getPublishers().size(), 0);
    }

    @Test
    public void testGetRelations() {
        assertEquals(metaData.getRelations().size(), 0);
    }

    @Test
    public void testGetRights() {
        assertEquals(metaData.getRights().size(), 0);
    }

    @Test
    public void testGetSources() {
        assertEquals(metaData.getSources().size(), 0);
    }

    @Test
    public void testGetSubjects() {
        assertEquals(metaData.getSubjects().size(), 0);
    }

    @Test
    public void testGetTitles() {
        assertEquals(metaData.getTitles().size(), 0);
    }

    @Test
    public void testGetTypes() {
        assertEquals(metaData.getTypes().size(), 0);
    }
}
