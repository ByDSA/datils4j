package Log.String;

import java.util.Date;

public class Logging {
	public static int defaultErrorCode = -1;

	public static void log(String msg) {
		System.out.println( new Date() + " Log: " + msg );
	}

	public static void error(String msg) {
		System.err.println( new Date() + " Error: " + msg );
	}

	public static void info(String msg) {
		System.out.println( msg );
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
		for (StackTraceElement elem : Thread.currentThread().getStackTrace()) {
			if (first || elem.getClassName().equals( Logging.class.getName() )) {
				first = false; // Elimina línea: java.lang.Thread.getStackTrace(Unknown Source)
				continue;
			}
			System.err.println(elem);
		}
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
