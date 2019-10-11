package es.danisales.io.text.csv;

import es.danisales.others.Keyable;

@SuppressWarnings("unused")
public class CsvLine implements Keyable<String> {
    private final String[] values;
    private String key;

    public CsvLine(String[] v) {
        values = v;
        key = v[0];
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String k) {
        key = k;
    }

    public String get(int n) {
        return values[n];
    }

    public int size() {
        return values.length;
    }
}
