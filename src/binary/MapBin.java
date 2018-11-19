package binary;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MapBin extends TypeBin<Map<? extends Binary, ? extends Binary>> implements Binary {

	@Override
	public int sizeBytes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void write(ByteBuffer buff) {
		super.write( buff );

		if (getPutType()) {
			ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();

			Type key = type.getActualTypeArguments()[0];
			Type value = type.getActualTypeArguments()[1];

			Binary.writeId( (Class<? extends Binary>)key.getClass(), buff );
			Binary.writeId( (Class<? extends Binary>)value.getClass(), buff );
		}

		new IntegerBin(get().size()).write(buff);

		for(Entry<? extends Binary, ? extends Binary> e : get().entrySet()) {
			e.getKey().write( buff );
			e.getValue().write( buff );
		}
	}

	Class<? extends Binary>[] getGenericClasses() {
		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();

		Type key = type.getActualTypeArguments()[0];
		Type value = type.getActualTypeArguments()[1];

		Class<? extends Binary> keyClass = (Class<? extends Binary>)key.getClass();
		Class<? extends Binary> valueClass = (Class<? extends Binary>)key.getClass();

		return new Class[] {keyClass, valueClass};
	}

	@Override
	public void read(ByteBuffer buff) {
		super.read(buff);

		Class<? extends Binary> key, value;

		if (getPutType()) {
			long keyId = buff.getInt();
			long valueId = buff.getInt();

			key = Binary.getClass( keyId );
			value = Binary.getClass( valueId );

		} else {
			Class[] cs = getGenericClasses();
			key = cs[0];
			value = cs[1];
		}

		int size = buff.getInt();

		Map map = new HashMap();

		try {
			for(int i = 0; i < size; i++) {
				Binary keyInstance;

				keyInstance = key.newInstance();
				keyInstance.read( buff );
				Binary valueInstance = key.newInstance();
				valueInstance.read( buff );
				map.put(keyInstance, valueInstance);
			}
		} catch ( InstantiationException | IllegalAccessException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		set(map);
	}

}
