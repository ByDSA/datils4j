package es.danisales.io.process;

import es.danisales.process.ProcessAction;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused"})
public class Rm {
    private Rm() {
    }

    public static ProcessAction of(Path path, boolean recursive) {
        List<String> paramList = new ArrayList<>();
        if (recursive)
            paramList.add("-r");
        paramList.add( path.toAbsolutePath().toString() );

        String[] args = paramList.toArray(new String[0]);

        return ProcessAction.of("rm", args);
    }

    public static ProcessAction of(Path path) {
        return of(path, false);
    }
}
