package fun.lzwi.epubime.epub;

/**
 * EPUB章节模型类
 * 表示EPUB电子书中的单个章节，包含章节ID、标题和内容路径
 */
public class EpubChapter {
    private String id;
    private String title;
    private String content;

    public EpubChapter() {
        // Default constructor
    }
    
    /**
     * Copy constructor
     * @param other EpubChapter object to copy
     */
    public EpubChapter(EpubChapter other) {
        this.id = other.id;
        this.title = other.title;
        this.content = other.content;
    }

    /**
     * 获取章节内容路径
     * @return 章节内容路径
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置章节内容路径
     * @param content 章节内容路径
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取章节标题
     * @return 章节标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置章节标题
     * @param title 章节标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取章节ID
     * @return 章节ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置章节ID
     * @param id 章节ID
     */
    public void setId(String id) {
        this.id = id;
    }
}
