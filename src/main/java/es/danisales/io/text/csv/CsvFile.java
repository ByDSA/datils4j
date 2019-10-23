package es.danisales.io.text.csv;

import com.google.common.collect.ImmutableList;
import es.danisales.datastructures.ListMap;
import es.danisales.io.text.TextFile;
import es.danisales.log.string.Logging;
import es.danisales.others.Keyable;

import java.io.File;

public abstract class CsvFile<ID, L extends Keyable<ID>> extends TextFile<L> implements Iterable<L> {
    private String separator = ";";

    private ListMap<ID, L> _listMap = new ListMap<>();

    @SuppressWarnings("WeakerAccess")
    public CsvFile(File file) {
        super(file);
	}

	public L get(ID id) {
        return _listMap.getByKey(id);
	}

	@Override
    protected L stringToLine(long i, String lStr) {
		if (lStr.startsWith( "//" ))
            return null;
		String[] o = lStr.split( separator );
        try {
            L l = readLine(o);
            if (l != null) {
                _listMap.put(l.getKey(), l);
            }

            return l;
        } catch (Exception e) {
            Logging.error("Error leyendo la línea " + i + " del archivo " + this + "\r\n" + lStr);
            throw e;
        }
	}

	protected abstract L readLine(String[] e);

    @SuppressWarnings("unused")
    public void setSeparator(String s) {
        separator = s;
	}

    @SuppressWarnings("unused")
    public ImmutableList<L> lines() {
        return ImmutableList.copyOf(_listMap.values());
    }

    @SuppressWarnings("unused")
    public ListMap<ID, L> listMap() {
        return _listMap;
    }
}
