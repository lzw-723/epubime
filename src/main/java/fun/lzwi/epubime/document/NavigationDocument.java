package fun.lzwi.epubime.document;

import java.util.ArrayList;
import java.util.List;

import fun.lzwi.epubime.document.section.Nav;
import fun.lzwi.epubime.util.ListUtils;

/*
 * https://www.w3.org/TR/epub-33/#sec-nav-content-req
 */
public class NavigationDocument extends AbstractXhtml implements Cloneable {
    private final List<Nav> navs = new ArrayList<>();

    private String href;

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

    public List<Nav> getNavs() {
        return ListUtils.copy(navs);
    }

    public void setNavs(List<Nav> navs) {
        this.navs.clear();
        this.navs.addAll(navs);
    }

    @Override
    public NavigationDocument clone() {
        try {
            return (NavigationDocument) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
