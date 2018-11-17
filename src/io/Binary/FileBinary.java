package io.Binary;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import Log.String.Logging;
import concurrency.Lockable;
import io.FileRWAdapter;
import io.FileReadable;
import io.FileWritable;

public class FileBinary extends FileRWAdapter implements BinaryElement, FileWritable, FileReadable, Binary, Lockable {	
	public FileBinary(String folder, String fn, String ext) {
		super( folder, fn, ext );
	}

	@Override
	public boolean write() {
		try {
			Logging.info( "write: " + path().toAbsolutePath() );
			Files.createDirectories( path().getParent() );
			Files.write( path(), getBytes());
			return true;
		} catch ( IOException e ) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void addBuff(ByteBuffer buff) {
		it((Binary b) -> {
			b.addBuff( buff );
		}, (BooleanArrayBin bab) -> {
			bab.addBuff( buff );
		});
	}

	@Override
	public boolean save() {
		write();
		return true;
	}

	public static byte[] getBytes(ByteBuffer buff, final int N) {
		byte[] ret = new byte[N];
		for(int i = 0; i < N; i++)
			ret[i] = buff.get();

		return ret;
	}

	@Override
	public void read(ByteBuffer buff) {
		Class c = null;
		try {
			updateBinaryFields();

			for(Field f : binaryFields) {
				c = f.getType();
				
				f.setAccessible(true);
				
				f.set( this, c.newInstance() );

				Method readMethod = c.getDeclaredMethod( "read", ByteBuffer.class );
				readMethod.invoke( f.get( this ), buff );
			}

			if (booleanFields.size() > 0) {
				final int N = booleanFields.size() / 8 + 1;
				int bit = 0, n = 0;
				byte[] bytesBoolean = getBytes( buff, N );
				for(Field f : booleanFields) {
					boolean value = (((bytesBoolean[n] >> bit) & 1) == 1);
					f.set( this, new BooleanBin(value) );

					bit++;
					if (bit == 8) {
						n++;
						bit = 0;
					}
				}
			}

		} catch ( NoSuchMethodException e) {
			Logging.fatalError("La clase " + c + " no tiene el método read.");
		} catch(InstantiationException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
			e.printStackTrace();
			Logging.fatalError();
		}
	}

	@Override
	public boolean load() {
		ByteBuffer buff;
		try {
			buff = ByteBuffer.wrap( Files.readAllBytes(path()) );
		} catch ( IOException e ) {
			return false;
		}
		read(buff);
		return true;
	}

	@Override
	public int size() {
		initializeNull();

		AtomicInteger s = new AtomicInteger(0);
		it((Binary b)-> {
			s.set( s.addAndGet( b.size() ) );
		}, (BooleanArrayBin bab) -> {
			s.set( s.addAndGet( bab.size() ) );
		});

		return s.get();
	}
}
