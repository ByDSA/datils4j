package es.danisales.io.process;

import es.danisales.process.ProcessAction;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Rm extends ProcessAction {
    public Rm(Path path) {
        this(path, false);
    }

    public Rm(Path path, boolean recursive) {
        super();

        List<String> paramList = new ArrayList<>();
        if (recursive)
            paramList.add("-r");
        paramList.add( path.toAbsolutePath().toString() );

        String[] args = paramList.toArray(new String[0]);

        setFilenameAndParams("rm", args);
    }
}
