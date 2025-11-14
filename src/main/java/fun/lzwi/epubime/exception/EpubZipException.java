package fun.lzwi.epubime.exception;

/**
 * EPUB ZIP异常类
 * 当处理ZIP文件时发生错误时抛出此异常
 * 
 * 增强功能：
 * - 使用标准化的错误码
 * - 提供详细的错误上下文
 * - 支持异常构建器模式
 */
public class EpubZipException extends EpubParseException {
    
    /**
     * 构造函数，创建基本的ZIP异常
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     */
    public EpubZipException(String message, String fileName, String filePath) {
        super(new EpubParseException.Builder()
                .message(message)
                .fileName(fileName)
                .filePath(filePath)
                .operation("zipProcessing")
                .errorCode(ErrorCode.ZIP_INVALID));
    }

    /**
     * 构造函数，创建ZIP异常（带原因）
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param cause 异常原因
     */
    public EpubZipException(String message, String fileName, String filePath, Throwable cause) {
        super(new EpubParseException.Builder()
                .message(message)
                .fileName(fileName)
                .filePath(filePath)
                .operation("zipProcessing")
                .errorCode(ErrorCode.ZIP_INVALID)
                .cause(cause));
    }
    
    /**
     * 创建ZIP条目未找到异常
     */
    public static EpubZipException entryNotFound(String fileName, String filePath, String entryName) {
        return new EpubZipException(new EpubParseException.Builder()
                .message("ZIP entry not found: " + entryName)
                .fileName(fileName)
                .filePath(filePath)
                .operation("zipEntryAccess")
                .errorCode(ErrorCode.ZIP_ENTRY_NOT_FOUND)
                .addContext("entryName", entryName));
    }
    
    /**
     * 创建ZIP解压失败异常
     */
    public static EpubZipException decompressionFailed(String fileName, String filePath, String entryName, Throwable cause) {
        return new EpubZipException(new EpubParseException.Builder()
                .message("Failed to decompress ZIP entry: " + entryName)
                .fileName(fileName)
                .filePath(filePath)
                .operation("zipDecompression")
                .errorCode(ErrorCode.ZIP_DECOMPRESSION_FAILED)
                .addContext("entryName", entryName)
                .cause(cause));
    }
    
    /**
     * 创建ZIP格式无效异常
     */
    public static EpubZipException invalidFormat(String fileName, String filePath, Throwable cause) {
        return new EpubZipException(new EpubParseException.Builder()
                .message("Invalid ZIP file format")
                .fileName(fileName)
                .filePath(filePath)
                .operation("zipValidation")
                .errorCode(ErrorCode.ZIP_INVALID)
                .recoverySuggestion("请验证文件是否为有效的ZIP格式")
                .cause(cause));
    }
    
    /**
     * 私有构造函数，使用构建器模式
     */
    private EpubZipException(EpubParseException.Builder builder) {
        super(builder);
    }
}