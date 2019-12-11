package es.danisales.io;

import java.io.IOException;
import java.util.List;

public interface FileAppendable<O> {
    void append(O f) throws IOException;

    void appendAll(List<O> f) throws IOException;
}
