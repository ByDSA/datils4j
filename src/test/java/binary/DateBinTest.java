package es.danisales.binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import es.danisales.io.Binary.BinaryFileAutoBin;

public class DateBinTest {
	class FileTest extends BinaryFileAutoBin {
		public FileTest() {
			super( "test/DateBin.bin" );
		}
		DateBin ib = DateBin.of( 1970, 1, 1 , DateBin.GMT);
		DateBin ib2 = new DateBin( 16L );
		DateBin ib3 = new DateBin( Long.MIN_VALUE );
		DateBin ib4 = new DateBin( Long.MAX_VALUE );
	}
	
	@Test
	public void bin() {
		FileTest f = new FileTest();
		
		assertArrayEquals(new byte[] {0,0,0,0,0,0,0,0}, f.ib.getBytes());
		assertArrayEquals(new byte[] {0,0,0,0,0,0,0,0x10}, f.ib2.getBytes());
		assertArrayEquals(new byte[] {(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00}, f.ib3.getBytes());
		assertArrayEquals(new byte[] {(byte)0x7F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF}, f.ib4.getBytes());
	}
	
	@Test
	public void ios() {
		FileTest f = new FileTest();
		assert f.save();
		FileTest f2 = new FileTest();
		assert f2.load();
		
		assertEquals((Long)0L, f2.ib.get());
		assertEquals((Long)16L, f2.ib2.get());
		assertEquals((Long)Long.MIN_VALUE, f2.ib3.get());
		assertEquals((Long)Long.MAX_VALUE, f2.ib4.get());
		
		assert f2.delete();
	}
}
