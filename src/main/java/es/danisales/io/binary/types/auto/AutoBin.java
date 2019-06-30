package es.danisales.io.binary.types.auto;

import es.danisales.io.binary.types.Binary;
import es.danisales.io.binary.types.BooleanArrayBin;
import es.danisales.io.binary.types.BooleanBin;
import es.danisales.log.string.Logging;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public interface AutoBin extends Binary {
	List<Field> binaryFields = new ArrayList<>(), booleanFields = new ArrayList<>();

	default void initializeNull() {
		updateBinaryFields();

		for (Field f : binaryFields) {
			Class<?> clazz = null;
			try {
				if (f.get( this ) == null) {
					clazz = f.getType();
					f.set( this, clazz.newInstance() );
				}
			} catch ( IllegalArgumentException | IllegalAccessException | SecurityException e ) {
				e.printStackTrace();
				Logging.fatalError();
			} catch ( InstantiationException e ) {
				Logging.fatalError("No hay constructor por defecto para la clase " + clazz.getName());
			}
		}
	}

	default void updateBinaryFields() {
		binaryFields.clear();
		booleanFields.clear();
		List<Class> inheritance = new ArrayList<>();

		Class currentClass = getClass();
		do {
			inheritance.add( currentClass );
			currentClass = currentClass.getSuperclass();
		} while(Binary.class.isAssignableFrom( currentClass ));
		Collections.reverse( inheritance );

		for (Class c : inheritance) {
			Field[] allFields = c.getDeclaredFields();
			for (Field field : allFields) {
				if (Binary.class.isAssignableFrom( field.getType() )) {
					try {
						if (!field.isAccessible())
							field.setAccessible(true);

						if (BooleanBin.class.isAssignableFrom( field.getType() ))
							booleanFields.add(field);
						else if (field.get(this) != this)
							binaryFields.add(field);
					} catch ( IllegalArgumentException | IllegalAccessException e ) {
						e.printStackTrace();
						Logging.fatalError();
					}
				}
			}
		}
	}

	default void write(ByteBuffer buff) {
		it(
				b -> b.write(buff),
				bab -> bab.write( buff )
		);
	}


	static byte[] getBytes(ByteBuffer buff, final int N) {
		byte[] ret = new byte[N];
		for(int i = 0; i < N; i++)
			ret[i] = buff.get();

		return ret;
	}

	default void read(ByteBuffer buff) {
		Class c = null;
		try {
			updateBinaryFields();

			for(Field f : binaryFields) {
				c = f.getType();

				f.setAccessible(true);

				Constructor constructor;
				constructor = c.getDeclaredConstructor();
				constructor.setAccessible(true);

				f.set( this, constructor.newInstance() );

				Method readMethod = c.getDeclaredMethod( "read", ByteBuffer.class );
				readMethod.invoke( f.get( this ), buff );
			}

			if (booleanFields.size() > 0) {
				final int N = booleanFields.size() / 8 + 1;
				int bit = 0, n = 0;
				byte[] bytesBoolean = getBytes( buff, N );
				for(Field f : booleanFields) {
					boolean value = (((bytesBoolean[n] >> bit) & 1) == 1);
					f.set( this, new BooleanBin(value) );

					bit++;
					if (bit == 8) {
						n++;
						bit = 0;
					}
				}
			}

		} catch ( NoSuchMethodException e) {
			e.printStackTrace();
			Logging.fatalError("La clase " + c + " no tiene el m�todo read.");
		} catch(InstantiationException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
			e.printStackTrace();
			Logging.fatalError();
		}
	}

	default void it(Consumer<Binary> cb, Consumer<BooleanArrayBin> cbb) {
		try {
			for(Field f : binaryFields) {
				Binary b = (Binary) f.get( this );
				cb.accept( b );
			}

			if (booleanFields.size() > 0) {
				BooleanArrayBin bab = new BooleanArrayBin();
				for(Field f : booleanFields) {
					BooleanBin bb;

					bb = (BooleanBin) f.get( this );
					bab.add( bb.get() );
				}
				cbb.accept( bab );
			}
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
			Logging.fatalError();
		}
	}

	default void itRead(Consumer<Field> cb, Consumer<Field> cbb) {
		try {
			for(Field f : binaryFields) {
				cb.accept( f );
			}

			if (booleanFields.size() > 0) {
				for(Field f : booleanFields) {
					cbb.accept( f );
				}
			}
		} catch ( IllegalArgumentException e ) {
			e.printStackTrace();
			Logging.fatalError();
		}
	}

	default int sizeBytes() {
		initializeNull();

		AtomicInteger s = new AtomicInteger(0);
		it(
				b -> s.set( s.addAndGet( b.sizeBytes() ) ),
				bab -> s.set( s.addAndGet( bab.sizeBytes() ) )
		);

		return s.get();
	}
}
