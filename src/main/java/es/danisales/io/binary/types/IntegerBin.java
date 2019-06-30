package es.danisales.io.binary.types;

import es.danisales.enums.EnumValue;

import java.nio.ByteBuffer;

public final class IntegerBin extends TypeBin<Integer> {
	public IntegerBin(Integer v) {
		super(v);
	}
	
	public IntegerBin(EnumValue<Integer> v) {
		super(v.value());
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
