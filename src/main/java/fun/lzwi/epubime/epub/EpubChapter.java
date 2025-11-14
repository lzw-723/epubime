package fun.lzwi.epubime.epub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * EPUB章节模型类
 * 表示EPUB电子书中的单个章节，包含章节ID、标题和内容路径
 */
public class EpubChapter {
    private String id;
    private String title;
    private String content;
    private List<EpubChapter> children; // 子章节列表

    public EpubChapter() {
        this.children = new ArrayList<>();
    }
    
    /**
     * Copy constructor
     * @param other EpubChapter object to copy
     */
    public EpubChapter(EpubChapter other) {
        this.id = other.id;
        this.title = other.title;
        this.content = other.content;
        this.children = new ArrayList<>();
        if (other.children != null) {
            for (EpubChapter child : other.children) {
                this.children.add(new EpubChapter(child));
            }
        }
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

    /**
     * 获取子章节列表
     * @return 子章节列表（不可修改）
     */
    public List<EpubChapter> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * 添加子章节
     * @param child 子章节
     */
    public void addChild(EpubChapter child) {
        this.children.add(child);
    }

    /**
     * 设置子章节列表
     * @param children 子章节列表
     */
    public void setChildren(List<EpubChapter> children) {
        this.children = new ArrayList<>(children);
    }

    /**
     * 检查是否有子章节
     * @return 是否有子章节
     */
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }
}