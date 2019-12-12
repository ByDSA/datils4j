package es.danisales.log;

import es.danisales.io.binary.types.DateBin;

import java.util.Date;
import java.util.function.Supplier;

public abstract class BinaryLine<A extends Supplier<Integer>>
		implements Supplier<Integer> {
	@SuppressWarnings({"FieldCanBeLocal","unused"})
	private DateBin date;
	private int actionCode;

	public BinaryLine(A a) {
		actionCode = a.get();
		date = new DateBin( new Date() );
	}

	public byte[] getBytes() {
		return null;
	}

	@Override
	public Integer get() {
		return actionCode;
	}
}
