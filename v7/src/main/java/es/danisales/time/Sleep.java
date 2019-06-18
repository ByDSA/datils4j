package es.danisales.time;

import static es.danisales.log.string.Logging.*;

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
		info("Sleep: " + ms + " milisegundos.");
		return _sleep(ms);
	}
	
	public static boolean sleep(long ms, boolean b) {
		if (b)
			info("Sleep: " + ms + " milisegundos.");
		return _sleep(ms);
	}

	public static boolean sleepSec(long s, boolean b) {
		if (b)
			info("Sleep: " + s + " segundos.");
		return _sleep(1000 * s);
	}

	public static boolean sleepSec(long s) {
		return sleepSec(s, true);
	}

	public static boolean sleepMin(long min, boolean b) {
		if (b)
			info("Sleep: " + min + " minutos.");
		return _sleep(60 * 1000 * min);
	}

	public static boolean sleepMin(long m) {
		return sleepMin(m, true);
	}
}
