package fun.lzwi.epubime.epub;

import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

public class EpubResourceFallbackTest {

    @Test
    public void testGetFallbackResourceWithNoFallback() {
        // 测试没有回退资源的情况
        EpubResource resource = new EpubResource();
        resource.setId("test-resource");
        
        List<EpubResource> allResources = Arrays.asList(resource);
        
        // 应该返回自身
        EpubResource fallback = resource.getFallbackResource(allResources);
        assertSame(resource, fallback);
    }

    @Test
    public void testGetFallbackResourceWithDirectFallback() {
        // 测试有直接回退资源的情况
        EpubResource resource = new EpubResource();
        resource.setId("primary-resource");
        resource.setFallback("fallback-resource");
        
        EpubResource fallbackResource = new EpubResource();
        fallbackResource.setId("fallback-resource");
        
        List<EpubResource> allResources = Arrays.asList(resource, fallbackResource);
        
        // 应该返回回退资源
        EpubResource fallback = resource.getFallbackResource(allResources);
        assertSame(fallbackResource, fallback);
    }

    @Test
    public void testGetFallbackResourceWithChainFallback() {
        // 测试有链式回退资源的情况
        EpubResource resource1 = new EpubResource();
        resource1.setId("primary-resource");
        resource1.setFallback("fallback1");
        
        EpubResource resource2 = new EpubResource();
        resource2.setId("fallback1");
        resource2.setFallback("fallback2");
        
        EpubResource resource3 = new EpubResource();
        resource3.setId("fallback2");
        
        List<EpubResource> allResources = Arrays.asList(resource1, resource2, resource3);
        
        // 应该返回最终的回退资源
        EpubResource fallback = resource1.getFallbackResource(allResources);
        assertSame(resource3, fallback);
    }

    @Test
    public void testGetFallbackResourceWithMissingFallback() {
        // 测试回退资源不存在的情况
        EpubResource resource = new EpubResource();
        resource.setId("primary-resource");
        resource.setFallback("missing-fallback");
        
        EpubResource otherResource = new EpubResource();
        otherResource.setId("other-resource");
        
        List<EpubResource> allResources = Arrays.asList(resource, otherResource);
        
        // 应该返回自身
        EpubResource fallback = resource.getFallbackResource(allResources);
        assertSame(resource, fallback);
    }

    @Test
    public void testGetResourceWithFallbackInEpubBook() {
        // 测试EpubBook中的回退机制
        EpubBook book = new EpubBook();
        
        // 创建资源列表
        EpubResource resource1 = new EpubResource();
        resource1.setId("primary-resource");
        resource1.setFallback("fallback-resource");
        
        EpubResource resource2 = new EpubResource();
        resource2.setId("fallback-resource");
        resource2.setType("application/xhtml+xml");
        
        List<EpubResource> resources = Arrays.asList(resource1, resource2);
        book.setResources(resources);
        
        // 测试getResource方法
        EpubResource retrieved = EpubBookProcessor.getResource(book, "primary-resource");
        assertSame(resource1, retrieved);

        // 测试getResourceWithFallback方法
        EpubResource fallbackRetrieved = EpubBookProcessor.getResourceWithFallback(book, "primary-resource");
        assertSame(resource2, fallbackRetrieved);
    }
}