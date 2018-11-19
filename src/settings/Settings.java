package settings;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import Log.String.Logging;
import binary.Binary;
import binary.IntegerBin;
import binary.MapBin;
import binary.StringBin;
import binary.auto.DateAutoBin;
import datastructures.MapEntry;
import io.File;

public class Settings extends DateAutoBin implements Map<String, Binary> {
	private static final long serialVersionUID = 781110217776323164L;
	
	String path;
	MapBin map;

	public Settings(String p) {
		path = p;
		map = new MapBin(new ConcurrentHashMap());
	}

	@Override
	public void write(ByteBuffer buff) {
		super.write( buff );
		
		new IntegerBin(map.size()).write(buff);
		
		for(Entry<StringBin, Binary> e : map.entrySet()) {
			e.getKey().write( buff );
			e.getValue().write( buff );
		}
	}
	
	@Override
	public void read(ByteBuffer buff) {
		super.read( buff );
		
		int size = buff.getInt();
		
		for(Entry<StringBin, Binary> e : map.entrySet()) {
			e.getKey().write( buff );
			e.getValue().write( buff );
		}
	}

	public boolean load() {
		boolean ret = File.readObject( path, (in) -> {
			final int S = in.readInt();

			for(int i = 0; i < S; i++) {
				int kLength = in.readInt();
				StringBuilder sb = new StringBuilder();
				for(int j = 0; j < kLength; j++)
					sb.append( in.readChar() );

				String key = sb.toString();
				Binary value = in.readObject();

				put(key, value);
			}
		});

		if (!ret)
			Logging.error("No se pudo cargar la configuración: " + path);

		return ret;
	}

	public void save() {
		File.writeObject( path, (o) -> {
			o.writeInt( sizeBytes() );

			forEachThrowing((String key, Object value ) -> {
				o.writeInt( key.length() );
				o.writeChars( key );
				o.writeObject( value );
			});
		});
	}

	public void show() {
		forEach( (String key, Object value ) -> {
			Logging.info( key + ": " + value );
		});
	}

	/** Static **/

	private static Settings _settings;

	private static void loadIfNotExists() {
		if (_settings == null) {
			_settings = new Settings("data/settings");
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
	public Set<Entry<String, Binary>> entrySet() {
		Set<Entry<String, Binary>> set = new HashSet();
		for(Entry<StringBin, Binary> e : map.entrySet() ) {
			set.add( new MapEntry(e.getKey().get(), e.getValue()));
		}

		return set;
	}

	@Override
	public Binary get(Object key) {
		return map.get( key );
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		Set<String> set = ConcurrentHashMap.newKeySet();

		for(StringBin strb : map.keySet() ) {
			set.add( strb.get() );
		}

		return set;
	}

	@Override
	public Binary put(String key, Binary value) {
		map.put( new StringBin(key), value );
	}

	@Override
	public void putAll(Map<? extends String, ? extends Binary> m) {
		for(Entry<? extends String, ? extends Binary> e : m.entrySet())
			put( e.getKey(), e.getValue() );
	}

	@Override
	public Binary remove(Object key) {
		map.remove( key );
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<Binary> values() {
		return map.values();
	}
}
