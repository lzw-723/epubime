package fun.lzwi.epubime.document;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fun.lzwi.epubime.document.section.Nav;
import fun.lzwi.epubime.util.XmlUtils;

public class NavigationDocReader {
    private InputStream in;

    public NavigationDocReader(InputStream in) {
        super();
        this.in = in;
    }

    public NavigationDocument read() throws SAXException, IOException, ParserConfigurationException {
        NavigationDocument navigationDocument = new NavigationDocument();
        Document document = XmlUtils.getDocument(in);
        navigationDocument.setTitle(XhtmlDocUtils.getTitle(document));
        Node body = NavigationDocUtils.getBody(document);
        navigationDocument.setBody(body.getTextContent());
        List<Nav> navs = NavigationDocUtils.getNavs(body);
        navigationDocument.setNavs(navs);
        return navigationDocument;
    }
}
