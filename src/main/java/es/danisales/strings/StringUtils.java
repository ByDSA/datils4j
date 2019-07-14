package es.danisales.strings;

public class StringUtils {
	private StringUtils() {
	} // noninstantiable

	public static String replaceLast(String base, String find, String replacement) {
		int ind = base.lastIndexOf(find);
		if( ind>=0 )
			base = new StringBuilder(base).replace(ind, ind+find.length(), replacement).toString();
		return base;
	}

	public static String zerosPad(int n, int cifras) {
		return leftPad(Integer.toString( n ), '0', cifras);
	}

	public static String leftPad(String base, char padChar, int cifras) {
		StringBuilder zs = new StringBuilder();

		for (int i = 0; i < cifras - base.length(); i++)
			zs.append( padChar );

		zs.append( base );

		return zs.toString();
	}

	public static String join(String delim, String[] array, int ini) {
		int end = array.length-1;
		return join(delim, array, ini, end);
	}

	public static String join(String delim, String[] array, int ini, int end) {
		StringBuilder paramsStrBuilder = new StringBuilder();
		for (int i = ini; i <= end; i++) {
			paramsStrBuilder.append(array[i]);
			if (i < end)
				paramsStrBuilder.append(delim);
		}

		return paramsStrBuilder.toString();
	}
}
