package es.danisales.process;

import es.danisales.listeners.ListenerOne;
import es.danisales.listeners.ListenerZero;
import es.danisales.tasks.Action;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.List;

public interface ProcessAction extends Action {
    static @NonNull ProcessAction of(@NonNull String fname, @NonNull List<String> params) {
        return ProcessActionAdapter.of(ProcessActionAdapter.class, fname, params);
    }

    static @NonNull ProcessAction of(@NonNull String fname, String... params) {
        return ProcessActionAdapter.of(ProcessActionAdapter.class, fname, params);
    }

    @SuppressWarnings("unused")
    ListenerOne<IOException> notFoundListeners();

    @SuppressWarnings("unused")
    ListenerZero beforeListeners();

    @SuppressWarnings("unused")
    ListenerOne<String> errorLineListeners();

    @SuppressWarnings("unused")
    ListenerOne<String> outLineListeners();

    @SuppressWarnings("unused")
    ListenerOne<Integer> errorListeners();

    @SuppressWarnings("unused")
    ListenerOne<NoArgumentsException> onNoArgumentsListeners();

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
