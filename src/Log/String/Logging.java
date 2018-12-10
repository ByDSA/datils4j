package Log.String;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import listeners.BiConsumerListener;

public class Logging {
	public static enum SeverityLevel {
		Emergency(0), Alert(1), Critical(2), Error(3), Warning(4), Notice(5), Informational(6), Debug(7); 

		int val;

		private SeverityLevel(int v) {
			val = v;
		}

		public int value() {
			return val;
		}
	}

	public static int defaultErrorCode = -1;

	static Map<SeverityLevel, BiConsumerListener<SeverityLevel, String>> map = new ConcurrentHashMap();
	static Object lock = new Object();

	public static void addListener(SeverityLevel sl, BiConsumer<SeverityLevel, String> f) {
		synchronized(lock) {
			BiConsumerListener<SeverityLevel, String> l = map.get( sl );
			if (l == null) {
				l = new BiConsumerListener();
				map.put( sl, l );
			}

			l.add( f );
		}
	}

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
		System.out.println( new Date() + " Log: " + msg );

		callListeners(SeverityLevel.Debug, msg);
	}
	
	private static void callListeners(SeverityLevel sl, String msg) {
		synchronized(lock) {
			BiConsumerListener<SeverityLevel, String> l = (BiConsumerListener<SeverityLevel, String>)map.get( sl );
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
		fatalError( msg, defaultErrorCode );
	}

	public static void fatalError(String msg, int code) {
		error( "(exit=" + code + ") " + msg);

		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement elem : Thread.currentThread().getStackTrace()) {
			if (first || elem.getClassName().equals( Logging.class.getName() )) {
				first = false; // Elimina línea: java.lang.Thread.getStackTrace(Unknown Source)
				continue;
			}
			sb.append( elem + "\n");
			System.err.println(elem);
		}
		error(sb.toString());
		System.exit( code );
	}

	public static void fatalErrorIf(boolean check, String msg, int code) {
		if (check)
			fatalError(msg, code);
	}

	public static void fatalErrorIf(boolean check, String msg) {
		fatalErrorIf(check, msg, defaultErrorCode);
	}

	public static void fatalErrorIf(boolean check) {
		fatalErrorIf(check, "La condición es falsa");
	}

	public static void fei(boolean check) {
		fatalErrorIf(check);
	}

	public static void fatalErrorIfNull(Object o, String msg, int code) {
		fatalErrorIf(o == null, msg, code);
	}

	public static void fatalErrorIfNull(Object o, String msg) {
		fatalErrorIfNull(o, msg, defaultErrorCode);
	}

	public static void fatalErrorIfNull(Object o) {
		fatalErrorIfNull(o, "Objeto nulo", defaultErrorCode);
	}

	public static void fein(Object o) {
		fatalErrorIfNull(o);
	}
}
