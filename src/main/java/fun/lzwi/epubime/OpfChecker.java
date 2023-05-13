package fun.lzwi.epubime;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

public class OpfChecker {

    public static String getIdentifier(File file) throws ParserConfigurationException, SAXException, IOException {
        String id = XmlUtils.getElementsByTagName(file, "dc:identifier").item(0).getTextContent();
        return id;
    }

    public static boolean checkIdentifier(File file) {
        try {
            String id = getIdentifier(file);
            return id != null;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // 读取id失败
        }
        return false;
    }

    public static String getLanguage(File file)
            throws DOMException, ParserConfigurationException, SAXException, IOException {
        String lang = XmlUtils.getElementsByTagName(file, "dc:language").item(0).getTextContent();
        return lang;
    }

    public static boolean checkLanguage(File file) {
        try {
            String language = getLanguage(file);
            return language != null;
        } catch (DOMException | ParserConfigurationException | SAXException | IOException e) {
            // 读取language失败
        }
        return false;
    }

    public static String getTitle(File file)
            throws DOMException, ParserConfigurationException, SAXException, IOException {
        String title = XmlUtils.getElementsByTagName(file, "dc:title").item(0).getTextContent();
        return title;
    }

    public static boolean checkTitle(File file) {
        try {
            String title = getTitle(file);
            return title != null;
        } catch (DOMException | ParserConfigurationException | SAXException | IOException e) {
            // 读取title失败
        }
        return false;
    }
}
