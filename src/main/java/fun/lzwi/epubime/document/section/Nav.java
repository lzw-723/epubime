package fun.lzwi.epubime.document.section;

import java.util.ArrayList;
import java.util.List;

import fun.lzwi.epubime.document.section.element.NavItem;

/*
 * https://www.w3.org/TR/epub-33/#sec-nav-def-model
 */
public class Nav {
    // toc page-list landmarks
    private String epubType;
    private final List<NavItem> items = new ArrayList<>();

    /**
     * @return the items
     */
    public List<NavItem> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<NavItem> items) {
        this.items.addAll(items);
    }

    public String getEpubType() {
        return epubType;
    }

    public void setEpubType(String epubType) {
        this.epubType = epubType;
    }

}
