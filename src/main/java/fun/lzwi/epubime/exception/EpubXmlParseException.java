package fun.lzwi.epubime.exception;

/**
 * XML解析异常
 * 用于处理EPUB文件中XML格式错误的问题
 */
public class EpubXmlParseException extends SimpleEpubException {
    private final String fileName;
    private final int lineNumber;
    private final int columnNumber;
    
    public EpubXmlParseException(String message, String fileName) {
        this(message, fileName, -1, -1, null);
    }
    
    /**
     * 为了向后兼容，提供接受XPath的构造函数
     */
    public EpubXmlParseException(String message, String fileName, String filePath, String xpath, int lineNumber, int columnNumber) {
        this(message, fileName, lineNumber, columnNumber, null);
    }
    
    /**
     * 为了向后兼容，提供接受XPath的构造函数（带cause）
     */
    public EpubXmlParseException(String message, String fileName, String filePath, String xpath, int lineNumber, int columnNumber, Throwable cause) {
        this(message, fileName, lineNumber, columnNumber, cause);
    }
    
    /**
     * 为了向后兼容，提供接受Element的构造函数
     */
    public EpubXmlParseException(String message, String fileName, String filePath, org.jsoup.nodes.Element element, String expectedStructure) {
        this(message, fileName, -1, -1, null);
    }
    
    /**
     * 为了向后兼容，提供接受期望结构和实际内容的构造函数
     */
    public EpubXmlParseException(String message, String fileName, String filePath, String xpath, String expectedStructure, String actualContent) {
        this(message, fileName, -1, -1, null);
    }
    
    /**
     * 为了向后兼容，提供缺失必需元素的构造函数
     */
    public EpubXmlParseException(String fileName, String filePath, String missingElement, org.jsoup.nodes.Element parentElement) {
        this("Missing required XML element: " + missingElement, fileName, -1, -1, null);
    }
    
    /**
     * 为了向后兼容，提供无效属性的构造函数
     */
    public EpubXmlParseException(String fileName, String filePath, org.jsoup.nodes.Element element, String attributeName, String expectedValue, String actualValue) {
        this("Invalid XML attribute: " + attributeName, fileName, -1, -1, null);
    }
    
    public EpubXmlParseException(String message, String fileName, Throwable cause) {
        this(message, fileName, -1, -1, cause);
    }
    
    public EpubXmlParseException(String message, String fileName, int lineNumber, int columnNumber, Throwable cause) {
        super(formatMessage(message, fileName, lineNumber, columnNumber), cause);
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    

    
    public String getFileName() {
        return fileName;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public int getColumnNumber() {
        return columnNumber;
    }
    
    /**
     * 为了向后兼容，提供getFilePath方法
     */
    public String getFilePath() {
        return fileName;
    }
    
    /**
     * 为了向后兼容，提供getXPath方法
     */
    public String getXPath() {
        return null; // 简化设计，不提供XPath信息
    }
    
    /**
     * 为了向后兼容，提供getErrorCode方法
     */
    public Object getErrorCode() {
        return null; // 简化设计，不提供错误码
    }
    
    /**
     * 为了向后兼容，提供getExpectedStructure方法
     */
    public String getExpectedStructure() {
        return null; // 简化设计，不提供期望结构信息
    }
    
    /**
     * 为了向后兼容，提供getElementInfo方法
     */
    public String getElementInfo() {
        return null; // 简化设计，不提供元素信息
    }
    
    /**
     * 为了向后兼容，提供getActualContent方法
     */
    public String getActualContent() {
        return null; // 简化设计，不提供实际内容信息
    }
    
    /**
     * 为了向后兼容，提供getOperation方法
     */
    public String getOperation() {
        return "xmlParsing";
    }
    
    private static String formatMessage(String message, String fileName, int lineNumber, int columnNumber) {
        StringBuilder sb = new StringBuilder(message);
        if (fileName != null) {
            sb.append(" [File: ").append(fileName).append("]");
        }
        if (lineNumber > 0) {
            sb.append(" [Line: ").append(lineNumber).append("]");
        }
        if (columnNumber > 0) {
            sb.append(" [Column: ").append(columnNumber).append("]");
        }
        return sb.toString();
    }
}