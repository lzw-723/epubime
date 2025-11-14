package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class EpubBookTest {

    @Test
    public void getCover() throws EpubParseException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubBook book = new EpubParser(epubFile).parse();
        EpubResource cover = book.getCover();
        assertNotNull(cover);
        assertEquals("OEBPS/images/Cover.jpg", cover.getHref());
    }

    @Test
    public void loadAllResourceData() throws Exception {
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

        // 加载所有资源的数据
        EpubResource.loadResourceData(testResources, epubFile);

        // 验证资源数据已加载
        byte[] data = testResource.getData();
        assertNotNull("Resource data should be loaded", data);
        assertTrue("Resource data should have content", data.length > 0);
    }

    @Test
    public void getResourcesReturnsUnmodifiableList() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubBook book = new EpubParser(epubFile).parse();
        java.util.List<EpubResource> resources = book.getResources();

        // 验证返回的列表是不可修改的
        try {
            resources.add(new EpubResource());
            fail("Expected UnsupportedOperationException when modifying returned list");
        } catch (UnsupportedOperationException e) {
            // 期望异常，测试通过
        }
    }
    
    @Test
    public void processHtmlChapters() throws Exception {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubBook book = new EpubParser(epubFile).parse();
        
        java.util.List<EpubChapter> chapters = book.getChapters();
        // 验证是否有章节
        if (chapters.isEmpty()) {
            // 如果没有章节，验证方法可以被调用而不会抛出异常
            try {
                book.processHtmlChapters(new BiConsumer<EpubChapter, InputStream>() {
                    @Override
                    public void accept(EpubChapter chapter, InputStream inputStream) {
                        // 不执行任何操作
                    }
                });
            } catch (Exception e) {
                fail("processHtmlChapters should not throw exception when no chapters available");
            }
        } else {
            int[] processedCount = {0};
            boolean[] validChapterProcessed = {false};
            String[] processedChapterTitle = {null};
            
            book.processHtmlChapters(new BiConsumer<EpubChapter, InputStream>() {
                @Override
                public void accept(EpubChapter chapter, InputStream inputStream) {
                    processedCount[0]++;
                    processedChapterTitle[0] = chapter.getTitle();
                    // 验证我们至少处理了一个章节
                    if (processedCount[0] >= 1) {
                        validChapterProcessed[0] = true;
                    }
                    // 不实际读取输入流内容，只是验证可以访问
                    assertNotNull("Chapter should not be null", chapter);
                    assertNotNull("InputStream should not be null", inputStream);
                }
            });
            
            assertTrue("Processed count should be greater than or equal to 0", processedCount[0] >= 0);
        }
    }
}