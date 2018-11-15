package Log;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;

import io.Binary.BinaryElement;
import io.Binary.DateBin;
import io.Binary.IntegerBin;
import others.Codeable;

public abstract class BinaryLine<A extends Codeable> implements BinaryElement, Codeable {
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
