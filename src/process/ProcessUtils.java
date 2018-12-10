package process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

import Log.String.Logging;

public class ProcessUtils {
	public static int execute(String fname, List<String> params) {
		String[] paramsArray = new String[params.size()];
		for (int i = 0; i < params.size(); i++)
			paramsArray[i] = params.get( i );
		return execute(fname, paramsArray);
	}
	
	public static int execute(String fname, String[] params) {
		String[] paramsWithName = new String[ params.length +1 ];
		paramsWithName[0] = fname;
		StringBuilder paramsStr = new StringBuilder();
		for (int i = 1; i <= params.length; i++) {
			paramsWithName[i] = params[i-1];
			paramsStr.append( params[i-1] + " ");
		}

		int result = 1;
		try {
			Logging.log("Executing " + fname + " " + paramsStr.toString());
			final Process p = Runtime.getRuntime().exec(paramsWithName);
			Thread thread = new Thread() {
				public void run() {
					try {
						String line;
						BufferedReader input =
								new BufferedReader
								(new InputStreamReader(p.getInputStream()));

						while ((line = input.readLine()) != null)
							Logging.log(line);


						input.close();
					} catch(Exception e) {}
				}
			};
			Thread thread2 = new Thread() {
				public void run() {
					try {
						String line;
						BufferedReader input =
								new BufferedReader
								(new InputStreamReader(p.getErrorStream()));

						while ((line = input.readLine()) != null)
							Logging.error(line);


						input.close();
					} catch(Exception e) {}
				}
			};
			thread.start();
			thread2.start();
			result = p.waitFor();
			thread.join();
			if (result != 0) {
				Logging.error("Process failed with status: " + result);
			}
		} catch (IOException | InterruptedException e) {Logging.log(" procccess not read"+e);}

		return result;
	}
	
	public static void rm(Path path) {
		rm(path, false);
	}

	public static void rm(Path path, boolean r) {
		String args = (r ? "-r " : "") + path.toAbsolutePath().toString();

		int ret = ProcessUtils.execute("rm", new String[] {args});

		if ( ret != 0 )
			throw new RuntimeException("asd");
	}
}
