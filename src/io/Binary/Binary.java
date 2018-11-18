package io.Binary;

import java.nio.ByteBuffer;
import java.util.List;

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
	 * Writes into buffer buff the contained data in the object
	 * 
	 * @param buff Buffer in which binary data will be added
	 */
	public void write(ByteBuffer buff);
	
	/**
	 * Reads from buffer buff the binary data and stores it into the object
	 * 
	 * @param buff Buffer from which binary data will be read
	 */
	public void read(ByteBuffer buff);
	
	/**
	 * Gets total sum up size of binary objects' list
	 * 
	 * @param list List
	 * @return size in bytes
	 */
	public static <L extends Binary> int size(List<L> list) {
		int s = 0;
		for (L b : list)
			s += b.size();

		return s;
	}
}
