package fun.lzwi.epubime.epub;

import java.io.File;
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
        if (other.metadata != null) {
            this.metadata = new Metadata(other.metadata);
        }
        if (other.ncx != null) {
            this.ncx = new ArrayList<>();
            for (EpubChapter chapter : other.ncx) {
                this.ncx.add(new EpubChapter(chapter));
            }
        }
        if (other.nav != null) {
            this.nav = new ArrayList<>();
            for (EpubChapter chapter : other.nav) {
                this.nav.add(new EpubChapter(chapter));
            }
        }
        if (other.landmarks != null) {
            this.landmarks = new ArrayList<>();
            for (EpubChapter chapter : other.landmarks) {
                this.landmarks.add(new EpubChapter(chapter));
            }
        }
        if (other.pageList != null) {
            this.pageList = new ArrayList<>();
            for (EpubChapter chapter : other.pageList) {
                this.pageList.add(new EpubChapter(chapter));
            }
        }
        if (other.resources != null) {
            this.resources = new ArrayList<>();
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
     * 获取主要章节列表，优先使用NAV目录，如果NAV为空则使用NCX目录
     * @return 章节列表（不可修改）
     */
    public List<EpubChapter> getChapters() {
        if (nav.size() > ncx.size()) {
            return getNav();
        }
        // Default to ncx
        return getNcx();
    }

    /**
     * 获取元数据副本
     * @return 元数据副本
     */
    public Metadata getMetadata() {
        return new Metadata(metadata);
    }

    /**
     * 设置元数据
     * @param metadata 元数据对象
     */
    public void setMetadata(Metadata metadata) {
        this.metadata = new Metadata(metadata);
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
     * @return 封面资源对象
     */
    public EpubResource getCover() {
        // 优先使用properties="cover-image"属性查找封面图片
        EpubResource coverResource = resources.stream()
            .filter(r -> r.getProperties() != null && r.getProperties().contains("cover-image"))
            .findFirst()
            .orElse(null);
        
        // 如果没有找到properties="cover-image"的资源，尝试使用旧的meta标签方式
        if (coverResource == null && metadata.getCover() != null) {
            coverResource = resources.stream()
                .filter(r -> r.getId().equals(metadata.getCover()))
                .findFirst()
                .orElse(null);
        }
        
        return coverResource;
    }
    
    /**
     * 批量加载所有资源的数据
     * @param epubFile EPUB文件
     * @throws IOException 文件读取异常
     */
    public void loadAllResourceData(File epubFile) throws IOException {
        EpubResource.loadResourceData(resources, epubFile);
    }
    
    /**
     * 流式处理HTML章节内容以避免将整个文件加载到内存中
     * @param processor 消费者函数，用于处理HTML内容
     * @throws EpubParseException 解析异常
     */
    public void processHtmlChapters(BiConsumer<EpubChapter, InputStream> processor) throws EpubParseException {
        File epubFile = this.resources.isEmpty() ? null : this.resources.get(0).getEpubFile();
        if (epubFile == null) {
            throw new EpubParseException("No EPUB file reference available for streaming");
        }
        
        List<EpubChapter> chapters = getChapters();
        for (EpubChapter chapter : chapters) {
            try {
                EpubParser.processHtmlChapterContent(epubFile, chapter.getContent(), inputStream -> {
                    processor.accept(chapter, inputStream);
                });
            } catch (Exception e) {
                throw new EpubParseException("Failed to process chapter: " + chapter.getContent(), e);
            }
        }
    }
}