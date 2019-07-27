package es.danisales.tasks;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

public interface Action extends Runnable {
    static Action createPointless() {
        return Action.of(ActionAdapter.pointless);
    }

    static <A extends Action> Action of(A action) {
        return of(action.getMode(), action.getFunc());
    }

    static <A extends Action> Action of(@NonNull Mode m, @NonNull Consumer<A> innerRun) {
        checkNotNull(m);
        checkNotNull(innerRun);

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
    void addAfter(@NonNull Runnable r);

    @SuppressWarnings("unused")
    void addOnInterrupt(@NonNull Runnable a);

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