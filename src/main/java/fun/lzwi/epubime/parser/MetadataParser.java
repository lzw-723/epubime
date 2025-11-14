package fun.lzwi.epubime.parser;

import fun.lzwi.epubime.epub.Metadata;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.util.HashMap;
import java.util.Map;

/**
 * 元数据解析器
 * 负责解析OPF文件中的元数据信息
 */
public class MetadataParser {
    
    // 预编译的元数据映射表，提高性能
    private static final Map<String, String> DC_ELEMENT_MAP = new HashMap<>();
    private static final Map<String, String> META_PROPERTY_MAP = new HashMap<>();
    
    static {
        // 初始化DC元素映射
        DC_ELEMENT_MAP.put("dc:title", "title");
        DC_ELEMENT_MAP.put("dc:creator", "creator");
        DC_ELEMENT_MAP.put("dc:language", "language");
        DC_ELEMENT_MAP.put("dc:identifier", "identifier");
        DC_ELEMENT_MAP.put("dc:publisher", "publisher");
        DC_ELEMENT_MAP.put("dc:date", "date");
        DC_ELEMENT_MAP.put("dc:description", "description");
        DC_ELEMENT_MAP.put("dc:subject", "subject");
        DC_ELEMENT_MAP.put("dc:type", "type");
        DC_ELEMENT_MAP.put("dc:format", "format");
        DC_ELEMENT_MAP.put("dc:source", "source");
        DC_ELEMENT_MAP.put("dc:rights", "rights");
        DC_ELEMENT_MAP.put("dc:contributor", "contributor");
        
        // 初始化meta属性映射
        META_PROPERTY_MAP.put("dcterms:rightsHolder", "rightsHolder");
        META_PROPERTY_MAP.put("dcterms:modified", "modified");
        META_PROPERTY_MAP.put("rendition:layout", "layout");
        META_PROPERTY_MAP.put("rendition:orientation", "orientation");
        META_PROPERTY_MAP.put("rendition:spread", "spread");
        META_PROPERTY_MAP.put("rendition:viewport", "viewport");
        META_PROPERTY_MAP.put("rendition:media", "media");
        META_PROPERTY_MAP.put("rendition:flow", "flow");
        META_PROPERTY_MAP.put("rendition:align-x-center", "alignXCenter");
        META_PROPERTY_MAP.put("schema:accessibilityFeature", "accessibilityFeature");
        META_PROPERTY_MAP.put("schema:accessibilityHazard", "accessibilityHazard");
        META_PROPERTY_MAP.put("schema:accessibilitySummary", "accessibilitySummary");
    }
    
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
        
        // 使用更快的解析配置
        Document opfDocument = Jsoup.parse(opfContent, "", Parser.xmlParser());
        
        // 缓存常用选择器结果，避免重复查询
        Element packageElement = opfDocument.selectFirst("package");
        if (packageElement != null) {
            String uniqueIdentifierId = packageElement.attr("unique-identifier");
            if (!uniqueIdentifierId.isEmpty()) {
                // 使用更高效的查询方式
                for (Element identifier : packageElement.select("metadata > dc|identifier")) {
                    if (uniqueIdentifierId.equals(identifier.attr("id"))) {
                        metadata.setUniqueIdentifier(identifier.text());
                        break; // 找到后立即退出
                    }
                }
            }
        }

        // 优化：一次性获取metadata元素，避免重复查询
        Element metadataElement = opfDocument.selectFirst("metadata");
        if (metadataElement != null) {
            // 使用更高效的遍历方式
            for (Element child : metadataElement.children()) {
                parseMetadataElement(child, metadata);
            }
        }
        
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
        
        // 使用映射表进行快速查找，避免大量的switch-case
        String elementType = DC_ELEMENT_MAP.get(tagName);
        if (elementType != null) {
            switch (elementType) {
                case "title":
                    metadata.addTitle(text);
                    break;
                case "creator":
                    metadata.addCreator(text);
                    break;
                case "language":
                    metadata.addLanguage(text);
                    break;
                case "identifier":
                    metadata.addIdentifier(text);
                    break;
                case "publisher":
                    metadata.addPublisher(text);
                    break;
                case "date":
                    metadata.addDate(text);
                    break;
                case "description":
                    metadata.addDescription(text);
                    break;
                case "subject":
                    metadata.addSubject(text);
                    break;
                case "type":
                    metadata.addType(text);
                    break;
                case "format":
                    metadata.addFormat(text);
                    break;
                case "source":
                    metadata.addSource(text);
                    break;
                case "rights":
                    metadata.addRights(text);
                    break;
                case "contributor":
                    metadata.addContributor(text);
                    break;
                default:
                    // 未知类型，忽略处理
                    break;
            }
            return; // 处理完成后直接返回
        }
        
        // 处理meta元素
        if ("meta".equals(tagName)) {
            parseMetaElement(child, metadata);
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
        
        // 处理cover属性
        if ("cover".equals(name)) {
            // 保持对旧meta name="cover"方法的支持，但优先级较低
            if (metadata.getCover() == null || metadata.getCover().isEmpty()) {
                metadata.setCover(meta.attr("content"));
            }
            return;
        }
        
        // 使用映射表处理其他meta属性
        String metaType = META_PROPERTY_MAP.get(property);
        if (metaType != null) {
            switch (metaType) {
                case "rightsHolder":
                    metadata.setRightsHolder(content);
                    break;
                case "modified":
                    metadata.setModified(content);
                    break;
                case "layout":
                    metadata.setLayout(content);
                    break;
                case "orientation":
                    metadata.setOrientation(content);
                    break;
                case "spread":
                    metadata.setSpread(content);
                    break;
                case "viewport":
                    metadata.setViewport(content);
                    break;
                case "media":
                    metadata.setMedia(content);
                    break;
                case "flow":
                    metadata.setFlow(content);
                    break;
                case "alignXCenter":
                    metadata.setAlignXCenter("true".equalsIgnoreCase(content) || 
                                           "yes".equalsIgnoreCase(content) || 
                                           "1".equals(content));
                    break;
                case "accessibilityFeature":
                    metadata.addAccessibilityFeature(content);
                    break;
                case "accessibilityHazard":
                    metadata.addAccessibilityHazard(content);
                    break;
                case "accessibilitySummary":
                    metadata.addAccessibilitySummary(content);
                    break;
                default:
                    // 未知类型，忽略处理
                    break;
            }
        }
    }
}