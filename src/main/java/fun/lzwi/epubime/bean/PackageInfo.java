package fun.lzwi.epubime.bean;

public class PackageInfo implements Cloneable {
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

    @Override
    public PackageInfo clone() {
        PackageInfo packageInfo;
        try {
            packageInfo = (PackageInfo) super.clone();
            packageInfo.setDir(dir);
            packageInfo.setId(id);
            packageInfo.setPrefix(prefix);
            packageInfo.setUniqueIdentifier(uniqueIdentifier);
            packageInfo.setVersion(version);
            packageInfo.setXmlLang(xmlLang);
            return packageInfo;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
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

    @Override
    public String toString() {
        return "PackageInfo [dir=" + dir + ", id=" + id + ", prefix=" + prefix + ", xmlLang=" + xmlLang
                + ", uniqueIdentifier=" + uniqueIdentifier + ", version=" + version + "]";
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
