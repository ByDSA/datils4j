package es.danisales.io.binary.types;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * Class which implements this interface could be turned into a byte array.
 */
public interface Binary extends Serializable {
	IdTypeEnum ID_TYPE = IdTypeEnum.Integer;
	String ID_VARNAME = "serialVersionUID";
	/**
     * Gets total sum up size from es.danisales.io.binary.types objects' list.
     *
     * @param <L> the generic type
     * @param list List
     * @return size in bytes
	 */
    static <L extends Binary> int sizeBytes(List<L> list) {
        int s = 0;
        for (L b : list)
            s += b.sizeBytes();

        return s;
    }

	/**
	 * Turns the object information into a byte array.
	 * 
	 * @return byte array
	 */
	default byte[] getBytes() {
		ByteBuffer buff = ByteBuffer.allocate( sizeBytes() );
		write(buff);

		return buff.array();
	}

	/**
	 * Writes into buffer buff the contained data in the object.
	 *
	 * @param buff Buffer in which es.danisales.io.binary.types data will be added
	 */
	void write(ByteBuffer buff);

	/**
	 * Reads from buffer buff the es.danisales.io.binary.types data and stores it into the object.
	 *
	 * @param buff Buffer from which es.danisales.io.binary.types data will be read
	 */
	void read(ByteBuffer buff);

	/**
	 * Gets n bytes from a buffer at its current seek.
	 *
	 * @param buff the buff
     * @param N number from bytes to get
	 * @return the got bytes
	 */
	static byte[] getNBytes(ByteBuffer buff, final int N) {
		byte[] ret = new byte[N];
		for(int i = 0; i < N; i++)
			ret[i] = buff.get();

		return ret;
	}

    /**
     * Write the es.danisales.io.binary.types objects contained in list into a buffer.
     *
     * @param buff the buffer
     * @param list the list
     */
    static void writeList(ByteBuffer buff, List<Binary> list) {
        buff.putInt(list.size());
        for (Binary b : list)
            buff.put(b.getBytes());
    }

    /**
     * Size in bytes from es.danisales.io.binary.types data composed in function getBytes.
     *
     * @return size
     */
    int sizeBytes();

	static Object getId(Class<? extends Binary> c) {
		try {
			Field myField = c.getDeclaredField(ID_VARNAME);
			return myField.get(null);
		} catch ( Exception e ) {
			return -1;
		}
	}

	static <B extends Binary> void writeId(Class<B> b, ByteBuffer buff) {
		Object id = getId(b);
		if (ID_TYPE == IdTypeEnum.Long)
			buff.putLong( (long)id);
		else if (ID_TYPE == IdTypeEnum.Integer)
			buff.putInt( (int)id);
		else if (ID_TYPE == IdTypeEnum.Short)
			buff.putShort( (short)id);
	}
	
	static <B extends Binary> void writeId(B b, ByteBuffer buff) {
		writeId(b.getClass(), buff);
	}
	
	static Class readId( ByteBuffer buff ) {
		long id = -1;
		if (ID_TYPE == IdTypeEnum.Long)
			id = buff.getLong();
		else if (ID_TYPE == IdTypeEnum.Integer)
			id = buff.getInt();
		else if (ID_TYPE == IdTypeEnum.Short)
			id = buff.getShort();
		return getClass(id);
	}
	
	Map<Long, Class> classes = new HashMap<>();
	Map<Class<Object>, Class<TypeBin>> c2b = new HashMap<>();
	
	static Class getClass(long id) {
		return classes.get( id );
	}
	
	static Class<? extends TypeBin> getBinaryClass(Class c) {
		return c2b.get( c );
	}

	static int idSizeBytes() {
		if (ID_TYPE == IdTypeEnum.Long)
			return 8; // Long.BYTES in Java 8
		else if (ID_TYPE == IdTypeEnum.Integer)
			return 4; // Integer.BYTES in Java 8
		else if (ID_TYPE == IdTypeEnum.Short)
			return 2; // Short.BYTES in Java 8
		else
			return 0;
	}
	
	static Binary toBinary(byte[] bytes) {
		return new Binary() {

			@Override
			public int sizeBytes() {
				return bytes.length;
			}

			@Override
			public void write(ByteBuffer buff) {
				buff.put( bytes );
			}

			@Override
			public void read(ByteBuffer buff) {
				// TODO Auto-generated method stub
			}
			
		};
	}
}
