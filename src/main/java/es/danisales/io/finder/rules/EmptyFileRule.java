package es.danisales.io.finder.rules;

import java.io.File;

public class EmptyFileRule implements FinderRule {
    @Override
    public boolean check(File f) {
        if (f.isFile())
            return f.length() == 0;
        else
            return false;
    }
}
