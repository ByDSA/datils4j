package io.Binary;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

public final class BinaryUtils {
	public static void putList(ByteBuffer buff, List<Binary> l) {
		buff.putInt( l.size() );
		for(Binary b : l)
			buff.put( b.getBytes() );
	}
}
