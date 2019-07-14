package es.danisales.datastructures;

import java.util.EnumSet;

@SuppressWarnings("unused")
public class SetUtils {
	private SetUtils() {
	} // noninstantiable

	@SuppressWarnings("all")
	public static <E extends Enum<E>> EnumSet<E> concat(EnumSet<E>... sets) {
		EnumSet<E> ret = EnumSet.copyOf( sets[0] );
		for (int i = 1; i < sets.length; i++)
			ret.addAll( sets[i] );
		
		return ret;
	}
}
