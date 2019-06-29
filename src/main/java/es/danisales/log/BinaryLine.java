package es.danisales.log;

import java.util.Date;

import es.danisales.io.binary.types.DateBin;
import es.danisales.io.binary.types.IntegerBin;
import es.danisales.io.binary.types.auto.AutoBin;
import es.danisales.others.Codeable;

public abstract class BinaryLine<A extends Codeable> implements AutoBin, Codeable {
	@SuppressWarnings({"FieldCanBeLocal","unused"})
	private DateBin date;
	private IntegerBin actionCode;

	public BinaryLine(A a) {
		actionCode = new IntegerBin( a );
		date = new DateBin( new Date() );
	}

	public int getCode() {
		return actionCode.get();
	}
}
