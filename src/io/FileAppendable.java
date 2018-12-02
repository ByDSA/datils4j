package io;

import java.util.List;

public interface FileAppendable<O extends Object> {	
	boolean append(O f);
	boolean append(List<O> f);
}
