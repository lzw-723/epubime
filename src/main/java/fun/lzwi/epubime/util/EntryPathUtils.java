package fun.lzwi.epubime.util;

import java.io.IOException;
import java.net.URL;

public class EntryPathUtils {
    public static String parse(String base, String href) throws IOException {

        if (base != null && !base.startsWith("/")) {
            base = "/" + base;
        }
        if (base != null && !base.endsWith("/")) {
            base += "/";
        }

        String path = new URL(new URL("file://" + base), href).getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return path;
    }

    public static String parent(String path) throws IOException {
        path = parse("", path);
        int lastIndexOf = path.lastIndexOf("/");
        if (lastIndexOf <= 0) {
            return "";
        }
        return path.substring(0, lastIndexOf);
    }

    public static String hash(String href) {
        int hash = href.lastIndexOf("#");
        if (href.lastIndexOf(".") < hash) {
            return href.substring(hash + 1);
        }
        return null;
    }
}
