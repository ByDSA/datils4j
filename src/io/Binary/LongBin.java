package io.Binary;

import java.nio.ByteBuffer;

public final class LongBin extends TypeBin<Long> {
	public LongBin(Long v) {
		super(v);
	}

	@Override
	public int size() {
		return Long.BYTES;
	}

	@Override
	public void addBuff(ByteBuffer buff) {
		buff.putLong( get() );
	}
	
	@Override
	public void read(ByteBuffer buff) {
		set( buff.getLong() );
	}
}
