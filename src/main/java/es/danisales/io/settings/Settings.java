package es.danisales.io.settings;

import es.danisales.io.binary.BinaryFile;
import es.danisales.io.binary.types.DateBin;
import es.danisales.io.binary.types.MapBin;
import es.danisales.log.string.Logging;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Deprecated
public class Settings extends BinaryFile implements Map<String, Object> {
	private static final long serialVersionUID = 781110217776323164L;
	
	public static String defaultSettingsPath = "data/main.es.danisales.settings";

	DateBin date;
	Map<String, Object> map;
	MapBin mapBin;

	public Settings(String path) {
		super(path);
		map = new HashMap();
	}

	@Override
	public void write(ByteBuffer buff) {
		initializeMapBinIfNull();

		date.write( buff );
		mapBin.write( buff );
	}

	void initializeMapBinIfNull() {
		if (mapBin == null) {
			mapBin = new MapBin(map);
			mapBin.setPutType( true );
		}
	}

	@Override
	public int sizeBytes() {
		initializeMapBinIfNull();

		return date.sizeBytes() + mapBin.sizeBytes();
	}

	@Override
	public void read(ByteBuffer buff) {
		date = DateBin.of( buff );  /** TODO **/
        //mapBin = MapBin.from( buff );
	}
	/*
	public boolean load() {
		boolean ret = File.readObject( path, (in) -> {
			final int S = in.readInt();

			for(int i = 0; i < S; i++) {
				int kLength = in.readInt();
				StringBuilder sb = new StringBuilder();
				for(int j = 0; j < kLength; j++)
					sb.append( in.readChar() );

				string key = sb.toString();
				types getValue = in.readObject();

				put(key, getValue);
			}
		});

		if (!ret)
			Logging.error("No se pudo cargar la configuraci�n: " + path);

		return ret;
	}

	public boolean save() {
		File.writeObject( path, (o) -> {
			o.writeInt( sizeBytes() );

			forEachThrowing((string key, Object getValue ) -> {
				o.writeInt( key.length() );
				o.writeChars( key );
				o.writeObject( getValue );
			});
		});

		return true;
	}*/

	public void show() {
		for (Entry<String, Object> e : this.entrySet()) {
			String key = e.getKey();
			Object value = e.getValue();
			Logging.info( key + ": " + value );
		}
	}

	/** Static **/

	private static Settings _settings;

	private static void loadIfNotExists() {
		if (_settings == null) {
			_settings = new Settings(defaultSettingsPath);
			_settings.load();
		}
	}

	public static Settings main() {
		loadIfNotExists();
		return _settings;
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey( key );
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue( value );
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return map.entrySet();
	}

	@Override
	public Object get(Object key) {
		return map.get( key );
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Object put(String key, Object value) {
		return map.put( key, value );
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		map.putAll( m );
	}

	@Override
	public Object remove(Object key) {
		return map.remove( key );
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<Object> values() {
		return map.values();
	}
}
