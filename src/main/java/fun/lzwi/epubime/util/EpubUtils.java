package fun.lzwi.epubime.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class EpubUtils {
    public static final String MIMETYPE = "application/epub+zip";
    public static final String XML_CONTAINER_PATH = "META-INF/container.xml";

    protected static boolean existEntry(File file, String path)
            throws ZipException, IOException, ParserConfigurationException, SAXException {
        try (ZipFile epub = new ZipFile(file)) {
            return epub.getEntry(path) != null;
        }
    }

    public static boolean existMimeType(File file) throws IOException, ParserConfigurationException, SAXException {

        try (ZipFile epub = new ZipFile(file)) {
            byte[] data = new byte[MIMETYPE.length()];
            try (DataInputStream din = new DataInputStream(epub.getInputStream(epub.getEntry("mimetype")))) {
                din.readFully(data);
                String mimetype = new String(data, "utf-8");
                return MIMETYPE.equals(mimetype);

            }
        }
    }

    public static boolean existContainerXML(File file)
            throws ZipException, IOException, ParserConfigurationException, SAXException {
        return existEntry(file, XML_CONTAINER_PATH);
    }

}
