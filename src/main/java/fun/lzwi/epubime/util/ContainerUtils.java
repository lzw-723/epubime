package fun.lzwi.epubime.util;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ContainerUtils {

    public static boolean existContainer(File container)
            throws SAXException, IOException, ParserConfigurationException {
        return existContainer(Files.newInputStream(container.toPath()));
    }

    public static boolean existContainer(InputStream container)
            throws SAXException, IOException, ParserConfigurationException {
        String xmlns = XmlUtils.getElementsByTagName(container, "container").item(0).getAttributes()
                .getNamedItem("xmlns")
                .getNodeValue();
        return "urn:oasis:names:tc:opendocument:xmlns:container".equals(xmlns);
    }

    public static boolean existRootFiles(File container)
            throws ParserConfigurationException, SAXException, IOException {
        return existRootFiles(Files.newInputStream(container.toPath()));
    }

    public static boolean existRootFiles(InputStream container)
            throws ParserConfigurationException, SAXException, IOException {
        String mimetype = XmlUtils.getElementsByTagName(container, "rootfile").item(0).getAttributes()
                .getNamedItem("media-type").getNodeValue();
        return "application/oebps-package+xml".equals(mimetype);
    }

    public static String getRootFile(File container) throws ParserConfigurationException, SAXException, IOException {
        return getRootFile(Files.newInputStream(container.toPath()));
    }

    public static String getRootFile(InputStream container)
            throws ParserConfigurationException, SAXException, IOException {
        return XmlUtils.getElementsByTagName(container, "rootfile").item(0).getAttributes()
                .getNamedItem("full-path").getNodeValue();
    }
}
