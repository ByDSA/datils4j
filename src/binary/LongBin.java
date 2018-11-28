package binary;

import java.nio.ByteBuffer;

public final class LongBin extends TypeBin<Long> {
	public LongBin(Long v) {
		super(v);
	}
	
	protected LongBin() {
		super();
	}

	@Override
	public int sizeBytes() {
		return Long.BYTES;
	}

	@Override
	public void write(ByteBuffer buff) {
		buff.putLong( get() );
	}
	
	@Override
	public void read(ByteBuffer buff) {
		set( buff.getLong() );
	}
}
