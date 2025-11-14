package fun.lzwi.epubime.exception;

/**
 * EPUB资源异常类
 * 当加载或处理EPUB资源时发生错误时抛出此异常
 * 
 * 增强功能：
 * - 使用标准化的错误码
 * - 提供详细的资源错误信息
 * - 支持异常构建器模式
 */
public class EpubResourceException extends EpubParseException {
    
    private final String resourceId;
    private final String resourceType;
    
    /**
     * 构造函数，创建基本的资源异常
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     */
    public EpubResourceException(String message, String fileName, String filePath) {
        super(new EpubParseException.Builder()
                .message(message)
                .fileName(fileName)
                .filePath(filePath)
                .operation("resourceLoading")
                .errorCode(ErrorCode.RESOURCE_LOAD_FAILED));
        this.resourceId = null;
        this.resourceType = null;
    }

    /**
     * 构造函数，创建资源异常（带原因）
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param cause 异常原因
     */
    public EpubResourceException(String message, String fileName, String filePath, Throwable cause) {
        super(new EpubParseException.Builder()
                .message(message)
                .fileName(fileName)
                .filePath(filePath)
                .operation("resourceLoading")
                .errorCode(ErrorCode.RESOURCE_LOAD_FAILED)
                .cause(cause));
        this.resourceId = null;
        this.resourceType = null;
    }
    
    /**
     * 创建资源未找到异常
     */
    public static EpubResourceException resourceNotFound(String fileName, String filePath, String resourceId, String resourceType) {
        EpubParseException.Builder builder = new EpubParseException.Builder()
                .message("Resource not found: " + resourceId)
                .fileName(fileName)
                .filePath(filePath)
                .operation("resourceAccess")
                .errorCode(ErrorCode.RESOURCE_NOT_FOUND)
                .addContext("resourceId", resourceId)
                .addContext("resourceType", resourceType)
                .recoverySuggestion("请检查资源ID是否正确，资源是否存在于EPUB文件中");
        return new EpubResourceException(builder);
    }
    
    /**
     * 创建资源加载失败异常
     */
    public static EpubResourceException loadFailed(String fileName, String filePath, String resourceId, String resourceType, Throwable cause) {
        EpubParseException.Builder builder = new EpubParseException.Builder()
                .message("Failed to load resource: " + resourceId)
                .fileName(fileName)
                .filePath(filePath)
                .operation("resourceLoading")
                .errorCode(ErrorCode.RESOURCE_LOAD_FAILED)
                .addContext("resourceId", resourceId)
                .addContext("resourceType", resourceType)
                .recoverySuggestion("请检查资源文件是否存在且格式正确")
                .cause(cause);
        return new EpubResourceException(builder);
    }
    
    /**
     * 创建无效资源类型异常
     */
    public static EpubResourceException invalidType(String fileName, String filePath, String resourceId, String resourceType, String expectedType) {
        EpubParseException.Builder builder = new EpubParseException.Builder()
                .message("Invalid resource type: " + resourceType + " (expected: " + expectedType + ")")
                .fileName(fileName)
                .filePath(filePath)
                .operation("resourceValidation")
                .errorCode(ErrorCode.RESOURCE_INVALID_TYPE)
                .addContext("resourceId", resourceId)
                .addContext("resourceType", resourceType)
                .addContext("expectedType", expectedType)
                .recoverySuggestion("请检查资源的媒体类型是否正确");
        return new EpubResourceException(builder);
    }
    
    /**
     * 创建回退资源异常
     */
    public static EpubResourceException fallbackFailed(String fileName, String filePath, String resourceId, String fallbackId, Throwable cause) {
        EpubParseException.Builder builder = new EpubParseException.Builder()
                .message("Failed to load fallback resource: " + resourceId + " -> " + fallbackId)
                .fileName(fileName)
                .filePath(filePath)
                .operation("resourceFallback")
                .errorCode(ErrorCode.RESOURCE_LOAD_FAILED)
                .addContext("resourceId", resourceId)
                .addContext("fallbackId", fallbackId)
                .recoverySuggestion("请检查主资源和回退资源是否存在")
                .cause(cause);
        return new EpubResourceException(builder);
    }
    
    /**
     * 私有构造函数，使用构建器模式
     */
    private EpubResourceException(EpubParseException.Builder builder) {
        super(builder);
        this.resourceId = (String) builder.getContext("resourceId");
        this.resourceType = (String) builder.getContext("resourceType");
    }
    
    /**
     * 获取资源ID
     */
    public String getResourceId() {
        return resourceId;
    }
    
    /**
     * 获取资源类型
     */
    public String getResourceType() {
        return resourceType;
    }
}