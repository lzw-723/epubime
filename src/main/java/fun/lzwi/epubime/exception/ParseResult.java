package fun.lzwi.epubime.exception;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import fun.lzwi.epubime.epub.EpubBook;

import java.util.List;

/**
 * 解析结果类
 * 封装EPUB解析的结果，包括解析的书籍对象和错误信息
 * 
 * 功能：
 * - 包含解析成功的EpubBook对象
 * - 包含解析过程中的错误和警告信息
 * - 提供解析状态信息
 * - 支持部分解析结果
 */
public class ParseResult {
    
    /**
     * 解析状态
     */
    public enum ParseStatus {
        /** 完全成功 */
        SUCCESS,
        /** 部分成功（有警告） */
        PARTIAL_SUCCESS,
        /** 部分成功（有错误但被恢复） */
        RECOVERED,
        /** 失败 */
        FAILURE
    }
    
    private final EpubBook epubBook;
    private final ErrorContext errorContext;
    private final ParseStatus status;
    private final boolean hasCriticalErrors;
    private final long parseTimeMs;
    private final String parseSummary;
    
    /**
     * 私有构造函数，使用构建器模式
     */
    private ParseResult(Builder builder) {
        this.epubBook = builder.epubBook;
        this.errorContext = builder.errorContext;
        this.parseTimeMs = builder.parseTimeMs;
        this.hasCriticalErrors = builder.hasCriticalErrors;
        
        // 确定解析状态
        if (builder.hasCriticalErrors || (builder.epubBook == null && builder.errorContext.hasErrors())) {
            this.status = ParseStatus.FAILURE;
        } else if (builder.errorContext.hasFatalErrors()) {
            this.status = ParseStatus.FAILURE;
        } else if (builder.errorContext.hasErrors()) {
            this.status = ParseStatus.RECOVERED;
        } else if (builder.errorContext.hasWarnings()) {
            this.status = ParseStatus.PARTIAL_SUCCESS;
        } else {
            this.status = ParseStatus.SUCCESS;
        }
        
        this.parseSummary = generateSummary();
    }
    
    /**
     * 生成解析摘要
     */
    private String generateSummary() {
        if (errorContext == null) {
            return "解析完成：成功";
        }
        
        ErrorContext.ErrorStatistics stats = errorContext.getStatistics();
        StringBuilder summary = new StringBuilder();
        summary.append("解析完成：").append(status).append("\n");
        summary.append("耗时：").append(parseTimeMs).append("ms\n");
        
        if (stats.getTotalCount() > 0) {
            summary.append("错误统计：\n");
            summary.append("- 警告：").append(stats.getWarningCount()).append("\n");
            summary.append("- 错误：").append(stats.getErrorCount()).append("\n");
            summary.append("- 致命：").append(stats.getFatalCount()).append("\n");
            summary.append("- 已恢复：").append(stats.getRecoveredCount()).append("\n");
        }
        
        if (epubBook != null) {
            summary.append("解析结果：成功解析EPUB文件\n");
            if (epubBook.getMetadata() != null) {
                summary.append("- 标题：").append(epubBook.getMetadata().getTitle()).append("\n");
                summary.append("- 作者：").append(epubBook.getMetadata().getCreator()).append("\n");
            }
        } else {
            summary.append("解析结果：未能解析EPUB文件\n");
        }
        
        return summary.toString();
    }
    
    /**
     * 获取解析的EPUB书籍
     * @return EpubBook对象，可能为null（如果解析完全失败）
     */
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public EpubBook getEpubBook() {
        return epubBook; // 返回原始引用，这是有意的设计
    }
    
    /**
     * 获取错误上下文
     * @return 错误上下文，包含所有错误、警告和调试信息
     */
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public ErrorContext getErrorContext() {
        return errorContext; // 返回原始引用，这是有意的设计
    }
    
    /**
     * 获取解析状态
     * @return 解析状态
     */
    public ParseStatus getStatus() {
        return status;
    }
    
    /**
     * 获取解析时间（毫秒）
     * @return 解析时间
     */
    public long getParseTimeMs() {
        return parseTimeMs;
    }
    
    /**
     * 获取解析摘要
     * @return 解析摘要
     */
    public String getParseSummary() {
        return parseSummary;
    }
    
    /**
     * 是否成功
     * @return true如果解析完全成功
     */
    public boolean isSuccess() {
        return status == ParseStatus.SUCCESS;
    }
    
    /**
     * 是否部分成功
     * @return true如果解析部分成功（有警告或已恢复的错误）
     */
    public boolean isPartialSuccess() {
        return status == ParseStatus.PARTIAL_SUCCESS || status == ParseStatus.RECOVERED;
    }
    
    /**
     * 是否失败
     * @return true如果解析失败
     */
    public boolean isFailure() {
        return status == ParseStatus.FAILURE;
    }
    
    /**
     * 是否有严重错误
     * @return true如果有严重错误
     */
    public boolean hasCriticalErrors() {
        return hasCriticalErrors || hasFatalErrors();
    }
    
    /**
     * 是否有致命错误
     * @return true如果有致命错误
     */
    public boolean hasFatalErrors() {
        return errorContext != null && errorContext.hasFatalErrors();
    }
    
    /**
     * 获取所有错误
     * @return 错误列表
     */
    public List<ErrorContext.ErrorRecord> getAllErrors() {
        return errorContext != null ? errorContext.getErrors() : new java.util.ArrayList<>();
    }
    
    /**
     * 获取所有警告
     * @return 警告列表
     */
    public List<ErrorContext.ErrorRecord> getAllWarnings() {
        return errorContext != null ? errorContext.getWarnings() : new java.util.ArrayList<>();
    }
    
    /**
     * 获取错误统计
     * @return 错误统计
     */
    public ErrorContext.ErrorStatistics getErrorStatistics() {
        return errorContext != null ? errorContext.getStatistics() : new ErrorContext.ErrorStatistics(0, 0, 0, 0, 0, 0);
    }
    
    /**
     * 创建成功的解析结果
     */
    public static ParseResult success(EpubBook epubBook, long parseTimeMs) {
        return new Builder()
                .epubBook(epubBook)
                .errorContext(new ErrorContext(0, ParseOptions.LogLevel.NONE))
                .parseTimeMs(parseTimeMs)
                .hasCriticalErrors(false)
                .build();
    }
    
    /**
     * 创建失败的解析结果
     */
    public static ParseResult failure(ErrorContext errorContext, long parseTimeMs) {
        return new Builder()
                .epubBook(null)
                .errorContext(errorContext)
                .parseTimeMs(parseTimeMs)
                .hasCriticalErrors(true)
                .build();
    }
    
    /**
     * 创建部分成功的解析结果
     */
    public static ParseResult partialSuccess(EpubBook epubBook, ErrorContext errorContext, long parseTimeMs) {
        return new Builder()
                .epubBook(epubBook)
                .errorContext(errorContext)
                .parseTimeMs(parseTimeMs)
                .hasCriticalErrors(false)
                .build();
    }
    
    /**
     * 构建器类
     */
    public static class Builder {
        private EpubBook epubBook;
        private ErrorContext errorContext;
        private long parseTimeMs = 0;
        private boolean hasCriticalErrors = false;
        
        @SuppressFBWarnings("EI_EXPOSE_REP2")
        public Builder epubBook(EpubBook epubBook) {
            this.epubBook = epubBook; // SpotBugs警告：但我们需要保留原始引用
            return this;
        }
        
        @SuppressFBWarnings("EI_EXPOSE_REP2")
        public Builder errorContext(ErrorContext errorContext) {
            this.errorContext = errorContext; // SpotBugs警告：但我们需要保留原始引用
            return this;
        }
        
        public Builder parseTimeMs(long parseTimeMs) {
            this.parseTimeMs = parseTimeMs;
            return this;
        }
        
        public Builder hasCriticalErrors(boolean hasCriticalErrors) {
            this.hasCriticalErrors = hasCriticalErrors;
            return this;
        }
        
        public ParseResult build() {
            if (errorContext == null) {
                errorContext = new ErrorContext(0, ParseOptions.LogLevel.NONE);
            }
            return new ParseResult(this);
        }
    }
}