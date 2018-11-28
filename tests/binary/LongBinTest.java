package binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.Binary.BinaryFileAutoBin;

public class LongBinTest {
	class FileTest extends BinaryFileAutoBin {
		public FileTest() {
			super( "test", "LongBin", "bin" );
		}
		LongBin ib = new LongBin( 0L );
		LongBin ib2 = new LongBin( 16L );
		LongBin ib3 = new LongBin( Long.MIN_VALUE );
		LongBin ib4 = new LongBin( Long.MAX_VALUE );
	}
	
	@Test
	public void bin() {
		LongBin ib = new LongBin( 0L );
		LongBin ib2 = new LongBin( 16L );
		LongBin ib3 = new LongBin( Long.MIN_VALUE );
		LongBin ib4 = new LongBin( Long.MAX_VALUE );
		
		assertArrayEquals(new byte[] {0,0,0,0,0,0,0,0}, ib.getBytes());
		assertArrayEquals(new byte[] {0,0,0,0,0,0,0,0x10}, ib2.getBytes());
		assertArrayEquals(new byte[] {(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00}, ib3.getBytes());
		assertArrayEquals(new byte[] {(byte)0x7F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF}, ib4.getBytes());
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
