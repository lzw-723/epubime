package fun.lzwi.epubime.document.section.element;

import java.util.ArrayList;
import java.util.List;

public class NavItem {
    private String title;
    private String href;
    private final List<NavItem> children = new ArrayList<>();

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the href
     */
    public String getHref() {
        return href;
    }

    /**
     * @param href the href to set
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * @param the children
     */
    public void setChildren(List<NavItem> children) {
        this.children.addAll(children);
    }

    /**
     * @return the children
     */
    public List<NavItem> getChildren() {
        return children;
    }
}
