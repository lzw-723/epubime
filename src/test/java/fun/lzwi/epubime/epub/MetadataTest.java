package fun.lzwi.epubime.epub;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class MetadataTest {
    
    @Test
    public void testDefaultConstructor() {
        Metadata metadata = new Metadata();
        
        // 测试默认构造函数创建空的列表
        assertNotNull(metadata.getContributors());
        assertNotNull(metadata.getSubjects());
        assertTrue(metadata.getContributors().isEmpty());
        assertTrue(metadata.getSubjects().isEmpty());
    }
    
    @Test
    public void testCopyConstructor() {
        // 创建一个原始Metadata对象并设置所有字段
        Metadata original = new Metadata();
        original.setTitle("Test Title");
        original.setCreator("Test Creator");
        original.addContributor("Contributor 1");
        original.addContributor("Contributor 2");
        original.setPublisher("Test Publisher");
        original.setIdentifier("Test Identifier");
        original.addSubject("Subject 1");
        original.addSubject("Subject 2");
        original.setDate("2023-01-01");
        original.setLanguage("en");
        original.setDescription("Test Description");
        original.setRights("Test Rights");
        original.setType("Test Type");
        original.setFormat("Test Format");
        original.setSource("Test Source");
        original.setModified("2023-01-01T12:00:00Z");
        original.setRightsHolder("Test Rights Holder");
        original.setCover("cover.jpg");
        
        // 使用复制构造函数创建新对象
        Metadata copy = new Metadata(original);
        
        // 验证所有字段都被正确复制
        assertEquals("Test Title", copy.getTitle());
        assertEquals("Test Creator", copy.getCreator());
        assertEquals("Test Publisher", copy.getPublisher());
        assertEquals("Test Identifier", copy.getIdentifier());
        assertEquals("2023-01-01", copy.getDate());
        assertEquals("en", copy.getLanguage());
        assertEquals("Test Description", copy.getDescription());
        assertEquals("Test Rights", copy.getRights());
        assertEquals("Test Type", copy.getType());
        assertEquals("Test Format", copy.getFormat());
        assertEquals("Test Source", copy.getSource());
        assertEquals("2023-01-01T12:00:00Z", copy.getModified());
        assertEquals("Test Rights Holder", copy.getRightsHolder());
        assertEquals("cover.jpg", copy.getCover());
        
        // 验证列表字段
        assertEquals(2, copy.getContributors().size());
        assertEquals("Contributor 1", copy.getContributor());
        assertEquals(2, copy.getSubjects().size());
        assertEquals("Subject 1", copy.getSubject());
    }
    
    @Test
    public void testTitleGetterAndSetter() {
        Metadata metadata = new Metadata();
        metadata.setTitle("Test Title");
        assertEquals("Test Title", metadata.getTitle());
    }
    
    @Test
    public void testCreatorGetterAndSetter() {
        Metadata metadata = new Metadata();
        metadata.setCreator("Test Creator");
        assertEquals("Test Creator", metadata.getCreator());
    }
    
    @Test
    public void testPublisherGetterAndSetter() {
        Metadata metadata = new Metadata();
        metadata.setPublisher("Test Publisher");
        assertEquals("Test Publisher", metadata.getPublisher());
    }
    
    @Test
    public void testIdentifierGetterAndSetter() {
        Metadata metadata = new Metadata();
        metadata.setIdentifier("Test Identifier");
        assertEquals("Test Identifier", metadata.getIdentifier());
    }
    
    @Test
    public void testDateGetterAndSetter() {
        Metadata metadata = new Metadata();
        metadata.setDate("2023-01-01");
        assertEquals("2023-01-01", metadata.getDate());
    }
    
    @Test
    public void testLanguageGetterAndSetter() {
        Metadata metadata = new Metadata();
        metadata.setLanguage("en");
        assertEquals("en", metadata.getLanguage());
    }
    
    @Test
    public void testDescriptionGetterAndSetter() {
        Metadata metadata = new Metadata();
        metadata.setDescription("Test Description");
        assertEquals("Test Description", metadata.getDescription());
    }
    
    @Test
    public void testRightsGetterAndSetter() {
        Metadata metadata = new Metadata();
        metadata.setRights("Test Rights");
        assertEquals("Test Rights", metadata.getRights());
    }
    
    @Test
    public void testTypeGetterAndSetter() {
        Metadata metadata = new Metadata();
        metadata.setType("Test Type");
        assertEquals("Test Type", metadata.getType());
    }
    
    @Test
    public void testFormatGetterAndSetter() {
        Metadata metadata = new Metadata();
        metadata.setFormat("Test Format");
        assertEquals("Test Format", metadata.getFormat());
    }
    
    @Test
    public void testSourceGetterAndSetter() {
        Metadata metadata = new Metadata();
        metadata.setSource("Test Source");
        assertEquals("Test Source", metadata.getSource());
    }
    
    @Test
    public void testModifiedGetterAndSetter() {
        Metadata metadata = new Metadata();
        metadata.setModified("2023-01-01T12:00:00Z");
        assertEquals("2023-01-01T12:00:00Z", metadata.getModified());
    }
    
    @Test
    public void testRightsHolderGetterAndSetter() {
        Metadata metadata = new Metadata();
        metadata.setRightsHolder("Test Rights Holder");
        assertEquals("Test Rights Holder", metadata.getRightsHolder());
    }
    
    @Test
    public void testCoverGetterAndSetter() {
        Metadata metadata = new Metadata();
        metadata.setCover("cover.jpg");
        assertEquals("cover.jpg", metadata.getCover());
    }
    
    @Test
    public void testContributorsList() {
        Metadata metadata = new Metadata();
        
        // 测试添加贡献者
        metadata.addContributor("Contributor 1");
        metadata.addContributor("Contributor 2");
        
        // 测试获取贡献者列表
        List<String> contributors = metadata.getContributors();
        assertEquals(2, contributors.size());
        assertEquals("Contributor 1", contributors.get(0));
        assertEquals("Contributor 2", contributors.get(1));
        
        // 测试获取第一个贡献者
        assertEquals("Contributor 1", metadata.getContributor());
        
        // 验证返回的列表是不可变的
        assertThrows(UnsupportedOperationException.class, () -> {
            contributors.add("Another Contributor");
        });
    }
    
    @Test
    public void testSubjectsList() {
        Metadata metadata = new Metadata();
        
        // 测试添加主题
        metadata.addSubject("Subject 1");
        metadata.addSubject("Subject 2");
        
        // 测试获取主题列表
        List<String> subjects = metadata.getSubjects();
        assertEquals(2, subjects.size());
        assertEquals("Subject 1", subjects.get(0));
        assertEquals("Subject 2", subjects.get(1));
        
        // 测试获取第一个主题
        assertEquals("Subject 1", metadata.getSubject());
        
        // 验证返回的列表是不可变的
        assertThrows(UnsupportedOperationException.class, () -> {
            subjects.add("Another Subject");
        });
    }
    
    @Test
    public void testGetSubjectAndContributorFromEmptyList() {
        Metadata metadata = new Metadata();
        
        // 测试从空列表获取第一个元素会抛出异常
        assertThrows(IndexOutOfBoundsException.class, () -> {
            metadata.getSubject();
        });
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            metadata.getContributor();
        });
    }
    
    @Test
    public void testNewFieldsGettersAndSetters() {
        Metadata metadata = new Metadata();
        
        // 测试 modified 字段
        metadata.setModified("2023-01-01T12:00:00Z");
        assertEquals("2023-01-01T12:00:00Z", metadata.getModified());
        
        // 测试 rightsHolder 字段
        metadata.setRightsHolder("Rights Holder Name");
        assertEquals("Rights Holder Name", metadata.getRightsHolder());
    }
}