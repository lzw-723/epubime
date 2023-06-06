package fun.lzwi.epubime.document.section.element;

/*
 * https://www.w3.org/TR/epub-33/#sec-item-elem
 */
public class ManifestItem {
    // [conditionally required]
    private String fallback;

    // [required]
    private String href;

    // [required]
    private String id;

    // [optional]
    private String mediaOverlay;

    // [required]
    private String mediaType;

    // [optional]
    private String properties;

    public String getFallback() {
        return fallback;
    }

    public void setFallback(String fallback) {
        this.fallback = fallback;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMediaOverlay() {
        return mediaOverlay;
    }

    public void setMediaOverlay(String mediaOverlay) {
        this.mediaOverlay = mediaOverlay;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

}
