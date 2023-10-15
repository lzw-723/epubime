package fun.lzwi.epubime.document;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fun.lzwi.epubime.util.XmlUtils;

public class ContentDocReader {
    private final InputStream in;

    public ContentDocReader(InputStream in) {
        super();
        this.in = in;
    }

    public ContentDocument read() throws SAXException, IOException, ParserConfigurationException {
        ContentDocument contentDocument = new ContentDocument();
        Document document = XmlUtils.getDocument(in);
        contentDocument.setTitle(XhtmlDocUtils.getTitle(document));
        contentDocument.setBody(XhtmlDocUtils.getBody(document));
        return contentDocument;
    }
}
