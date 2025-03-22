package fun.lzwi.epubime;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ResUtils {
    public static File getFileFromRes(String path) {
        URL resource = ResUtils.class.getClassLoader().getResource(path);
        try {
            return new File(URLDecoder.decode(Objects.requireNonNull(resource).getPath(),
                    StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
