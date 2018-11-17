package io.Binary;

import java.util.Date;

public abstract class BinaryDateElement implements BinaryElement {
	DateBin date;
	
	public BinaryDateElement(Date d) {
		date = new DateBin( d );
	}
	
	protected BinaryDateElement() { }
}
