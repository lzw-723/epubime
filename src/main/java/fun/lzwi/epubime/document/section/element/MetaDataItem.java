package fun.lzwi.epubime.document.section.element;

/*
 * https://www.w3.org/TR/epub-33/#sec-package-elem
 */
public class MetaDataItem {
    // Element Name
    private String name;
    // Attributes
    private String property;
    private String id;
    private String dir;
    private String xmlLang;
    private String refines;
    // Content Model
    private String content;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getProperty() {
        return property;
    }
    public void setProperty(String property) {
        this.property = property;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDir() {
        return dir;
    }
    public void setDir(String dir) {
        this.dir = dir;
    }
    public String getXmlLang() {
        return xmlLang;
    }
    public void setXmlLang(String xmlLang) {
        this.xmlLang = xmlLang;
    }
    public String getRefines() {
        return refines;
    }
    public void setRefines(String refines) {
        this.refines = refines;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    @Override
    public String toString() {
        return "MetaDataItem [name=" + name + ", property=" + property + ", id=" + id + ", dir=" + dir + ", xmlLang="
                + xmlLang + ", refines=" + refines + ", content=" + content + "]";
    }
}
