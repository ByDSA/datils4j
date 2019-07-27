package es.danisales.tasks;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public interface Action extends Runnable {
    static Action createPointless() {
        return Action.of(ActionAdapter.pointless);
    }

    static <A extends Action> Action of(A action) {
        return of(action.getMode(), action.getFunc());
    }

    static <A extends Action> Action of(@NonNull Mode m, @NonNull Consumer<A> innerRun) {
        final AtomicReference<ActionAdapter> a = new AtomicReference<>();
        a.set(
                (ActionAdapter) builder(m, innerRun)
                        .addSuccessRule(() -> a.get().status != ActionStatus.NONE)
                        .build()
        );

        return a.get();
    }

    static <A extends Action> ActionBuilder<?, A> builder(@NonNull Mode m, @NonNull Consumer<A> innerRun) {
        return new ActionAdapter.Builder<A>()
                .setMode(m)
                .setRun(innerRun);
    }

    @SuppressWarnings("unused")
    void addAfter(Runnable r);

    @SuppressWarnings("unused")
    void addOnInterrupt(Runnable a);

    boolean isRunning();

    boolean isDone();

    boolean isReady();

    boolean isSuccessful();

    void interrupt();

    default boolean isConcurrent() {
        return getMode() == Mode.CONCURRENT;
    }

    default boolean isSequential() {
        return getMode() == Mode.SEQUENTIAL;
    }

    Mode getMode();

    void addNext(Action a);

    void addPrevious(Action a);

    @SuppressWarnings({"unused", "UnusedReturnValue"})
    default int runAndWaitFor() {
        run();
        return waitFor();
    }

    @SuppressWarnings("unused")
    default int runAndWaitForNext() {
        run();
        return waitForNext();
    }

    int waitFor();

    int waitForNext();

    String getName();

    void setName(String s);

    boolean hasPrevious(Action a);

    boolean hasNext(Action a);

    Object getContext();

    void run(Object context);

    Consumer<? extends Action> getFunc();

    enum Mode {
        CONCURRENT, SEQUENTIAL
    }
}