package fun.lzwi.epubime.epub;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EpubBook {
    private Metadata metadata;

    private List<EpubChapter> ncx = new ArrayList<>();
    private List<EpubChapter> nav = new ArrayList<>();
    private List<EpubResource> resources = new ArrayList<>();

    public List<EpubChapter> getNcx() {
        return Collections.unmodifiableList(ncx);
    }

    public void setNcx(List<EpubChapter> ncx) {
        this.ncx = new ArrayList<>(ncx);
    }

    public List<EpubChapter> getNav() {
        return Collections.unmodifiableList(nav);
    }

    public void setNav(List<EpubChapter> nav) {
        this.nav = new ArrayList<>(nav);
    }

    public List<EpubChapter> getChapters() {
        if (nav.size() > ncx.size()) {
            return getNav();
        }
        // 默认使用 ncx
        return getNcx();
    }

    public Metadata getMetadata() {
        return new Metadata(metadata);
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = new Metadata(metadata);
    }

    public List<EpubResource> getResources() {
        return Collections.unmodifiableList(resources);
    }

    public void setResources(List<EpubResource> resources) {
        this.resources = new ArrayList<>(resources);
    }

    public EpubResource getCover() {
        return resources.stream().filter(r -> r.getId().equals(metadata.getCover())).findFirst().get();
    }
    
    /**
     * 批量加载所有资源的数据
     * @param epubFile EPUB文件
     * @throws IOException
     */
    public void loadAllResourceData(File epubFile) throws IOException {
        EpubResource.loadResourceData(resources, epubFile);
    }
}