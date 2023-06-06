package fun.lzwi.epubime.document.section.element;

public class A extends HtmlTag {
    private String href;

    public A() {
        super();
        setName("a");
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String getName() {
        return super.getName();
    }
}
