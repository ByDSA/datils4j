package io.Binary;

import java.nio.ByteBuffer;

public final class BooleanBin extends TypeBin<Boolean> {
	public BooleanBin(Boolean v) {
		super(v);
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public void addBuff(ByteBuffer buff) {
		buff.put( (byte) (get() ? 0x1 : 0x0));
	}
	
	@Override
	public void read(ByteBuffer buff) {
		set( (buff.get() & 1) == 1 );
	}
}
