package io.Binary;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

import Log.String.Logging;

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
				// TODO Auto-generated catch block
				e.printStackTrace();
				Logging.fatalError();
			}

	}

	@Override
	public int size() {
		int s = Integer.BYTES;
		for(T t : varBin)
			s += t.size();
		return s;
	}

	@Override
	public void write(ByteBuffer buff) {
		buff.putInt( get().length );
		for(T t : varBin)
			t.write( buff );
	}

	@Override
	public void read(ByteBuffer buff) {
		try {
			int length = buff.getInt();
			varBin = (T[])Array.newInstance( tClass, length);
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
