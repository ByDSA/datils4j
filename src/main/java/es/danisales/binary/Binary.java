package es.danisales.binary;

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
	public enum IdTypeEnum {
		Integer, Long, Short;
	}

	static IdTypeEnum ID_TYPE = IdTypeEnum.Integer;
	static final String ID_VARNAME = "serialVersionUID";
	/**
	 * Size in bytes of es.danisales.binary data composed in function getBytes.
	 * 
	 * @return size
	 */
	int sizeBytes();

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
	 * @param buff Buffer in which es.danisales.binary data will be added
	 */
	public void write(ByteBuffer buff);

	/**
	 * Reads from buffer buff the es.danisales.binary data and stores it into the object.
	 *
	 * @param buff Buffer from which es.danisales.binary data will be read
	 */
	public void read(ByteBuffer buff);

	/**
	 * Gets total sum up size of es.danisales.binary objects' list.
	 *
	 * @param <L> the generic type
	 * @param list List
	 * @return size in bytes
	 */
	public static <L extends Binary> int sizeBytes(List<L> list) {
		int s = 0;
		for (L b : list)
			s += b.sizeBytes();

		return s;
	}

	/**
	 * Write the es.danisales.binary objects contained in list into a buffer.
	 *
	 * @param buff the buffer
	 * @param list the list
	 */
	public static void writeList(ByteBuffer buff, List<Binary> list) {
		buff.putInt( list.size() );
		for(Binary b : list)
			buff.put( b.getBytes() );
	}

	/**
	 * Gets n bytes from a buffer at its current seek.
	 *
	 * @param buff the buff
	 * @param N number of bytes to get
	 * @return the got bytes
	 */
	public static byte[] getNBytes(ByteBuffer buff, final int N) {
		byte[] ret = new byte[N];
		for(int i = 0; i < N; i++)
			ret[i] = buff.get();

		return ret;
	}

	public static Object getId(Class<? extends Binary> c) {
		try {
			Field myField = c.getDeclaredField(ID_VARNAME);
			return myField.get(null);
		} catch ( Exception e ) {
			return -1;
		}
	}

	public static <B extends Binary> void writeId(Class<B> b, ByteBuffer buff) {
		Object id = getId(b);
		if (ID_TYPE == IdTypeEnum.Long)
			buff.putLong( (long)id);
		else if (ID_TYPE == IdTypeEnum.Integer)
			buff.putInt( (int)id);
		else if (ID_TYPE == IdTypeEnum.Short)
			buff.putShort( (short)id);
	}
	
	public static <B extends Binary> void writeId(B b, ByteBuffer buff) {
		writeId(b.getClass(), buff);
	}
	
	public static Class readId( ByteBuffer buff ) {
		long id = -1;
		if (ID_TYPE == IdTypeEnum.Long)
			id = buff.getLong();
		else if (ID_TYPE == IdTypeEnum.Integer)
			id = buff.getInt();
		else if (ID_TYPE == IdTypeEnum.Short)
			id = buff.getShort();
		return getClass(id);
	}
	
	static Map<Long, Class> classes = new HashMap();
	static Map<Class<Object>, Class<TypeBin>> c2b = new HashMap();
	
	public static Class getClass(long id) {
		return classes.get( id );
	}
	
	public static Class<? extends TypeBin> getBinaryClass(Class c) {
		return c2b.get( c );
	}

	public static int idSizeBytes() {
		if (ID_TYPE == IdTypeEnum.Long)
			return Long.BYTES;
		else if (ID_TYPE == IdTypeEnum.Integer)
			return Integer.BYTES;
		else if (ID_TYPE == IdTypeEnum.Short)
			return Short.BYTES;
		else
			return 0;
	}
	
	public static Binary toBinary(byte[] bytes) {
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
