package fun.lzwi.epubime.document.section.element;

import java.util.ArrayList;
import java.util.List;

import fun.lzwi.epubime.util.ListUtils;

public class HtmlTag {
    private String name;
    private List<HtmlTag> children = new ArrayList<>();

    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected List<HtmlTag> getChildren() {
        return ListUtils.copy(children);
    }

    protected void setChildren(List<HtmlTag> children) {
        this.children.clear();
        this.children.addAll(children);
    }
}
