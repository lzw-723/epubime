package fun.lzwi.epubime.exception;

/**
 * EPUB路径验证异常类
 * 当EPUB文件路径验证失败时抛出此异常
 */
public class EpubPathValidationException extends EpubParseException {
    public EpubPathValidationException(String message, String fileName, String filePath) {
        super(message, fileName, filePath, "pathValidation");
    }

    public EpubPathValidationException(String message, String fileName, String filePath, Throwable cause) {
        super(message, fileName, filePath, "pathValidation", cause);
    }
}