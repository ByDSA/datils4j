package es.danisales.process;

import es.danisales.tasks.Action;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public interface ProcessAction extends Action {
    static ProcessAction of(String fname, List<String> params) {
        ProcessAction ret = ProcessActionAdapter.of(fname, params);

        return ret;
    }

    static ProcessAction of(String fname, String... params) {
        ProcessAction ret = ProcessActionAdapter.of(fname, params);

        return ret;
    }

    static ProcessAction newInstance() {
        return ProcessActionAdapter.newInstance();
    }

    @SuppressWarnings("unused")
    boolean addNotFoundListener(Consumer<IOException> consumer);

    @SuppressWarnings("unused")
    boolean addBeforeListener(Runnable runnable);

    @SuppressWarnings("unused")
    boolean addErrorLineListener(Consumer<String> consumer);

    @SuppressWarnings("unused")
    boolean addOutLineListener(Consumer<String> consumer);

    @SuppressWarnings("unused")
    boolean addErrorListener(Consumer<Integer> consumer);

    @SuppressWarnings("unused")
    boolean addOnNoArgumentsListener(Consumer<NoArgumentsException> consumer);

    @SuppressWarnings("unused")
    boolean removeNotFoundListener(Consumer<IOException> consumer);

    @SuppressWarnings("unused")
    boolean removeBeforeListener(Runnable runnable);

    @SuppressWarnings("unused")
    boolean removeErrorLineListener(Consumer<String> consumer);

    @SuppressWarnings("unused")
    boolean removeOutLineListener(Consumer<String> consumer);

    @SuppressWarnings("unused")
    boolean removeErrorListener(Consumer<Integer> consumer);

    @SuppressWarnings("unused")
    boolean removeOnNoArgumentsListener(Consumer<NoArgumentsException> consumer);

    @SuppressWarnings("unused")
    void clearNotFoundListeners();

    @SuppressWarnings("unused")
    void clearBeforeListeners();

    @SuppressWarnings("unused")
    void clearErrorLineListeners();
    @SuppressWarnings("unused")
    void clearOutLineListener();

    @SuppressWarnings("unused")
    void clearErrorListeners();

    @SuppressWarnings("unused")
    void clearOnNoArgumentsListeners();

    @SuppressWarnings("WeakerAccess")
    int getResultCode();

    @SuppressWarnings("unused")
    String getFileName();

    void setFilenameAndParams(String file, String... params);

    @SuppressWarnings("WeakerAccess")
    class NoArgumentsException extends RuntimeException {
        public NoArgumentsException() {
            super("No arguments");
        }
    }
}
