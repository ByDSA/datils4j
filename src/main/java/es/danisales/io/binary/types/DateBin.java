package es.danisales.io.binary.types;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class DateBin extends TypeBin<Long> {
	public static final TimeZone GMT = TimeZone.getTimeZone("GMT");
	
	public DateBin(Long v) {
		super( v );
	}
	
	public DateBin(Date v) {
		super( v.getTime() );
	}
	
	public static DateBin of(int y, int m, int d, TimeZone zone) {
		Calendar c = Calendar.getInstance( zone );
		c.clear();
		c.set( y, m-1, d );
		System.out.println( c.getTimeInMillis() );
		return new DateBin(c.getTimeInMillis());
	}
	
	public static DateBin of(int y, int m, int d) {
		return DateBin.of(y, m, d, TimeZone.getDefault());
	}
	
	DateBin() { }
	
	public static DateBin of(ByteBuffer buff) {
		DateBin db = new DateBin();
		db.read( buff );
		
		return db;
	}

	@Override
	public int sizeBytes() {
		return Long.BYTES;
	}

	@Override
	public void write(ByteBuffer buff) {
		buff.putLong( get() );
	}
	
	public Date getDate() {
		return new Date( get() );
	}
	
	@Override
	public void read(ByteBuffer buff) {
		set( buff.getLong() );
	}

}
