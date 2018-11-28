package binary;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapBin<K extends Binary, V extends Binary> extends TypeBin<Map<K, V>> implements Binary, Map<K, V> {
	Class<? extends Binary> keyClass;
	Class<? extends Binary> valueClass;
	
	public MapBin(Map m) {
		super(null);
		
		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();

		Type key = type.getActualTypeArguments()[0];
		Type value = type.getActualTypeArguments()[1];
		
		keyClass = (Class<? extends Binary>) key.getClass();
		valueClass = (Class<? extends Binary>) value.getClass();
		
		try {
			Map m2 = m.getClass().newInstance();
			set(m2);
		} catch ( InstantiationException | IllegalAccessException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public int sizeBytes() {
		int s = super.sizeBytes();
		
		if (getPutType()) {
			s += Binary.idSizeBytes();
		}
		
		for(Entry<K, V> e : get().entrySet()) {
			s += e.getKey().sizeBytes();
			s += e.getValue().sizeBytes();
		}
		
		return s;
	}

	@Override
	public void write(ByteBuffer buff) {
		super.write( buff );

		if (getPutType()) {
			ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();

			Type key = type.getActualTypeArguments()[0];
			Type value = type.getActualTypeArguments()[1];

			Binary.writeId( keyClass, buff );
			Binary.writeId( valueClass, buff );
		}

		new IntegerBin(get().size()).write(buff);

		for(Entry<K, V> e : get().entrySet()) {
			 e.getKey().write( buff );
			 e.getValue().write( buff );
		}
	}

	@Override
	public void read(ByteBuffer buff) {
		super.read(buff);

		if (getPutType()) {
			long keyId = buff.getInt();
			long valueId = buff.getInt();

			keyClass = Binary.getClass( keyId );
			valueClass = Binary.getClass( valueId );
		}

		int size = buff.getInt();

		Map map = new HashMap();

		try {
			for(int i = 0; i < size; i++) {
				Binary keyInstance;

				keyInstance = keyClass.newInstance();
				keyInstance.read( buff );
				Binary valueInstance = keyClass.newInstance();
				valueInstance.read( buff );
				map.put(keyInstance, valueInstance);
			}
		} catch ( InstantiationException | IllegalAccessException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		set(map);
	}
	@Override
	public void clear() {
		get().clear();
	}
	
	@Override
	public boolean containsKey(Object key) {
		return get().containsKey(key);
	}
	
	@Override
	public boolean containsValue(Object value) {
		return get().containsValue( value );
	}
	
	@Override
	public Set<Entry<K,V>> entrySet() {
		return get().entrySet();
	}
	
	@Override
	public V get(Object key) {
		return get().get( key );
	}
	
	@Override
	public boolean isEmpty() {
		return get().isEmpty();
	}
	
	@Override
	public Set<K> keySet() {
		return get().keySet();
	}
	
	@Override
	public V put(K key, V value) {
		return get().put( key, value );
	}
	
	@Override
	public void putAll(Map m) {
		get().putAll( m );
	}
	
	@Override
	public V remove(Object key) {
		return get().remove( key );
	}
	
	@Override
	public int size() {
		return get().size();
	}
	
	@Override
	public Collection values() {
		return get().values();
	}
}
