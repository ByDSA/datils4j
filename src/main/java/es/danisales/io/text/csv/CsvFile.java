package es.danisales.io.text.csv;

import com.google.common.collect.ImmutableList;
import es.danisales.io.text.TextFile;
import es.danisales.log.string.Logging;
import es.danisales.others.Keyable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CsvFile<ID, L extends Keyable<ID>> extends TextFile<L> {
    private String separator = ";";

    private Map<ID, L> map = new HashMap<>();
    private List<L> csvLines = new ArrayList<>();

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
            if (l != null) {
                map.put(l.getKey(), l);
                csvLines.add(l);
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
        return ImmutableList.copyOf(csvLines);
    }
}
