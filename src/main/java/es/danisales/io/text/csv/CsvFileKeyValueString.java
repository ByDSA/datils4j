package es.danisales.io.text.csv;

import es.danisales.datastructures.KeyValuePair;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class CsvFileKeyValueString extends CsvFile<String, KeyValuePair<String, String>> {
    public CsvFileKeyValueString(Path path) {
        super(path);
    }

    @Override
    protected KeyValuePair<String, String> readLine(String[] e) {
        return new KeyValuePair<>(e[0], e[1]);
    }

    @Override
    protected List<String> saveLine(KeyValuePair<String, String> l) {
        return Arrays.asList(l.getKey(), l.getValue());
    }
}