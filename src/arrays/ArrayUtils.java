package arrays;

import java.lang.reflect.Array;

public class ArrayUtils {
	public static <T> T[] concat(T[] a, T[] b) {
		int aLen = a.length;
		int bLen = b.length;

		@SuppressWarnings("unchecked")
		T[] c = (T[]) Array.newInstance( a.getClass().getComponentType(), aLen + bLen );
		System.arraycopy( a, 0, c, 0, aLen );
		System.arraycopy( b, 0, c, aLen, bLen );

		return c;
	}

	public static <T> T[] concat(T[]... v) {
		int len = 0;
		for ( T[] t : v )
			len += t.length;

		@SuppressWarnings("unchecked")
		T[] c = (T[]) Array.newInstance( v[0].getClass().getComponentType(), len );
		int iLen = 0;
		for ( T[] t : v ) {
			System.arraycopy( t, 0, c, iLen, t.length );
			iLen += t.length;
		}

		return c;
	}

	public static byte[] concat(byte[] a, byte[] b) {
		int aLen = a.length;
		int bLen = b.length;

		@SuppressWarnings("unchecked")
		byte[] c = (byte[]) Array.newInstance( a.getClass().getComponentType(), aLen + bLen );
		System.arraycopy( a, 0, c, 0, aLen );
		System.arraycopy( b, 0, c, aLen, bLen );

		return c;
	}

	public static <T> boolean contained(T t, T[] array) {
		for ( T a : array )
			if ( a.equals( t ) )
				return true;
		return false;
	}
	
	public static Byte[] byteBoxing(byte[] bytes) {
		Byte[] byteObjects = new Byte[bytes.length];

		int i=0;    
		// Associating Byte array values with bytes. (byte[] to Byte[])
		for(byte b: bytes)
		   byteObjects[i++] = b;  // Autoboxing.
		
		return byteObjects;
	}
}
