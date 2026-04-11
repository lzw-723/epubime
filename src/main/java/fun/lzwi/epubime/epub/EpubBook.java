package fun.lzwi.epubime.epub;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * EPUB书籍模型类
 * 表示一个完整的EPUB电子书，包含元数据、章节和资源文件
 */
public class EpubBook {

    private Metadata metadata;
    private String version; // EPUB版本
    private List<EpubChapter> ncx = new ArrayList<>();
    private List<EpubChapter> nav = new ArrayList<>();
    private List<EpubChapter> landmarks = new ArrayList<>(); // 地标导航
    private List<EpubChapter> pageList = new ArrayList<>(); // 页面列表导航
    private List<EpubResource> resources = new ArrayList<>();

    /**
     * 默认构造函数
     */
    public EpubBook() {
        // Default constructor
    }

    /**
     * 复制构造函数，用于缓存
     * @param other 要复制的EpubBook对象
     */
    public EpubBook(EpubBook other) {
        this.version = other.version;
        if (other.metadata != null) {
            this.metadata = new Metadata(other.metadata);
        }
        if (other.ncx != null) {
            this.ncx = new ArrayList<>(other.ncx.size());
            for (EpubChapter chapter : other.ncx) {
                this.ncx.add(new EpubChapter(chapter));
            }
        }
        if (other.nav != null) {
            this.nav = new ArrayList<>(other.nav.size());
            for (EpubChapter chapter : other.nav) {
                this.nav.add(new EpubChapter(chapter));
            }
        }
        if (other.landmarks != null) {
            this.landmarks = new ArrayList<>(other.landmarks.size());
            for (EpubChapter chapter : other.landmarks) {
                this.landmarks.add(new EpubChapter(chapter));
            }
        }
        if (other.pageList != null) {
            this.pageList = new ArrayList<>(other.pageList.size());
            for (EpubChapter chapter : other.pageList) {
                this.pageList.add(new EpubChapter(chapter));
            }
        }
        if (other.resources != null) {
            this.resources = new ArrayList<>(other.resources.size());
            for (EpubResource resource : other.resources) {
                this.resources.add(new EpubResource(resource));
            }
        }
    }

    /**
     * 获取NCX目录章节列表
     * @return NCX目录章节列表（不可修改）
     */
    public List<EpubChapter> getNcx() {
        return Collections.unmodifiableList(ncx);
    }

    /**
     * 设置NCX目录章节列表
     * @param ncx NCX目录章节列表
     */
    public void setNcx(List<EpubChapter> ncx) {
        this.ncx = new ArrayList<>(ncx);
    }

    /**
     * 获取NAV目录章节列表
     * @return NAV目录章节列表（不可修改）
     */
    public List<EpubChapter> getNav() {
        return Collections.unmodifiableList(nav);
    }

    /**
     * 设置NAV目录章节列表
     * @param nav NAV目录章节列表
     */
    public void setNav(List<EpubChapter> nav) {
        this.nav = new ArrayList<>(nav);
    }

    /**
     * 获取地标导航章节列表
     * @return 地标导航章节列表（不可修改）
     */
    public List<EpubChapter> getLandmarks() {
        return Collections.unmodifiableList(landmarks);
    }

    /**
     * 设置地标导航章节列表
     * @param landmarks 地标导航章节列表
     */
    public void setLandmarks(List<EpubChapter> landmarks) {
        this.landmarks = new ArrayList<>(landmarks);
    }

    /**
     * 获取页面列表导航章节列表
     * @return 页面列表导航章节列表（不可修改）
     */
    public List<EpubChapter> getPageList() {
        return Collections.unmodifiableList(pageList);
    }

    /**
     * 设置页面列表导航章节列表
     * @param pageList 页面列表导航章节列表
     */
    public void setPageList(List<EpubChapter> pageList) {
        this.pageList = new ArrayList<>(pageList);
    }

    /**
     * 获取主要章节列表，优先使用NAV目录（EPUB3标准），如果NAV为空则使用NCX目录（向后兼容）
     * @return 章节列表（不可修改）
     */
    public List<EpubChapter> getChapters() {
        if (!nav.isEmpty()) {
            return getNav();
        }
        return getNcx();
    }

    /**
     * 获取元数据副本
     * @return 元数据副本，如果元数据未设置则返回null
     */
    public Metadata getMetadata() {
        return metadata != null ? new Metadata(metadata) : null;
    }

    /**
     * 设置元数据
     * @param metadata 元数据对象，为null时将清空元数据
     */
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata != null ? new Metadata(metadata) : null;
    }

    /**
     * 获取EPUB版本
     * @return EPUB版本字符串
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置EPUB版本
     * @param version EPUB版本字符串
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 获取资源文件列表
     * @return 资源文件列表（不可修改）
     */
    public List<EpubResource> getResources() {
        return Collections.unmodifiableList(resources);
    }

    /**
     * 设置资源文件列表
     * @param resources 资源文件列表
     */
    public void setResources(List<EpubResource> resources) {
        this.resources = new ArrayList<>(resources);
    }

    /**
     * 获取封面资源
     * @return 封面资源对象，如果不存在返回null
     */
    public EpubResource getCover() {
        return EpubBookProcessor.getCover(this);
    }

    /**
     * 根据ID获取资源，自动应用回退机制
     * @param resourceId 资源ID
     * @return 应用回退机制后的资源，如果不存在返回null
     */
    public EpubResource getResourceWithFallback(String resourceId) {
        return EpubBookProcessor.getResourceWithFallback(this, resourceId);
    }

    /**
     * 根据ID获取资源
     * @param resourceId 资源ID
     * @return 资源对象，如果不存在返回null
     */
    public EpubResource getResourceById(String resourceId) {
        return EpubBookProcessor.getResource(this, resourceId);
    }

    /**
     * 批量加载所有资源数据
     * @throws IOException 文件读取异常
     * @deprecated 使用流式处理避免将所有资源加载到内存
     */
    @Deprecated
    public void loadAllResourceData() throws IOException {
        EpubBookProcessor.loadAllResourceData(this);
    }

    /**
     * 流式处理HTML章节内容，避免将整个文件加载到内存
     * @param processor 处理HTML内容的消费者函数
     * @throws fun.lzwi.epubime.exception.EpubParseException 解析异常
     */
    public void processHtmlChapter(BiConsumer<EpubChapter, InputStream> processor)
            throws fun.lzwi.epubime.exception.EpubParseException {
        if (resources.isEmpty()) {
            return;
        }
        java.io.File epubFile = resources.get(0).getEpubFile();
        if (epubFile == null) {
            return;
        }
        try {
            EpubStreamProcessor processorObj = new EpubStreamProcessor(epubFile);
            processorObj.processBookChapters(this, processor);
        } catch (Exception e) {
            throw new fun.lzwi.epubime.exception.EpubParseException("Failed to process chapters: " + e.getMessage(), e);
        }
    }
}
