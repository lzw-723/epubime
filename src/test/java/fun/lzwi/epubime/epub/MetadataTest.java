package fun.lzwi.epubime.epub;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MetadataTest {
    
    @Test
    public void testMultipleValues() {
        Metadata metadata = new Metadata();
        
        // 测试标题
        metadata.addTitle("Title 1");
        metadata.addTitle("Title 2");
        assertEquals("Title 1", metadata.getTitle());
        assertEquals(2, metadata.getTitles().size());
        
        // 测试创建者
        metadata.addCreator("Creator 1");
        metadata.addCreator("Creator 2");
        assertEquals("Creator 1", metadata.getCreator());
        assertEquals(2, metadata.getCreators().size());
        
        // 测试语言
        metadata.addLanguage("en");
        metadata.addLanguage("zh");
        assertEquals("en", metadata.getLanguage());
        assertEquals(2, metadata.getLanguages().size());
    }
    
    @Test
    public void testAllMetadataFields() {
        Metadata metadata = new Metadata();
        
        // 测试所有元数据字段
        metadata.addTitle("Test Title");
        metadata.addCreator("Test Author");
        metadata.addPublisher("Test Publisher");
        metadata.addIdentifier("test-identifier");
        metadata.addSubject("Test Subject");
        metadata.addDate("2023-01-01");
        metadata.addLanguage("en");
        metadata.addDescription("Test Description");
        metadata.addRights("Test Rights");
        metadata.addType("Test Type");
        metadata.addFormat("application/epub+zip");
        metadata.addSource("Test Source");
        metadata.addContributor("Test Contributor");
        
        // 验证获取第一个值的方法
        assertEquals("Test Title", metadata.getTitle());
        assertEquals("Test Author", metadata.getCreator());
        assertEquals("Test Publisher", metadata.getPublisher());
        assertEquals("test-identifier", metadata.getIdentifier());
        assertEquals("Test Subject", metadata.getSubject());
        assertEquals("2023-01-01", metadata.getDate());
        assertEquals("en", metadata.getLanguage());
        assertEquals("Test Description", metadata.getDescription());
        assertEquals("Test Rights", metadata.getRights());
        assertEquals("Test Type", metadata.getType());
        assertEquals("application/epub+zip", metadata.getFormat());
        assertEquals("Test Source", metadata.getSource());
        assertEquals("Test Contributor", metadata.getContributor());
        
        // 验证列表大小
        assertEquals(1, metadata.getTitles().size());
        assertEquals(1, metadata.getCreators().size());
        assertEquals(1, metadata.getPublishers().size());
        assertEquals(1, metadata.getIdentifiers().size());
        assertEquals(1, metadata.getSubjects().size());
        assertEquals(1, metadata.getDates().size());
        assertEquals(1, metadata.getLanguages().size());
        assertEquals(1, metadata.getDescriptions().size());
        assertEquals(1, metadata.getRightsList().size());
        assertEquals(1, metadata.getTypes().size());
        assertEquals(1, metadata.getFormats().size());
        assertEquals(1, metadata.getSources().size());
        assertEquals(1, metadata.getContributors().size());
    }
    
    @Test
    public void testMultipleValuesForAllFields() {
        Metadata metadata = new Metadata();
        
        // 添加多个值到每个字段
        metadata.addTitle("Title 1");
        metadata.addTitle("Title 2");
        metadata.addCreator("Creator 1");
        metadata.addCreator("Creator 2");
        metadata.addPublisher("Publisher 1");
        metadata.addPublisher("Publisher 2");
        metadata.addIdentifier("ID 1");
        metadata.addIdentifier("ID 2");
        metadata.addSubject("Subject 1");
        metadata.addSubject("Subject 2");
        metadata.addDate("Date 1");
        metadata.addDate("Date 2");
        metadata.addLanguage("en");
        metadata.addLanguage("fr");
        metadata.addDescription("Description 1");
        metadata.addDescription("Description 2");
        metadata.addRights("Rights 1");
        metadata.addRights("Rights 2");
        metadata.addType("Type 1");
        metadata.addType("Type 2");
        metadata.addFormat("Format 1");
        metadata.addFormat("Format 2");
        metadata.addSource("Source 1");
        metadata.addSource("Source 2");
        metadata.addContributor("Contributor 1");
        metadata.addContributor("Contributor 2");
        
        // 验证获取第一个值的方法
        assertEquals("Title 1", metadata.getTitle());
        assertEquals("Creator 1", metadata.getCreator());
        assertEquals("Publisher 1", metadata.getPublisher());
        assertEquals("ID 1", metadata.getIdentifier());
        assertEquals("Subject 1", metadata.getSubject());
        assertEquals("Date 1", metadata.getDate());
        assertEquals("en", metadata.getLanguage());
        assertEquals("Description 1", metadata.getDescription());
        assertEquals("Rights 1", metadata.getRights());
        assertEquals("Type 1", metadata.getType());
        assertEquals("Format 1", metadata.getFormat());
        assertEquals("Source 1", metadata.getSource());
        assertEquals("Contributor 1", metadata.getContributor());
        
        // 验证列表大小
        assertEquals(2, metadata.getTitles().size());
        assertEquals(2, metadata.getCreators().size());
        assertEquals(2, metadata.getPublishers().size());
        assertEquals(2, metadata.getIdentifiers().size());
        assertEquals(2, metadata.getSubjects().size());
        assertEquals(2, metadata.getDates().size());
        assertEquals(2, metadata.getLanguages().size());
        assertEquals(2, metadata.getDescriptions().size());
        assertEquals(2, metadata.getRightsList().size());
        assertEquals(2, metadata.getTypes().size());
        assertEquals(2, metadata.getFormats().size());
        assertEquals(2, metadata.getSources().size());
        assertEquals(2, metadata.getContributors().size());
    }
    
    @Test
    public void testSetSingleValueMethods() {
        Metadata metadata = new Metadata();
        
        // 测试设置单个值的方法（清空现有值并添加新值）
        metadata.setTitle("Single Title");
        assertEquals("Single Title", metadata.getTitle());
        assertEquals(1, metadata.getTitles().size());
        
        metadata.setCreator("Single Creator");
        assertEquals("Single Creator", metadata.getCreator());
        assertEquals(1, metadata.getCreators().size());
        
        metadata.setPublisher("Single Publisher");
        assertEquals("Single Publisher", metadata.getPublisher());
        assertEquals(1, metadata.getPublishers().size());
        
        metadata.setIdentifier("Single ID");
        assertEquals("Single ID", metadata.getIdentifier());
        assertEquals(1, metadata.getIdentifiers().size());
        
        metadata.addSubject("Single Subject");
        assertEquals("Single Subject", metadata.getSubject());
        assertEquals(1, metadata.getSubjects().size());
        
        metadata.setDate("Single Date");
        assertEquals("Single Date", metadata.getDate());
        assertEquals(1, metadata.getDates().size());
        
        metadata.setLanguage("Single Language");
        assertEquals("Single Language", metadata.getLanguage());
        assertEquals(1, metadata.getLanguages().size());
        
        metadata.setDescription("Single Description");
        assertEquals("Single Description", metadata.getDescription());
        assertEquals(1, metadata.getDescriptions().size());
        
        metadata.setRights("Single Rights");
        assertEquals("Single Rights", metadata.getRights());
        assertEquals(1, metadata.getRightsList().size());
        
        metadata.setType("Single Type");
        assertEquals("Single Type", metadata.getType());
        assertEquals(1, metadata.getTypes().size());
        
        metadata.setFormat("Single Format");
        assertEquals("Single Format", metadata.getFormat());
        assertEquals(1, metadata.getFormats().size());
        
        metadata.setSource("Single Source");
        assertEquals("Single Source", metadata.getSource());
        assertEquals(1, metadata.getSources().size());
        
        // 测试添加多个值后使用设置单个值的方法
        metadata.addTitle("Additional Title");
        assertEquals(2, metadata.getTitles().size());
        
        metadata.setTitle("Replaced Title");
        assertEquals("Replaced Title", metadata.getTitle());
        assertEquals(1, metadata.getTitles().size()); // 应该只有1个，因为setTitle清空了之前的值
    }
    
    @Test
    public void testAccessibilityMetadata() {
        Metadata metadata = new Metadata();
        
        // 测试可访问性特征
        metadata.addAccessibilityFeature("alternativeText");
        metadata.addAccessibilityFeature("longDescriptions");
        assertEquals(2, metadata.getAccessibilityFeatures().size());
        assertTrue(metadata.getAccessibilityFeatures().contains("alternativeText"));
        assertTrue(metadata.getAccessibilityFeatures().contains("longDescriptions"));
        
        // 测试可访问性危害
        metadata.addAccessibilityHazard("noFlashingHazard");
        metadata.addAccessibilityHazard("noMotionSimulationHazard");
        assertEquals(2, metadata.getAccessibilityHazard().size());
        assertTrue(metadata.getAccessibilityHazard().contains("noFlashingHazard"));
        assertTrue(metadata.getAccessibilityHazard().contains("noMotionSimulationHazard"));
        
        // 测试可访问性摘要
        metadata.addAccessibilitySummary("This publication includes markup to enable accessibility and compatibility with assistive technology.");
        assertEquals("This publication includes markup to enable accessibility and compatibility with assistive technology.", metadata.getAccessibilitySummary());
    }
    
    @Test
    public void testRenderingProperties() {
        Metadata metadata = new Metadata();
        
        // 测试渲染属性
        metadata.setLayout("pre-paginated");
        metadata.setOrientation("landscape");
        metadata.setSpread("both");
        metadata.setViewport("width=1200,height=600");
        metadata.setMedia("(min-width: 600px)");
        metadata.setFlow("paginated");
        metadata.setAlignXCenter(true);
        
        assertEquals("pre-paginated", metadata.getLayout());
        assertEquals("landscape", metadata.getOrientation());
        assertEquals("both", metadata.getSpread());
        assertEquals("width=1200,height=600", metadata.getViewport());
        assertEquals("(min-width: 600px)", metadata.getMedia());
        assertEquals("paginated", metadata.getFlow());
        assertTrue(metadata.isAlignXCenter());
    }
    
    @Test
    public void testCopyConstructor() {
        Metadata original = new Metadata();
        original.addTitle("Original Title");
        original.addCreator("Original Creator");
        original.addLanguage("en");
        original.setCover("cover-image");
        original.setModified("2023-01-01T00:00:00Z");
        original.addAccessibilityFeature("alternativeText");
        original.setLayout("reflowable");
        
        Metadata copy = new Metadata(original);
        
        // 验证复制是否成功
        assertEquals("Original Title", copy.getTitle());
        assertEquals("Original Creator", copy.getCreator());
        assertEquals("en", copy.getLanguage());
        assertEquals("cover-image", copy.getCover());
        assertEquals("2023-01-01T00:00:00Z", copy.getModified());
        assertEquals(1, copy.getAccessibilityFeatures().size());
        assertEquals("alternativeText", copy.getAccessibilityFeatures().get(0));
        assertEquals("reflowable", copy.getLayout());
        
        // 验证副本的独立性
        copy.addTitle("Additional Title");
        assertEquals(2, copy.getTitles().size());
        assertEquals(1, original.getTitles().size()); // 原始对象不应受影响
    }
    
    @Test
    public void testUniqueIdentifier() {
        Metadata metadata = new Metadata();
        
        // 测试uniqueIdentifier的getter和setter
        assertNull(metadata.getUniqueIdentifier());
        metadata.setUniqueIdentifier("test-unique-id");
        assertEquals("test-unique-id", metadata.getUniqueIdentifier());
        
        // 验证uniqueIdentifier与其他identifier的区别
        metadata.addIdentifier("other-id");
        assertEquals("test-unique-id", metadata.getUniqueIdentifier()); // uniqueIdentifier保持不变
        assertEquals("other-id", metadata.getIdentifier()); // identifier为列表中的第一个
        assertEquals(1, metadata.getIdentifiers().size());
    }

    @Test
    public void testEmptyListHandling() {
        Metadata metadata = new Metadata();
        
        // 验证空列表处理
        assertNull(metadata.getTitle());
        assertTrue(metadata.getTitles().isEmpty());
        
        assertNull(metadata.getCreator());
        assertTrue(metadata.getCreators().isEmpty());
        
        assertNull(metadata.getLanguage());
        assertTrue(metadata.getLanguages().isEmpty());
        
        assertNull(metadata.getIdentifier());
        assertTrue(metadata.getIdentifiers().isEmpty());
        
        assertNull(metadata.getUniqueIdentifier());
        
        assertNull(metadata.getPublisher());
        assertTrue(metadata.getPublishers().isEmpty());
        
        assertNull(metadata.getSubject());
        assertTrue(metadata.getSubjects().isEmpty());
        
        assertNull(metadata.getDate());
        assertTrue(metadata.getDates().isEmpty());
        
        assertNull(metadata.getDescription());
        assertTrue(metadata.getDescriptions().isEmpty());
        
        assertNull(metadata.getRights());
        assertTrue(metadata.getRightsList().isEmpty());
        
        assertNull(metadata.getType());
        assertTrue(metadata.getTypes().isEmpty());
        
        assertNull(metadata.getFormat());
        assertTrue(metadata.getFormats().isEmpty());
        
        assertNull(metadata.getSource());
        assertTrue(metadata.getSources().isEmpty());
        
        assertNull(metadata.getContributor());
        assertTrue(metadata.getContributors().isEmpty());
        
        assertNull(metadata.getAccessibilitySummary());
        assertTrue(metadata.getAccessibilityFeatures().isEmpty());
        assertTrue(metadata.getAccessibilityHazard().isEmpty());
    }
}