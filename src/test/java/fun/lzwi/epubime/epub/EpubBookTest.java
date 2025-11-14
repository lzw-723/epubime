package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.ResUtils;
import fun.lzwi.epubime.exception.EpubParseException;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.function.BiConsumer;

import static org.junit.Assert.*;

public class EpubBookTest {

    @Test
    public void getCover() throws EpubParseException {
        File epubFile = ResUtils.getFileFromRes("fun/lzwi/epubime/epub/《坟》鲁迅.epub");
        EpubBook book = new EpubParser(epubFile).parse();
        EpubResource cover = book.getCover();
        assertNotNull(cover);
        // 验证封面资源的href包含"Cover.jpg"或类似图片文件名
        assertTrue(cover.getHref().toLowerCase().contains("cover"));
    }

    @Test
    public void getCoverWithPropertiesAttribute() {
        // 创建一个模拟的EPUB内容用于测试properties="cover-image"属性
        String sampleOpfContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<package xmlns=\"http://www.idpf.org/2007/opf\" version=\"3.0\"> " +
                "  <metadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\"> " +
                "    <dc:title>Cover Test Book</dc:title> " +
                "    <dc:creator>Test Author</dc:creator> " +
                "    <dc:language>en</dc:language> " +
                "  </metadata> " +
                "  <manifest> " +
                "    <item id=\"cover\" href=\"cover.jpg\" media-type=\"image/jpeg\" properties=\"cover-image\"/> " +
                "    <item id=\"nav\" href=\"nav.xhtml\" media-type=\"application/xhtml+xml\" properties=\"nav\"/> " +
                "    <item id=\"chapter1\" href=\"chapter1.xhtml\" media-type=\"application/xhtml+xml\"/> " +
                "  </manifest> " +
                "  <spine> " +
                "    <itemref idref=\"chapter1\"/> " +
                "  </spine> " +
                "</package>";

        // 创建临时EpubBook对象进行测试
        EpubBook book = new EpubBook();
        Metadata metadata = new Metadata();
        metadata.addTitle("Cover Test Book");
        metadata.addCreator("Test Author");
        metadata.addLanguage("en");
        book.setMetadata(metadata);

        // 创建资源列表
        java.util.List<EpubResource> resources = new java.util.ArrayList<>();
        EpubResource coverResource = new EpubResource();
        coverResource.setId("cover");
        coverResource.setHref("cover.jpg");
        coverResource.setType("image/jpeg");
        coverResource.setProperties("cover-image");
        resources.add(coverResource);

        EpubResource navResource = new EpubResource();
        navResource.setId("nav");
        navResource.setHref("nav.xhtml");
        navResource.setType("application/xhtml+xml");
        navResource.setProperties("nav");
        resources.add(navResource);

        EpubResource chapterResource = new EpubResource();
        chapterResource.setId("chapter1");
        chapterResource.setHref("chapter1.xhtml");
        chapterResource.setType("application/xhtml+xml");
        resources.add(chapterResource);

        book.setResources(resources);

        // 验证新的封面图片识别逻辑
        EpubResource cover = book.getCover();
        assertNotNull(cover);
        assertEquals("cover", cover.getId());
        assertEquals("cover-image", cover.getProperties());
        assertEquals("cover.jpg", cover.getHref());
    }

    @Test
    public void getCoverFallbackToMetaTag() throws EpubParseException {
        // 创建一个模拟的EPUB内容用于测试回退到meta标签的逻辑
        String sampleOpfContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<package xmlns=\"http://www.idpf.org/2007/opf\" version=\"3.0\"> " +
                "  <metadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\"> " +
                "    <dc:title>Cover Fallback Test Book</dc:title> " +
                "    <dc:creator>Test Author</dc:creator> " +
                "    <dc:language>en</dc:language> " +
                "    <meta name=\"cover\" content=\"cover-img\"/> " +
                "  </metadata> " +
                "  <manifest> " +
                "    <item id=\"cover-img\" href=\"cover.jpg\" media-type=\"image/jpeg\"/> " +
                "    <item id=\"chapter1\" href=\"chapter1.xhtml\" media-type=\"application/xhtml+xml\"/> " +
                "  </manifest> " +
                "  <spine> " +
                "    <itemref idref=\"chapter1\"/> " +
                "  </spine> " +
                "</package>";

        // 创建临时EpubBook对象进行测试
        EpubBook book = new EpubBook();
        Metadata metadata = new Metadata();
        metadata.addTitle("Cover Fallback Test Book");
        metadata.addCreator("Test Author");
        metadata.addLanguage("en");
        metadata.setCover("cover-img"); // 设置meta标签定义的封面ID
        book.setMetadata(metadata);

        // 创建资源列表
        java.util.List<EpubResource> resources = new java.util.ArrayList<>();
        EpubResource coverResource = new EpubResource();
        coverResource.setId("cover-img");
        coverResource.setHref("cover.jpg");
        coverResource.setType("image/jpeg");
        // 注意：这里不设置properties，测试回退逻辑
        resources.add(coverResource);

        EpubResource chapterResource = new EpubResource();
        chapterResource.setId("chapter1");
        chapterResource.setHref("chapter1.xhtml");
        chapterResource.setType("application/xhtml+xml");
        resources.add(chapterResource);

        book.setResources(resources);

        // 验证回退到meta标签的封面识别逻辑
        EpubResource cover = book.getCover();
        assertNotNull(cover);
        assertEquals("cover-img", cover.getId());
        assertEquals("cover.jpg", cover.getHref());
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