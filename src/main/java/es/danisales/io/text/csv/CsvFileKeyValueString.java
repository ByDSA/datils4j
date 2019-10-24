package es.danisales.io.text.csv;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class CsvFileKeyValueString extends CsvFile<String, KeyValueLine<String, String>> {
    public CsvFileKeyValueString(Path path) {
        super(path);
    }

    @Override
    protected KeyValueLine<String, String> readLine(String[] e) {
        return new KeyValueLine<>(e[0], e[1]);
    }

    @Override
    protected List<String> saveLine(KeyValueLine<String, String> l) {
        return Arrays.asList(l.getKey(), l.getValue());
    }
}