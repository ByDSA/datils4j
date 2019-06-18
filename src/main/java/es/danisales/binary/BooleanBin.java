package es.danisales.binary;

import java.nio.ByteBuffer;

public final class BooleanBin extends TypeBin<Boolean> {
	public BooleanBin(Boolean v) {
		super(v);
	}

	@Override
	public int sizeBytes() {
		return 1;
	}

	@Override
	public void write(ByteBuffer buff) {
		buff.put( (byte) (get() ? 0x1 : 0x0));
	}
	
	@Override
	public void read(ByteBuffer buff) {
		set( (buff.get() & 1) == 1 );
	}
}
