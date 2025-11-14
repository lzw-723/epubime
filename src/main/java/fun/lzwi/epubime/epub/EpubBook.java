package fun.lzwi.epubime.epub;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

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
    
    /**
     * 流式处理HTML章节内容，避免将整个文件加载到内存中
     * @param processor 处理HTML内容的消费者函数
     * @throws EpubParseException
     */
    public void processHtmlChapters(BiConsumer<EpubChapter, InputStream> processor) throws EpubParseException {
        File epubFile = this.resources.isEmpty() ? null : this.resources.get(0).getEpubFile();
        if (epubFile == null) {
            throw new EpubParseException("No EPUB file reference available for streaming");
        }
        
        List<EpubChapter> chapters = getChapters();
        for (EpubChapter chapter : chapters) {
            try {
                EpubParser.processHtmlChapterContent(epubFile, chapter.getContent(), inputStream -> {
                    processor.accept(chapter, inputStream);
                });
            } catch (Exception e) {
                throw new EpubParseException("Failed to process chapter: " + chapter.getContent(), e);
            }
        }
    }
}