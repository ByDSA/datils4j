package es.danisales.arrays;

import java.util.Arrays;

public class ArrayWrapperInteger extends ArrayWrapper<Integer> {
	public ArrayWrapperInteger(Integer... d) {
		super(d);
	}

	@Override
	public boolean equals(Object o) {
		if ( o instanceof Integer[] ) {
			Integer[] a = (Integer[]) o;
			return Arrays.equals( data, a );
		} else
			return super.equals( o );
	}
}