package fun.lzwi.epubime.exception;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 错误上下文收集器
 * 用于在EPUB解析过程中收集错误、警告和调试信息
 * 
 * 功能：
 * - 收集解析过程中的错误和警告
 * - 记录详细的错误上下文信息
 * - 提供错误统计和分析
 * - 支持线程安全的错误收集
 */
public class ErrorContext {
    
    /**
     * 错误级别
     */
    public enum ErrorLevel {
        DEBUG, INFO, WARNING, ERROR, FATAL
    }
    
    /**
     * 错误记录类
     */
    public static class ErrorRecord {
        private final LocalDateTime timestamp;
        private final ErrorLevel level;
        private final String message;
        private final String fileName;
        private final String filePath;
        private final String operation;
        private final EpubParseException.ErrorCode errorCode;
        private final Throwable exception;
        private final Map<String, Object> context;
        private final boolean recovered;
        
        @SuppressFBWarnings("EI_EXPOSE_REP2")
        public ErrorRecord(ErrorLevel level, String message, String fileName, String filePath, 
                          String operation, EpubParseException.ErrorCode errorCode, 
                          Throwable exception, Map<String, Object> context, boolean recovered) {
            this.timestamp = LocalDateTime.now();
            this.level = level;
            this.message = message;
            this.fileName = fileName;
            this.filePath = filePath;
            this.operation = operation;
            this.errorCode = errorCode;
            this.exception = exception; // SpotBugs警告：但我们需要保留原始异常引用
            this.context = context != null ? new ConcurrentHashMap<>(context) : new ConcurrentHashMap<>();
            this.recovered = recovered;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        public ErrorLevel getLevel() {
            return level;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public String getFilePath() {
            return filePath;
        }
        
        public String getOperation() {
            return operation;
        }
        
        public EpubParseException.ErrorCode getErrorCode() {
            return errorCode;
        }
        
        @SuppressFBWarnings("EI_EXPOSE_REP")
        public Throwable getException() {
            return exception; // 返回原始异常引用，这是有意的设计
        }
        
        @SuppressFBWarnings("EI_EXPOSE_REP")
        public Map<String, Object> getContext() {
            return new ConcurrentHashMap<>(context); // 返回副本以防止外部修改
        }
        
        public boolean isRecovered() {
            return recovered;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("] ");
            sb.append("[").append(level).append("] ");
            if (errorCode != null) {
                sb.append("[").append(errorCode).append("] ");
            }
            sb.append(message);
            
            if (fileName != null || filePath != null || operation != null) {
                sb.append(" [File: ").append(fileName != null ? fileName : "unknown")
                  .append(", Path: ").append(filePath != null ? filePath : "unknown")
                  .append(", Operation: ").append(operation != null ? operation : "unknown").append("]");
            }
            
            if (recovered) {
                sb.append(" [RECOVERED]");
            }
            
            return sb.toString();
        }
    }
    
    /**
     * 错误统计类
     */
    public static class ErrorStatistics {
        private final int debugCount;
        private final int infoCount;
        private final int warningCount;
        private final int errorCount;
        private final int fatalCount;
        private final int recoveredCount;
        
        public ErrorStatistics(int debugCount, int infoCount, int warningCount, int errorCount, int fatalCount, int recoveredCount) {
            this.debugCount = debugCount;
            this.infoCount = infoCount;
            this.warningCount = warningCount;
            this.errorCount = errorCount;
            this.fatalCount = fatalCount;
            this.recoveredCount = recoveredCount;
        }
        
        public int getDebugCount() { return debugCount; }
        public int getInfoCount() { return infoCount; }
        public int getWarningCount() { return warningCount; }
        public int getErrorCount() { return errorCount; }
        public int getFatalCount() { return fatalCount; }
        public int getRecoveredCount() { return recoveredCount; }
        
        public int getTotalCount() {
            return debugCount + infoCount + warningCount + errorCount + fatalCount;
        }
    }
    
    private final List<ErrorRecord> errors = new CopyOnWriteArrayList<>();
    private final Map<String, Object> globalContext = new ConcurrentHashMap<>();
    private final int maxErrors;
    private final ParseOptions.LogLevel logLevel;
    
    /**
     * 构造函数
     * @param maxErrors 最大错误数
     * @param logLevel 日志级别
     */
    public ErrorContext(int maxErrors, ParseOptions.LogLevel logLevel) {
        this.maxErrors = maxErrors > 0 ? maxErrors : 1000;
        this.logLevel = logLevel != null ? logLevel : ParseOptions.LogLevel.ERROR;
    }
    
    /**
     * 添加全局上下文
     */
    public void addGlobalContext(String key, Object value) {
        globalContext.put(key, value);
    }
    
    /**
     * 获取全局上下文
     */
    public Map<String, Object> getGlobalContext() {
        return new ConcurrentHashMap<>(globalContext);
    }
    
    /**
     * 记录调试信息
     */
    public void debug(String message, String fileName, String filePath, String operation, Map<String, Object> context) {
        if (shouldLog(ParseOptions.LogLevel.DEBUG)) {
            addRecord(ErrorLevel.DEBUG, message, fileName, filePath, operation, null, null, context, false);
        }
    }
    
    /**
     * 记录信息
     */
    public void info(String message, String fileName, String filePath, String operation, Map<String, Object> context) {
        if (shouldLog(ParseOptions.LogLevel.INFO)) {
            addRecord(ErrorLevel.INFO, message, fileName, filePath, operation, null, null, context, false);
        }
    }
    
    /**
     * 记录警告
     */
    public void warning(String message, String fileName, String filePath, String operation, 
                       EpubParseException.ErrorCode errorCode, Map<String, Object> context) {
        if (shouldLog(ParseOptions.LogLevel.WARNING)) {
            addRecord(ErrorLevel.WARNING, message, fileName, filePath, operation, errorCode, null, context, false);
        }
    }
    
    /**
     * 添加错误
     */
    public void error(String message, String fileName, String filePath, String operation, 
                     EpubParseException.ErrorCode errorCode, Throwable exception, Map<String, Object> context) {
        if (shouldLog(ParseOptions.LogLevel.ERROR)) {
            addRecord(ErrorLevel.ERROR, message, fileName, filePath, operation, errorCode, exception, context, false);
        }
    }
    
    /**
     * 添加致命错误
     */
    public void fatal(String message, String fileName, String filePath, String operation, 
                     EpubParseException.ErrorCode errorCode, Throwable exception, Map<String, Object> context) {
        if (shouldLog(ParseOptions.LogLevel.ERROR)) {
            addRecord(ErrorLevel.FATAL, message, fileName, filePath, operation, errorCode, exception, context, false);
        }
    }
    
    /**
     * 记录已恢复的错误
     */
    public void recovered(String message, String fileName, String filePath, String operation, 
                         EpubParseException.ErrorCode errorCode, Map<String, Object> context) {
        if (shouldLog(ParseOptions.LogLevel.INFO)) {
            addRecord(ErrorLevel.WARNING, message, fileName, filePath, operation, errorCode, null, context, true);
        }
    }
    
    /**
     * 添加异常记录
     */
    public void exception(EpubParseException exception, boolean recovered) {
        ErrorLevel level = recovered ? ErrorLevel.WARNING : ErrorLevel.ERROR;
        
        if (shouldLog(ParseOptions.LogLevel.ERROR)) {
            addRecord(level, exception.getMessage(), exception.getFileName(), 
                     exception.getFilePath(), exception.getOperation(), 
                     exception.getErrorCode(), exception, exception.getAllContext(), recovered);
        }
    }
    
    /**
     * 添加记录
     */
    private void addRecord(ErrorLevel level, String message, String fileName, String filePath, 
                          String operation, EpubParseException.ErrorCode errorCode, Throwable exception, 
                          Map<String, Object> context, boolean recovered) {
        
        if (errors.size() >= maxErrors) {
            // 如果达到最大错误数，移除最旧的非致命错误
            if (level != ErrorLevel.FATAL) {
                errors.removeIf(record -> record.getLevel() != ErrorLevel.FATAL);
            }
            
            if (errors.size() >= maxErrors) {
                // 如果仍然达到最大错误数，不再添加
                return;
            }
        }
        
        ErrorRecord record = new ErrorRecord(level, message, fileName, filePath, operation, 
                                           errorCode, exception, context, recovered);
        errors.add(record);
        
        // 控制台输出（简单的日志实现）- 只打印警告和错误级别
        if (level == ErrorLevel.WARNING || level == ErrorLevel.ERROR || level == ErrorLevel.FATAL) {
            System.out.println(record.toString());
        }
    }
    
    /**
     * 是否应该记录日志
     */
    private boolean shouldLog(ParseOptions.LogLevel level) {
        return logLevel.ordinal() >= level.ordinal();
    }

    /**
     * 将ErrorLevel映射到LogLevel用于日志检查
     */
    private ParseOptions.LogLevel mapToLogLevel(ErrorLevel level) {
        switch (level) {
            case DEBUG: return ParseOptions.LogLevel.DEBUG;
            case INFO: return ParseOptions.LogLevel.INFO;
            case WARNING: return ParseOptions.LogLevel.WARNING;
            case ERROR:
            case FATAL: return ParseOptions.LogLevel.ERROR;
            default: return ParseOptions.LogLevel.ERROR;
        }
    }
    
    /**
     * 获取所有错误记录
     */
    public List<ErrorRecord> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * 获取指定级别的错误记录
     */
    public List<ErrorRecord> getErrors(ErrorLevel level) {
        List<ErrorRecord> result = new ArrayList<>();
        for (ErrorRecord record : errors) {
            if (record.getLevel() == level) {
                result.add(record);
            }
        }
        return result;
    }
    
    /**
     * 获取错误（不包括警告和信息）
     */
    public List<ErrorRecord> getErrorRecords() {
        List<ErrorRecord> result = new ArrayList<>();
        for (ErrorRecord record : errors) {
            if (record.getLevel() == ErrorLevel.ERROR || record.getLevel() == ErrorLevel.FATAL) {
                result.add(record);
            }
        }
        return result;
    }
    
    /**
     * 获取警告
     */
    public List<ErrorRecord> getWarnings() {
        return getErrors(ErrorLevel.WARNING);
    }
    
    /**
     * 获取致命错误
     */
    public List<ErrorRecord> getFatalErrors() {
        return getErrors(ErrorLevel.FATAL);
    }
    
    /**
     * 获取已恢复的错误
     */
    public List<ErrorRecord> getRecoveredErrors() {
        List<ErrorRecord> result = new ArrayList<>();
        for (ErrorRecord record : errors) {
            if (record.isRecovered()) {
                result.add(record);
            }
        }
        return result;
    }
    
    /**
     * 是否有错误
     */
    public boolean hasErrors() {
        for (ErrorRecord record : errors) {
            if (record.getLevel() == ErrorLevel.ERROR || record.getLevel() == ErrorLevel.FATAL) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 是否有警告
     */
    public boolean hasWarnings() {
        for (ErrorRecord record : errors) {
            if (record.getLevel() == ErrorLevel.WARNING) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 是否有致命错误
     */
    public boolean hasFatalErrors() {
        for (ErrorRecord record : errors) {
            if (record.getLevel() == ErrorLevel.FATAL) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取错误统计
     */
    public ErrorStatistics getStatistics() {
        int debugCount = 0;
        int infoCount = 0;
        int warningCount = 0;
        int errorCount = 0;
        int fatalCount = 0;
        int recoveredCount = 0;
        
        for (ErrorRecord record : errors) {
            switch (record.getLevel()) {
                case DEBUG: debugCount++; break;
                case INFO: infoCount++; break;
                case WARNING: warningCount++; break;
                case ERROR: errorCount++; break;
                case FATAL: fatalCount++; break;
            }
            if (record.isRecovered()) {
                recoveredCount++;
            }
        }
        
        return new ErrorStatistics(debugCount, infoCount, warningCount, errorCount, fatalCount, recoveredCount);
    }
    
    /**
     * 生成错误报告
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("EPUB解析错误报告\n");
        report.append("================\n\n");
        
        // 错误统计
        ErrorStatistics stats = getStatistics();
        report.append("错误统计:\n");
        report.append("- 调试: ").append(stats.getDebugCount()).append("\n");
        report.append("- 信息: ").append(stats.getInfoCount()).append("\n");
        report.append("- 警告: ").append(stats.getWarningCount()).append("\n");
        report.append("- 错误: ").append(stats.getErrorCount()).append("\n");
        report.append("- 致命: ").append(stats.getFatalCount()).append("\n");
        report.append("- 已恢复: ").append(stats.getRecoveredCount()).append("\n");
        report.append("- 总计: ").append(stats.getTotalCount()).append("\n\n");
        
        // 全局上下文
        if (!globalContext.isEmpty()) {
            report.append("全局上下文:\n");
            for (Map.Entry<String, Object> entry : globalContext.entrySet()) {
                report.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            report.append("\n");
        }
        
        // 详细错误列表
        if (!errors.isEmpty()) {
            report.append("详细错误列表:\n");
            for (ErrorRecord record : errors) {
                report.append("- ").append(record.toString()).append("\n");
            }
        } else {
            report.append("没有记录到错误。\n");
        }
        
        return report.toString();
    }
}