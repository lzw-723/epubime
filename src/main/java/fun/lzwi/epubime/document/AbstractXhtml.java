package fun.lzwi.epubime.document;

public abstract class AbstractXhtml {
    private String title;
    private String body;
    private String content;
    private String plainText;

    public String getPlainText() {
        return plainText;
    }

    protected void setPlainText(String plainText) {
        this.plainText = plainText;
    }

    public String getTitle() {
        return title;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    protected void setBody(String body) {
        this.body = body;
    }

    public String getContent() {
        return content;
    }

    protected void setContent(String content) {
        this.content = content;
    }
}
