package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import binary.BooleanBin;
import binary.IntegerBin;
import io.Binary.BinaryFileAutoBin;

public class IoBinaryTests {
	class TestBin extends BinaryFileAutoBin {
		public BooleanBin b1, b2, b3, b4, b5, b6, b7, b8;
		public IntegerBin tt;
		
		public TestBin(String folder, String fn, String ext) {
			super( folder, fn, ext );
		}
		
		public void assign() {
			tt = new IntegerBin(5);
			b1 = new BooleanBin(true);
			b2 = new BooleanBin(false);
			b3 = new BooleanBin(false);
			b4 = new BooleanBin(true);
			b5 = new BooleanBin(true);
			b6 = new BooleanBin(false);
			b7 = new BooleanBin(true);
			b8 = new BooleanBin(false);
		}
	}
	
	class TestBin2 extends TestBin {
		public IntegerBin subInteger;
		public BooleanBin b9;
		public TestBin2(String folder, String fn, String ext) {
			super( folder, fn, ext );
		}
		
		public void assign() {
			super.assign();
			
			subInteger = new IntegerBin(3);
			b9 = new BooleanBin(true);
		}
		
	}

	@Test
	public void integer() {
		TestBin2 t = new TestBin2( "", "test", "bin" );
		t.assign();
		//assertEquals(Integer.BYTES, t.size());
		t.save();
		
		TestBin2 t2 = new TestBin2( "", "test", "bin" );
		t2.load();
		assertEquals((Integer)5, t2.tt.get());
		assertEquals(true, t2.b1.get());
		assertEquals(false, t2.b2.get());
		assertEquals(false, t2.b3.get());
		assertEquals(true, t2.b4.get());
		assertEquals(true, t2.b5.get());
		assertEquals(false, t2.b6.get());
		assertEquals(true, t2.b7.get());
		assertEquals(false, t2.b8.get());
		assertEquals(true, t2.b9.get());
		assertEquals((Integer)3, t2.subInteger.get());
	}
}
