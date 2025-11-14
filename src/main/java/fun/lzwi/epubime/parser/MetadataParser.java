package fun.lzwi.epubime.parser;

import fun.lzwi.epubime.epub.Metadata;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

/**
 * 元数据解析器
 * 负责解析OPF文件中的元数据信息
 */
public class MetadataParser {
    
    /**
     * 解析OPF内容中的元数据
     *
     * @param opfContent OPF文件内容
     * @return 元数据对象
     */
    public Metadata parseMetadata(String opfContent) {
        if (opfContent == null) {
            throw new IllegalArgumentException("OPF content cannot be null");
        }
        
        Metadata metadata = new Metadata();
        Document opfDocument = Jsoup.parse(opfContent, Parser.xmlParser());

        // 解析package元素获取unique-identifier属性
        Element packageElement = opfDocument.selectFirst("package");
        if (packageElement != null) {
            String uniqueIdentifierId = packageElement.attr("unique-identifier");
            if (!uniqueIdentifierId.isEmpty()) {
                // 查找对应的dc:identifier元素并设置为uniqueIdentifier
                opfDocument.select("metadata > dc\\:identifier").forEach(identifier -> {
                    String id = identifier.attr("id");
                    if (uniqueIdentifierId.equals(id)) {
                        metadata.setUniqueIdentifier(identifier.text());
                    }
                });
            }
        }

        opfDocument.select("metadata").forEach(meta -> 
            meta.children().forEach(child -> parseMetadataElement(child, metadata))
        );
        
        return metadata;
    }
    
    /**
     * 解析单个元数据元素
     *
     * @param child 子元素
     * @param metadata 元数据对象
     */
    private void parseMetadataElement(org.jsoup.nodes.Element child, Metadata metadata) {
        String tagName = child.tagName();
        String text = child.text();
        
        switch (tagName) {
            case "dc:title":
                metadata.addTitle(text);
                break;
            case "dc:creator":
                metadata.addCreator(text);
                break;
            case "dc:language":
                metadata.addLanguage(text);
                break;
            case "dc:identifier":
                metadata.addIdentifier(text);
                break;
            case "dc:publisher":
                metadata.addPublisher(text);
                break;
            case "dc:date":
                metadata.addDate(text);
                break;
            case "dc:description":
                metadata.addDescription(text);
                break;
            case "dc:subject":
                metadata.addSubject(text);
                break;
            case "dc:type":
                metadata.addType(text);
                break;
            case "dc:format":
                metadata.addFormat(text);
                break;
            case "dc:source":
                metadata.addSource(text);
                break;
            case "dc:rights":
                metadata.addRights(text);
                break;
            case "dc:contributor":
                metadata.addContributor(text);
                break;
            case "meta":
                parseMetaElement(child, metadata);
                break;
            default:
                break;
        }
    }
    
    /**
     * 解析meta元素
     *
     * @param meta meta元素
     * @param metadata 元数据对象
     */
    private void parseMetaElement(org.jsoup.nodes.Element meta, Metadata metadata) {
        String property = meta.attr("property");
        String name = meta.attr("name");
        String content = meta.text();
        
        if ("cover".equals(name)) {
            // 保持对旧meta name="cover"方法的支持，但优先级较低
            if (metadata.getCover() == null || metadata.getCover().isEmpty()) {
                metadata.setCover(meta.attr("content"));
            }
        } else if ("dcterms:rightsHolder".equals(property)) {
            metadata.setRightsHolder(content);
        } else if ("dcterms:modified".equals(property)) {
            metadata.setModified(content);
        } else if ("rendition:layout".equals(property)) {
            metadata.setLayout(content);
        } else if ("rendition:orientation".equals(property)) {
            metadata.setOrientation(content);
        } else if ("rendition:spread".equals(property)) {
            metadata.setSpread(content);
        } else if ("rendition:viewport".equals(property)) {
            metadata.setViewport(content);
        } else if ("rendition:media".equals(property)) {
            metadata.setMedia(content);
        } else if ("rendition:flow".equals(property)) {
            metadata.setFlow(content);
        } else if ("rendition:align-x-center".equals(property)) {
            metadata.setAlignXCenter("true".equalsIgnoreCase(content) || 
                                   "yes".equalsIgnoreCase(content) || 
                                   "1".equals(content));
        } else if ("schema:accessibilityFeature".equals(property)) {
            metadata.addAccessibilityFeature(content);
        } else if ("schema:accessibilityHazard".equals(property)) {
            metadata.addAccessibilityHazard(content);
        } else if ("schema:accessibilitySummary".equals(property)) {
            metadata.addAccessibilitySummary(content);
        }
    }
}