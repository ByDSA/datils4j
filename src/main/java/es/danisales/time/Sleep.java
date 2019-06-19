package es.danisales.time;

public abstract class Sleep {
	private static boolean _sleep(long ms) {
		try {
			Thread.sleep( ms );
			return true;
		} catch ( InterruptedException e ) {
			return false;
		}
	}

	public static boolean sleep(long ms) {
		//info("Sleep: " + ms + " milisegundos.");
		return _sleep(ms);
	}

	public static boolean sleepSec(long s) {
		return _sleep(1000 * s);
	}

	public static boolean sleepMin(long m) {
		return _sleep(60 * 1000 * m);
	}
}
