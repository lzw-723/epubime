package fun.lzwi.epubime.exception;

import java.util.HashSet;
import java.util.Set;

/**
 * 解析选项配置类
 * 用于配置EPUB解析过程中的错误处理行为
 * 
 * 支持的功能：
 * - 错误恢复策略
 * - 降级处理选项
 * - 日志级别配置
 * - 容错模式设置
 */
public class ParseOptions {
    
    /**
     * 错误处理策略
     */
    public enum ErrorHandlingStrategy {
        /** 严格模式：遇到任何错误都抛出异常 */
        STRICT,
        /** 宽松模式：尝试恢复可恢复的错误 */
        LENIENT,
        /** 最佳努力模式：尽可能解析，忽略非致命错误 */
        BEST_EFFORT
    }
    
    /**
     * 日志级别
     */
    public enum LogLevel {
        /** 不记录日志 */
        NONE,
        /** 只记录错误 */
        ERROR,
        /** 记录错误和警告 */
        WARNING,
        /** 记录所有信息 */
        INFO,
        /** 记录详细信息（调试模式） */
        DEBUG
    }
    
    private ErrorHandlingStrategy errorHandlingStrategy = ErrorHandlingStrategy.STRICT;
    private LogLevel logLevel = LogLevel.ERROR;
    private boolean collectWarnings = false;
    private boolean continueOnMetadataError = false;
    private boolean continueOnNavigationError = false;
    private boolean continueOnResourceError = false;
    private boolean skipInvalidResources = false;
    private boolean useFallbackMetadata = true;
    private int maxWarnings = 100;
    private final Set<String> ignoredErrorPatterns = new HashSet<>();
    
    /**
     * 默认配置：严格模式
     */
    public static ParseOptions strict() {
        return new ParseOptions()
                .withErrorHandlingStrategy(ErrorHandlingStrategy.STRICT)
                .withLogLevel(LogLevel.ERROR);
    }
    
    /**
     * 宽松配置：尝试恢复错误
     */
    public static ParseOptions lenient() {
        return new ParseOptions()
                .withErrorHandlingStrategy(ErrorHandlingStrategy.LENIENT)
                .withLogLevel(LogLevel.WARNING)
                .withCollectWarnings(true)
                .withContinueOnMetadataError(true)
                .withContinueOnNavigationError(true)
                .withContinueOnResourceError(true)
                .withSkipInvalidResources(true)
                .withUseFallbackMetadata(true);
    }
    
    /**
     * 最佳努力配置：尽可能解析
     */
    public static ParseOptions bestEffort() {
        return new ParseOptions()
                .withErrorHandlingStrategy(ErrorHandlingStrategy.BEST_EFFORT)
                .withLogLevel(LogLevel.INFO)
                .withCollectWarnings(true)
                .withContinueOnMetadataError(true)
                .withContinueOnNavigationError(true)
                .withContinueOnResourceError(true)
                .withSkipInvalidResources(true)
                .withUseFallbackMetadata(true);
    }
    
    /**
     * 调试配置：详细日志
     */
    public static ParseOptions debug() {
        return new ParseOptions()
                .withErrorHandlingStrategy(ErrorHandlingStrategy.LENIENT)
                .withLogLevel(LogLevel.DEBUG)
                .withCollectWarnings(true);
    }
    
    /**
     * 设置错误处理策略
     */
    public ParseOptions withErrorHandlingStrategy(ErrorHandlingStrategy strategy) {
        this.errorHandlingStrategy = strategy;
        return this;
    }
    
    /**
     * 设置日志级别
     */
    public ParseOptions withLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }
    
    /**
     * 设置是否收集警告
     */
    public ParseOptions withCollectWarnings(boolean collectWarnings) {
        this.collectWarnings = collectWarnings;
        return this;
    }
    
    /**
     * 设置是否在元数据错误时继续
     */
    public ParseOptions withContinueOnMetadataError(boolean continueOnError) {
        this.continueOnMetadataError = continueOnError;
        return this;
    }
    
    /**
     * 设置是否在导航错误时继续
     */
    public ParseOptions withContinueOnNavigationError(boolean continueOnError) {
        this.continueOnNavigationError = continueOnError;
        return this;
    }
    
    /**
     * 设置是否在资源错误时继续
     */
    public ParseOptions withContinueOnResourceError(boolean continueOnError) {
        this.continueOnResourceError = continueOnError;
        return this;
    }
    
    /**
     * 设置是否跳过无效资源
     */
    public ParseOptions withSkipInvalidResources(boolean skipInvalidResources) {
        this.skipInvalidResources = skipInvalidResources;
        return this;
    }
    
    /**
     * 设置是否使用回退元数据
     */
    public ParseOptions withUseFallbackMetadata(boolean useFallbackMetadata) {
        this.useFallbackMetadata = useFallbackMetadata;
        return this;
    }
    
    /**
     * 设置最大警告数量
     */
    public ParseOptions withMaxWarnings(int maxWarnings) {
        this.maxWarnings = maxWarnings;
        return this;
    }
    
    /**
     * 添加忽略的错误模式
     */
    public ParseOptions withIgnoredErrorPattern(String pattern) {
        this.ignoredErrorPatterns.add(pattern);
        return this;
    }
    
    /**
     * 获取错误处理策略
     */
    public ErrorHandlingStrategy getErrorHandlingStrategy() {
        return errorHandlingStrategy;
    }
    
    /**
     * 获取日志级别
     */
    public LogLevel getLogLevel() {
        return logLevel;
    }
    
    /**
     * 是否收集警告
     */
    public boolean isCollectWarnings() {
        return collectWarnings;
    }
    
    /**
     * 是否在元数据错误时继续
     */
    public boolean isContinueOnMetadataError() {
        return continueOnMetadataError;
    }
    
    /**
     * 是否在导航错误时继续
     */
    public boolean isContinueOnNavigationError() {
        return continueOnNavigationError;
    }
    
    /**
     * 是否在资源错误时继续
     */
    public boolean isContinueOnResourceError() {
        return continueOnResourceError;
    }
    
    /**
     * 是否跳过无效资源
     */
    public boolean isSkipInvalidResources() {
        return skipInvalidResources;
    }
    
    /**
     * 是否使用回退元数据
     */
    public boolean isUseFallbackMetadata() {
        return useFallbackMetadata;
    }
    
    /**
     * 获取最大警告数量
     */
    public int getMaxWarnings() {
        return maxWarnings;
    }
    
    /**
     * 检查错误是否应该被忽略
     */
    public boolean shouldIgnoreError(String errorMessage) {
        return ignoredErrorPatterns.stream()
                .anyMatch(pattern -> errorMessage.contains(pattern));
    }
    
    /**
     * 检查给定的日志级别是否应该被记录
     */
    public boolean shouldLog(LogLevel level) {
        return logLevel.ordinal() >= level.ordinal();
    }
    
    /**
     * 根据异常决定是否应该继续解析
     */
    public boolean shouldContinueOnError(EpubParseException exception) {
        if (errorHandlingStrategy == ErrorHandlingStrategy.STRICT) {
            return false;
        }
        
        if (shouldIgnoreError(exception.getMessage())) {
            return true;
        }
        
        switch (exception.getErrorCode()) {
            case XML_PARSE_ERROR:
            case XML_INVALID_STRUCTURE:
            case XML_MISSING_REQUIRED_ELEMENT:
            case XML_INVALID_ATTRIBUTE:
                return isContinueOnMetadataError() || errorHandlingStrategy == ErrorHandlingStrategy.BEST_EFFORT;
                
            case RESOURCE_NOT_FOUND:
            case RESOURCE_LOAD_FAILED:
            case RESOURCE_INVALID_TYPE:
                return isContinueOnResourceError() || errorHandlingStrategy == ErrorHandlingStrategy.BEST_EFFORT;
                
            case EPUB_INVALID_NCX:
            case EPUB_INVALID_NAV:
                return isContinueOnNavigationError() || errorHandlingStrategy == ErrorHandlingStrategy.BEST_EFFORT;
                
            default:
                return errorHandlingStrategy == ErrorHandlingStrategy.BEST_EFFORT;
        }
    }
}