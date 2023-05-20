package fun.lzwi.epubime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class ContainerChecker {

    public static boolean checkContainer(File container)
            throws SAXException, IOException, ParserConfigurationException {
        return checkContainer(new FileInputStream(container));
    }

    public static boolean checkContainer(InputStream container)
            throws SAXException, IOException, ParserConfigurationException {
        String xmlns = XmlUtils.getElementsByTagName(container, "container").item(0).getAttributes()
                .getNamedItem("xmlns")
                .getNodeValue();
        return "urn:oasis:names:tc:opendocument:xmlns:container".equals(xmlns);
    }

    public static boolean checkRootFiles(File container)
            throws ParserConfigurationException, SAXException, IOException {
        return checkRootFiles(new FileInputStream(container));
    }

    public static boolean checkRootFiles(InputStream container)
            throws ParserConfigurationException, SAXException, IOException {
        String mimetype = XmlUtils.getElementsByTagName(container, "rootfile").item(0).getAttributes()
                .getNamedItem("media-type").getNodeValue();
        return "application/oebps-package+xml".equals(mimetype);
    }

    public static String getRootFile(File container) throws ParserConfigurationException, SAXException, IOException {
        return getRootFile(new FileInputStream(container));
    }

    public static String getRootFile(InputStream container)
            throws ParserConfigurationException, SAXException, IOException {
        String fullPath = XmlUtils.getElementsByTagName(container, "rootfile").item(0).getAttributes()
                .getNamedItem("full-path").getNodeValue();
        return fullPath;
    }
}
