package fun.lzwi.epubime.document.section;

import java.util.ArrayList;
import java.util.List;

import fun.lzwi.epubime.document.section.element.SpineItemRef;
import fun.lzwi.epubime.util.ListUtils;

/*
 * https://www.w3.org/TR/epub-33/#sec-spine-elem
 */
public class Spine implements Cloneable {
    // [optional]
    private String id;
    // [optional]
    private String pageProgressionDirection;
    // itemref [1 or more]
    private final List<SpineItemRef> itemRefs = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPageProgressionDirection() {
        return pageProgressionDirection;
    }

    public void setPageProgressionDirection(String pageProgressionDirection) {
        this.pageProgressionDirection = pageProgressionDirection;
    }

    public List<SpineItemRef> getItemRefs() {
        return ListUtils.copy(itemRefs);
    }

    public void setItemRefs(List<SpineItemRef> itemRefs) {
        this.itemRefs.clear();
        this.itemRefs.addAll(itemRefs);
    }

    public void addItemRef(SpineItemRef itemRef) {
        itemRefs.add(itemRef);
    }

    public int size() {
        return itemRefs.size();
    }

    @Override
    public Spine clone() {
        try {
            return (Spine) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
