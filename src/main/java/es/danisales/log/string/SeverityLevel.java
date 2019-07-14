package es.danisales.log.string;

import java.util.function.Supplier;

public enum SeverityLevel implements Supplier<Integer> {
    Emergency(0), Alert(1), Critical(2), Error(3), Warning(4), Notice(5), Informational(6), Debug(7);

    int val;

    SeverityLevel(int v) {
        val = v;
    }

    @Override
    public Integer get() {
        return val;
    }
}
