package fun.lzwi.epubime.document;

import fun.lzwi.epubime.document.section.Manifest;
import fun.lzwi.epubime.document.section.MetaData;
import fun.lzwi.epubime.document.section.Spine;

public class PackageDocument implements Cloneable {
    // dir [optional]
    private String dir;
    // id [optional]
    private String id;
    // prefix [optional]
    private String prefix;
    // xml:lang [optional]
    private String xmlLang;
    // unique-identifier [required]
    private String uniqueIdentifier;
    // version [required]
    private String version;

    private MetaData metaData;
    private Manifest manifest;
    private Spine spine;

    private String href;

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

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getXmlLang() {
        return xmlLang;
    }

    public void setXmlLang(String xmlLang) {
        this.xmlLang = xmlLang;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public MetaData getMetaData() {
        if (metaData != null) {
            return metaData.clone();
        }
        return null;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData.clone();
    }

    public Manifest getManifest() {
        if (manifest == null) {
            return null;
        }
        return manifest.clone();
    }

    public void setManifest(Manifest manifest) {
        this.manifest = manifest.clone();
    }

    public Spine getSpine() {
        if (spine == null) {
            return null;
        }
        return spine.clone();
    }

    public void setSpine(Spine spine) {
        this.spine = spine.clone();
    }

    @Override
    public PackageDocument clone() {
        try {
            return (PackageDocument) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "PackageDocument [dir=" + dir + ", id=" + id + ", prefix=" + prefix + ", xmlLang=" + xmlLang
                + ", uniqueIdentifier=" + uniqueIdentifier + ", version=" + version + ", metaData=" + metaData
                + ", manifest=" + manifest + ", spine=" + spine + "]";
    }
}
