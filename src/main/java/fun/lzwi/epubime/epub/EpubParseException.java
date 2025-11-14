package fun.lzwi.epubime.epub;

/**
 * EPUB解析异常类
 * 当EPUB文件解析过程中发生错误时抛出此异常
 */
public class EpubParseException extends Exception {
    private final String fileName;
    private final String filePath;
    private final String operation;

    /**
     * 构造函数，创建带有指定消息的EPUB解析异常
     * @param message 异常消息
     */
    public EpubParseException(String message) {
        super(message);
        this.fileName = null;
        this.filePath = null;
        this.operation = null;
    }

    /**
     * 构造函数，创建带有指定消息和原因的EPUB解析异常
     * @param message 异常消息
     * @param cause 异常原因
     */
    public EpubParseException(String message, Throwable cause) {
        super(message, cause);
        this.fileName = null;
        this.filePath = null;
        this.operation = null;
    }

    /**
     * 构造函数，创建带有详细错误信息的EPUB解析异常
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param operation 操作类型
     * @param cause 异常原因
     */
    public EpubParseException(String message, String fileName, String filePath, String operation, Throwable cause) {
        super(formatMessage(message, fileName, filePath, operation), cause);
        this.fileName = fileName;
        this.filePath = filePath;
        this.operation = operation;
    }

    /**
     * 构造函数，创建带有详细错误信息的EPUB解析异常（无原因）
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param operation 操作类型
     */
    public EpubParseException(String message, String fileName, String filePath, String operation) {
        super(formatMessage(message, fileName, filePath, operation));
        this.fileName = fileName;
        this.filePath = filePath;
        this.operation = operation;
    }

    /**
     * 格式化异常消息，包含额外的上下文信息
     */
    private static String formatMessage(String message, String fileName, String filePath, String operation) {
        StringBuilder sb = new StringBuilder();
        sb.append(message != null ? message : "Unknown error");

        boolean hasContext = false;
        if (fileName != null) {
            sb.append(" [File: ").append(fileName);
            hasContext = true;
        }

        if (filePath != null) {
            if (hasContext) sb.append(",");
            sb.append(" Path: ").append(filePath);
            hasContext = true;
        }

        if (operation != null) {
            if (hasContext) sb.append(",");
            sb.append(" Operation: ").append(operation);
            hasContext = true;
        }

        if (hasContext) {
            sb.append("]");
        }

        return sb.toString();
    }

    /**
     * 获取文件名
     * @return 文件名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 获取文件路径
     * @return 文件路径
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * 获取操作类型
     * @return 操作类型
     */
    public String getOperation() {
        return operation;
    }
}