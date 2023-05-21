package fun.lzwi.epubime;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class EpubChecker {
    public static final String MIMETYPE = "application/epub+zip";
    public static final String XML_CONTAINER_PATH = "META-INF/container.xml";

    protected static boolean existEntry(File file, String path)
            throws ZipException, IOException, ParserConfigurationException, SAXException {
        EpubFile epub = new EpubFile(file);
        return epub.getEntry(path) != null;
    }

    public static boolean existMimeType(File file) throws IOException, ParserConfigurationException, SAXException {

        EpubFile epub = new EpubFile(file);
        byte[] data = new byte[MIMETYPE.length()];
        DataInputStream din = new DataInputStream(epub.getInputStream("mimetype"));
        din.readFully(data);
        String mimetype = new String(data);
        return MIMETYPE.equals(mimetype);
    }

    public static boolean existContainerXML(File file)
            throws ZipException, IOException, ParserConfigurationException, SAXException {
        return existEntry(file, XML_CONTAINER_PATH);
    }

}
