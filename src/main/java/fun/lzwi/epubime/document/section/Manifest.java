package fun.lzwi.epubime.document.section;

import java.util.ArrayList;
import java.util.List;

import fun.lzwi.epubime.document.section.element.ManifestItem;
import fun.lzwi.epubime.util.ListUtils;

public class Manifest implements Cloneable {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private final List<ManifestItem> items = new ArrayList<>();

    public List<ManifestItem> getItems() {
        return ListUtils.copy(items);
    }

    public void setItems(List<ManifestItem> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public void addItem(ManifestItem item) {
        items.add(item);
    }

    public ManifestItem getItemById(String id) {
        return items.stream().filter(f -> id.equals(f.getId())).findFirst().get();
    }

    @Override
    public Manifest clone() {
        try {
            return (Manifest) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int size() {
        return items.size();
    }
}
