package fun.lzwi.epubime.bean;

public class SpineItemRef {
    private String idref;
    private String linear;

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
    @Override
    public String toString() {
        return "SpineItemRef [idref=" + idref + ", linear=" + linear + "]";
    }
}
