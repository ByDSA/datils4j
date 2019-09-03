package es.danisales.io.process;

import es.danisales.process.ProcessActionAdapter;

import java.nio.file.Path;

@SuppressWarnings({"unused"})
public class Rm extends ProcessActionAdapter {
    static final String PROCESS_NAME = "rm";

    private Rm() {
    }

    public static Rm of(Path path) {
        String[] fnameAndParams = new String[]{
                PROCESS_NAME,
                path.toAbsolutePath().toString()
        };

        return ProcessActionAdapter.of(Rm.class, fnameAndParams);
    }

    public static Rm recursiveOf(Path path) {
        String[] fnameAndParams = new String[]{
                PROCESS_NAME,
                "-r",
                path.toAbsolutePath().toString()
        };

        return ProcessActionAdapter.of(Rm.class, fnameAndParams);
    }
}
