package fun.lzwi.epubime;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ContainerChecker {

    public static boolean checkContainer(File file) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        String xmlns = doc.getElementsByTagName("container").item(0).getAttributes().getNamedItem("xmlns")
                .getNodeValue();
        return "urn:oasis:names:tc:opendocument:xmlns:container".equals(xmlns);
    }

    public static boolean checkRootFiles(File file) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        Node rf = doc.getElementsByTagName("rootfile").item(0);
        String fullPath = rf.getAttributes().getNamedItem("full-path").getNodeValue();
        String mimetype = rf.getAttributes().getNamedItem("media-type").getNodeValue();
        return "EPUB/package.opf".equals(fullPath) && "application/oebps-package+xml".equals(mimetype);
    }

}
