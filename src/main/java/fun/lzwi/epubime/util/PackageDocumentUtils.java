package fun.lzwi.epubime.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fun.lzwi.epubime.document.PackageDocument;
import fun.lzwi.epubime.document.section.Manifest;
import fun.lzwi.epubime.document.section.MetaData;
import fun.lzwi.epubime.document.section.element.ManifestItem;
import fun.lzwi.epubime.document.section.element.MetaDataItem;

public class PackageDocumentUtils {
    public static Node getPackageElement(InputStream opf) {
        Node item = null;
        try {
            item = XmlUtils.getElementsByTagName(opf, "package").item(0);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException("读取Package失败", e);
        }
        return item;
    }

    public static PackageDocument getPackageDocument(Node pkgDocEle) {
        PackageDocument packageDocument = new PackageDocument();

        packageDocument.setDir(XmlUtils.getNodeAttribute(pkgDocEle, "dir"));
        packageDocument.setId(XmlUtils.getNodeAttribute(pkgDocEle, "id"));
        packageDocument.setPrefix(XmlUtils.getNodeAttribute(pkgDocEle, "prefix"));
        packageDocument.setUniqueIdentifier(XmlUtils.getNodeAttribute(pkgDocEle, "unique-identifier"));
        packageDocument.setVersion(XmlUtils.getNodeAttribute(pkgDocEle, "version"));
        packageDocument.setXmlLang(XmlUtils.getNodeAttribute(pkgDocEle, "xml:lang"));

        packageDocument.setMetaData(
                getMetaDataSection(getMetaDataItems(XmlUtils.getChildNodeByTagName(pkgDocEle, "metadata"))));
        packageDocument.setManifest(
                getManifestSection(getManifestItems(XmlUtils.getChildNodeByTagName(pkgDocEle, "manifest"))));
        return packageDocument;
    }

    private static List<MetaDataItem> getMetaDataItems(Node metaDataNode) {
        List<MetaDataItem> list = new ArrayList<>();

        XmlUtils.foreachNodeList(metaDataNode.getChildNodes(), md -> {
            MetaDataItem item = new MetaDataItem();
            item.setName(md.getNodeName());
            item.setContent(md.getTextContent());
            item.setDir(XmlUtils.getNodeAttribute(md, "dir"));
            item.setId(XmlUtils.getNodeAttribute(md, "id"));
            item.setProperty(XmlUtils.getNodeAttribute(md, "property"));
            item.setXmlLang(XmlUtils.getNodeAttribute(md, "xml:lang"));
            // TODO: item.setRefines(null);
            list.add(item);
        });
        return list;
    }

    private static MetaData getMetaDataSection(List<MetaDataItem> metaDataItems) {
        MetaData metaData = new MetaData();
        metaDataItems.stream().forEach(a -> {
            String content = a.getContent();
            switch (a.getName()) {
                case "dc:title":
                    metaData.addTitle(content);
                    break;
                case "dc:identifier":
                    metaData.addIdentifier(content);
                    break;
                case "dc:language":
                    metaData.addLanguage(content);
                    break;
                case "dc:contributor":
                    metaData.addContributor(content);
                    break;
                case "dc:coverage":
                    metaData.addCoverage(content);
                    break;
                case "dc:creator":
                    metaData.addCreator(content);
                    break;
                case "dc:date":
                    metaData.addDate(content);
                    break;
                case "dc:description":
                    metaData.addDescription(content);
                    break;
                case "dc:format":
                    metaData.addFormat(content);
                    break;
                case "dc:publisher":
                    metaData.addPublisher(content);
                    break;
                case "dc:relation":
                    metaData.addRelation(content);
                    break;
                case "dc:right":
                    metaData.addRight(content);
                    break;
                case "dc:source":
                    metaData.addSource(content);
                    break;
                case "dc:subject":
                    metaData.addSubject(content);
                    break;
                case "dc:type":
                    metaData.addType(content);
                    break;
                case "meta":
                    // TODO: 处理meta
                    break;
                default:
                    break;
            }
        });
        return metaData;
    }

    private static List<ManifestItem> getManifestItems(Node manifestNode) {
        List<ManifestItem> list = new ArrayList<>();
        XmlUtils.foreachNodeList(manifestNode.getChildNodes(), n -> {
            ManifestItem item = new ManifestItem();
            item.setId(XmlUtils.getNodeAttribute(n, "id"));
            item.setHref(XmlUtils.getNodeAttribute(n, "href"));
            item.setMediaType(XmlUtils.getNodeAttribute(n, "media-type"));
            item.setProperties(XmlUtils.getNodeAttribute(n, "properties"));
            list.add(item);
        });
        return list;
    }

    private static Manifest getManifestSection(List<ManifestItem> items) {
        Manifest manifest = new Manifest();
        manifest.setItems(items);
        return manifest;
    }
}
