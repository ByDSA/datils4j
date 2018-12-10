package io.Text;

import java.io.File;
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

	public CSV(File path) {
		this( path.toString() );
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
			map.put( (ID)l.getId(), l );

		return l;
	}

	protected abstract L readLine(String[] e);


	public static class Line<ID> {
		protected ID id;

		public Line(ID id) {
			this.id = id;
		}

		public ID getId() {
			return id;
		}
	}

	public static class KeyValueLine<K, V> extends Line<K> {
		protected V value;

		public KeyValueLine(K key, V v) {
			super(key);
			value = v;
		}

		public V getValue() {
			return value;
		}
	}

	public static class CSVKeyValueString extends CSV<String, CSV.CSVKeyValueString.Line> {
		public CSVKeyValueString(String f) {
			super(f);
		}

		@Override
		protected CSV.CSVKeyValueString.Line readLine(String[] e) {
			return new CSV.CSVKeyValueString.Line(e[0], e[1]);
		}

		public static class Line extends KeyValueLine<String, String> {
			public Line(String key, String value) {
				super( key, value );
			}		
		}
	}
}
