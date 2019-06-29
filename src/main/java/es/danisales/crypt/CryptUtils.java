package es.danisales.crypt;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptUtils {
	@SuppressWarnings("WeakerAccess")
	public static byte[] hashFile(File f, HashingAlgorithm alg) throws NoSuchAlgorithmException, IOException {
		byte[] buffer= new byte[8192];
		int count;
		MessageDigest digest = MessageDigest.getInstance(alg.value());
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
		while ((count = bis.read(buffer)) > 0) {
			digest.update(buffer, 0, count);
		}
		bis.close();

		return digest.digest();
	}

	public static byte[] hashFileSHA256(File f) throws IOException {
		try {
			return hashFile(f, HashingAlgorithm.SHA256);
		} catch ( NoSuchAlgorithmException e ) {
			e.printStackTrace();
			System.exit( -1 );
			return null;
		}
	}
}
