package fun.lzwi.epubime.util;

import java.util.List;
import java.util.stream.Collectors;

public class ListUtils {
    public static <T> List<T> copy(List<T> list) {
        return list.stream().collect(Collectors.toList());
    }
}
