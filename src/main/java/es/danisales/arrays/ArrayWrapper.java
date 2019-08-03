package es.danisales.arrays;

import java.util.Arrays;

public class ArrayWrapper<T> {
    final T[] data;

    @SafeVarargs
	public ArrayWrapper(T... v) {
		data = v;
	}
	
	public T get(int n) {
		return data[n];
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode( data );
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Object[])
			return Arrays.equals( data, ((Object[])o) );
		else if (o instanceof ArrayWrapper)
			return Arrays.equals( data, ((ArrayWrapper)o).data );
		else
			return false;
	}
	
	public String toString() {
		return Arrays.toString( data );
	}
}
