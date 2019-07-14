package es.danisales.io.binary.types;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

@SuppressWarnings("WeakerAccess")
public final class IntegerBin extends TypeBin<Integer> {
	public IntegerBin(Integer v) {
		super(v);
	}

	public IntegerBin(Supplier<Integer> v) {
		super(v.get());
	}
	
	protected IntegerBin() {
		super();
	}

	@Override
	public int sizeBytes() {
		return Integer.BYTES;
	}

	@Override
	public void write(ByteBuffer buff) {
		assert get() != null;
		buff.putInt( get() );
	}

	@Override
	public void read(ByteBuffer buff) {
		set( buff.getInt() );
	}
}
