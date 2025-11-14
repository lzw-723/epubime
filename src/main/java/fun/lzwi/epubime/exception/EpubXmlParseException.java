package fun.lzwi.epubime.exception;

import org.jsoup.nodes.Element;

/**
 * EPUB XML解析异常类
 * 当XML解析过程中发生错误时抛出此异常
 * 
 * 提供详细的XML解析错误信息，包括：
 * - 错误发生的具体位置（行号、列号）
 * - 相关的XML元素信息
 * - XPath表达式
 * - 预期的XML结构
 */
public class EpubXmlParseException extends EpubParseException {
    
    private final String xpath;
    private final String elementInfo;
    private final String expectedStructure;
    private final String actualContent;
    
    /**
     * 构造函数，创建XML解析异常
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param xpath XPath表达式
     * @param lineNumber 行号
     * @param columnNumber 列号
     */
    public EpubXmlParseException(String message, String fileName, String filePath, String xpath, 
                                int lineNumber, int columnNumber) {
        super(new Builder()
                .message(message)
                .fileName(fileName)
                .filePath(filePath)
                .operation("xmlParsing")
                .errorCode(ErrorCode.XML_PARSE_ERROR)
                .lineNumber(lineNumber)
                .columnNumber(columnNumber)
                .addContext("xpath", xpath));
        this.xpath = xpath;
        this.elementInfo = null;
        this.expectedStructure = null;
        this.actualContent = null;
    }
    
    /**
     * 构造函数，创建XML解析异常（带元素信息）
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param element 相关XML元素
     * @param expectedStructure 预期的XML结构
     */
    public EpubXmlParseException(String message, String fileName, String filePath, 
                                Element element, String expectedStructure) {
        super(new Builder()
                .message(message)
                .fileName(fileName)
                .filePath(filePath)
                .operation("xmlParsing")
                .errorCode(ErrorCode.XML_INVALID_STRUCTURE)
                .addContext("elementTag", element != null ? element.tagName() : "unknown")
                .addContext("elementAttributes", element != null ? element.attributes().toString() : "none")
                .addContext("elementContent", element != null ? element.text() : "empty"));
        
        this.xpath = buildXPath(element);
        this.elementInfo = buildElementInfo(element);
        this.expectedStructure = expectedStructure;
        this.actualContent = element != null ? element.outerHtml() : "null";
    }
    
    /**
     * 构造函数，创建XML解析异常（带实际内容）
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param xpath XPath表达式
     * @param expectedStructure 预期的XML结构
     * @param actualContent 实际的XML内容
     */
    public EpubXmlParseException(String message, String fileName, String filePath, String xpath,
                                String expectedStructure, String actualContent) {
        super(new Builder()
                .message(message)
                .fileName(fileName)
                .filePath(filePath)
                .operation("xmlParsing")
                .errorCode(ErrorCode.XML_INVALID_STRUCTURE)
                .addContext("xpath", xpath)
                .addContext("expectedStructure", expectedStructure)
                .addContext("actualContent", truncateContent(actualContent)));
        
        this.xpath = xpath;
        this.elementInfo = null;
        this.expectedStructure = expectedStructure;
        this.actualContent = truncateContent(actualContent);
    }
    
    /**
     * 构造函数，创建XML解析异常（缺少必需元素）
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param missingElement 缺少的元素名称
     * @param parentElement 父元素信息
     */
    public EpubXmlParseException(String fileName, String filePath, String missingElement, Element parentElement) {
        super(new Builder()
                .message("Missing required XML element: " + missingElement)
                .fileName(fileName)
                .filePath(filePath)
                .operation("xmlParsing")
                .errorCode(ErrorCode.XML_MISSING_REQUIRED_ELEMENT)
                .addContext("missingElement", missingElement)
                .addContext("parentElement", parentElement != null ? parentElement.tagName() : "root"));
        
        this.xpath = buildXPath(parentElement) + "/" + missingElement;
        this.elementInfo = buildElementInfo(parentElement);
        this.expectedStructure = "Expected element: " + missingElement;
        this.actualContent = parentElement != null ? parentElement.outerHtml() : "null";
    }
    
    /**
     * 构造函数，创建XML解析异常（无效属性）
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param element 元素
     * @param attributeName 属性名
     * @param expectedValue 预期值
     * @param actualValue 实际值
     */
    public EpubXmlParseException(String fileName, String filePath, Element element, 
                                String attributeName, String expectedValue, String actualValue) {
        super(new Builder()
                .message("Invalid XML attribute: " + attributeName)
                .fileName(fileName)
                .filePath(filePath)
                .operation("xmlParsing")
                .errorCode(ErrorCode.XML_INVALID_ATTRIBUTE)
                .addContext("attributeName", attributeName)
                .addContext("expectedValue", expectedValue)
                .addContext("actualValue", actualValue)
                .addContext("elementTag", element != null ? element.tagName() : "unknown"));
        
        this.xpath = buildXPath(element);
        this.elementInfo = buildElementInfo(element);
        this.expectedStructure = "Expected value: " + expectedValue;
        this.actualContent = "Actual value: " + actualValue;
    }
    
    /**
     * 构建XPath表达式
     */
    private static String buildXPath(Element element) {
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
     * 构建元素信息
     */
    private static String buildElementInfo(Element element) {
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
     */
    private static String truncateContent(String content) {
        if (content == null) {
            return "null";
        }
        
        if (content.length() <= 200) {
            return content;
        }
        
        return content.substring(0, 200) + "... (truncated)";
    }
    
    /**
     * 获取XPath表达式
     * @return XPath表达式
     */
    public String getXPath() {
        return xpath;
    }
    
    /**
     * 获取元素信息
     * @return 元素信息
     */
    public String getElementInfo() {
        return elementInfo;
    }
    
    /**
     * 获取预期的XML结构
     * @return 预期的XML结构
     */
    public String getExpectedStructure() {
        return expectedStructure;
    }
    
    /**
     * 获取实际的XML内容
     * @return 实际的XML内容
     */
    public String getActualContent() {
        return actualContent;
    }
}