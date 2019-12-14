package es.danisales.log;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.function.Supplier;

public abstract class BinaryLine<A extends Supplier<Integer>>
		implements Supplier<Integer> {
	@SuppressWarnings({"FieldCanBeLocal","unused"})
	private GregorianCalendar date;
	private int actionCode;

	public BinaryLine(A a) {
		actionCode = a.get();
		date = new GregorianCalendar();
		date.setGregorianChange(new Date());
	}

    @SuppressWarnings("WeakerAccess")
    public abstract byte[] getBytes();

	@Override
	public Integer get() {
		return actionCode;
	}
}
