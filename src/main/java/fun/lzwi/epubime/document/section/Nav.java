package fun.lzwi.epubime.document.section;

import java.util.List;

import fun.lzwi.epubime.document.section.element.HtmlTag;

/*
 * https://www.w3.org/TR/epub-33/#sec-nav-def-model
 */
public class Nav extends HtmlTag {
    // toc page-list landmarks
    private String epubType;

    public Nav() {
        super();
        setName("nav");
    }

    public String getEpubType() {
        return epubType;
    }

    public void setEpubType(String epubType) {
        this.epubType = epubType;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public List<HtmlTag> getChildren() {
        return super.getChildren();
    }

    @Override
    public void setChildren(List<HtmlTag> children) {
        super.setChildren(children);
    }
}
