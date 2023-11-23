package fun.lzwi;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import fun.lzwi.epubime.util.LoggerUtils;

public class Utils {
    public static File getFile(String path) throws UnsupportedEncodingException {

        URL resource = Utils.class.getClassLoader().getResource(path);
        if (resource != null) {
            return new File(URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8));
        }
        LoggerUtils.from(Utils.class).error(String.format("路径%s异常", path));
        return null;
    }
}
