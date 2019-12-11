package es.danisales.log;

import es.danisales.io.FileAppendable;
import es.danisales.io.FileReadable;

import java.util.List;

public interface Log<L> extends FileAppendable<L>, FileReadable {
	List<L> buffer();
}
