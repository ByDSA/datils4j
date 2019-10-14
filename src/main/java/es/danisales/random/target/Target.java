package es.danisales.random.target;

import java.util.ArrayList;
import java.util.List;

public interface Target<RET> {
	RET pickDart(long dart);
	RET pick();

    default List<RET> pick(int n) {
        List<RET> ret = new ArrayList<>();
        for (int i = 0; i < n; i++)
            ret.add(pick());
        return ret;
    }
	long getSurface();
	void next();
}
