package fun.lzwi.epubime.epub;

/**
 * EPUB解析异常类
 * 当EPUB文件解析过程中发生错误时抛出此异常
 */
public class EpubParseException extends Exception {
    /**
     * 构造函数，创建带有指定消息的EPUB解析异常
     * @param message 异常消息
     */
    public EpubParseException(String message) {
        super(message);
    }

    /**
     * 构造函数，创建带有指定消息和原因的EPUB解析异常
     * @param message 异常消息
     * @param cause 异常原因
     */
    public EpubParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
