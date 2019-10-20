package es.danisales.io;

import es.danisales.functions.ThrowingConsumer;

import java.io.*;
import java.nio.file.Path;
import java.util.Comparator;

@SuppressWarnings("WeakerAccess")
public final class FileUtils {
    private FileUtils() {
    } // noninstantiable

    @SuppressWarnings("ResultOfMethodCallIgnored")
    // Source: https://stackoverflow.com/a/7768086
    public static boolean deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory())
                    deleteFolder(f);
                else
                    f.delete();
            }
        }
        return folder.delete();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean mkdirsParent(File file) {
        File parent = file.getParentFile();

        if (parent == null)
            return false;

        boolean done = parent.mkdirs();

        if (!done && !parent.exists())
            throw new RuntimeException("No se pudo crear la carpeta parent " + parent);

        return done;
    }

    @Deprecated
    public static boolean fileExists(Path filePath) {
        File f = filePath.toFile();
        return f.exists() && !f.isDirectory();
    }

    public static boolean rename(File file, String newNameWithExtension) {
        boolean ret = !file.getName().equals(newNameWithExtension);
        return ret && file.renameTo(new File(file.getAbsolutePath() + "/" + newNameWithExtension));
    }

    @Deprecated
    public static boolean appendObject(Path filepath, ThrowingConsumer<ObjectOutputStream, Exception> f) {
        ObjectOutputStream output;
        try {
            boolean exists = fileExists(filepath);
            FileOutputStream fos = new FileOutputStream(filepath.toString(), exists);
            if (!exists) {
                output = new ObjectOutputStream(fos);
            } else {
                output = new AppendingObjectOutputStream(fos);
            }

            f.acceptThrows(output);
            output.close();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

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
        public final static Comparator<File> filenameComparator = (o1, o2) -> {
            String fname1 = o1.getName().toLowerCase();
            String fname2 = o2.getName().toLowerCase();
            return fname1.compareTo(fname2);
        };
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
}
