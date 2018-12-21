package datastructures;

import java.util.EnumSet;

public class SetUtils {
	public static <E extends Enum<E>> EnumSet<E> of(EnumSet<E>... sets) {
		EnumSet<E> ret = EnumSet.copyOf( sets[0] );
		for (int i = 1; i < sets.length; i++)
			ret.addAll( sets[i] );
		
		return ret;
	}
}
