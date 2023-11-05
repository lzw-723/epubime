package fun.lzwi.epubime.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipFile;

import fun.lzwi.epubime.EpubConstants;

public class EpubUtils {

    protected static boolean existEntry(File file, String path)
            throws IOException {
        try (ZipFile epub = new ZipFile(file)) {
            return epub.getEntry(path) != null;
        }
    }

    public static boolean existMimeType(File file) throws IOException {

        try (ZipFile epub = new ZipFile(file)) {
            byte[] data = new byte[EpubConstants.MIMETYPE.length()];
            try (DataInputStream din = new DataInputStream(epub.getInputStream(epub.getEntry("mimetype")))) {
                din.readFully(data);
                String mimetype = new String(data, StandardCharsets.UTF_8);
                return EpubConstants.MIMETYPE.equals(mimetype);

            }
        }
    }

    public static boolean existContainerXML(File file)
            throws IOException {
        return existEntry(file, EpubConstants.CONTAINER);
    }

}
