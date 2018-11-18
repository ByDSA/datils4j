package binary.auto;

import java.util.Date;

import binary.DateBin;

public abstract class DateAutoBin implements AutoBin {
	DateBin date;
	
	public DateAutoBin(Date d) {
		date = new DateBin( d );
	}
	
	protected DateAutoBin() { }
}
