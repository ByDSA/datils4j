package es.danisales.io.text.csv;

import java.io.File;
import java.nio.file.Path;

@SuppressWarnings("unused")
public class CsvFileKeyValueString extends CsvFile<String, KeyValueLine<String, String>> {
    public CsvFileKeyValueString(Path path) {
        super(path);
    }

    @Override
    protected String lineToString(long i, KeyValueLine<String, String> l) {
        return l.getKey() + File.separator + l.getValue();
    }

    @Override
    protected KeyValueLine<String, String> readLine(String[] e) {
        return new KeyValueLine<>(e[0], e[1]);
    }
}