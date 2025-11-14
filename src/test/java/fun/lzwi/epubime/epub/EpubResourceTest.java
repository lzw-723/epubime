package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class EpubResourceTest {

    @Test
    public void testEpubResourceStreamProcessing() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubBook book = new EpubParser(epubFile).parse();
        
        // 验证资源现在只存储引用而不立即加载数据
        assertFalse(book.getResources().isEmpty());
        
        // 验证第一个资源可以通过流访问
        EpubResource resource = book.getResources().get(0);
        assertNotNull(resource.getHref());
        assertNotNull(resource.getId());
        
        // 验证可以通过流访问资源
        try (InputStream inputStream = resource.getInputStream()) {
            assertNotNull("InputStream should not be null for valid resource", inputStream);
            assertTrue("InputStream should be able to read data", inputStream.read() != -1);
        }
        
        // 验证getData()仍然可以按需加载数据
        byte[] data = resource.getData();
        assertNotNull("Data should be available when requested", data);
        assertTrue("Data array should have content", data.length > 0);
    }

    @Test
    public void testEpubResourceWithNullEpubFile() throws Exception {
        // 测试当epubFile为null时的行为
        EpubResource resource = new EpubResource();
        resource.setHref("test/path");
        resource.setEpubFile(null);
        
        // 当epubFile为null时，getInputStream应该返回null
        try (InputStream inputStream = resource.getInputStream()) {
            assertNull("InputStream should be null when epubFile is null", inputStream);
        }
        
        // 当epubFile为null时，getData也应该返回null
        byte[] data = resource.getData();
        assertNull("Data should be null when epubFile is null", data);
    }

    @Test
    public void testEpubResourceWithNullHref() throws Exception {
        // 测试当href为null时的行为
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubResource resource = new EpubResource();
        resource.setEpubFile(epubFile);
        resource.setHref(null);
        
        // 当href为null时，getInputStream应该返回null
        try (InputStream inputStream = resource.getInputStream()) {
            assertNull("InputStream should be null when href is null", inputStream);
        }
    }

    @Test
    public void testLoadResourceData() throws Exception {
        // 测试批量加载资源数据的功能
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        
        // 为测试创建新资源，以确保数据未加载
        EpubResource testResource = new EpubResource();
        testResource.setEpubFile(epubFile);
        testResource.setHref("mimetype"); // 使用一个简单的文件进行测试
        
        // 不要调用getData()方法，因为这会触发数据加载
        // 直接设置一个初始数据为null的资源
        testResource.setData(null); // 确保数据是null
        
        // 创建只包含测试资源的列表
        java.util.List<EpubResource> testResources = java.util.Arrays.asList(testResource);

        // 批量加载所有资源的数据
        EpubResource.loadResourceData(testResources, epubFile);

        // 验证资源数据已加载
        byte[] data = testResource.getData();
        assertNotNull("Resource data should be loaded", data);
        assertTrue("Resource data should have content", data.length > 0);
    }

    @Test
    public void testLoadResourceDataWithEmptyList() throws Exception {
        // 测试批量加载空资源列表的功能
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        java.util.List<EpubResource> emptyResources = new java.util.ArrayList<>();

        // 批量加载空资源列表的数据 - 这不应该抛出异常
        EpubResource.loadResourceData(emptyResources, epubFile);

        // 验证空列表仍为空
        assertTrue("Resource list should remain empty", emptyResources.isEmpty());
    }
    
    @Test
    public void testProcessContent() throws Exception {
        // 测试流式处理资源内容的功能
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubResource resource = new EpubResource();
        resource.setEpubFile(epubFile);
        resource.setHref("mimetype");
        
        StringBuilder content = new StringBuilder();
        boolean[] processed = {false};
        
        resource.processContent(new Consumer<java.io.InputStream>() {
            @Override
            public void accept(java.io.InputStream inputStream) {
                try {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream, "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    processed[0] = true;
                } catch (java.io.IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        
        assertTrue("Resource content should be processed", processed[0]);
        assertEquals("application/epub+zip", content.toString());
    }
    
    @Test
    public void testPropertiesAttribute() throws Exception {
        // 测试properties属性的解析功能
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubBook book = new EpubParser(epubFile).parse();
        
        // 验证资源列表不为空
        assertFalse(book.getResources().isEmpty());
        
        // 检查是否正确解析了properties属性
        for (EpubResource resource : book.getResources()) {
            // 属性可能为null，这是正常的
            // 验证可以获取和设置properties属性
            String originalProperties = resource.getProperties();
            String testProperties = "test-property";
            resource.setProperties(testProperties);
            assertEquals(testProperties, resource.getProperties());
            resource.setProperties(originalProperties); // 恢复原始值
        }
    }
}