package fun.lzwi.epubime.exception;

/**
 * 路径验证异常
 * 用于处理文件路径验证失败的情况，如目录遍历攻击检测
 */
public class EpubPathValidationException extends EpubParseException {
    private final String invalidPath;
    private final String fileName;
    private final Object errorCode;
    
    /**
     * 主要构造函数
     */
    public EpubPathValidationException(String message, String invalidPath) {
        super(message, null, invalidPath, "pathValidation", ErrorCode.PATH_INVALID_CHARACTER);
        this.invalidPath = invalidPath;
        this.fileName = null;
        this.errorCode = ErrorCode.PATH_INVALID_CHARACTER;
    }
    
    /**
     * 主要构造函数（带cause）
     */
    public EpubPathValidationException(String message, String invalidPath, Throwable cause) {
        super(message, null, invalidPath, "pathValidation", ErrorCode.PATH_INVALID_CHARACTER, cause);
        this.invalidPath = invalidPath;
        this.fileName = null;
        this.errorCode = ErrorCode.PATH_INVALID_CHARACTER;
    }
    
    /**
     * 主要构造函数（带文件名和错误码）
     */
    public EpubPathValidationException(String message, String invalidPath, String fileName, Throwable cause) {
        super(message, fileName, invalidPath, "pathValidation", ErrorCode.PATH_INVALID_CHARACTER, cause);
        this.invalidPath = invalidPath;
        this.fileName = fileName;
        this.errorCode = ErrorCode.PATH_INVALID_CHARACTER;
    }
    
    /**
     * 为了向后兼容，提供接受String参数的构造函数
     * 注意：这个构造函数与上面的构造函数有相同的参数类型，需要特殊处理
     */
    public static EpubPathValidationException createForCompatibility(String message, String fileName, String filePath) {
        return new EpubPathValidationException(message, filePath, fileName, (Throwable)null);
    }
    
    /**
     * 为了向后兼容，提供接受String参数的构造函数（带cause）
     */
    public static EpubPathValidationException createForCompatibility(String message, String fileName, String filePath, Throwable cause) {
        return new EpubPathValidationException(message, filePath, fileName, cause);
    }
    
    public String getInvalidPath() {
        return invalidPath;
    }
    
    /**
     * 为了向后兼容，提供getFileName方法
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * 为了向后兼容，提供getFilePath方法
     */
    public String getFilePath() {
        return invalidPath;
    }
    
    /**
     * 为了向后兼容，提供getOperation方法
     */
    public String getOperation() {
        return "pathValidation";
    }
    
    /**
     * 为了向后兼容，提供getErrorCode方法
     */
    @Override
    public ErrorCode getErrorCode() {
        return (ErrorCode) errorCode;
    }
    

}