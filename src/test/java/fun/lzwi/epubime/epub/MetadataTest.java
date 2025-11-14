package fun.lzwi.epubime.epub;

import org.junit.Test;
import static org.junit.Assert.*;

public class MetadataTest {
    
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