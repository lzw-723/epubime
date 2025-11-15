package fun.lzwi.epubime.exception;

import java.io.File;

/**
 * ZIP文件处理异常
 * 用于处理EPUB ZIP文件格式错误、解压失败等问题
 */
public class EpubZipException extends SimpleEpubException {
    private final File file;
    private final String entryName;
    
    public EpubZipException(String message, File file) {
        this(message, file, null, null);
    }
    
    public EpubZipException(String message, File file, Throwable cause) {
        this(message, file, null, cause);
    }
    
    public EpubZipException(String message, File file, String entryName, Throwable cause) {
        super(formatMessage(message, file, entryName), cause);
        this.file = file;
        this.entryName = entryName;
    }
    
    /**
     * 为了向后兼容，提供接受String参数的构造函数
     */
    public EpubZipException(String message, String fileName, String filePath) {
        this(message, fileName, filePath, (Throwable)null);
    }
    
    /**
     * 为了向后兼容，提供接受String参数的构造函数（带cause）
     */
    public EpubZipException(String message, String fileName, String filePath, Throwable cause) {
        super(formatMessageWithErrorCode(message, fileName, filePath), cause);
        this.file = fileName != null ? new File(fileName) : null;
        this.entryName = filePath; // 将路径作为entryName保存
    }
    
    public File getFile() {
        return file;
    }
    
    public String getEntryName() {
        return entryName;
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
        return entryName;
    }
    
    /**
     * 为了向后兼容，提供getOperation方法
     */
    public String getOperation() {
        return "zipProcessing";
    }
    
    /**
     * 为了向后兼容，提供getErrorCode方法
     */
    public Object getErrorCode() {
        return EpubParseException.ErrorCode.ZIP_INVALID;
    }
    
    private static String formatMessage(String message, File file, String entryName) {
        StringBuilder sb = new StringBuilder(message);
        if (file != null) {
            sb.append(" [File: ").append(file.getName()).append("]");
        }
        if (entryName != null) {
            sb.append(" [Entry: ").append(entryName).append("]");
        }
        return sb.toString();
    }
    
    private static String formatMessageWithErrorCode(String message, String fileName, String filePath) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(EpubParseException.ErrorCode.ZIP_INVALID.toString()).append("] ");
        sb.append(message);
        if (fileName != null) {
            sb.append(" [File: ").append(fileName).append("]");
        }
        if (filePath != null) {
            sb.append(" [Path: ").append(filePath).append("]");
        }
        sb.append(" [Operation: zipProcessing]");
        return sb.toString();
    }
    
    
}