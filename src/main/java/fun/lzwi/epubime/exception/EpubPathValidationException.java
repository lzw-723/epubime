package fun.lzwi.epubime.exception;

/**
 * EPUB路径验证异常类
 * 当EPUB文件路径验证失败时抛出此异常
 * 
 * 增强功能：
 * - 使用标准化的错误码
 * - 提供详细的路径错误信息
 * - 支持异常构建器模式
 * - 重构后消除重复代码
 */
public class EpubPathValidationException extends EpubParseException {
    
    private final String invalidPath;
    private final String validationRule;
    
    /**
     * 构造函数，创建基本的路径验证异常
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     */
    public EpubPathValidationException(String message, String fileName, String filePath) {
        super(ExceptionBuilder.createBuilder(message, fileName, filePath, 
                "pathValidation", ErrorCode.PATH_INVALID_CHARACTER));
        this.invalidPath = filePath;
        this.validationRule = null;
    }

    /**
     * 构造函数，创建路径验证异常（带原因）
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param cause 异常原因
     */
    public EpubPathValidationException(String message, String fileName, String filePath, Throwable cause) {
        super(ExceptionBuilder.createBuilder(message, fileName, filePath, 
                "pathValidation", ErrorCode.PATH_INVALID_CHARACTER)
                .cause(cause));
        this.invalidPath = filePath;
        this.validationRule = null;
    }
    
    /**
     * 创建路径遍历攻击异常
     */
    public static EpubPathValidationException pathTraversalAttack(String fileName, String filePath, String invalidPath) {
        return new EpubPathValidationException(
            ExceptionBuilder.createBuilderWithSuggestion(
                "Path traversal attack detected: " + invalidPath, fileName, filePath,
                "pathSecurityValidation", ErrorCode.PATH_TRAVERSAL_ATTACK,
                "请检查文件路径是否安全，避免路径遍历攻击"
            ).addContext("invalidPath", invalidPath)
        );
    }
    
    /**
     * 创建无效字符异常
     */
    public static EpubPathValidationException invalidCharacter(String fileName, String filePath, String invalidPath, char invalidChar) {
        return new EpubPathValidationException(
            ExceptionBuilder.createBuilderWithSuggestion(
                "Invalid character in path: '" + invalidChar + "'", fileName, filePath,
                "pathCharacterValidation", ErrorCode.PATH_INVALID_CHARACTER,
                "请检查文件路径中的特殊字符"
            )
            .addContext("invalidPath", invalidPath)
            .addContext("invalidChar", String.valueOf(invalidChar))
            .addContext("invalidCharCode", (int) invalidChar)
        );
    }
    
    /**
     * 创建路径过长异常
     */
    public static EpubPathValidationException pathTooLong(String fileName, String filePath, String invalidPath, int maxLength) {
        return new EpubPathValidationException(
            ExceptionBuilder.createBuilderWithSuggestion(
                "Path too long: " + invalidPath.length() + " characters (max: " + maxLength + ")", 
                fileName, filePath, "pathLengthValidation", ErrorCode.PATH_TOO_LONG,
                "请缩短文件路径长度"
            )
            .addContext("invalidPath", invalidPath)
            .addContext("pathLength", invalidPath.length())
            .addContext("maxLength", maxLength)
        );
    }
    
    /**
     * 创建相对路径异常
     */
    public static EpubPathValidationException relativePathNotAllowed(String fileName, String filePath, String invalidPath) {
        return new EpubPathValidationException(
            ExceptionBuilder.createBuilderWithSuggestion(
                "Relative path not allowed: " + invalidPath, fileName, filePath,
                "pathTypeValidation", ErrorCode.PATH_INVALID_CHARACTER,
                "请使用绝对路径"
            ).addContext("invalidPath", invalidPath)
        );
    }
    
    /**
     * 私有构造函数，使用构建器模式
     */
    private EpubPathValidationException(EpubParseException.Builder builder) {
        super(builder);
        this.invalidPath = builder.getFilePath();
        this.validationRule = builder.getOperation();
    }
    
    /**
     * 获取无效路径
     */
    public String getInvalidPath() {
        return invalidPath;
    }
    
    /**
     * 获取验证规则
     */
    public String getValidationRule() {
        return validationRule;
    }
}