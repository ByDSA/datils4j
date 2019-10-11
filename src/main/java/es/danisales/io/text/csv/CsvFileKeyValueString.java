package es.danisales.io.text.csv;

import java.io.File;

@SuppressWarnings("unused")
public class CsvFileKeyValueString extends CsvFile<String, CsvFileKeyValueString.Line> {
    public CsvFileKeyValueString(File file) {
        super(file);
    }

    @Override
    protected Line readLine(String[] e) {
        return new Line(e[0], e[1]);
    }


    @SuppressWarnings("WeakerAccess")
    public static class Line extends KeyValueLine<String, String> {
        Line(String key, String value) {
            super(key, value);
        }
    }
}