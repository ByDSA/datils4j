package io.Text;

import java.util.HashMap;
import java.util.Map;

import Log.String.Logging;

public abstract class CSV<ID, L extends CSV.Line<ID>> extends TextFile<L> {
	public static String defaultSeparator = ";";
	protected String separator = defaultSeparator;

	Map<ID, L> map = new HashMap();

	public CSV(String pathname) {
		super( pathname );
	}

	public static class Line<ID> {
		protected ID id;

		public Line(ID id) {
			this.id = id;
		}

		public ID getId() {
			return id;
		}
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
		map.put( (ID)l.getId(), l );

		Logging.fatalErrorIfNull( l );

		return l;
	}

	protected abstract L readLine(String[] e);

}
