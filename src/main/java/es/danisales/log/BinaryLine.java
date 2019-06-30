package es.danisales.log;

import es.danisales.enums.EnumValue;
import es.danisales.io.binary.types.DateBin;
import es.danisales.io.binary.types.IntegerBin;
import es.danisales.io.binary.types.auto.AutoBin;

import java.util.Date;

public abstract class BinaryLine<A extends EnumValue<Integer>> implements AutoBin, EnumValue<Integer> {
	@SuppressWarnings({"FieldCanBeLocal","unused"})
	private DateBin date;
	private IntegerBin actionCode;

	public BinaryLine(A a) {
		actionCode = new IntegerBin( a );
		date = new DateBin( new Date() );
	}

	public Integer value() {
		return actionCode.get();
	}
}
