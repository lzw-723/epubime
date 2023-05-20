package fun.lzwi.epubime.bean;

public class MetaItem {
    private String property;
    private String name;
    private String content;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MetaItem [property=" + property + ", name=" + name + ", content=" + content + "]";
    }
}
