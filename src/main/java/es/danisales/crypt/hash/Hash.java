package es.danisales.crypt.hash;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@SuppressWarnings("unused")
public class Hash {
    private final byte[] hash;

    private Hash(byte[] hash) {
        this.hash = hash;
    }

    public static Hash from(byte[] hash) {
        return new Hash(hash);
    }

    public byte[] getBytes() {
        return hash;
    }

    /** File **/

    @SuppressWarnings("WeakerAccess")
    public static Hash fromFile(File f, Algorithm alg) throws NoSuchAlgorithmException, IOException {
        byte[] buffer = new byte[8192];
        int count;
        MessageDigest digest = MessageDigest.getInstance(alg.getStringCode());
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
        while ((count = bis.read(buffer)) > 0) {
            digest.update(buffer, 0, count);
        }
        bis.close();

        return Hash.from(digest.digest());
    }

    @SuppressWarnings("unused")
    public static Hash sha256fromFile(File f) throws IOException {
        try {
            return fromFile(f, Algorithm.SHA256);
        } catch (NoSuchAlgorithmException unused) {
            // impossible!
            return null;
        }
    }

    /** Override Object **/

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Override
    public boolean equals(Object o) {
        if (o instanceof byte[])
            return Arrays.equals(hash, (byte[])o);
        if (o instanceof Hash)
            return equals(((Hash)o).hash);

        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(hash);
    }

    @Override
    public String toString() {
        return Arrays.toString(hash);
    }
}
