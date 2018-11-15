package Log.String;

import java.util.Date;

public class Logging {
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
		fatalError( msg, -1 );
	}
	
	public static void fatalError(String msg, int code) {
		error( "(exit=" + code + ") " + msg);
		System.exit( code );
	}
}
