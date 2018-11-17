package io.Binary;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class StringBin extends TypeBin<String> {
	Charset charset;
	
	public static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
	
	public StringBin(String v) {
		this(v, DEFAULT_CHARSET);
	}
	
	public StringBin(String v, Charset c) {
		super(v);
		charset = c;
	}

	@Override
	public int size() {
		return get().getBytes( charset ).length + 1;
	}

	@Override
	public void addBuff(ByteBuffer buff) {
		buff.put( get().getBytes( charset ) );
		buff.put( (byte) 0 );
	}
	
	@Override
	public void read(ByteBuffer buff) {
		StringBuilder sb = new StringBuilder();
		byte r;
		do {
			r = buff.get();
			sb.append( r );
		} while(r != 0);
		
		set( sb.toString() );
	}
}
