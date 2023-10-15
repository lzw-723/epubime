package fun.lzwi.epubime.util;

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
import fun.lzwi.epubime.bean.PackageInfo;
import fun.lzwi.epubime.bean.SpineItemRef;

public class OpfUtils {

    public static Node getPackage(InputStream opf) {
        try {
            return XmlUtils.getElementsByTagName(opf, "package").item(0);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException("读取Package失败", e);
        }
    }

    private static String getPackageAttribute(NamedNodeMap attributes, String name) {
        try {
            return attributes.getNamedItem(name).getTextContent();
        } catch (Exception e) {
            LoggerUtils.from(OpfUtils.class).info("package的" + name + "属性未设置");
        }
        return null;
    }

    public static PackageInfo getPackageInfo(Node pkg) {
        NamedNodeMap attributes = pkg.getAttributes();
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.setDir(getPackageAttribute(attributes, "dir"));
        packageInfo.setId(getPackageAttribute(attributes, "id"));
        packageInfo.setPrefix(getPackageAttribute(attributes, "prefix"));
        packageInfo.setUniqueIdentifier(getPackageAttribute(attributes, "unique-identifier"));
        packageInfo.setVersion(getPackageAttribute(attributes, "version"));
        packageInfo.setXmlLang(getPackageAttribute(attributes, "xml:lang"));
        return packageInfo;
    }

    public static List<String> getIdentifiers(List<MetaDC> metaDCs) {
        return metaDCs.stream().filter(p -> p.getName().equals("dc:identifier"))
                .map(MetaDC::getContent).collect(Collectors.toList());
    }

    public static String getIdentifier(List<MetaDC> metaDCs) {
        return getIdentifiers(metaDCs).get(0);
    }

    public static List<String> getLanguages(List<MetaDC> metaDCs) {
        return metaDCs.stream().filter(p -> p.getName().equals("dc:language")).map(MetaDC::getContent)
                .collect(Collectors.toList());
    }

    public static String getLanguage(List<MetaDC> metaDCs) {
        return getLanguages(metaDCs).get(0);
    }

    public static List<String> getTitles(List<MetaDC> metaDCs) {
        return metaDCs.stream().filter(p -> p.getName().equals("dc:title")).map(MetaDC::getContent)
                .collect(Collectors.toList());
    }

    public static String getTitle(List<MetaDC> metaDCs) {
        return getTitles(metaDCs).get(0);
    }

    public static Node getMetaData(Node pkg) {
        return XmlUtils.getChildNodeByTagName(pkg, "metadata");
    }

    public static List<MetaDC> getMetaDCs(List<MetaItem> metaItems) {
        return metaItems.stream().filter(item -> item.getName() != null && item.getName().startsWith("dc:"))
                .map(item -> {
                    MetaDC dc = new MetaDC();
                    dc.setName(item.getName());
                    dc.setContent(item.getContent());
                    return dc;
                }).collect(Collectors.toList());
    }

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

    public static Node getManifest(Node pkg) {
        return XmlUtils.getChildNodeByTagName(pkg, "manifest");
    }

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

    public static Node getSpine(Node pkg) {
        return XmlUtils.getChildNodeByTagName(pkg, "spine");
    }

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

}
