package es.danisales.datastructures;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

@SuppressWarnings("unused")
public class SetUtils {
	private SetUtils() {
	} // noninstantiable

    @SuppressWarnings("WeakerAccess")
    public static <E extends Enum<E>> EnumSet<E> concat(@NonNull List<EnumSet<E>> sets) {
        checkArgument(sets.size() > 0);

        EnumSet<E> ret = EnumSet.copyOf(sets.get(0));
        for (int i = 1; i < sets.size(); i++)
            ret.addAll(sets.get(i));

        return ret;
    }

    @SafeVarargs
    public static <E extends Enum<E>> EnumSet<E> concat(@NonNull EnumSet<E>... sets) {
        return concat(Arrays.asList(sets));
    }

    @SafeVarargs
    public static <T> Set<T> concatImmutable(Set<T>... sets) {
        Set<T> result = new HashSet<>();

        for (Set<T> l : sets)
            result.addAll(l);

        return Collections.unmodifiableSet(result);
    }
}
