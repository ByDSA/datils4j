package es.danisales.io.finder.rules;

import java.io.File;

public class EmptyFolderRule implements FinderRule {
    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean check(File f) {
        if (f.isDirectory())
            return f.listFiles().length == 0;
        else
            return false;
    }
}
