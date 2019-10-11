package es.danisales.io.text.csv;

import es.danisales.io.text.SkipLineException;
import es.danisales.io.text.TextFile;
import es.danisales.others.Keyable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class CsvFile<ID, L extends Keyable<ID>> extends TextFile<L> {
    private String separator = ";";

    private Map<ID, L> map = new HashMap<>();

    @SuppressWarnings("WeakerAccess")
    public CsvFile(File file) {
        super(file);
	}

	public L get(ID id) {
		return map.get( id );
	}

	@Override
	protected L stringToLine(long i, String lStr) throws SkipLineException {
		if (lStr.startsWith( "//" ))
			throw new SkipLineException();
		String[] o = lStr.split( separator );
		L l = readLine(o);
		if (l != null)
            map.put(l.getKey(), l);

		return l;
	}

	protected abstract L readLine(String[] e);

    @SuppressWarnings("unused")
    public void setSeparator(String s) {
        separator = s;
	}
}
