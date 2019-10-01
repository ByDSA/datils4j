package es.danisales.io.process;

import es.danisales.process.ProcessAction;

import java.nio.file.Path;

@SuppressWarnings({"unused"})
public class Rm {
    private static final String PROCESS_NAME = "rm";

    private Rm() {
    }

    public static ProcessAction of(Path path) {
        return ProcessAction.from(PROCESS_NAME,
                path.toAbsolutePath().toString()
        );
    }

    public static ProcessAction recursiveOf(Path path) {
        return ProcessAction.from(PROCESS_NAME,
                "-r",
                path.toAbsolutePath().toString()
        );
    }
}
