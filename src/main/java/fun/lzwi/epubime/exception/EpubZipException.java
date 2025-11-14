package fun.lzwi.epubime.exception;

/**
 * EPUB ZIP异常类
 * 当处理ZIP文件时发生错误时抛出此异常
 */
public class EpubZipException extends EpubParseException {
    public EpubZipException(String message, String fileName, String filePath) {
        super(message, fileName, filePath, "zipProcessing");
    }

    public EpubZipException(String message, String fileName, String filePath, Throwable cause) {
        super(message, fileName, filePath, "zipProcessing", cause);
    }
}