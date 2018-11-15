package net;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientRaw {

	public static String raw(String host, String message) {
		String text = null;
		try {
			Socket socket = new Socket(host, 80);

			BufferedWriter out = new BufferedWriter(
				new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
			BufferedReader in = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));

			sendMessage(out, message);
			text = readResponse(in);
			System.out.println( "done" );
			out.close();
			in.close();
			socket.close();
			System.out.println( "closed" );
		} catch (IOException e) {
			e.printStackTrace();
			System.exit( -1 );
		}
		
		System.out.println( "end" );

		return text;
	}

	private static void sendMessage(BufferedWriter out, String message) throws IOException {

		out.write(message);
		out.flush();
	}

	private static String readResponse(BufferedReader in) throws IOException {
		System.out.println("\n * Response");

		StringBuilder text = new StringBuilder();
		String line;
		while ((line = in.readLine()) != null) {
			System.out.println(" " + line);
			text.append( line );
		}

		return text.toString();
	}

	private List<String> getContents(File file) throws IOException {
		List<String> contents = new ArrayList<String>();

		BufferedReader input = new BufferedReader(new FileReader(file));
		String line;
		while ((line = input.readLine()) != null) {
			contents.add(line);
		}
		input.close();

		return contents;
	}
}
