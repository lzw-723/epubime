package fun.lzwi.epubime.document.section;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
        assertEquals(metaData.getDc().getIdentifiers().size(), 0);
    }

    @Test
    public void testGetLanguages() {
        assertEquals(metaData.getDc().getLanguages().size(), 0);
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
        assertEquals(metaData.getDc().getTitles().size(), 0);
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

}
