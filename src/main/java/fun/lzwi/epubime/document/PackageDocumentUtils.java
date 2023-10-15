package fun.lzwi.epubime.document;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fun.lzwi.epubime.document.section.Manifest;
import fun.lzwi.epubime.document.section.MetaData;
import fun.lzwi.epubime.document.section.Spine;
import fun.lzwi.epubime.document.section.element.ManifestItem;
import fun.lzwi.epubime.document.section.element.MetaDataItem;
import fun.lzwi.epubime.document.section.element.SpineItemRef;
import fun.lzwi.epubime.util.XmlUtils;

public class PackageDocumentUtils {
    public static Node getPackageElement(InputStream opf) {
        try {
            return XmlUtils.getElementsByTagName(opf, "package").item(0);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException("读取Package失败", e);
        }
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
                getMetaDataSection(XmlUtils.getChildNodeByTagName(pkgDocEle, "metadata")));
        packageDocument.setManifest(
                getManifestSection(XmlUtils.getChildNodeByTagName(pkgDocEle, "manifest")));
        packageDocument.setSpine(getSpineSection(XmlUtils.getChildNodeByTagName(pkgDocEle, "spine")));
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

    protected static MetaData getMetaDataSection(Node metaDataNode) {
        MetaData metaData = new MetaData();
        getMetaDataItems(metaDataNode).forEach(a -> {
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

    protected static Manifest getManifestSection(Node manifestNode) {
        Manifest manifest = new Manifest();
        manifest.setId(XmlUtils.getNodeAttribute(manifestNode, "id"));
        manifest.setItems(getManifestItems(manifestNode));
        return manifest;
    }

    private static List<SpineItemRef> getSpineItemRefs(Node spineNode) {
        List<SpineItemRef> list = new ArrayList<>();
        XmlUtils.foreachNodeList(spineNode.getChildNodes(), n -> {
            SpineItemRef item = new SpineItemRef();
            item.setId(XmlUtils.getNodeAttribute(n, "id"));
            item.setIdref(XmlUtils.getNodeAttribute(n, "idref"));
            item.setLinear(XmlUtils.getNodeAttribute(n, "linear"));
            item.setProperties(XmlUtils.getNodeAttribute(n, "properties"));
            list.add(item);
        });
        return list;
    }

    protected static Spine getSpineSection(Node spineNode) {
        Spine spine = new Spine();
        spine.setId(XmlUtils.getNodeAttribute(spineNode, "id"));
        spine.setPageProgressionDirection(XmlUtils.getNodeAttribute(spineNode, "page-progression-direction"));
        spine.setItemRefs(getSpineItemRefs(spineNode));
        return spine;
    }
}
