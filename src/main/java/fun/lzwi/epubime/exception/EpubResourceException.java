package fun.lzwi.epubime.exception;

/**
 * EPUB资源异常类
 * 当加载或处理EPUB资源时发生错误时抛出此异常
 */
public class EpubResourceException extends EpubParseException {
    public EpubResourceException(String message, String fileName, String filePath) {
        super(message, fileName, filePath, "resourceLoading");
    }

    public EpubResourceException(String message, String fileName, String filePath, Throwable cause) {
        super(message, fileName, filePath, "resourceLoading", cause);
    }
}