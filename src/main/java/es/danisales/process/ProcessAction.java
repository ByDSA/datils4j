package es.danisales.process;

import es.danisales.listeners.ListenerListOne;
import es.danisales.listeners.ListenerListZero;
import es.danisales.tasks.Action;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface ProcessAction extends Action {
    static @NonNull ProcessAction from(@NonNull String... params) {
        return new ProcessActionBuilder().addArgs(Arrays.asList(params)).build();
    }

    static @NonNull ProcessAction from(@NonNull List<String> params) {
        return new ProcessActionBuilder().addArgs(params).build();
    }

    static @NonNull ProcessAction from(@NonNull String fname, @NonNull List<String> params) {
        return new ProcessActionBuilder().addArgs(Collections.singletonList(fname)).addArgs(params).build();
    }

    static ProcessActionBuilder builder() {
        return new ProcessActionBuilder();
    }

    @SuppressWarnings("unused")
    ListenerListOne<IOException> notFoundListeners();

    @SuppressWarnings("unused")
    ListenerListZero beforeListeners();

    @SuppressWarnings("unused")
    ListenerListOne<String> errorLineListeners();

    @SuppressWarnings("unused")
    ListenerListOne<String> outLineListeners();

    @SuppressWarnings("unused")
    ListenerListOne<Integer> errorListeners();

    @SuppressWarnings("unused")
    ListenerListOne<NoArgumentsException> onNoArgumentsListeners();

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
