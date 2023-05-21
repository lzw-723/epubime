package fun.lzwi;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Utils {
    public static File getFile(String path) {
        try {
            return new File(URLDecoder.decode(AppTest.class.getClassLoader().getResource(path).getPath(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
