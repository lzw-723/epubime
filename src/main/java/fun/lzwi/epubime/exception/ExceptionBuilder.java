package fun.lzwi.epubime.exception;

/**
 * 统一异常构建器工具类
 * 用于消除异常类中的重复代码
 */
public class ExceptionBuilder {
    
    /**
     * 为异常类创建标准构造函数
     * 
     * @param exceptionClass 异常类
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param operation 操作类型
     * @param errorCode 错误码
     * @return 构建的异常对象
     */
    public static <T extends EpubParseException> T buildException(
            Class<T> exceptionClass, String message, String fileName, 
            String filePath, String operation, EpubParseException.ErrorCode errorCode) {
        try {
            EpubParseException.Builder builder = new EpubParseException.Builder()
                    .message(message)
                    .fileName(fileName)
                    .filePath(filePath)
                    .operation(operation)
                    .errorCode(errorCode);
            return exceptionClass.getConstructor(EpubParseException.Builder.class).newInstance(builder);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build exception", e);
        }
    }
    
    /**
     * 为异常类创建标准构造函数（带原因）
     * 
     * @param exceptionClass 异常类
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param operation 操作类型
     * @param errorCode 错误码
     * @param cause 异常原因
     * @return 构建的异常对象
     */
    public static <T extends EpubParseException> T buildException(
            Class<T> exceptionClass, String message, String fileName, 
            String filePath, String operation, EpubParseException.ErrorCode errorCode, 
            Throwable cause) {
        try {
            EpubParseException.Builder builder = new EpubParseException.Builder()
                    .message(message)
                    .fileName(fileName)
                    .filePath(filePath)
                    .operation(operation)
                    .errorCode(errorCode)
                    .cause(cause);
            return exceptionClass.getConstructor(EpubParseException.Builder.class).newInstance(builder);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build exception", e);
        }
    }
    
    /**
     * 创建带有上下文信息的异常构建器
     * 
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param operation 操作类型
     * @param errorCode 错误码
     * @return 配置好的构建器
     */
    public static EpubParseException.Builder createBuilder(
            String message, String fileName, String filePath, 
            String operation, EpubParseException.ErrorCode errorCode) {
        return new EpubParseException.Builder()
                .message(message)
                .fileName(fileName)
                .filePath(filePath)
                .operation(operation)
                .errorCode(errorCode);
    }
    
    /**
     * 创建带有恢复建议的异常构建器
     * 
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param operation 操作类型
     * @param errorCode 错误码
     * @param recoverySuggestion 恢复建议
     * @return 配置好的构建器
     */
    public static EpubParseException.Builder createBuilderWithSuggestion(
            String message, String fileName, String filePath, 
            String operation, EpubParseException.ErrorCode errorCode, 
            String recoverySuggestion) {
        return new EpubParseException.Builder()
                .message(message)
                .fileName(fileName)
                .filePath(filePath)
                .operation(operation)
                .errorCode(errorCode)
                .recoverySuggestion(recoverySuggestion);
    }
}