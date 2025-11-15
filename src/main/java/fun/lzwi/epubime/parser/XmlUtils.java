package fun.lzwi.epubime.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

/**
 * XML工具类
 * 提供通用的XML解析和处理方法，消除解析器中的重复代码
 */
public class XmlUtils {
    
    /**
     * 解析XML内容
     * 
     * @param xmlContent XML内容
     * @return 解析的文档
     */
    public static Document parseXml(String xmlContent) {
        if (xmlContent == null) {
            throw new IllegalArgumentException("XML content cannot be null");
        }
        return Jsoup.parse(xmlContent, "", Parser.xmlParser());
    }
    
    /**
     * 安全获取元素属性
     * 
     * @param element 元素
     * @param attributeName 属性名
     * @return 属性值，如果不存在返回空字符串
     */
    public static String getAttribute(Element element, String attributeName) {
        if (element == null) {
            return "";
        }
        return element.attr(attributeName);
    }
    
    /**
     * 安全获取元素文本内容
     * 
     * @param element 元素
     * @return 文本内容，如果不存在返回空字符串
     */
    public static String getText(Element element) {
        if (element == null) {
            return "";
        }
        return element.text();
    }
    
    /**
     * 查找第一个匹配的元素
     * 
     * @param document 文档
     * @param cssQuery CSS选择器
     * @return 匹配的元素，如果不存在返回null
     */
    public static Element selectFirst(Document document, String cssQuery) {
        if (document == null || cssQuery == null) {
            return null;
        }
        return document.selectFirst(cssQuery);
    }
    
    /**
     * 查找所有匹配的元素
     * 
     * @param document 文档
     * @param cssQuery CSS选择器
     * @return 匹配的元素列表，如果不存在返回空列表
     */
    public static Elements select(Document document, String cssQuery) {
        if (document == null || cssQuery == null) {
            return new Elements();
        }
        return document.select(cssQuery);
    }
    
    /**
     * 从元素中查找第一个匹配的元素
     * 
     * @param element 父元素
     * @param cssQuery CSS选择器
     * @return 匹配的元素，如果不存在返回null
     */
    public static Element selectFirst(Element element, String cssQuery) {
        if (element == null || cssQuery == null) {
            return null;
        }
        return element.selectFirst(cssQuery);
    }
    
    /**
     * 从元素中查找所有匹配的元素
     * 
     * @param element 父元素
     * @param cssQuery CSS选择器
     * @return 匹配的元素列表，如果不存在返回空列表
     */
    public static Elements select(Element element, String cssQuery) {
        if (element == null || cssQuery == null) {
            return new Elements();
        }
        return element.select(cssQuery);
    }
    
    /**
     * 构建XPath表达式
     * 
     * @param element 元素
     * @return XPath表达式
     */
    public static String buildXPath(Element element) {
        if (element == null) {
            return "/";
        }
        
        StringBuilder xpath = new StringBuilder();
        Element current = element;
        
        while (current != null) {
            String tagName = current.tagName();
            
            // 如果有id属性，使用id定位
            String id = current.attr("id");
            if (!id.isEmpty()) {
                xpath.insert(0, "/" + tagName + "[@id='" + id + "']");
                break;
            }
            
            // 如果有class属性，使用class定位
            String className = current.attr("class");
            if (!className.isEmpty()) {
                xpath.insert(0, "/" + tagName + "[@class='" + className + "']");
            } else {
                // 计算同级元素中的位置
                Element parent = current.parent();
                if (parent != null) {
                    int index = 1;
                    for (Element sibling : parent.children()) {
                        if (sibling.tagName().equals(tagName)) {
                            if (sibling == current) {
                                xpath.insert(0, "/" + tagName + "[" + index + "]");
                                break;
                            }
                            index++;
                        }
                    }
                } else {
                    xpath.insert(0, "/" + tagName);
                }
            }
            
            current = current.parent();
        }
        
        return xpath.length() > 0 ? xpath.toString() : "/";
    }
    
    /**
     * 构建元素信息字符串
     * 
     * @param element 元素
     * @return 元素信息
     */
    public static String buildElementInfo(Element element) {
        if (element == null) {
            return "Element: null";
        }
        
        StringBuilder info = new StringBuilder();
        info.append("Element: ").append(element.tagName());
        
        // 添加属性信息
        if (element.attributes().size() > 0) {
            info.append(" [Attributes: ");
            element.attributes().forEach(attr -> {
                info.append(attr.getKey()).append("='").append(attr.getValue()).append("' ");
            });
            info.append("]");
        }
        
        // 添加文本内容（如果简短）
        String text = element.text();
        if (text != null && text.length() > 0 && text.length() <= 100) {
            info.append(" [Text: '").append(text).append("']");
        }
        
        return info.toString();
    }
    
    /**
     * 截断内容以避免过长的异常消息
     * 
     * @param content 内容
     * @return 截断后的内容
     */
    public static String truncateContent(String content) {
        if (content == null) {
            return "null";
        }
        
        if (content.length() <= 200) {
            return content;
        }
        
        return content.substring(0, 200) + "... (truncated)";
    }
    
    /**
     * 截断内容以避免过长的异常消息（自定义长度）
     * 
     * @param content 内容
     * @param maxLength 最大长度
     * @return 截断后的内容
     */
    public static String truncateContent(String content, int maxLength) {
        if (content == null) {
            return "null";
        }
        
        if (content.length() <= maxLength) {
            return content;
        }
        
        return content.substring(0, maxLength) + "... (truncated)";
    }
    
    /**
     * 检查元素是否有指定的属性
     * 
     * @param element 元素
     * @param attributeName 属性名
     * @return 如果有该属性返回true，否则返回false
     */
    public static boolean hasAttribute(Element element, String attributeName) {
        if (element == null || attributeName == null) {
            return false;
        }
        return element.hasAttr(attributeName);
    }
    
    /**
     * 获取元素的子元素数量
     * 
     * @param element 父元素
     * @return 子元素数量
     */
    public static int getChildCount(Element element) {
        if (element == null) {
            return 0;
        }
        return element.children().size();
    }
    
    /**
     * 检查元素是否有子元素
     * 
     * @param element 元素
     * @return 如果有子元素返回true，否则返回false
     */
    public static boolean hasChildren(Element element) {
        if (element == null) {
            return false;
        }
        return !element.children().isEmpty();
    }
}