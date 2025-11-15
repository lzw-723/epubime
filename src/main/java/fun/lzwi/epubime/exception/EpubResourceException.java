package fun.lzwi.epubime.exception;

import java.io.File;

/**
 * EPUB资源异常
 * 用于处理资源加载、访问失败等问题
 */
public class EpubResourceException extends BaseEpubException {
    private final File file;
    private final String resourceId;
    private final String resourcePath;
    
    public EpubResourceException(String message, File file) {
        this(message, file, null, null, null);
    }
    
    public EpubResourceException(String message, File file, String resourcePath) {
        this(message, file, null, resourcePath, null);
    }
    
    public EpubResourceException(String message, File file, String resourceId, String resourcePath, Throwable cause) {
        super(formatMessage(message, file, resourceId, resourcePath), cause);
        this.file = file;
        this.resourceId = resourceId;
        this.resourcePath = resourcePath;
    }
    
    /**
     * 为了向后兼容，提供接受String参数的构造函数
     */
    public EpubResourceException(String message, String fileName, String resourcePath) {
        this(message, fileName, resourcePath, (Throwable)null);
    }
    
    /**
     * 为了向后兼容，提供接受String参数的构造函数（带cause）
     */
    public EpubResourceException(String message, String fileName, String resourcePath, Throwable cause) {
        super(formatMessageWithErrorCode(message, fileName, resourcePath), cause);
        this.file = fileName != null ? new File(fileName) : null;
        this.resourceId = null;
        this.resourcePath = resourcePath;
    }
    
    public File getFile() {
        return file;
    }
    
    public String getResourceId() {
        return resourceId;
    }
    
    public String getResourcePath() {
        return resourcePath;
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
        return resourcePath;
    }
    
    /**
     * 为了向后兼容，提供getOperation方法
     */
    public String getOperation() {
        return "resourceLoading";
    }
    
    /**
     * 为了向后兼容，提供getErrorCode方法
     */
    public Object getErrorCode() {
        return EpubParseException.ErrorCode.RESOURCE_LOAD_FAILED;
    }
    
    private static String formatMessage(String message, File file, String resourceId, String resourcePath) {
        StringBuilder sb = new StringBuilder(message);
        if (file != null) {
            sb.append(" [File: ").append(file.getName()).append("]");
        }
        if (resourceId != null) {
            sb.append(" [Resource ID: ").append(resourceId).append("]");
        }
        if (resourcePath != null) {
            sb.append(" [Resource Path: ").append(resourcePath).append("]");
        }
        return sb.toString();
    }
    
    private static String formatMessageWithErrorCode(String message, String fileName, String resourcePath) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(EpubParseException.ErrorCode.RESOURCE_LOAD_FAILED.toString()).append("] ");
        sb.append(message);
        if (fileName != null) {
            sb.append(" [File: ").append(fileName).append("]");
        }
        if (resourcePath != null) {
            sb.append(" [Path: ").append(resourcePath).append("]");
        }
        sb.append(" [Operation: resourceLoading]");
        return sb.toString();
    }
    
    
}