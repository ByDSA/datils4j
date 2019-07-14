package es.danisales.log;

import es.danisales.io.binary.types.DateBin;
import es.danisales.io.binary.types.IntegerBin;
import es.danisales.io.binary.types.auto.AutoBin;

import java.util.Date;
import java.util.function.Supplier;

public abstract class BinaryLine<A extends Supplier<Integer>> implements AutoBin, Supplier<Integer> {
	@SuppressWarnings({"FieldCanBeLocal","unused"})
	private DateBin date;
	private IntegerBin actionCode;

	public BinaryLine(A a) {
		actionCode = new IntegerBin( a );
		date = new DateBin( new Date() );
	}

	@Override
	public Integer get() {
		return actionCode.get();
	}
}
