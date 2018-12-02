package datastructures;

import java.util.Arrays;

public class ArrayWrapper<K> {
	K[] values;

	public ArrayWrapper(K[] v) {
		values = v;
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode( values );
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Object[])
			return Arrays.deepEquals( values, ((Object[])o) );
		else if (o instanceof ArrayWrapper)
			return Arrays.deepEquals( values, ((ArrayWrapper)o).values );
		else
			return false;
	}
}
