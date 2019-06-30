package es.danisales.log.string;

import es.danisales.utils.Valuable;

public enum SeverityLevel implements Valuable<Integer> {
    Emergency(0), Alert(1), Critical(2), Error(3), Warning(4), Notice(5), Informational(6), Debug(7);

    int val;

    SeverityLevel(int v) {
        val = v;
    }

    public Integer getValue() {
        return val;
    }
}
