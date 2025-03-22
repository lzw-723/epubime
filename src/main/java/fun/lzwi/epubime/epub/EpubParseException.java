package fun.lzwi.epubime.epub;

public class EpubParseException extends Exception {
    public EpubParseException(String message) {
        super(message);
    }

    public EpubParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
