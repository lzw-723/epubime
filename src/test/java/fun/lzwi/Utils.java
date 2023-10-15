package fun.lzwi;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import fun.lzwi.epubime.util.LoggerUtils;

public class Utils {
    public static File getFile(String path) throws UnsupportedEncodingException {

        URL resource = Utils.class.getClassLoader().getResource(path);
        if (resource != null) {
            return new File(URLDecoder.decode(resource.getPath(), "utf-8"));
        }
        LoggerUtils.from(Utils.class).severe(String.format("path: %s异常", path));
        return null;
    }
}
