package fun.lzwi.epubime.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    public static <T> List<T> copy(List<T> list) {
        return new ArrayList<>(list);
    }
}
