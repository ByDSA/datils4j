package es.danisales.io.text.csv;

import java.io.File;

@SuppressWarnings("unused")
public class CsvFileKeyValue extends CsvFile<String, CsvFileKeyValue.Line> {
    public CsvFileKeyValue(File file) {
        super(file);
    }

    @Override
    protected Line readLine(String[] e) {
        return new Line(e[0], e[1]);
    }

    static class Line extends KeyValueLine<String, String> {
        Line(String key, String value) {
            super(key, value);
        }
    }
}