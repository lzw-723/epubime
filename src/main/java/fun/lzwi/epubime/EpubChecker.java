package fun.lzwi.epubime;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

public class EpubChecker {
    public static final String MIMETYPE = "application/epub+zip";

    

    public static boolean checkMimeType(File file) throws IOException {

        Epub epub = new Epub(file);
        String mimetype = new String(epub.getInputStream("mimetype").readAllBytes());
        return MIMETYPE.equals(mimetype);
    }

    public static boolean check(File file) throws ZipException, IOException {
        Epub epub = new Epub(file);
        epub.forEach(e -> System.out.println(e.getName()));
        return false;
    }
}
