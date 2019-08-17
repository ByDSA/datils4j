package es.danisales.log.string;

import es.danisales.listeners.ListenerTwo;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class Logging {
	private static Map<SeverityLevel, ListenerTwo<SeverityLevel, String>> map = new ConcurrentHashMap<>();
	private static final Object lock = new Object();

	private Logging() {
	} // noninstantiable

	@SuppressWarnings("WeakerAccess")
	public static void addListener(SeverityLevel sl, BiConsumer<SeverityLevel, String> f) {
		synchronized(lock) {
			ListenerTwo<SeverityLevel, String> l = map.get(sl);
			if (l == null) {
				l = ListenerTwo.newInstanceSequential();
				map.put( sl, l );
			}

			l.add( f );
		}
	}

	@SuppressWarnings("unused")
	public static void addListener(BiConsumer<SeverityLevel, String> f) {
		synchronized(lock) {
			addListener(SeverityLevel.Alert, f);
			addListener(SeverityLevel.Critical, f);
			addListener(SeverityLevel.Debug, f);
			addListener(SeverityLevel.Emergency, f);
			addListener(SeverityLevel.Error, f);
			addListener(SeverityLevel.Informational, f);
			addListener(SeverityLevel.Notice, f);
			addListener(SeverityLevel.Warning, f);
		}
	}

	public static void log(String msg) {
		System.out.println( new Date() + " es.danisales.log: " + msg );

		callListeners(SeverityLevel.Debug, msg);
	}
	
	private static void callListeners(SeverityLevel sl, String msg) {
		synchronized(lock) {
			ListenerTwo<SeverityLevel, String> l = map.get(sl);
			if (l != null)
				l.call( sl, msg );
		}
	}

	public static void error(String msg) {
		System.err.println( new Date() + " Error: " + msg );

		callListeners(SeverityLevel.Error, msg);
	}

	public static void info(String msg) {
		System.out.println( msg );

		callListeners(SeverityLevel.Informational, msg);
	}

	public static void fatalError() {
		fatalError( "" );
	}

	public static void fatalError(String msg) {
		int defaultErrorCode = -1;
		fatalError( msg, defaultErrorCode);
	}

	@SuppressWarnings("WeakerAccess")
	public static void fatalError(String msg, int code) {
		error( "(exit=" + code + ") " + msg);

		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement elem : Thread.currentThread().getStackTrace()) {
			if (first || elem.getClassName().equals( Logging.class.getName() )) {
				first = false; // Elimina línea: java.lang.Thread.getStackTrace(Unknown Source)
				continue;
			}
			sb.append(elem).append("\n");
			System.err.println(elem);
		}
		error(sb.toString());
		System.exit( code );
	}
}
