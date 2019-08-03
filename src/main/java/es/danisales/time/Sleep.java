package es.danisales.time;

@SuppressWarnings("unused")
public final class Sleep {
	private Sleep() {
	} // noninstantiable

	private static void _sleep(long ms) {
		try {
			Thread.sleep( ms );
		} catch (InterruptedException ignored) {
		}
	}

	public static void sleep(long ms) {
		//info("Sleep: " + ms + " milisegundos.");
		_sleep(ms);
	}

	public static void sleepSec(long s) {
		_sleep(1000 * s);
	}

	public static void sleepMin(long m) {
		_sleep(60 * 1000 * m);
	}
}
