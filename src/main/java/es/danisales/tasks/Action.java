package es.danisales.tasks;

import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

public interface Action extends Runnable {
    static Action createPointless() {
        return Action.of(ActionAdapter.pointless);
    }

    static <A extends Action> Action of(A action) {
        return of(action.getMode(), action.getFunc());
    }

    static <A extends Action> Action of(Mode m, Consumer<A> innerRun) {
        checkNotNull(m);
        checkNotNull(innerRun);

        return new ActionAdapter.Builder<A>()
                .setMode(m)
                .setRun(innerRun)
                .build();
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