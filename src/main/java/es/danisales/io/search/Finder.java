package es.danisales.io.search;

import es.danisales.io.search.rules.ExtensionRule;
import es.danisales.io.search.rules.FinderRule;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

final public class Finder {
    private Boolean recursively;
    private File folderBase;
    private List<FinderRule> rules = new ArrayList<>();
    private boolean onlyReturnFiles = false;
    private boolean onlyReturnFolders = false;

    public Finder nonRecursively() {
        recursively = false;

        return self();
    }

    public Finder recursively() {
        recursively = true;

        return self();
    }

    public Finder byExtension(String ext) {
        rules.add(new ExtensionRule(ext));

        onlyReturnFiles = true;

        return self();
    }

    public Finder from(File folder) {
        folderBase = folder;

        return self();
    }

    public Finder addRule(FinderRule rule) {
        rules.add(rule);

        return self();
    }

    public List<File> find() {
        if (recursively == null)
            throw new IllegalStateException("Recursively not defined");

        if (folderBase == null || !folderBase.exists() || !folderBase.isDirectory())
            throw new IllegalStateException("Folder not defined, not exists or is not a directory");

        List<File> ret = findingProcess(folderBase);
        if (ret == null)
            ret = new ArrayList<>();
        return ret;
    }

    protected Finder self() {
        return this;
    }

    private boolean checkRules(File f) {
        for (FinderRule r : rules)
            if (!r.check(f))
                return false;

        return true;
    }

    private ArrayList<File> findingProcess(File dir) {
        ArrayList<File> ret = new ArrayList<>();

        File[] files = dir.listFiles();
        if (files == null) {
            //error("El directorio no existe: " + dir);
            return null;
        }
        for (File file : files) {
            if (file.isFile()) {
                if (checkRules(file) && !onlyReturnFolders)
                    ret.add(file);
            } else if (recursively && file.isDirectory()) {
                if (checkRules(file) && !onlyReturnFiles)
                    ret.add(file);

                List<File> ret2 = findingProcess(file);
                if (ret2 != null)
                    ret.addAll(ret2);
            }
        }

        return ret;
    }

    public Finder onlyFiles() {
        onlyReturnFiles = true;

        return self();
    }

    public Finder onlyFolders() {
        onlyReturnFolders = true;

        return self();
    }
}
