package es.danisales.io.text.csv;

import es.danisales.io.text.TextFile;
import es.danisales.log.string.Logging;
import es.danisales.others.Keyable;

import java.io.File;
import java.util.Collection;
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
    protected L stringToLine(long i, String lStr) {
		if (lStr.startsWith( "//" ))
            return null;
		String[] o = lStr.split( separator );
        try {
            L l = readLine(o);
            if (l != null)
                map.put(l.getKey(), l);

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

    public Collection<L> lines() {
        return map.values();
    }
}
