package fun.lzwi.epubime.epub;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
