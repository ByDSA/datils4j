package es.danisales.io.binary.types;

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
		return 4;
	} // Integer.BYTES in Java 8

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
