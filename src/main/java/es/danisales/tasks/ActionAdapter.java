package es.danisales.tasks;

import es.danisales.log.string.Logging;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;
import static es.danisales.time.Sleep.sleep;

class ActionAdapter<A extends Action> implements ActionBounding<A> {
    static final Action pointless = Action.of(Mode.SEQUENTIAL, (Action a) -> {
    });
    final List<Action> next = new ArrayList<>();
    // Non-duplicated
    private final AtomicBoolean done = new AtomicBoolean(false);
    private final Object threadLock = new Object();
    private final List<Action> previous = new ArrayList<>();
    private final List<Runnable> afterListeners = new ArrayList<>();
    private final List<Runnable> interruptionListeners = new ArrayList<>();
    private final AtomicBoolean ending = new AtomicBoolean(false);
    private final AtomicBoolean waitingCheck = new AtomicBoolean(false);
    private final Mode mode;
    private final Consumer<A> innerRun;
    private AtomicBoolean running = new AtomicBoolean(false);
    // Duplicated
    private long checkingTime = 100;
    private Thread thread;
    private Object context;
    private String name;
    private A caller;
    private Thread checkThread;
    private Supplier<Boolean> checkFunction;

    ActionAdapter(Builder<A> builder) {
        checkNotNull(builder.mode);
        checkNotNull(builder.function);

        mode = builder.mode;
        innerRun = builder.function;

        if (builder.checkFunction == null)
            checkFunction = () -> Boolean.TRUE;
        else
            checkFunction = builder.checkFunction;

        if (builder.caller != null)
            setCaller(builder.caller);
        else
            setCaller((A) this);
    }

    private static boolean equalList(List<Action> l1, List<Action> l2, Action o1, Action o2) {
        if (l1.size() != l2.size())
            return false;
        else
            for (int i = 0; i < l1.size(); i++) {
                if (l1.get(i) == o1 && l2.get(i) == o2)
                    continue;
                else if (l1.get(i) == o1 || l2.get(i) == o2) {
                    return false;
                } else if (!l1.get(i).equals(l2.get(i))) {
                    return false;
                }
            }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionAdapter))
            return false;

        ActionAdapter casted = (ActionAdapter) o;


        return Objects.equals(getFunc(), casted.getFunc())
                && getCheckingTime() == casted.getCheckingTime()
                && getMode() == casted.getMode()
                && (caller == this || Objects.equals(getCaller(), casted.getCaller()))
                && Objects.equals(getContext(), casted.getContext())
                && Objects.equals(getName(), casted.getName())
                && Objects.equals(this.isDone(), casted.isDone())
                && Objects.equals(this.isEnding(), casted.isEnding())
                && Objects.equals(this.isRunning(), casted.isRunning())
                && Objects.equals(this.isWaitingCheck(), casted.isWaitingCheck())
                ;
    }

    @SuppressWarnings("unused")
    public long getCheckingTime() {
        return checkingTime;
    }

    @SuppressWarnings("unused")
    public void setCheckingTime(long checkingTime) {
        this.checkingTime = checkingTime;
    }

    @SuppressWarnings("unused")
    public final void addAfter(Runnable r) {
        synchronized (afterListeners) {
            afterListeners.add(r);
        }
    }

    @SuppressWarnings("unused")
    public final void addInterruptedListener(Runnable a) {
        synchronized (interruptionListeners) {
            interruptionListeners.add(a);
        }
    }

    public final boolean isRunning() {
        return running.get();
    }

    public final boolean isWaitingCheck() {
        return waitingCheck.get();
    }

    public boolean isEnding() {
        return ending.get();
    }

    public final boolean isDone() {
        return thread != null && !thread.isAlive() || done.get();
    }

    public synchronized void interrupt() {
        if (!ending.get() || !running.get())
            return;

        Logging.log("Interrumpida acción " + this);
        ending.set(true);
        running.set(false);
        synchronized (threadLock) {
            if (thread != null)
                thread.interrupt();
        }
        synchronized (interruptionListeners) {
            for (Runnable r : interruptionListeners)
                r.run();
        }
        ending.set(false);
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void addNext(Action a) {
        synchronized (next) {
            if (!next.contains(a))
                next.add(a);
        }

        if (!a.hasPrevious(this) && a != this)
            a.addPrevious(this);
    }

    @Override
    public void addPrevious(Action a) {
        if (a == this)
            throw new RunningException();

        synchronized (previous) {
            if (!previous.contains(a))
                previous.add(a);
        }

        synchronized (a) {
            if (!a.hasNext(this))
                a.addNext(this);
        }
    }

    public synchronized final void run() {
        if (isRunning())
            throw new IllegalStateException("Action " + this + " already started");

        if (ending.get())
            return;

        checkNotNull(caller);

        running.set(true);
        if (isSequential()) {
            synchronized (threadLock) {
                thread = Thread.currentThread(); // Todo: no para la ejecución de Sequential con Interrupt
            }
            doAction();
        } else {
            synchronized (threadLock) {
                thread = new Thread(this::doAction);
                thread.setName("Thread-Action-" + name);
                thread.start();
            }
        }
    }

    void forceCheck() {
        if (checkThread != null && checkThread.isAlive())
            checkThread.interrupt();
    }

    private void doAction() {
        try {
            // Wait for previous
            if (previous.size() > 0)
                Logging.log("Waiting for previous of " + this + "...");
            for (Action a : previous) {
                try {
                    a.join();
                } catch (InterruptedException ignored) {
                }
            }

            // Wait for conditions
            checkThread = Thread.currentThread();
            if (!check())
                Logging.log("Waiting for " + this + " check...");
            waitingCheck.set(true);
            while (!checkFunction.get()) {
                try {
                    Thread.sleep(checkingTime);
                } catch (InterruptedException ignored) {
                }
            }
            waitingCheck.set(false);

            Logging.log("Running " + this + "...");

            innerRun.accept(caller);

            Logging.log("Done " + this + "!");
            running.set(false);
            done.set(true);

            // End Actions
            if (next.size() > 0) {
                ActionList nextActionList = ActionList.of(Mode.CONCURRENT, next);
                nextActionList.setName("next of " + this);
                Logging.log("Running " + nextActionList + "...");
                nextActionList.run();
            }

            synchronized (afterListeners) {
                if (afterListeners.size() > 0)
                    Logging.log(this + " Executing afterListeners...");
                for (Runnable r : afterListeners)
                    r.run();
            }
        } catch (Exception e) {
            interrupt();
            throw e;
        }
    }

    public final Object getContext() {
        return context;
    }

    public final A getCaller() {
        return caller;
    }

    @Override
    public void setCaller(A c) {
        caller = c;
    }

    @Override
    public void run(Object context) {
        this.context = context;
        run();
        this.context = null;
    }

    @Override
    public Consumer<A> getFunc() {
        return innerRun;
    }

    @Override
    public void setCheckFunction(Supplier<Boolean> f) {
        checkFunction = f;
    }

    public void join() throws InterruptedException {
        if (isDone() || isEnding())
            return;

        if (isConcurrent())
            thread.join();
        else
            while (!isDone() && !isEnding()) {
                Thread.sleep(checkingTime);
            }
    }

    public void joinNext() {
        boolean error = false;
        do
        { // Ni idea de por qué a veces falla (en test 'tree'). Si se pone un sleep/println antes del join, no suele lanzar InterrruptedException (a veces aún así, especialmente si el sleep es pequeño)
            try {
                if (error)
                    sleep(20);
                join();
                error = false;
            } catch (InterruptedException e) {
                error = true;
            }
        } while (error);


        for (Action a : next)
            a.joinNext();
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    @Override
    public void setName(String s) {
        name = s;
    }

    @Override
    public boolean hasPrevious(Action a) {
        return previous.contains(a);
    }

    @Override
    public boolean hasNext(Action a) {
        return next.contains(a);
    }

    @Override
    public String toString() {
        return name == null ? super.toString() : name;
    }

    @Override
    public boolean check() {
        return checkFunction.get();
    }

    static class Builder<A extends Action> extends ActionBuilder<Builder<A>, A> {
        A caller;

        Builder<A> setCaller(A caller) {
            this.caller = caller;

            return self();
        }

        @Override
        public ActionAdapter<A> build() {
            return new ActionAdapter<>(this);
        }

        @Override
        protected Builder<A> self() {
            return this;
        }
    }
}