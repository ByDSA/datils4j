package io;

import static Log.String.Logging.error;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import arrays.ArrayUtils;
import arrays.ArrayWrapper;
import crypt.CryptUtils;
import functions.ThrowingConsumer;

public final class FileUtils {
	public static final String DATA_FOLDER = "data/";

	public static ArrayList<java.io.File> finderRecursive(String dirName, String ext){
		Path dir = new java.io.File(dirName).toPath();

		return finderRecursive(dir, ext);
	}

	public static ArrayList<java.io.File> finderRecursive(Path dir, String ext){
		ArrayList<java.io.File> ret = new ArrayList<java.io.File>();

		java.io.File[] files = dir.toFile().listFiles(); 
		if (files == null) {
			error("El directorio no existe: " + dir);
			return null;
		}
		for (java.io.File file : files) {
			if (file.isFile()) {
				if (file.getName().endsWith("." + ext))
					ret.add(file);
			} else if (file.isDirectory()) {
				ret.addAll( finderRecursive(file.toPath(), ext) );
			}
		}

		return ret;
	}

	public static ArrayList<java.io.File> find(Path dir, String ext){
		ArrayList<java.io.File> ret = new ArrayList<java.io.File>();

		java.io.File[] files = dir.toFile().listFiles(); 
		if (files == null) {
			error("El directorio no existe: " + dir);
			return null;
		}
		for (java.io.File file : files) {
			if (file.isFile()) {
				if (file.getName().endsWith("." + ext))
					ret.add(file);
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
		java.io.File f = new java.io.File(filePathString);
		return f.exists() && !f.isDirectory();
	}

	public static boolean appendObject(String filename, ThrowingConsumer<ObjectOutputStream, Exception> f) {
		ObjectOutputStream output;
		try {
			boolean exists = fileExists(filename );
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

	public final static Comparator<java.io.File> fullpathComparator = new Comparator<java.io.File>() {
		@Override
		public int compare(java.io.File arg0, java.io.File arg1) {
			return arg0.getAbsolutePath().compareTo( arg1.getAbsolutePath() );
		}
	};

	public final static Comparator<java.io.File> filenameComparator = new Comparator<java.io.File>() {
		@Override
		public int compare(java.io.File arg0, java.io.File arg1) {
			return arg0.getName().compareTo( arg1.getName() );
		}
	};


	public static List<java.io.File> findFilesRecursive(Path dir, Function<java.io.File, Boolean> funcFile, Function<java.io.File, Boolean> funcFolder) {
		List<java.io.File> ret = new ArrayList<java.io.File>();

		java.io.File[] files = dir.toFile().listFiles(); 
		if (files == null) {
			error("El directorio no existe: " + dir);
			return null;
		}
		for (java.io.File file : files) {
			if (file.isFile() && (funcFile == null || funcFile.apply( file ) )) {
				ret.add(file);
			} else if (file.isDirectory() && (funcFolder == null || funcFolder.apply( file ) )) {
				ret.addAll( findFilesRecursive(file.toPath(), funcFile, funcFolder) );
			}
		}

		return ret;
	}

	public static List<java.io.File> findRecursive(Path dir, Function<java.io.File, Boolean> funcFile, Function<java.io.File, Boolean> funcFolder) {
		List<java.io.File> ret = new ArrayList<java.io.File>();

		java.io.File[] files = dir.toFile().listFiles(); 
		if (files == null) {
			error("El directorio no existe: " + dir);
			return null;
		}
		for (java.io.File file : files) {
			assert file != null;
			if (funcFile == null || funcFile.apply( file ) )
				ret.add(file);

			if (file.isDirectory() && (funcFolder == null || funcFolder.apply( file ) )) {
				ret.addAll( findRecursive(file.toPath(), funcFile, funcFolder) );
			}
		}

		return ret;
	}

	public static List<java.io.File> findFilesRecursive(List<Path> dirs) {
		return findFilesRecursive(dirs, null, null);
	}

	public static List<java.io.File> findFilesRecursive(List<Path> dirs, Function<java.io.File, Boolean> funcFile, Function<java.io.File, Boolean> funcFolder) {
		List<java.io.File> ret = new ArrayList<java.io.File>();
		for (Path p : dirs)
			ret.addAll( findFilesRecursive(p, funcFile, funcFolder) );

		return ret;
	}

	public static List<List<java.io.File>> duplicatedFiles(List<java.io.File> files, boolean secure, Consumer<java.io.File> func) throws IOException {
		Map<java.io.File, byte[]> file2hashMap = new HashMap();
		Map<ArrayWrapper<Byte>, List<java.io.File>> hash2filesMap = new HashMap();

		for (java.io.File f : files) {
			func.accept( f );
			byte[] hash = defaultHashFileFunction( f );

			file2hashMap.put( f, hash );

			ArrayWrapper<Byte> hashWrapper = new ArrayWrapper( ArrayUtils.byteBoxing( hash ) );
			List<java.io.File> l = hash2filesMap.get( hashWrapper );
			if (l == null) {
				l = new ArrayList();
				hash2filesMap.put( hashWrapper, l );
			}
			l.add( f );
		}

		List<List<java.io.File>> ret = null;

		if (secure);
		else
			ret = duplicatedFilesInsecure(hash2filesMap);

		return ret;
	}

	static List<List<java.io.File>> duplicatedFilesInsecure(Map<ArrayWrapper<Byte>, List<java.io.File>> hash2filesMap) {
		List<List<java.io.File>> ret = new ArrayList<>();

		hash2filesMap.forEach( (hash, list) -> {
			ret.add( list );
		});

		return ret;
	}

	public static byte[] defaultHashFileFunction(java.io.File f) throws IOException {
		return CryptUtils.hashFileSHA256( f );
	}

	public static List<java.io.File> getEmptyFolders(Path p) {
		List<java.io.File> files = io.FileUtils.findRecursive( p, (java.io.File f) -> {
			return f.isDirectory() && f.list().length == 0;
		}, null );

		return files;
	}

	public static List<java.io.File> getEmptyFiles(Path p) {
		List<java.io.File> files = io.FileUtils.findFilesRecursive( p, (java.io.File f) -> {
			return f.length() == 0;
		}, null );

		return files;
	}

	public static List<java.io.File> getEmptyElements(Path p) {
		List<java.io.File> files = io.FileUtils.findRecursive( p, (java.io.File f) -> {
			assert f != null;
			//assert f.exists() && (f.isDirectory() && f.list() != null || f.isFile()) : f;
			return f.exists() && (f.isDirectory() && f.listFiles() != null && f.list().length == 0 || f.isFile() && f.length() == 0);
		}, null );

		return files;
	}

	public static List<java.io.File> deleteEmptyElements(Path p) {
		List<java.io.File> files = getEmptyElements(p);
		List<java.io.File> ret = new ArrayList();

		for(java.io.File f : files)
			ret.addAll( deleteItAndEmptyParents(f) );

		return ret;
	}

	public static List<java.io.File> deleteItAndEmptyParents(java.io.File f) {
		List<java.io.File> ret = new ArrayList();
		if ( f.delete() ) {
			ret.add( f );

			java.io.File parent = f.getParentFile();

			ret.addAll( deleteEmptyParents(parent) );
		}
		return ret;
	}

	public static List<java.io.File> deleteEmptyParents(java.io.File parent) {
		List<java.io.File> ret = new ArrayList();
		while (parent != null && parent.exists() && parent.isDirectory() && parent.list().length == 0) {
			if ( parent.delete() ) {
				ret.add( parent );
				parent = parent.getParentFile();
			}
		}

		return ret;
	}

	public static List<java.io.File> getUniqueElements(Path p) {
		List<java.io.File> folders = findRecursive(p, (java.io.File f) -> {
			java.io.File parent = f.getParentFile();
			return parent.listFiles().length == 1;
		}, null);

		return folders;
	}

	public static List<java.io.File> getUniqueFiles(Path p) {
		List<java.io.File> files = findFilesRecursive(p, (java.io.File f) -> {
			java.io.File parent = f.getParentFile();
			return parent.listFiles().length == 1;
		}, null);

		return files;
	}

	public static List<java.io.File> getUniqueFolders(Path p) {
		List<java.io.File> folders = findRecursive(p, (java.io.File f) -> {
			if (f.isDirectory()) {
				java.io.File parent = f.getParentFile();
				return parent.listFiles().length == 1;
			} else
				return false;
		}, null);

		return folders;
	}
}
