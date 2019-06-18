package es.danisales.log;

import java.util.Date;

import es.danisales.binary.DateBin;
import es.danisales.binary.IntegerBin;
import es.danisales.binary.auto.AutoBin;
import es.danisales.others.Codeable;

public abstract class BinaryLine<A extends Codeable> implements AutoBin, Codeable {
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
