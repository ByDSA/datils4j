package es.danisales.process;

import es.danisales.tasks.Action;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public interface ProcessAction extends Action {
    static @NonNull ProcessAction of(@NonNull String fname, @NonNull List<String> params) {
        return ProcessActionAdapter.of(ProcessActionAdapter.class, fname, params);
    }

    static @NonNull ProcessAction of(@NonNull String fname, String... params) {
        return ProcessActionAdapter.of(ProcessActionAdapter.class, fname, params);
    }

    @SuppressWarnings("unused")
    boolean addNotFoundListener(@NonNull Consumer<IOException> consumer);

    @SuppressWarnings("unused")
    boolean addBeforeListener(@NonNull Runnable runnable);

    @SuppressWarnings("unused")
    boolean addErrorLineListener(@NonNull Consumer<String> consumer);

    @SuppressWarnings("unused")
    boolean addOutLineListener(@NonNull Consumer<String> consumer);

    @SuppressWarnings("unused")
    boolean addErrorListener(@NonNull Consumer<Integer> consumer);

    @SuppressWarnings("unused")
    boolean addOnNoArgumentsListener(@NonNull Consumer<NoArgumentsException> consumer);

    @SuppressWarnings("unused")
    boolean removeNotFoundListener(@NonNull Consumer<IOException> consumer);

    @SuppressWarnings("unused")
    boolean removeBeforeListener(@NonNull Runnable runnable);

    @SuppressWarnings("unused")
    boolean removeErrorLineListener(@NonNull Consumer<String> consumer);

    @SuppressWarnings("unused")
    boolean removeOutLineListener(@NonNull Consumer<String> consumer);

    @SuppressWarnings("unused")
    boolean removeErrorListener(@NonNull Consumer<Integer> consumer);

    @SuppressWarnings("unused")
    boolean removeOnNoArgumentsListener(@NonNull Consumer<NoArgumentsException> consumer);

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

    int getResultCode();

    @SuppressWarnings("unused")
    String getFileName();

    @SuppressWarnings("WeakerAccess")
    class NoArgumentsException extends RuntimeException {
        public NoArgumentsException() {
            super("No arguments");
        }
    }
}
