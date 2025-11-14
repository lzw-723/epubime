package fun.lzwi.epubime.exception;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashMap;
import java.util.Map;

/**
 * EPUB解析异常类
 * 当EPUB文件解析过程中发生错误时抛出此异常
 * 
 * 增强功能：
 * - 支持错误码系统
 * - 包含详细的上下文信息
 * - 支持异常链和根因分析
 * - 提供调试信息和恢复建议
 */
public class EpubParseException extends Exception {
    
    /**
     * 错误码枚举
     */
    public enum ErrorCode {
        // 文件相关错误 1000-1999
        FILE_NOT_FOUND(1001, "File not found"),
        FILE_ACCESS_DENIED(1002, "File access denied"),
        FILE_CORRUPTED(1003, "File appears to be corrupted"),
        INVALID_FILE_FORMAT(1004, "Invalid file format"),
        
        // ZIP相关错误 2000-2999
        ZIP_INVALID(2001, "Invalid ZIP file format"),
        ZIP_ENTRY_NOT_FOUND(2002, "ZIP entry not found"),
        ZIP_DECOMPRESSION_FAILED(2003, "ZIP decompression failed"),
        
        // XML解析错误 3000-3999
        XML_PARSE_ERROR(3001, "XML parsing failed"),
        XML_INVALID_STRUCTURE(3002, "Invalid XML structure"),
        XML_MISSING_REQUIRED_ELEMENT(3003, "Missing required XML element"),
        XML_INVALID_ATTRIBUTE(3004, "Invalid XML attribute"),
        
        // EPUB格式错误 4000-4999
        EPUB_INVALID_CONTAINER(4001, "Invalid EPUB container"),
        EPUB_MISSING_MIMETYPE(4002, "Missing mimetype file"),
        EPUB_INVALID_OPF(4003, "Invalid OPF file"),
        EPUB_INVALID_NCX(4004, "Invalid NCX file"),
        EPUB_INVALID_NAV(4005, "Invalid NAV file"),
        
        // 路径验证错误 5000-5999
        PATH_TRAVERSAL_ATTACK(5001, "Path traversal attack detected"),
        PATH_INVALID_CHARACTER(5002, "Invalid character in path"),
        PATH_TOO_LONG(5003, "Path too long"),
        
        // 资源相关错误 6000-6999
        RESOURCE_NOT_FOUND(6001, "Resource not found"),
        RESOURCE_LOAD_FAILED(6002, "Resource loading failed"),
        RESOURCE_INVALID_TYPE(6003, "Invalid resource type"),
        
        // 通用错误 9000-9999
        UNKNOWN_ERROR(9001, "Unknown error occurred"),
        OPERATION_NOT_SUPPORTED(9002, "Operation not supported"),
        INTERNAL_ERROR(9003, "Internal error");
        
        private final int code;
        private final String description;
        
        ErrorCode(int code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public int getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        @Override
        public String toString() {
            return String.format("%04d: %s", code, description);
        }
    }
    
    private final String fileName;
    private final String filePath;
    private final String operation;
    private final ErrorCode errorCode;
    private final Map<String, Object> context;
    private final String recoverySuggestion;
    private final int lineNumber;
    private final int columnNumber;
    
    /**
     * 构造函数，创建带有指定消息的EPUB解析异常
     * @param message 异常消息
     */
    public EpubParseException(String message) {
        super(formatSimpleMessage(message, ErrorCode.UNKNOWN_ERROR));
        this.fileName = null;
        this.filePath = null;
        this.operation = null;
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
        this.context = new HashMap<>();
        this.recoverySuggestion = generateRecoverySuggestion(ErrorCode.UNKNOWN_ERROR);
        this.lineNumber = -1;
        this.columnNumber = -1;
    }
    
    /**
     * 构造函数，创建带有指定消息和错误码的EPUB解析异常
     * @param message 异常消息
     * @param errorCode 错误码
     */
    public EpubParseException(String message, ErrorCode errorCode) {
        super(formatSimpleMessage(message, errorCode));
        this.fileName = null;
        this.filePath = null;
        this.operation = null;
        this.errorCode = errorCode;
        this.context = new HashMap<>();
        this.recoverySuggestion = generateRecoverySuggestion(errorCode);
        this.lineNumber = -1;
        this.columnNumber = -1;
    }

    /**
     * 构造函数，创建带有指定消息和原因的EPUB解析异常
     * @param message 异常消息
     * @param cause 异常原因
     */
    public EpubParseException(String message, Throwable cause) {
        this(message, ErrorCode.UNKNOWN_ERROR, cause);
    }
    
    /**
     * 构造函数，创建带有指定消息、错误码和原因的EPUB解析异常
     * @param message 异常消息
     * @param errorCode 错误码
     * @param cause 异常原因
     */
    public EpubParseException(String message, ErrorCode errorCode, Throwable cause) {
        super(formatSimpleMessage(message, errorCode), cause);
        this.fileName = null;
        this.filePath = null;
        this.operation = null;
        this.errorCode = errorCode;
        this.context = new HashMap<>();
        this.recoverySuggestion = generateRecoverySuggestion(errorCode);
        this.lineNumber = -1;
        this.columnNumber = -1;
    }

    /**
     * 构造函数，创建带有详细错误信息的EPUB解析异常
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param operation 操作类型
     * @param cause 异常原因
     */
    public EpubParseException(String message, String fileName, String filePath, String operation, Throwable cause) {
        this(message, fileName, filePath, operation, ErrorCode.UNKNOWN_ERROR, cause);
    }
    
    /**
     * 构造函数，创建带有详细错误信息和错误码的EPUB解析异常
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param operation 操作类型
     * @param errorCode 错误码
     * @param cause 异常原因
     */
    public EpubParseException(String message, String fileName, String filePath, String operation, ErrorCode errorCode, Throwable cause) {
        super(formatMessage(message, fileName, filePath, operation, errorCode), cause);
        this.fileName = fileName;
        this.filePath = filePath;
        this.operation = operation;
        this.errorCode = errorCode;
        this.context = new HashMap<>();
        this.recoverySuggestion = generateRecoverySuggestion(errorCode);
        this.lineNumber = -1;
        this.columnNumber = -1;
    }

    /**
     * 构造函数，创建带有详细错误信息的EPUB解析异常（无原因）
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param operation 操作类型
     */
    public EpubParseException(String message, String fileName, String filePath, String operation) {
        this(message, fileName, filePath, operation, ErrorCode.UNKNOWN_ERROR);
    }
    
    /**
     * 构造函数，创建带有详细错误信息和错误码的EPUB解析异常（无原因）
     * @param message 异常消息
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param operation 操作类型
     * @param errorCode 错误码
     */
    public EpubParseException(String message, String fileName, String filePath, String operation, ErrorCode errorCode) {
        super(formatMessage(message, fileName, filePath, operation, errorCode));
        this.fileName = fileName;
        this.filePath = filePath;
        this.operation = operation;
        this.errorCode = errorCode;
        this.context = new HashMap<>();
        this.recoverySuggestion = generateRecoverySuggestion(errorCode);
        this.lineNumber = -1;
        this.columnNumber = -1;
    }
    
    /**
     * 公共构造函数，用于构建器模式
     */
    public EpubParseException(Builder builder) {
        super(formatMessage(builder.message, builder.fileName, builder.filePath, builder.operation, builder.errorCode));
        this.fileName = builder.fileName;
        this.filePath = builder.filePath;
        this.operation = builder.operation;
        this.errorCode = builder.errorCode;
        this.context = new HashMap<>(builder.context);
        this.recoverySuggestion = builder.recoverySuggestion != null ? builder.recoverySuggestion : generateRecoverySuggestion(builder.errorCode);
        this.lineNumber = builder.lineNumber;
        this.columnNumber = builder.columnNumber;
        if (builder.cause != null) {
            initCause(builder.cause);
        }
    }
    
    /**
     * 格式化简单异常消息，包含错误码
     */
    private static String formatSimpleMessage(String message, ErrorCode errorCode) {
        return String.format("[%s] %s", errorCode.toString(), message != null ? message : errorCode.getDescription());
    }
    
    /**
     * 格式化异常消息，包含额外的上下文信息
     */
    private static String formatMessage(String message, String fileName, String filePath, String operation, ErrorCode errorCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(errorCode.toString()).append("] ");
        sb.append(message != null ? message : errorCode.getDescription());
        
        boolean hasContext = false;
        if (fileName != null) {
            sb.append(" [File: ").append(fileName);
            hasContext = true;
        }
        
        if (filePath != null) {
            if (hasContext) sb.append(",");
            sb.append(" Path: ").append(filePath);
            hasContext = true;
        }
        
        if (operation != null) {
            if (hasContext) sb.append(",");
            sb.append(" Operation: ").append(operation);
            hasContext = true;
        }
        
        if (hasContext) {
            sb.append("]");
        }
        
        return sb.toString();
    }
    
    /**
     * 生成恢复建议
     */
    private static String generateRecoverySuggestion(ErrorCode errorCode) {
        switch (errorCode) {
            case FILE_NOT_FOUND:
                return "请检查文件路径是否正确，确保文件存在";
            case FILE_CORRUPTED:
                return "请尝试重新下载或获取EPUB文件";
            case ZIP_INVALID:
                return "请验证文件是否为有效的ZIP格式";
            case XML_PARSE_ERROR:
                return "请检查XML文件格式是否正确";
            case EPUB_INVALID_CONTAINER:
                return "请检查META-INF/container.xml文件是否存在且格式正确";
            case PATH_TRAVERSAL_ATTACK:
                return "检测到路径遍历攻击，请检查文件安全性";
            default:
                return "请检查文件格式和内容是否符合EPUB规范";
        }
    }
    
    /**
     * 获取文件名
     * @return 文件名
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * 获取文件路径
     * @return 文件路径
     */
    public String getFilePath() {
        return filePath;
    }
    
    /**
     * 获取操作类型
     * @return 操作类型
     */
    public String getOperation() {
        return operation;
    }
    
    /**
     * 获取错误码
     * @return 错误码
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    /**
     * 获取错误码数值
     * @return 错误码数值
     */
    public int getErrorCodeValue() {
        return errorCode.getCode();
    }
    
    /**
     * 获取恢复建议
     * @return 恢复建议
     */
    public String getRecoverySuggestion() {
        return recoverySuggestion;
    }
    
    /**
     * 获取行号
     * @return 行号，如果不适用返回-1
     */
    public int getLineNumber() {
        return lineNumber;
    }
    
    /**
     * 获取列号
     * @return 列号，如果不适用返回-1
     */
    public int getColumnNumber() {
        return columnNumber;
    }
    
    /**
     * 添加上下文信息
     * @param key 键
     * @param value 值
     */
    public void addContext(String key, Object value) {
        context.put(key, value);
    }
    
    /**
     * 获取上下文信息
     * @param key 键
     * @return 值
     */
    public Object getContext(String key) {
        return context.get(key);
    }
    
    /**
     * 获取所有上下文信息
     * @return 上下文映射
     */
    public Map<String, Object> getAllContext() {
        return new HashMap<>(context);
    }
    
    /**
     * 获取根异常
     * @return 根异常
     */
    public Throwable getRootCause() {
        Throwable cause = getCause();
        if (cause == null) {
            return this;
        }
        
        Throwable rootCause = cause;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
    
    /**
     * 构建器类，用于构建复杂的异常对象
     */
    public static class Builder {
        private String message;
        private String fileName;
        private String filePath;
        private String operation;
        private ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;
        private Map<String, Object> context = new HashMap<>();
        private String recoverySuggestion;
        private int lineNumber = -1;
        private int columnNumber = -1;
        private Throwable cause;
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }
        
        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }
        
        public Builder operation(String operation) {
            this.operation = operation;
            return this;
        }
        
        public Builder errorCode(ErrorCode errorCode) {
            this.errorCode = errorCode;
            return this;
        }
        
        public Builder addContext(String key, Object value) {
            this.context.put(key, value);
            return this;
        }
        
        public Builder recoverySuggestion(String suggestion) {
            this.recoverySuggestion = suggestion;
            return this;
        }
        
        public Builder lineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }
        
        public Builder columnNumber(int columnNumber) {
            this.columnNumber = columnNumber;
            return this;
        }
        
        @SuppressFBWarnings("EI_EXPOSE_REP2")
        public Builder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }
        
        /**
         * 获取上下文信息（供子类使用）
         */
        public Object getContext(String key) {
            return context.get(key);
        }
        
        /**
         * 获取文件路径（供子类使用）
         */
        public String getFilePath() {
            return filePath;
        }
        
        /**
         * 获取操作（供子类使用）
         */
        public String getOperation() {
            return operation;
        }
        
        public EpubParseException build() {
            return new EpubParseException(this);
        }
    }
}