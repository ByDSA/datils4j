package es.danisales.io.binary.types;

import es.danisales.utils.Valuable;

import java.nio.ByteBuffer;

@SuppressWarnings("WeakerAccess")
public final class IntegerBin extends TypeBin<Integer> {
	public IntegerBin(Integer v) {
		super(v);
	}
	
	public IntegerBin(Valuable<Integer> v) {
		super(v.getValue());
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
