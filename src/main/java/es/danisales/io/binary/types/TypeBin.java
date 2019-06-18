package es.danisales.io.binary.types;

import java.nio.ByteBuffer;

public abstract class TypeBin<T> implements Binary {
	T var;
	protected Class type;

	boolean putType;

	public TypeBin(T v) {
		var = v;
		setPutType(false);
		if (var != null)
			type = var.getClass();
	}

	public TypeBin() {
		this(null);
	}

	public T get() {
		return var;
	}

	public void set(T v) {
		var = v;
	}

	@Override
	public String toString() {
		return var.toString();
	}

	@Override
	public void write(ByteBuffer buff) {
		if (putType)
			Binary.writeId( getClass(), buff );
	}

	@Override
	public void read(ByteBuffer buff) {
		if (putType)
			type = Binary.readId( buff );
	}

	public void setPutType(boolean b) {
		putType = b;
	}

	public boolean getPutType() {
		return putType;
	}

	@Override
	public int sizeBytes() {
		if (putType)
			return Binary.idSizeBytes();
		else
			return 0;
	}
}
