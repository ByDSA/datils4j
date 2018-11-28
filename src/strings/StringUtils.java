package strings;

public class StringUtils {
	public static String replaceLast(String base, String find, String replacement) {
		int ind = base.lastIndexOf(find);
		if( ind>=0 )
			base = new StringBuilder(base).replace(ind, ind+find.length(), replacement).toString();
		return base;
	}
}
