package es.danisales.datastructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListUtils {
    private ListUtils() {
    }

    @SafeVarargs
    public static <T> List<T> concatImmutable(List<T>... lists) {
        List<T> result = new ArrayList<>();

        for (List<T> l : lists)
            result.addAll(l);

        return Collections.unmodifiableList(result);
    }
}
