package binary;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import Log.String.Logging;

public final class BooleanArrayBin extends TypeBin<List<Boolean>> {	
	public BooleanArrayBin(List<Boolean> v) {
		super(v);
	}
	
	public BooleanArrayBin() {
		super(new ArrayList());
	}
	
	public static BooleanArrayBin of(List<BooleanBin> v) {
		List<Boolean> list = new ArrayList<>();
		for (BooleanBin b : v)
			list.add( b.get() );
		
		return new BooleanArrayBin(list);
	}
	
	public Boolean add(Boolean b) {
		get().add( b );
		
		return b;
	}

	@Override
	public int sizeBytes() {
		int s = get().size();
		return s == 0 ? 0 : s/8+1;
	}

	@Override
	public void write(ByteBuffer buff) {
		int n = -1;
		byte[] encodedBytes = new byte[sizeBytes()];
		for (boolean b : get()) {
			n++;
			if (!b)
				continue;

			switch(n%8) {
				case 0: encodedBytes[n/8] |= 0x1; break;
				case 1: encodedBytes[n/8] |= 0x2; break;
				case 2: encodedBytes[n/8] |= 0x4; break;
				case 3: encodedBytes[n/8] |= 0x8; break;
				case 4: encodedBytes[n/8] |= 0x10; break;
				case 5: encodedBytes[n/8] |= 0x20; break;
				case 6: encodedBytes[n/8] |= 0x40; break;
				case 7: encodedBytes[n/8] |= 0x80; break;
			}
		}
		buff.put( encodedBytes );
	}

	@Override
	public void read(ByteBuffer buff) {
		// TODO Auto-generated method stub
		Logging.fatalError();
	}
}
