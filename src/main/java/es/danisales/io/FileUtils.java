package es.danisales.io;

import es.danisales.crypt.hash.Hash;
import es.danisales.functions.ThrowingConsumer;
import es.danisales.io.search.Finder;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("WeakerAccess")
public final class FileUtils {
	private FileUtils() {
	} // noninstantiable

    public static void createParentFolders(File file) {
		File parent = file.getParentFile();
        if (parent == null)
            return;
		boolean done = parent.mkdirs();

		if (!done && !parent.exists())
			throw new RuntimeException("No se pudo crear la carpeta parent " + parent);
	}

    public static boolean fileExists(Path filePath) {
        File f = filePath.toFile();
		return f.exists() && !f.isDirectory();
	}

    @Deprecated
    public static boolean appendObject(Path filepath, ThrowingConsumer<ObjectOutputStream, Exception> f) {
		ObjectOutputStream output;
		try {
            boolean exists = fileExists(filepath);
            FileOutputStream fos = new FileOutputStream(filepath.toString(), exists);
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

    @Deprecated
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

    @Deprecated
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

    @SuppressWarnings("unused")
    public static class Paths {
        public static String stripExtensionFrom(String str) {
            // Handle null case specially.

            if (str == null) return null;

            // Get position from last '.'.

            int pos = str.lastIndexOf(".");

            // If there wasn't any '.' just return the string as is.

            if (pos == -1) return str;

            // Otherwise return the string, up to the dot.

            return str.substring(0, pos);
        }
    }

    @SuppressWarnings("unused")
    public static class Comparators {
        public final static Comparator<File> fullpathComparator = Comparator.comparing(File::getAbsolutePath);
        public final static Comparator<File> filenameComparator = Comparator.comparing(File::getName);
    }

    @SuppressWarnings("unused")
    public static class Duplicates {
        public static List<List<File>> duplicatedFiles(List<File> files, boolean secure, Consumer<File> func) throws IOException {
            Map<File, Hash> file2hashMap = new HashMap<>();
            Map<Hash, List<File>> hash2filesMap = new HashMap<>();

            for (File f : files) {
                func.accept(f);
                Hash hash = defaultHashFileFunction(f);

                file2hashMap.put(f, hash);

                List<File> filesList = hash2filesMap.computeIfAbsent(hash, k -> new ArrayList<>());
                filesList.add(f);
            }

            List<List<File>> ret = null;

            if (secure) ;
            else
                ret = duplicatedFilesInsecure(hash2filesMap);

            return ret;
        }

        static List<List<File>> duplicatedFilesInsecure(Map<Hash, List<File>> hash2filesMap) {
            List<List<File>> ret = new ArrayList<>();

            for (Map.Entry<Hash, List<File>> e : hash2filesMap.entrySet()) {
                Hash hash = e.getKey();
                List<File> list = e.getValue();

                ret.add(list);
            }

            return ret;
        }

        public static Hash defaultHashFileFunction(File f) throws IOException {
            return Hash.sha256fromFile(f);
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyFiles {
        @SuppressWarnings("ResultOfMethodCallIgnored")
        public static void deleteFrom(File folder) {
            List<File> files = getRecursively(folder);
            for (File f : files)
                f.delete();
        }

        public static void deleteAndEmptyParentsFrom(File folder) {
            List<File> files = getRecursively(folder);
            for (File f : files)
                EmptyFolders.deleteFileAndEmptyParents(f);
        }

        static List<File> getRecursively(File folder) {
            return new Finder()
                    .from(folder)
                    .addRule(f -> f.length() == 0)
                    .onlyFiles()
                    .recursively()
                    .find();
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyFolders {
        public static boolean deleteFrom(File folder) {
            boolean ret = false;
            List<File> folders = getRecursively(folder);
            for (File f : folders)
                ret |= deleteFileAndEmptyParents(f);

            return ret;
        }

        @SuppressWarnings("ConstantConditions")
        static List<File> getRecursively(File folder) {
            return new Finder()
                    .from(folder)
                    .addRule(f -> f.listFiles().length == 0)
                    .onlyFolders()
                    .recursively()
                    .find();
        }

        public static boolean deleteFileAndEmptyParents(File f) {
            boolean ret = false;
            if (f.delete()) {
                File parent = f.getParentFile();

                deleteEmptyParents(parent);

                ret = true;
            }
            return ret;
        }

        @SuppressWarnings({"ConstantConditions", "UnusedReturnValue"})
        static boolean deleteEmptyParents(File parent) {
            boolean ret = false;
            while (parent != null && parent.exists() && parent.isDirectory() && parent.list().length == 0) {
                if (parent.delete()) {
                    parent = parent.getParentFile();
                    ret = true;
                }
            }

            return ret;
        }
	}
}
