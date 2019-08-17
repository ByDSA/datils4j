package es.danisales.listeners;

import es.danisales.tasks.Action;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ListenerListZero extends ListenerListAdapter<Runnable> {
    private ListenerListZero(Action.Mode mode, Safety safety) {
        super(mode, safety);
    }

    public static @NonNull ListenerListZero newInstanceSequential() {
        return new ListenerListZero(Action.Mode.SEQUENTIAL, Safety.NonThreadSafe);
    }

    public static @NonNull ListenerListZero newInstanceSequentialThreadSafe() {
        return new ListenerListZero(Action.Mode.SEQUENTIAL, Safety.ThreadSafe);
    }

    @SuppressWarnings("WeakerAccess")
    public static @NonNull ListenerListZero newInstanceConcurrent() {
        return new ListenerListZero(Action.Mode.CONCURRENT, Safety.NonThreadSafe);
    }

    @SuppressWarnings("WeakerAccess")
    public static @NonNull ListenerListZero newInstanceConcurrentThreadSafe() {
        return new ListenerListZero(Action.Mode.CONCURRENT, Safety.ThreadSafe);
    }

    public void call() {
        if (getMode() == Action.Mode.SEQUENTIAL)
            forEach(Runnable::run);
        else if (getMode() == Action.Mode.CONCURRENT)
            parallelStream().forEach(Runnable::run);
        else
            throw new IllegalStateException();
    }
}
