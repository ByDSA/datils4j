package es.danisales.datastructures;

import java.util.ArrayList;
import java.util.List;

public interface Picker<RET> {
    RET pick();

    default List<RET> pick(int n) {
        List<RET> ret = new ArrayList<>();
        for (int i = 0; i < n; i++)
            ret.add(pick());
        return ret;
    }
}
