package es.danisales.strings;

import com.google.common.collect.Range;
import org.checkerframework.checker.nullness.qual.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

@SuppressWarnings("unused")
public final class StringUtils {
	private StringUtils() {
	} // noninstantiable

    public static String replaceLastOccurrence(String base, String toBeReplaced, String replacement) {
        int ind = base.lastIndexOf(toBeReplaced);
        if (ind >= 0)
            base = new StringBuilder(base).replace(ind, ind + toBeReplaced.length(), replacement).toString();
		return base;
	}

    public final static class PadChar {
        private PadChar() {
        } // noninstantiable

        public static String zerosLeft(int n, int cifras) {
            return left(Integer.toString(n), '0', cifras);
        }

        @SuppressWarnings("WeakerAccess")
        public static String left(String base, char padChar, int maxDigits) {
            StringBuilder zs = new StringBuilder();

            for (int i = 0; i < maxDigits - base.length(); i++)
                zs.append(padChar);

            zs.append(base);

            return zs.toString();
        }
    }

    public final static class Join {
        private Join() {
        } // noninstantiable

        private static void checkValidLengthArray(String... array) throws IllegalArgumentException {
            checkArgument(array.length > 0, "The array has no content");
        }

        @SuppressWarnings("WeakerAccess")
        public static String from(@NonNull String delim, @NonNull String... array) throws IllegalArgumentException {
            checkValidLengthArray(array);

            int upperIndex = array.length - 1;
            Range<Integer> range = Range.closed(0, upperIndex);

            return fromRange(range, delim, array);
        }

        public static String from(@NonNull String... array) throws IllegalArgumentException {
            return from(",", array);
        }

        @SuppressWarnings("WeakerAccess")
        public static String fromRange(@NonNull Range<Integer> rangeOfIndexesToJoin, @NonNull String delim, @NonNull String... array) throws IllegalArgumentException {
            checkValidLengthArray(array);

            StringBuilder paramsStrBuilder = new StringBuilder();
            for (int i = rangeOfIndexesToJoin.lowerEndpoint(); i <= rangeOfIndexesToJoin.upperEndpoint(); i++) {
                paramsStrBuilder.append(array[i]);
                if (i < rangeOfIndexesToJoin.upperEndpoint())
                    paramsStrBuilder.append(delim);
            }

            return paramsStrBuilder.toString();
        }
	}
}
