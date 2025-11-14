package fun.lzwi.epubime.exception;

/**
 * EPUB格式异常类
 * 当EPUB文件格式不符合规范时抛出此异常
 */
public class EpubFormatException extends EpubParseException {
    public EpubFormatException(String message, String fileName, String filePath) {
        super(message, fileName, filePath, "formatParsing");
    }

    public EpubFormatException(String message, String fileName, String filePath, Throwable cause) {
        super(message, fileName, filePath, "formatParsing", cause);
    }
}