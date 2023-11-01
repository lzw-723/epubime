package fun.lzwi.epubime;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Resource {
    private EpubFile epubFile;
    private String href;

    /**
     * @param epubFile
     */
    public Resource(EpubFile epubFile) {
        this.epubFile = epubFile;
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
    public void setHref(String href) {
        this.href = href;
    }

    public InputStream getInputStream() throws IOException {
        Objects.requireNonNull(href);
        return epubFile.getInputStream(href);
    }
}
