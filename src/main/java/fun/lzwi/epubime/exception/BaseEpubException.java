package fun.lzwi.epubime.exception;

/**
 * 简化的EPUB异常基类
 * 提供基本的异常信息，避免过度复杂的错误处理
 */
public class BaseEpubException extends Exception {
    
    /**
     * 基本构造函数
     * @param message 异常消息
     */
    public BaseEpubException(String message) {
        super(message);
    }

    /**
     * 带原因的构造函数
     * @param message 异常消息
     * @param cause 异常原因
     */
    public BaseEpubException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 仅带原因的构造函数
     * @param cause 异常原因
     */
    public BaseEpubException(Throwable cause) {
        super(cause);
    }
}