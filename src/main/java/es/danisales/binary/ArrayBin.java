package es.danisales.binary;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

import es.danisales.log.string.Logging;

public final class ArrayBin<T extends TypeBin<U>, U> extends TypeBin<U[]> {
	T[] varBin;
	Class<T> tClass;
	
	public ArrayBin(Class<T> c, U... v) {
		super(v);

		tClass = c;

		varBin = (T[]) Array.newInstance(c, v.length);
		for(int i = 0; i < v.length; i++)
			try {
				varBin[i] = c.getDeclaredConstructor( c ).newInstance( v[i] );
			} catch ( InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e ) {
				e.printStackTrace();
				Logging.fatalError();
			}
	}

	@Override
	public int sizeBytes() {
		int s = Integer.BYTES;
		for(T t : varBin)
			s += t.sizeBytes();
		return s;
	}

	@Override
	public void write(ByteBuffer buff) {
		super.write( buff );
		
		if (putType)
			Binary.writeId( tClass, buff );
		
		buff.putInt( get().length );
		for(T t : varBin)
			t.write( buff );
	}

	@Override
	public void read(ByteBuffer buff) {
		try {
			super.read(buff);
			Class c;
			if (putType)
				c = Binary.readId( buff );
			else
				c = tClass;
			int length = buff.getInt();
			varBin = (T[])Array.newInstance( c, length);
			for (int i = 0; i < length ; i++) {

				varBin[i] = tClass.newInstance();

				varBin[i].read( buff );
			}
		} catch ( InstantiationException | IllegalAccessException e ) {
			e.printStackTrace();
			Logging.fatalError();
		}
	}
}
