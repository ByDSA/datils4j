package es.danisales.tasks;

import es.danisales.rules.Rule;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

public interface Action extends Runnable, Rule {
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
    long getCheckingTime();

    @SuppressWarnings("unused")
    void setCheckingTime(long checkingTime);

    @SuppressWarnings("unused")
    void addAfter(Runnable r);

    @SuppressWarnings("unused")
    void addInterruptedListener(Runnable a);

    boolean isRunning();

    boolean isWaitingCheck();

    boolean isEnding();

    boolean isDone();

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

    @SuppressWarnings("unused")
    default void runAndJoin() throws InterruptedException {
        run();
        join();
    }

    @SuppressWarnings("unused")
    default void runAndJoinNext() {
        run();
        joinNext();
    }

    void join() throws InterruptedException;

    void joinNext();

    String getName();

    void setName(String s);

    boolean hasPrevious(Action a);

    boolean hasNext(Action a);

    Object getContext();

    void run(Object context);

    Consumer<? extends Action> getFunc();

    void setCheckFunction(Supplier<Boolean> f);

    boolean check();

    enum Mode {
        CONCURRENT, SEQUENTIAL
    }
}