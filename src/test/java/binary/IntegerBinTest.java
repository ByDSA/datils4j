package es.danisales.binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import es.danisales.io.Binary.BinaryFileAutoBin;

public class IntegerBinTest {
	class FileTest extends BinaryFileAutoBin {
		public FileTest() {
			super( "test/IntegerBin.bin" );
		}
		IntegerBin ib = new IntegerBin( 0 );
		IntegerBin ib2 = new IntegerBin( 16 );
		IntegerBin ib3 = new IntegerBin( Integer.MIN_VALUE );
		IntegerBin ib4 = new IntegerBin( Integer.MAX_VALUE );
	}
	
	@Test
	public void bin() {
		IntegerBin ib = new IntegerBin( 0 );
		IntegerBin ib2 = new IntegerBin( 16 );
		IntegerBin ib3 = new IntegerBin( Integer.MIN_VALUE );
		IntegerBin ib4 = new IntegerBin( Integer.MAX_VALUE );
		
		assertArrayEquals(new byte[] {0,0,0,0}, ib.getBytes());
		assertArrayEquals(new byte[] {0,0,0,0x10}, ib2.getBytes());
		assertArrayEquals(new byte[] {(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00}, ib3.getBytes());
		assertArrayEquals(new byte[] {(byte)0x7F,(byte)0xFF,(byte)0xFF,(byte)0xFF}, ib4.getBytes());
	}
	
	@Test
	public void ios() {
		FileTest f = new FileTest();
		assert f.save();
		FileTest f2 = new FileTest();
		assert f2.load();
		
		assertEquals((Integer)0, f2.ib.get());
		assertEquals((Integer)16, f2.ib2.get());
		assertEquals((Integer)Integer.MIN_VALUE, f2.ib3.get());
		assertEquals((Integer)Integer.MAX_VALUE, f2.ib4.get());
		
		assert f2.delete();
	}
}
