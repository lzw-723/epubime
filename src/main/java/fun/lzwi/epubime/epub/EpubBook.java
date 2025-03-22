package fun.lzwi.epubime.epub;

import fun.lzwi.epubime.zip.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EpubBook {
    private Metadata metadata;
    private List<EpubChapter> chapters = new ArrayList<>();
    private List<EpubResource> resources = new ArrayList<>();

    public List<EpubChapter> getChapters() {
        return Collections.unmodifiableList(chapters);
    }

    public void setChapters(List<EpubChapter> chapters) {
        this.chapters = new ArrayList<>(chapters);
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

}
