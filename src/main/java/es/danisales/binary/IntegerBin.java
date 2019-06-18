package es.danisales.binary;

import java.nio.ByteBuffer;

import es.danisales.others.Codeable;

public final class IntegerBin extends TypeBin<Integer> {
	public IntegerBin(Integer v) {
		super(v);
	}
	
	public IntegerBin(Codeable v) {
		super(v.getCode());
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
