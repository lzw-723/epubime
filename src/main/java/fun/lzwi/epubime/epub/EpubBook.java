package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.zip.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class EpubBook {
    private Metadata metadata;
    private List<EpubChapter> chapters;
    private List<EpubResource> resources;

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<EpubChapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<EpubChapter> chapters) {
        this.chapters = chapters;
    }

    public List<EpubResource> getResources() {
        return resources;
    }

    public void setResources(List<EpubResource> resources) {
        this.resources = resources;
    }

    public EpubResource getCover() {
        return resources.stream().filter(r -> r.getId().equals(metadata.getCover())).findFirst().get();
    }

}
