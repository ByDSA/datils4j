package io;

import static Log.String.Logging.error;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import functions.ThrowingConsumer;

public final class Files {
	public static final String DATA_FOLDER = "data/";
	public static ArrayList<File> finderRecursive(String dirName, String ext){
		File dir = new File(dirName);

		return finderRecursive(dir, ext);
	}

	public static ArrayList<File> finderRecursive(File dir, String ext){
		ArrayList<File> ret = new ArrayList<File>();

		File[] files = dir.listFiles(); 
		if (files == null) {
			error("El directorio no existe: " + dir.getPath());
			return null;
		}
		for (File file : files) {
			if (file.isFile()) {
				if (file.getName().endsWith("." + ext))
					ret.add(file);
			} else if (file.isDirectory()) {
				ret.addAll( finderRecursive(file, ext) );
			}
		}

		return ret;
	}

	public static String stripExtension(String str) {
		// Handle null case specially.

		if (str == null) return null;

		// Get position of last '.'.

		int pos = str.lastIndexOf(".");

		// If there wasn't any '.' just return the string as is.

		if (pos == -1) return str;

		// Otherwise return the string, up to the dot.

		return str.substring(0, pos);
	}

	public static boolean fileExists(String filePathString) {
		File f = new File(filePathString);
		return f.exists() && !f.isDirectory();
	}

	public static boolean appendObject(String filename, ThrowingConsumer<ObjectOutputStream, Exception> f) {
		ObjectOutputStream output;
		try {
			boolean exists = io.Files.fileExists(filename );
			FileOutputStream fos = new FileOutputStream(filename, exists);
			if (!exists) {
				output = new ObjectOutputStream( fos );
			} else {
				output = new AppendingObjectOutputStream(fos);
			}

			f.acceptThrows( output );
			output.close();
			fos.close();
			return true;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean writeObject(String filename, ThrowingConsumer<ObjectOutputStream, Exception> f) {
		try {
			ObjectOutputStream output;
			FileOutputStream fos = new FileOutputStream(filename);
			output = new ObjectOutputStream( fos );

			f.acceptThrows( output );
			output.close();
			fos.close();
			return true;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean readObject(String filename, ThrowingConsumer<ObjectInputStream, Exception> f) {
		try {
			ObjectInputStream input;
			FileInputStream fos = new FileInputStream(filename);
			input = new ObjectInputStream( fos );
			
			f.acceptThrows( input );
			input.close();
			fos.close();
			return true;
		} catch(IOException e) {
			//e.printStackTrace();
			return false;
		}
	}
}
