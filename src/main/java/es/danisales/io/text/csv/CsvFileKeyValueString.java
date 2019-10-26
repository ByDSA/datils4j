package es.danisales.io.text.csv;

import es.danisales.datastructures.MutablePair;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class CsvFileKeyValueString extends CsvFile<String, MutablePair<String, String>> {
    public CsvFileKeyValueString(Path path) {
        super(path);
    }

    @Override
    protected MutablePair<String, String> readLine(String[] e) {
        return new MutablePair<>(e[0], e[1]);
    }

    @Override
    protected List<String> saveLine(MutablePair<String, String> l) {
        return Arrays.asList(l.getKey(), l.getValue());
    }
}