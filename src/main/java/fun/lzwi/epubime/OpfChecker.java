package fun.lzwi.epubime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fun.lzwi.epubime.bean.ManifestItem;
import fun.lzwi.epubime.bean.SpineItemRef;

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

    public static NodeList getManifest(File file) throws ParserConfigurationException, SAXException, IOException {
        NodeList childNodes = XmlUtils.getElementsByTagName(file, "manifest").item(0).getChildNodes();
        return childNodes;
    }

    public static List<ManifestItem> getManifestItems(File file)
            throws ParserConfigurationException, SAXException, IOException {
        NodeList manifest = getManifest(file);
        List<ManifestItem> list = new ArrayList<>();
        XmlUtils.foreachNodeList(manifest, n -> {
            if (!"item".equals(n.getNodeName()))
                return;

            NamedNodeMap attributes = n.getAttributes();

            ManifestItem item = new ManifestItem();
            item.setId(attributes.getNamedItem("id").getTextContent());
            item.setHref(attributes.getNamedItem("href").getTextContent());
            item.setMediaType(attributes.getNamedItem("media-type").getTextContent());
            if (attributes.getLength() > 3)
                item.setProperties(attributes.getNamedItem("properties").getTextContent());

            list.add(item);
        });
        return list;
    }

    public static NodeList getSpine(File file) throws ParserConfigurationException, SAXException, IOException {
        NodeList childNodes = XmlUtils.getElementsByTagName(file, "spine").item(0).getChildNodes();
        return childNodes;
    }

    public static List<SpineItemRef> getSpineItemRefs(File file)
            throws ParserConfigurationException, SAXException, IOException {
        NodeList spine = getSpine(file);
        List<SpineItemRef> list = new ArrayList<>();
        XmlUtils.foreachNodeList(spine, n -> {
            if (!"itemref".equals(n.getNodeName()))
                return;

            NamedNodeMap attributes = n.getAttributes();

            SpineItemRef item = new SpineItemRef();
            item.setIdref(attributes.getNamedItem("idref").getTextContent());
            if (attributes.getLength() > 1)
                item.setLinear(attributes.getNamedItem("linear").getTextContent());

            list.add(item);
        });
        return list;
    }
}
