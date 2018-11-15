package io.Binary;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.List;

import Log.BinaryLine;
import Log.String.Logging;

public interface Binary {
	int size();
	default byte[] getBytes() {
		ByteBuffer buff = ByteBuffer.allocate( (int) size() );
		addBuff(buff);
		
		return buff.array();
	}
	
	public void addBuff(ByteBuffer buff);
	public void read(ByteBuffer buff);
	
	public static <L extends Binary> int size(List<L> f) {
		int s = 0;
		for (L l : f)
			s += l.size();

		return s;
	}
}
