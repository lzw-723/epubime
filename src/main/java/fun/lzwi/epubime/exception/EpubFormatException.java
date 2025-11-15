package fun.lzwi.epubime.exception;

/**
 * EPUB格式异常类
 * 当EPUB文件格式不符合规范时抛出此异常
 * 
 * 增强功能：
 * - 使用标准化的错误码
 * - 提供详细的格式错误信息
 * - 支持异常构建器模式
 * - 重构后消除重复代码
 */
public class EpubFormatException extends EpubParseException {
    
    /**
     * 构造函数，创建基本的格式异常
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     */
    public EpubFormatException(String message, String fileName, String filePath) {
        super(ExceptionBuilder.createBuilder(message, fileName, filePath, 
                "formatValidation", ErrorCode.EPUB_INVALID_CONTAINER));
    }

    /**
     * 构造函数，创建格式异常（带原因）
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param cause 异常原因
     */
    public EpubFormatException(String message, String fileName, String filePath, Throwable cause) {
        super(ExceptionBuilder.createBuilder(message, fileName, filePath, 
                "formatValidation", ErrorCode.EPUB_INVALID_CONTAINER)
                .cause(cause));
    }
    
    /**
     * 创建mimetype缺失异常
     */
    public static EpubFormatException missingMimetype(String fileName, String filePath) {
        return new EpubFormatException(
            ExceptionBuilder.createBuilderWithSuggestion(
                "Missing required mimetype file", fileName, filePath,
                "mimetypeValidation", ErrorCode.EPUB_MISSING_MIMETYPE,
                "请确保EPUB文件包含mimetype文件"
            )
        );
    }
    
    /**
     * 创建container.xml无效异常
     */
    public static EpubFormatException invalidContainer(String fileName, String filePath, String details) {
        return new EpubFormatException(
            ExceptionBuilder.createBuilderWithSuggestion(
                "Invalid container.xml: " + details, fileName, filePath,
                "containerValidation", ErrorCode.EPUB_INVALID_CONTAINER,
                "请检查META-INF/container.xml文件格式"
            ).addContext("details", details)
        );
    }
    
    /**
     * 创建OPF文件无效异常
     */
    public static EpubFormatException invalidOpf(String fileName, String filePath, String opfPath, String details) {
        return new EpubFormatException(
            ExceptionBuilder.createBuilderWithSuggestion(
                "Invalid OPF file: " + details, fileName, filePath,
                "opfValidation", ErrorCode.EPUB_INVALID_OPF,
                "请检查OPF文件的XML格式和结构"
            )
            .addContext("opfPath", opfPath)
            .addContext("details", details)
        );
    }
    
    /**
     * 创建NCX文件无效异常
     */
    public static EpubFormatException invalidNcx(String fileName, String filePath, String ncxPath, String details) {
        return new EpubFormatException(
            ExceptionBuilder.createBuilderWithSuggestion(
                "Invalid NCX file: " + details, fileName, filePath,
                "ncxValidation", ErrorCode.EPUB_INVALID_NCX,
                "请检查NCX文件的XML格式和结构"
            )
            .addContext("ncxPath", ncxPath)
            .addContext("details", details)
        );
    }
    
    /**
     * 创建NAV文件无效异常
     */
    public static EpubFormatException invalidNav(String fileName, String filePath, String navPath, String details) {
        return new EpubFormatException(
            ExceptionBuilder.createBuilderWithSuggestion(
                "Invalid NAV file: " + details, fileName, filePath,
                "navValidation", ErrorCode.EPUB_INVALID_NAV,
                "请检查NAV文件的XML格式和结构"
            )
            .addContext("navPath", navPath)
            .addContext("details", details)
        );
    }
    
    /**
     * 私有构造函数，使用构建器模式
     */
    private EpubFormatException(EpubParseException.Builder builder) {
        super(builder);
    }
}