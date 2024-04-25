package fun.lzwi.epubime;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import fun.lzwi.epubime.util.EntryPathUtils;

public class Resource {
    private EpubFile epubFile;
    private String base;
    private String href;

    /**
     * @return the epubFile
     */
    public EpubFile getEpubFile() {
        return epubFile;
    }

    /**
     * @param epubFile the epubFile to set
     */
    public void setEpubFile(EpubFile epubFile) {
        this.epubFile = epubFile;
    }

    /**
     * @return the base
     */
    public String getBase() {
        return base;
    }

    /**
     * @param base the base to set
     */
    public Resource setBase(String base) {
        this.base = base;
        return this;
    }

    /**
     * @param epubFile
     */
    public Resource(EpubFile epubFile) {
        this.epubFile = epubFile;
    }

    /**
     * @param parent
     */
    public Resource(Resource parent, String child) {
        this.epubFile = parent.getEpubFile();
        this.base = parent.getParent();
        this.href = child;

    }

    /**
     * @return the href
     */
    public String getHref() {
        return href;
    }

    /**
     * @param href the href to set
     */
    public Resource setHref(String href) {
        this.href = href;
        return this;
    }

    public String getHash() {
        return EntryPathUtils.hash(href);
    }

    protected String getPath() {
        try {
            return EntryPathUtils.parse(base, href);
        } catch (IOException e) {
            return null;
        }
    }

    protected String getParent() {
        try {
            return EntryPathUtils.parent(getPath());
        } catch (IOException e) {
            return null;
        }
    }

    public InputStream getInputStream() throws IOException {
        Objects.requireNonNull(href);
        return epubFile.getInputStream(getPath());
    }
}
