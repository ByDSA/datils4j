package es.danisales.io.text.csv;

import es.danisales.datastructures.Pair;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class CsvFileKeyValueString extends CsvFile<String, Pair<String, String>> {
    public CsvFileKeyValueString(Path path) {
        super(path);
    }

    @Override
    protected Pair<String, String> readLine(String[] e) {
        return new Pair<>(e[0], e[1]);
    }

    @Override
    protected List<String> saveLine(Pair<String, String> l) {
        return Arrays.asList(l.getKey(), l.getValue());
    }
}