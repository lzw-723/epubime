package fun.lzwi;

import java.io.File;
import java.net.URLDecoder;

import fun.lzwi.epubime.util.LoggerUtils;

public class Utils {
    public static File getFile(String path) {
        try {
            return new File(URLDecoder.decode(Utils.class.getClassLoader().getResource(path).getPath(), "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            LoggerUtils.from(Utils.class).severe(String.format("path: %s异常", path));
        }
        return null;
    }
}
