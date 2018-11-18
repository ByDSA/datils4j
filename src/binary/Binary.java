package binary;

import java.nio.ByteBuffer;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Class which implements this interface could be turned into a byte array.
 */
public interface Binary {
	/**
	 * Size in bytes of binary data composed in function getBytes.
	 * 
	 * @return size
	 */
	int size();

	/**
	 * Turns the object information into a byte array.
	 * 
	 * @return byte array
	 */
	default byte[] getBytes() {
		ByteBuffer buff = ByteBuffer.allocate( size() );
		write(buff);

		return buff.array();
	}

	/**
	 * Writes into buffer buff the contained data in the object.
	 *
	 * @param buff Buffer in which binary data will be added
	 */
	public void write(ByteBuffer buff);

	/**
	 * Reads from buffer buff the binary data and stores it into the object.
	 *
	 * @param buff Buffer from which binary data will be read
	 */
	public void read(ByteBuffer buff);

	/**
	 * Gets total sum up size of binary objects' list.
	 *
	 * @param <L> the generic type
	 * @param list List
	 * @return size in bytes
	 */
	public static <L extends Binary> int size(List<L> list) {
		int s = 0;
		for (L b : list)
			s += b.size();

		return s;
	}

	/**
	 * Write the binary objects contained in list into a buffer.
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
}
