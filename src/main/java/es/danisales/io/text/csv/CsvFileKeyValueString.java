package es.danisales.io.text.csv;

import java.io.File;

@SuppressWarnings("unused")
public class CsvFileKeyValueString extends CsvFile<String, KeyValueLine<String, String>> {
    public CsvFileKeyValueString(File file) {
        super(file);
    }

    @Override
    protected KeyValueLine<String, String> readLine(String[] e) {
        return new KeyValueLine<>(e[0], e[1]);
    }
}