package fun.lzwi.epubime.document.section;

/**
 * @see fun.lzwi.epubime.document.NCX
 */
public class NavPoint {
    private String id;
    private int playOrder;
    private String navLabel;
    private String content;
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @return the playOrder
     */
    public int getPlayOrder() {
        return playOrder;
    }
    /**
     * @param playOrder the playOrder to set
     */
    public void setPlayOrder(int playOrder) {
        this.playOrder = playOrder;
    }
    /**
     * @return the navLabel
     */
    public String getNavLabel() {
        return navLabel;
    }
    /**
     * @param navLabel the navLabel to set
     */
    public void setNavLabel(String navLabel) {
        this.navLabel = navLabel;
    }
    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }
    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

}
