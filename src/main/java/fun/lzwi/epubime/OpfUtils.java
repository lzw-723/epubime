package fun.lzwi.epubime;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fun.lzwi.epubime.bean.ManifestItem;
import fun.lzwi.epubime.bean.MetaDC;
import fun.lzwi.epubime.bean.MetaItem;
import fun.lzwi.epubime.bean.SpineItemRef;

public class OpfUtils {

    protected static Node getPackage(InputStream opf) {
        try {
            return XmlUtils.getElementsByTagName(opf, "package").item(0);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static String getVersion(Node pkg) {
        return pkg.getAttributes().getNamedItem("version").getTextContent();
    }

    // public static String getVersion(InputStream opf) {
    // return getVersion(getPackage(opf));
    // }

    // public static String getVersion(File opf) throws FileNotFoundException {
    // return getVersion(new FileInputStream(opf));
    // }

    protected static String getUniqueIdentifier(Node pkg) {
        return pkg.getAttributes()
                .getNamedItem("unique-identifier").getTextContent();
    }

    // public static String getUniqueIdentifier(InputStream opf) {
    // return getUniqueIdentifier(getPackage(opf));
    // }

    // public static String getUniqueIdentifier(File file) throws
    // FileNotFoundException {
    // return getUniqueIdentifier(new FileInputStream(file));
    // }
    public static List<String> getIdentifiers(List<MetaDC> metaDCs) {
        return metaDCs.stream().filter(p -> p.getName().equals("dc:identifier"))
                .map(m -> m.getContent()).collect(Collectors.toList());
    }

    public static String getIdentifier(List<MetaDC> metaDCs) {
        return getIdentifiers(metaDCs).get(0);
    }

    // public static String getIdentifier(InputStream opf) {
    // return getIdentifier(getMetaDCs(opf));
    // }

    // public static String getIdentifier(File opf) throws FileNotFoundException {
    // return getIdentifier(new FileInputStream(opf));
    // }

    // public static boolean existIdentifier(InputStream opf) {
    // String id = getIdentifier(opf);
    // return id != null;
    // }

    // public static boolean existIdentifier(File opf) throws FileNotFoundException
    // {
    // return existIdentifier(new FileInputStream(opf));
    // }
    public static List<String> getLanguages(List<MetaDC> metaDCs) {
        return metaDCs.stream().filter(p -> p.getName().equals("dc:language")).map(m -> m.getContent())
                .collect(Collectors.toList());
    }

    public static String getLanguage(List<MetaDC> metaDCs) {
        return getLanguages(metaDCs).get(0);
    }

    // public static String getLanguage(InputStream opf) {
    // return getLanguage(getMetaDCs(getPackage(opf)));
    // }

    // public static String getLanguage(File opf) throws FileNotFoundException {
    // return getLanguage(new FileInputStream(opf));
    // }

    // public static boolean existLanguage(InputStream opf) {
    // String language = getLanguage(opf);
    // return language != null;
    // }

    // public static boolean existLanguage(File opf) throws FileNotFoundException {
    // return existLanguage(new FileInputStream(opf));
    // }
    public static List<String> getTitles(List<MetaDC> metaDCs) {
        return metaDCs.stream().filter(p -> p.getName().equals("dc:title")).map(m -> m.getContent())
                .collect(Collectors.toList());
    }

    public static String getTitle(List<MetaDC> metaDCs) {
        return getTitles(metaDCs).get(0);
    }

    // public static String getTitle(InputStream opf) {
    // return getTitle(getMetaDCs(getPackage(opf)));
    // }

    // public static String getTitle(File opf) throws FileNotFoundException {
    // return getTitle(new FileInputStream(opf));
    // }

    // public static boolean existTitle(InputStream opf) {
    // String title = getTitle(opf);
    // return title != null;
    // }

    // public static boolean existTitle(File opf) throws FileNotFoundException {
    // return existTitle(new FileInputStream(opf));
    // }

    public static Node getMetaData(Node pkg) {
        return XmlUtils.getChildNodeByTagName(pkg, "metadata");
    }

    // public static Node getMetaData(InputStream opf) {
    // return getMetaData(getPackage(opf));
    // }

    // public static Node getMetaData(File opf) throws FileNotFoundException {
    // return getMetaData(new FileInputStream(opf));
    // }

    public static List<MetaDC> getMetaDCs(List<MetaItem> metaItems) {
        return metaItems.stream().filter(item -> item.getName() != null && item.getName().startsWith("dc:"))
                .map(item -> {
                    MetaDC dc = new MetaDC();
                    dc.setName(item.getName());
                    dc.setContent(item.getContent());
                    return dc;
                }).collect(Collectors.toList());
    }

    // public static List<MetaDC> getMetaDCs(InputStream opf) {
    // return getMetaDataItems(getPackage(opf)).stream()
    // .filter(item -> item.getName() != null && item.getName().startsWith("dc:"))
    // .map(item -> {
    // MetaDC dc = new MetaDC();
    // dc.setName(item.getName());
    // dc.setContent(item.getContent());
    // return dc;
    // }).collect(Collectors.toList());
    // }

    // public static List<MetaDC> getMetaDCs(File opf) throws FileNotFoundException
    // {
    // return getMetaDCs(new FileInputStream(opf));
    // }

    public static List<MetaItem> getMetaDataItems(Node metaData) {
        List<MetaItem> list = new ArrayList<>();

        XmlUtils.foreachNodeList(metaData.getChildNodes(), md -> {

            NamedNodeMap attributes = md.getAttributes();

            MetaItem item = new MetaItem();
            Node property;
            if (md.getNodeName().startsWith("dc:")) {
                item.setName(md.getNodeName());
                item.setContent(md.getTextContent());
            } else if ((property = attributes.getNamedItem("property")) != null) {
                item.setProperty(property.getTextContent());
                item.setContent(md.getTextContent());
            } else {
                item.setName(attributes.getNamedItem("name").getTextContent());
                item.setContent(attributes.getNamedItem("content").getTextContent());
            }
            list.add(item);
        });
        return list;
    }

    // public static List<MetaItem> getMetaDataItems(File opf) throws
    // FileNotFoundException {
    // return getMetaDataItems(new FileInputStream(opf));
    // }

    // private static List<MetaItem> getMetaDataItems(InputStream opf) {
    // return getMetaDataItems(getPackage(opf));
    // }

    public static Node getManifest(Node pkg) {
        return XmlUtils.getChildNodeByTagName(pkg, "manifest");
    }

    // public static Node getManifest(InputStream opf) {
    // return getManifest(getPackage(opf));
    // }

    // public static Node getManifest(File opf) throws FileNotFoundException {
    // return getManifest(new FileInputStream(opf));
    // }

    public static List<ManifestItem> getManifestItems(Node manifest) {
        NodeList manifests = manifest.getChildNodes();
        List<ManifestItem> list = new ArrayList<>();
        XmlUtils.foreachNodeList(manifests, n -> {
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

    // public static List<ManifestItem> getManifestItems(File opf) throws
    // FileNotFoundException {
    // return getManifestItems(new FileInputStream(opf));
    // }

    public static Node getSpine(Node pkg) {
        return XmlUtils.getChildNodeByTagName(pkg, "spine");
    }

    // public static Node getSpine(InputStream opf) {
    // return getSpine(getPackage(opf));
    // }

    // public static Node getSpine(File opf) throws FileNotFoundException {
    // return getSpine(new FileInputStream(opf));
    // }

    public static List<SpineItemRef> getSpineItemRefs(Node spine) {
        NodeList spines = spine.getChildNodes();
        List<SpineItemRef> list = new ArrayList<>();
        XmlUtils.foreachNodeList(spines, n -> {
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

    // public static List<SpineItemRef> getSpineItemRefs(File opf) throws
    // FileNotFoundException {
    // return getSpineItemRefs(new FileInputStream(opf));
    // }
}
