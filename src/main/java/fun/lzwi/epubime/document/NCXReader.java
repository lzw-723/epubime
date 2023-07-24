package fun.lzwi.epubime.document;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fun.lzwi.epubime.document.section.NavPoint;

public class NCXReader {
    private InputStream in;

    /**
     * @param in
     */
    public NCXReader(InputStream in) {
        this.in = in;
    }
    
    public NCX read() throws ParserConfigurationException, SAXException, IOException {
        NCX ncx = new NCX();
        Node ncxElement = NCXUtils.getNcxElement(in);
        Map<String,String> head = NCXUtils.getHead(ncxElement);
        ncx.setHead(head);
        String docTitle = NCXUtils.getDocTitle(ncxElement);
        ncx.setDocTitle(docTitle);
        String docAuthor = NCXUtils.getDocAuthor(ncxElement);
        ncx.setDocAuthor(docAuthor);
        List<NavPoint> navMap = NCXUtils.getNavMap(ncxElement);
        ncx.setNavMap(navMap);
        return ncx;
    }
}
