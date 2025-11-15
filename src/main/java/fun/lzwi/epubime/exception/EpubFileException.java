package fun.lzwi.epubime.exception;

import java.io.File;

/**
 * EPUB文件相关异常
 * 用于处理文件不存在、无法访问等问题
 */
public class EpubFileException extends BaseEpubException {
    private final File file;
    
    public EpubFileException(String message, File file) {
        this(message, file, null);
    }
    
    public EpubFileException(String message, File file, Throwable cause) {
        super(formatMessage(message, file), cause);
        this.file = file;
    }
    
    public File getFile() {
        return file;
    }
    
    /**
     * 为了向后兼容，提供getFileName方法
     */
    public String getFileName() {
        return file != null ? file.getName() : null;
    }
    
    private static String formatMessage(String message, File file) {
        if (file != null) {
            return String.format("%s [File: %s]", message, file.getName());
        }
        return message;
    }
}