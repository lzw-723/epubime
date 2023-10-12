package fun.lzwi.epubime.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fun.lzwi.epubime.document.section.NavPoint;

// epub 2.0
// https://idpf.org/epub/20/spec/OPF_2.0.1_draft.htm#TOC2.4.1
public class NCX implements Cloneable {
    private Map<String, String> head = new HashMap<>();
    private String docTitle;
    private String docAuthor;

    @Override
    public NCX clone(){
        try {
            return (NCX) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<NavPoint> navMap = new ArrayList<>();
    /**
     * @return the head
     */
    public Map<String, String> getHead() {
        return new HashMap<>(head);
    }
    /**
     * @param head the head to add
     */
    public void setHead(Map<String, String> head) {
        this.head.clear();
        this.head.putAll(head);
    }
    /**
     * @return the docTitle
     */
    public String getDocTitle() {
        return docTitle;
    }
    /**
     * @param docTitle the docTitle to set
     */
    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }
    /**
     * @return the docAuthor
     */
    public String getDocAuthor() {
        return docAuthor;
    }
    /**
     * @param docAuthor the docAuthor to set
     */
    public void setDocAuthor(String docAuthor) {
        this.docAuthor = docAuthor;
    }
    /**
     * @return the navMap
     */
    public List<NavPoint> getNavMap() {
        return new ArrayList<>(navMap);
    }
    /**
     * @param navMap the navMap to add
     */
    public void setNavMap(List<NavPoint> navMap) {
        this.navMap.clear();
        this.navMap.addAll(navMap);
    }

}
