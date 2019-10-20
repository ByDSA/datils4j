package es.danisales.io.search;

import es.danisales.io.FileUtils;
import es.danisales.io.search.rules.EmptyFolderRule;
import es.danisales.io.search.rules.ExtensionRule;
import es.danisales.io.search.rules.FinderRule;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final public class Finder {
    private Boolean recursively;
    private File folderBase;
    private List<FinderRule> rules = new ArrayList<>();
    private ReturnMode returnMode = ReturnMode.Misc;
    private Comparator<File> sorter;

    @SuppressWarnings("WeakerAccess")
    public Finder nonRecursively() {
        recursively = false;

        return self();
    }

    @SuppressWarnings("WeakerAccess")
    public Finder recursively() {
        recursively = true;

        return self();
    }

    @SuppressWarnings("WeakerAccess")
    public Finder byExtension(String ext) {
        setReturnMode(ReturnMode.Files);

        return addRule(new ExtensionRule(ext));
    }

    private void setReturnMode(ReturnMode mode) {
        if (returnMode == ReturnMode.Misc)
            returnMode = mode;
        else
            throw new IllegalStateException("Return type cannot be changed twice");
    }

    @SuppressWarnings("WeakerAccess")
    public Finder emptyFolders() {
        return addRule(new EmptyFolderRule());
    }

    public Finder from(File folder) {
        folderBase = folder;

        return self();
    }

    @SuppressWarnings("WeakerAccess")
    public Finder addRule(FinderRule rule) {
        rules.add(rule);

        return self();
    }

    @SuppressWarnings("WeakerAccess")
    public boolean findAndDelete() {
        List<File> files = find();

        boolean ret = new Deleter()
                .from(files)
                .delete();

        checkChangesAfterDeletion(files);

        return ret;
    }

    @SuppressWarnings("WeakerAccess")
    public List<File> find() {
        if (recursively == null)
            throw new IllegalStateException("Recursively not defined");

        if (folderBase == null || !folderBase.exists() || !folderBase.isDirectory())
            throw new IllegalStateException("Folder not defined, not exists or is not a directory");

        List<File> ret = findingProcess(folderBase);
        if (ret == null)
            ret = new ArrayList<>();
        else if (sorter != null)
            ret.sort(sorter);
        return ret;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkChangesAfterDeletion(List<File> files) {
        do {
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                File parent = file.getParentFile();

                if (!checkRules(parent)) {
                    files.remove(i);
                    i--;
                } else {
                    if (parent.isFile())
                        parent.delete();
                    else
                        FileUtils.deleteFolder(parent);
                    files.set(i, parent);
                }
            }
        } while (files.size() > 0);
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
                if (checkRules(file) && returnMode != ReturnMode.Folders)
                    ret.add(file);
            } else if (file.isDirectory()) {
                if (checkRules(file) && returnMode != ReturnMode.Files)
                    ret.add(file);

                if (recursively) {
                    List<File> ret2 = findingProcess(file);
                    if (ret2 != null)
                        ret.addAll(ret2);
                }
            }
        }

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

    @SuppressWarnings("WeakerAccess")
    public Finder onlyFiles() {
        setReturnMode(ReturnMode.Files);

        return self();
    }

    @SuppressWarnings("WeakerAccess")
    public Finder onlyFolders() {
        setReturnMode(ReturnMode.Folders);

        return self();
    }

    public Finder sort(Comparator<File> comparator) {
        sorter = comparator;
        return self();
    }

    private enum ReturnMode {
        Misc, Files, Folders
    }
}
