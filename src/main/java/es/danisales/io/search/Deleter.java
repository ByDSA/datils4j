package es.danisales.io.search;

import es.danisales.io.FileUtils;
import es.danisales.io.search.rules.EmptyFileRule;
import es.danisales.io.search.rules.EmptyFolderRule;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Deleter {
    private List<File> files = new ArrayList<>();
    private List<Consumer<File>> afterEachListeners = new ArrayList<>();

    public Deleter from(List<File> files) {
        this.files = files;

        return self();
    }

    @SuppressWarnings("WeakerAccess")
    public boolean delete() {
        if (files == null)
            throw new IllegalStateException("Any files was not assigned");

        boolean ret = true;
        for (File f : files)
            ret &= deleteElement(f);

        return ret;
    }

    private boolean deleteElement(File f) {
        boolean ret = f.isDirectory() ? FileUtils.deleteFolder(f) : f.delete();
        callAfterEachListeners(f);

        return ret;
    }

    @SuppressWarnings("WeakerAccess")
    public Deleter addAfterEach(Consumer<File> fileConsumer) {
        afterEachListeners.add(fileConsumer);

        return self();
    }

    @SuppressWarnings("WeakerAccess")
    public Deleter emptyParents() {
        return addAfterEach((File f) -> {
            File parent = f.getParentFile();
            if (new EmptyFolderRule().check(parent))
                deleteElement(parent);
        });
    }

    @SuppressWarnings({"ConstantConditions", "WeakerAccess"})
    public Deleter emptyFileParents() {
        return addAfterEach((File f) -> {
            File parent = f.getParentFile();
            if (parent.list().length == 0)
                return;

            for (File f2 : parent.listFiles())
                if (!new EmptyFileRule().check(f2))
                    return;

            FileUtils.deleteFolder(parent);
        });
    }

    protected Deleter self() {
        return this;
    }

    private void callAfterEachListeners(File file) {
        for (Consumer<File> fileConsumer : afterEachListeners)
            fileConsumer.accept(file);
    }
}
