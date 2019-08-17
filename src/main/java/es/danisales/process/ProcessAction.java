package es.danisales.process;

import es.danisales.listeners.Listener0;
import es.danisales.listeners.Listener1;
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
    Listener1<IOException> notFoundListeners();

    @SuppressWarnings("unused")
    Listener0 beforeListeners();

    @SuppressWarnings("unused")
    Listener1<String> errorLineListeners();

    @SuppressWarnings("unused")
    Listener1<String> outLineListeners();

    @SuppressWarnings("unused")
    Listener1<Integer> errorListeners();

    @SuppressWarnings("unused")
    Listener1<NoArgumentsException> onNoArgumentsListeners();

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
