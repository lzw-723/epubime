package fun.lzwi.epubime.exception;

import java.io.File;

/**
 * EPUB格式异常
 * 用于处理EPUB文件格式不符合规范的问题
 */
public class EpubFormatException extends SimpleEpubException {
    private final File file;
    private final String details;
    private final String operation;
    private final Object errorCode;
    private final String recoverySuggestion;
    
    public EpubFormatException(String message, File file) {
        this(message, file, null, null);
    }
    
    public EpubFormatException(String message, File file, String details) {
        this(message, file, details, null);
    }
    
    public EpubFormatException(String message, File file, String details, Throwable cause) {
        this(message, file, details, "formatValidation", null, "请检查文件格式和内容是否符合EPUB规范", cause);
    }
    
    private EpubFormatException(String message, File file, String details, String operation, Object errorCode, String recoverySuggestion, Throwable cause) {
        super(formatMessageWithErrorCode(message, file, details, errorCode, operation), cause);
        this.file = file;
        this.details = details;
        this.operation = operation;
        this.errorCode = errorCode;
        this.recoverySuggestion = recoverySuggestion;
    }
    
    /**
     * 为了向后兼容，提供接受String参数的构造函数
     */
    public EpubFormatException(String message, String fileName, String filePath) {
        this(message, fileName, filePath, (Throwable)null);
    }
    
    /**
     * 为了向后兼容，提供接受String参数的构造函数（带cause）
     */
    public EpubFormatException(String message, String fileName, String filePath, Throwable cause) {
        this(message, fileName != null ? new File(fileName) : null, filePath, "formatValidation", 
                EpubParseException.ErrorCode.EPUB_INVALID_CONTAINER, 
                "请检查文件格式和内容是否符合EPUB规范", cause);
    }
    
    /**
     * 静态工厂方法 - 为了向后兼容
     */
    public static EpubFormatException missingMimetype(String fileName, String filePath) {
        return new EpubFormatException("Missing required mimetype file", 
                fileName != null ? new File(fileName) : null, filePath, 
                "mimetypeValidation", EpubParseException.ErrorCode.EPUB_MISSING_MIMETYPE, 
                "请确保EPUB文件包含mimetype文件", null);
    }
    
    /**
     * 静态工厂方法 - 为了向后兼容
     */
    public static EpubFormatException invalidContainer(String fileName, String filePath, String details) {
        return new EpubFormatException("Invalid container.xml: " + details, 
                fileName != null ? new File(fileName) : null, filePath, 
                "containerValidation", EpubParseException.ErrorCode.EPUB_INVALID_CONTAINER, 
                "请检查META-INF/container.xml文件格式", null);
    }
    
    /**
     * 静态工厂方法 - 为了向后兼容
     */
    public static EpubFormatException invalidOpf(String fileName, String filePath, String opfPath, String details) {
        return new EpubFormatException("Invalid OPF file: " + details, 
                fileName != null ? new File(fileName) : null, filePath, 
                "opfValidation", EpubParseException.ErrorCode.EPUB_INVALID_OPF, 
                "请检查OPF文件的XML格式和结构", null);
    }
    
    /**
     * 静态工厂方法 - 为了向后兼容
     */
    public static EpubFormatException invalidNcx(String fileName, String filePath, String ncxPath, String details) {
        return new EpubFormatException("Invalid NCX file: " + details, 
                fileName != null ? new File(fileName) : null, filePath, 
                "ncxValidation", EpubParseException.ErrorCode.EPUB_INVALID_NCX, 
                "请检查NCX文件的XML格式和结构", null);
    }
    
    /**
     * 静态工厂方法 - 为了向后兼容
     */
    public static EpubFormatException invalidNav(String fileName, String filePath, String navPath, String details) {
        return new EpubFormatException("Invalid NAV file: " + details, 
                fileName != null ? new File(fileName) : null, filePath, 
                "navValidation", EpubParseException.ErrorCode.EPUB_INVALID_NAV, 
                "请检查NAV文件的XML格式和结构", null);
    }
    
    public File getFile() {
        return file;
    }
    
    public String getDetails() {
        return details;
    }
    
    /**
     * 为了向后兼容，提供getFileName方法
     */
    public String getFileName() {
        return file != null ? file.getName() : null;
    }
    
    /**
     * 为了向后兼容，提供getFilePath方法
     */
    public String getFilePath() {
        return details;
    }
    
    /**
     * 为了向后兼容，提供getOperation方法
     */
    public String getOperation() {
        return operation;
    }
    
    /**
     * 为了向后兼容，提供getErrorCode方法
     */
    public Object getErrorCode() {
        return errorCode;
    }
    
    /**
     * 为了向后兼容，提供getRecoverySuggestion方法
     */
    public String getRecoverySuggestion() {
        return recoverySuggestion;
    }
    
    private static String formatMessageWithErrorCode(String message, File file, String details, Object errorCode, String operation) {
        StringBuilder sb = new StringBuilder();
        if (errorCode != null) {
            sb.append("[").append(errorCode.toString()).append("] ");
        }
        sb.append(message);
        if (file != null) {
            sb.append(" [File: ").append(file.getName()).append("]");
        }
        if (details != null) {
            sb.append(" [Path: ").append(details).append("]");
        }
        if (operation != null) {
            sb.append(" [Operation: ").append(operation).append("]");
        }
        return sb.toString();
    }
    
    private static String formatMessage(String message, File file, String details) {
        return formatMessageWithErrorCode(message, file, details, null, null);
    }
    
    private static String formatMessage(String message, String fileName, String filePath) {
        StringBuilder sb = new StringBuilder();
        sb.append(message);
        if (fileName != null) {
            sb.append(" [File: ").append(fileName).append("]");
        }
        if (filePath != null) {
            sb.append(" [Path: ").append(filePath).append("]");
        }
        return sb.toString();
    }
}