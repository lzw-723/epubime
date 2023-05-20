package fun.lzwi.epubime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fun.lzwi.epubime.bean.ManifestItem;
import fun.lzwi.epubime.bean.SpineItemRef;

public class OpfReader {

    public static String getIdentifier(InputStream opf) throws ParserConfigurationException, SAXException, IOException {
        String id = XmlUtils.getElementsByTagName(opf, "dc:identifier").item(0).getTextContent();
        return id;
    }

    public static String getIdentifier(File opf) throws ParserConfigurationException, SAXException, IOException {
        return getIdentifier(new FileInputStream(opf));
    }

    public static boolean existIdentifier(InputStream opf) {
        try {
            String id = getIdentifier(opf);
            return id != null;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // 读取id失败
        }
        return false;
    }

    public static boolean existIdentifier(File opf) throws FileNotFoundException {
        return existIdentifier(new FileInputStream(opf));
    }

    public static String getLanguage(InputStream opf)
            throws DOMException, ParserConfigurationException, SAXException, IOException {
        String lang = XmlUtils.getElementsByTagName(opf, "dc:language").item(0).getTextContent();
        return lang;
    }

    public static String getLanguage(File opf)
            throws DOMException, ParserConfigurationException, SAXException, IOException {
        return getLanguage(new FileInputStream(opf));
    }

    public static boolean existLanguage(InputStream opf) {
        try {
            String language = getLanguage(opf);
            return language != null;
        } catch (DOMException | ParserConfigurationException | SAXException | IOException e) {
            // 读取language失败
        }
        return false;
    }

    public static boolean existLanguage(File opf) throws FileNotFoundException {
        return existLanguage(new FileInputStream(opf));
    }

    public static String getTitle(InputStream opf)
            throws DOMException, ParserConfigurationException, SAXException, IOException {
        String title = XmlUtils.getElementsByTagName(opf, "dc:title").item(0).getTextContent();
        return title;
    }

    public static String getTitle(File opf)
            throws DOMException, ParserConfigurationException, SAXException, IOException {
        return getTitle(new FileInputStream(opf));
    }

    public static boolean existTitle(InputStream opf) {
        try {
            String title = getTitle(opf);
            return title != null;
        } catch (DOMException | ParserConfigurationException | SAXException | IOException e) {
            // 读取title失败
        }
        return false;
    }

    public static boolean existTitle(File opf) throws FileNotFoundException {
        return existTitle(new FileInputStream(opf));
    }

    public static NodeList getManifest(InputStream opf) throws ParserConfigurationException, SAXException, IOException {
        NodeList childNodes = XmlUtils.getElementsByTagName(opf, "manifest").item(0).getChildNodes();
        return childNodes;
    }

    public static NodeList getManifest(File opf) throws ParserConfigurationException, SAXException, IOException {
        return getManifest(new FileInputStream(opf));
    }

    public static List<ManifestItem> getManifestItems(InputStream opf)
            throws ParserConfigurationException, SAXException, IOException {
        NodeList manifest = getManifest(opf);
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

    public static List<ManifestItem> getManifestItems(File opf)
            throws ParserConfigurationException, SAXException, IOException {
        return getManifestItems(new FileInputStream(opf));
    }

    public static NodeList getSpine(InputStream opf) throws ParserConfigurationException, SAXException, IOException {
        NodeList childNodes = XmlUtils.getElementsByTagName(opf, "spine").item(0).getChildNodes();
        return childNodes;
    }

    public static NodeList getSpine(File opf) throws ParserConfigurationException, SAXException, IOException {
        return getSpine(new FileInputStream(opf));
    }

    public static List<SpineItemRef> getSpineItemRefs(InputStream opf)
            throws ParserConfigurationException, SAXException, IOException {
        NodeList spine = getSpine(opf);
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
    public static List<SpineItemRef> getSpineItemRefs(File opf)
            throws ParserConfigurationException, SAXException, IOException {
        return getSpineItemRefs(new FileInputStream(opf));
    }
}
