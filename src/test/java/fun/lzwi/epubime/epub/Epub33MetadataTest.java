package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.parser.MetadataParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Epub33MetadataTest {
    
    @Test
    public void testRequiredMetadata() {
        // 测试EPUB 3.3必需的元数据
        String opfContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<package xmlns=\"http://www.idpf.org/2007/opf\" version=\"3.0\">\n" +
                "  <metadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
                "    <dc:identifier id=\"book-id\">urn:uuid:123456789</dc:identifier>\n" +
                "    <dc:title>Test Book</dc:title>\n" +
                "    <dc:language>en</dc:language>\n" +
                "    <meta property=\"dcterms:modified\">2023-01-01T12:00:00Z</meta>\n" +
                "  </metadata>\n" +
                "</package>";
        
        MetadataParser metadataParser = new MetadataParser();
        Metadata metadata = metadataParser.parseMetadata(opfContent);
        assertNotNull(metadata);
        assertEquals("Test Book", metadata.getTitle());
        assertEquals("urn:uuid:123456789", metadata.getIdentifier());
        assertEquals("en", metadata.getLanguage());
        assertEquals("2023-01-01T12:00:00Z", metadata.getModified());
    }
    
    @Test
    public void testAccessibilityMetadata() {
        // 测试EPUB 3.3可访问性元数据
        String opfContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<package xmlns=\"http://www.idpf.org/2007/opf\" version=\"3.0\">\n" +
                "  <metadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:meta=\"http://www.idpf.org/2013/metadata\">\n" +
                "    <dc:identifier>urn:uuid:123456789</dc:identifier>\n" +
                "    <dc:title>Accessible Book</dc:title>\n" +
                "    <dc:language>en</dc:language>\n" +
                "    <meta property=\"dcterms:modified\">2023-01-01T12:00:00Z</meta>\n" +
                "    <meta property=\"dcterms:conformsTo\">EPUB Accessibility 1.1 - WCAG 2.1 Level AA</meta>\n" +
                "    <meta property=\"schema:accessMode\">textual</meta>\n" +
                "    <meta property=\"schema:accessMode\">visual</meta>\n" +
                "    <meta property=\"schema:accessibilityFeature\">alternativeText</meta>\n" +
                "    <meta property=\"schema:accessibilityFeature\">longDescriptions</meta>\n" +
                "    <meta property=\"schema:accessibilityFeature\">tableOfContents</meta>\n" +
                "    <meta property=\"schema:accessibilityHazard\">noFlashingHazard</meta>\n" +
                "    <meta property=\"schema:accessibilityHazard\">noMotionSimulationHazard</meta>\n" +
                "    <meta property=\"schema:accessibilityHazard\">noSoundHazard</meta>\n" +
                "    <meta property=\"schema:accessibilitySummary\">This publication includes markup to enable accessibility and compatibility with assistive technology.</meta>\n" +
                "  </metadata>\n" +
                "</package>";
        
        MetadataParser metadataParser = new MetadataParser();
        Metadata metadata = metadataParser.parseMetadata(opfContent);
        assertNotNull(metadata);
        
        // 验证可访问性特征
        assertEquals(3, metadata.getAccessibilityFeatures().size());
        assertTrue(metadata.getAccessibilityFeatures().contains("alternativeText"));
        assertTrue(metadata.getAccessibilityFeatures().contains("longDescriptions"));
        assertTrue(metadata.getAccessibilityFeatures().contains("tableOfContents"));
        
        // 验证可访问性危害
        assertEquals(3, metadata.getAccessibilityHazard().size());
        assertTrue(metadata.getAccessibilityHazard().contains("noFlashingHazard"));
        assertTrue(metadata.getAccessibilityHazard().contains("noMotionSimulationHazard"));
        assertTrue(metadata.getAccessibilityHazard().contains("noSoundHazard"));
        
        // 验证可访问性摘要
        assertEquals("This publication includes markup to enable accessibility and compatibility with assistive technology.", 
                metadata.getAccessibilitySummary());
    }
    
    @Test
    public void testRenderingMetadata() {
        // 测试EPUB 3.3渲染元数据
        String opfContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<package xmlns=\"http://www.idpf.org/2007/opf\" version=\"3.0\">\n" +
                "  <metadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
                "    <dc:identifier>urn:uuid:123456789</dc:identifier>\n" +
                "    <dc:title>Fixed Layout Book</dc:title>\n" +
                "    <dc:language>en</dc:language>\n" +
                "    <meta property=\"dcterms:modified\">2023-01-01T12:00:00Z</meta>\n" +
                "    <meta property=\"rendition:layout\">pre-paginated</meta>\n" +
                "    <meta property=\"rendition:orientation\">landscape</meta>\n" +
                "    <meta property=\"rendition:spread\">both</meta>\n" +
                "  </metadata>\n" +
                "</package>";
        
        MetadataParser metadataParser = new MetadataParser();
        Metadata metadata = metadataParser.parseMetadata(opfContent);
        assertNotNull(metadata);
        
        assertEquals("pre-paginated", metadata.getLayout());
        assertEquals("landscape", metadata.getOrientation());
        assertEquals("both", metadata.getSpread());
    }
}