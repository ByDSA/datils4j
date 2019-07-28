package es.danisales.tasks;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

public interface Action extends Runnable {
    static Action createPointless() {
        return Action.of(ActionAdapter.pointless);
    }

    static <A extends Action> Action of(@NonNull A action) {
        return of(action.getMode(), action.getFunc());
    }

    static <A extends Action> Action of(@NonNull Mode m, @NonNull Consumer<A> innerRun) {
        checkNotNull(m);
        checkNotNull(innerRun);

        return builder(m, innerRun)
                .build();
    }

    static <CALLER extends Action> Action of(@NonNull Mode m, @NonNull Consumer<CALLER> innerRun, @NonNull CALLER caller) {
        checkNotNull(m);
        checkNotNull(innerRun);
        checkNotNull(caller);

        ActionBuilder<?, ?, CALLER> b = builder(m, innerRun)
                .setCaller(caller);

        return b.build();
    }

    static <CALLER extends Action> ActionBuilder<?, ?, CALLER> builder(@NonNull Mode m, @NonNull Consumer<CALLER> innerRun) {
        ActionBuilder<?, ?, CALLER> b = new ActionAdapter.Builder<CALLER>()
                .setMode(m)
                .setRun(innerRun);

        b.addSuccessRule(() -> b.getInstance().isLaunched());

        return b;
    }

    @SuppressWarnings("unused")
    void addAfter(@NonNull Runnable r);

    @SuppressWarnings("unused")
    void addOnInterrupt(@NonNull Runnable a);

    boolean isRunning();

    boolean isDone();

    boolean isReady();

    boolean isSuccessful();

    boolean isLaunched();

    void interrupt();

    default boolean isConcurrent() {
        return getMode() == Mode.CONCURRENT;
    }

    default boolean isSequential() {
        return getMode() == Mode.SEQUENTIAL;
    }

    Mode getMode();

    void addNext(@NonNull Action a);

    void addPrevious(@NonNull Action a);

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

    boolean hasPrevious(@NonNull Action a);

    boolean hasNext(@NonNull Action a);

    Object getContext();

    void run(@NonNull Object context);

    @NonNull
    Consumer<? extends Action> getFunc();

    enum Mode {
        CONCURRENT, SEQUENTIAL
    }
}