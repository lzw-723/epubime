package fun.lzwi.epubime.document.section.element;

/*
 * https://www.w3.org/TR/epub-33/#sec-itemref-elem
 */
public class SpineItemRef {
    // [optional]
    private String id;

    // [required]
    private String idref;

    // [optional]
    private String linear;

    // [optional]
    private String properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdref() {
        return idref;
    }

    public void setIdref(String idref) {
        this.idref = idref;
    }

    public String getLinear() {
        return linear;
    }

    public void setLinear(String linear) {
        this.linear = linear;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }
}
