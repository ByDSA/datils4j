package binary;

import java.nio.ByteBuffer;

import others.Codeable;

public final class IntegerBin extends TypeBin<Integer> {
	public IntegerBin(Integer v) {
		super(v);
	}
	
	public IntegerBin(Codeable v) {
		super(v.getCode());
	}
	
	public IntegerBin() {
		this(0);
	}

	@Override
	public int size() {
		return Integer.BYTES;
	}

	@Override
	public void write(ByteBuffer buff) {
		buff.putInt( get() );
	}

	@Override
	public void read(ByteBuffer buff) {
		set( buff.getInt() );
	}
}
