package es.danisales.tasks;

import es.danisales.log.string.Logging;
import es.danisales.rules.ListOfRules;

import java.util.*;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

class ActionAdapter<A extends Action> implements ActionBounding<A> {
    // todo: override hashCode()

    static final Action pointless = Action.of(Mode.SEQUENTIAL, (Action a) -> {
    });
    final Set<Action> next = new HashSet<>();
    // Non-duplicated
    private final Set<Action> previous = new HashSet<>();
    private final List<Runnable> afterListeners = new ArrayList<>();
    private final List<Runnable> interruptionListeners = new ArrayList<>();
    private final Object statusLock = new Object();
    protected ListOfRules readyRules, successRules;
    private boolean done = false;
    private boolean ending = false;
    private boolean waitingCheck = false;

    private final Mode mode;
    private final Consumer<A> innerRun;
    // Duplicated
    private long checkingTime = 100;
    private Thread thread;
    private Object context;
    private String name;
    private A caller;
    private Thread checkThread;
    private boolean running = false;

    ActionAdapter(Builder<A> builder) {
        checkNotNull(builder.mode);
        checkNotNull(builder.function);

        mode = builder.mode;
        innerRun = builder.function;

        if (builder.readyRules == null)
            readyRules = ListOfRules.of(true);
        else
            readyRules = builder.readyRules;

        if (builder.successRules == null)
            successRules = ListOfRules.of(false);
        else
            successRules = builder.successRules;

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
                && (getCaller() == this || Objects.equals(getCaller(), casted.getCaller()))
                && Objects.equals(getContext(), casted.getContext())
                && Objects.equals(getName(), casted.getName())
                && Objects.equals(this.isDone(), casted.isDone())
                && Objects.equals(this.isEnding(), casted.isEnding())
                && Objects.equals(this.isRunning(), casted.isRunning())
                && Objects.equals(this.isIddle(), casted.isIddle())
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
    public final void addOnInterrupt(Runnable a) {
        synchronized (interruptionListeners) {
            interruptionListeners.add(a);
        }
    }

    public final boolean isRunning() {
        synchronized (statusLock) {
            return running;
        }
    }

    public final boolean isIddle() {
        synchronized (statusLock) {
            return waitingCheck;
        }
    }

    public boolean isEnding() {
        synchronized (statusLock) {
            return ending;
        }
    }

    public final boolean isDone() {
        synchronized (statusLock) {
            return thread != null && !thread.isAlive() || done;
        }
    }

    @Override
    public boolean isReady() {
        return readyRules.check();
    }

    @Override
    public boolean isSuccessful() {
        return successRules.check();
    }

    public synchronized void interrupt() {
        synchronized (statusLock) {
            if (!ending || !running)
                return;

            Logging.log("Interrumpida acción " + this);
            ending = true;
            running = false;
            if (thread != null && thread.isAlive())
                thread.interrupt();
            synchronized (interruptionListeners) {
                for (Runnable r : interruptionListeners)
                    r.run();
            }
            ending = false;
        }
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void addNext(Action a) {
        synchronized (next) {
            next.add(a);
        }
    }

    @Override
    public void addPrevious(final Action a) {
        if (a == this)
            throw new RunningException();

        synchronized (previous) {
            previous.add(a);
        }

        synchronized (a) {
            if (!a.hasNext(this))
                a.addNext(this);
        }
    }

    public final void run() {
        synchronized (statusLock) {
            if (isRunning())
                throw new IllegalStateException("Action " + this + " already started");

            if (ending || isSuccessful())
                return;

            checkNotNull(caller);

            running = true;
            done = false;
        }

        if (isSequential()) {
            thread = Thread.currentThread(); // Todo: no para la ejecución de Sequential con Interrupt
            doAction();
        } else if (isConcurrent()) {
            thread = new Thread(this::doAction);
            thread.setName("Thread-Action-" + name);
            thread.start();
        }
    }

    void forceCheck() {
        if (checkThread != null && checkThread.isAlive())
            checkThread.interrupt();
    }

    private void doAction() {
        try {
            // Previous dependencies
            if (previous.size() > 0) {
                Logging.log("Waiting for previous of " + this + "...");
                ActionList prevActionList = ActionList.of(Mode.CONCURRENT, previous);
                prevActionList.setName("previous of " + this);
                prevActionList.runAndWaitFor();
            }

            // Wait for conditions
            synchronized (statusLock) {
                waitingCheck = true;
            }
            while (!caller.isReady()) {
                try {
                    if (checkThread == null) {
                        checkThread = Thread.currentThread();
                        Logging.log("Waiting for " + this + " check...");
                    }
                    Thread.sleep(checkingTime);
                } catch (InterruptedException ignored) {
                }
            }
            synchronized (statusLock) {
                waitingCheck = false;
            }

            // Running
            Logging.log("Running " + this + "...");

            innerRun.accept(caller);

            synchronized (this) {
                running = false;
                done = true;
            }
            Logging.log("Done " + this + "!");

            // After Listeners
            synchronized (afterListeners) {
                if (afterListeners.size() > 0)
                    Logging.log("Executing afterListeners " + this);
                for (Runnable r : afterListeners)
                    r.run();
            }

            // Notify waitingFor
            Logging.log("Notifying " + this + "!");
            synchronized (this) {
                notifyAll();
            }

            // Next Actions
            if (next.size() > 0) {
                ActionList nextActionList = ActionList.of(Mode.CONCURRENT, next);
                nextActionList.setName("next of " + this);
                nextActionList.run();
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
    public synchronized void run(Object context) {
        this.context = context;
        run();
        this.context = null;
    }

    @Override
    public Consumer<A> getFunc() {
        return innerRun;
    }

    public int waitFor() {
        do {
            try {
                synchronized (this) {
                    if (done) {
                        Logging.log("Don't WaitingFor " + this + ". It's done!");
                        return ActionValues.ok.intValue();
                    }
                    Logging.log("WaitingFor " + this);
                    wait();
                }
                break;
            } catch (InterruptedException ignored) {
            }
        } while (true);

        Logging.log("WaitingFor (End)" + this);

        return ActionValues.ok.intValue();
    }

    public int waitForNext() {
        Logging.log("WaitForNext " + this);
        // Ni idea de por qué a veces falla (en test 'tree'). Si se pone un sleep/println antes del waitFor, no suele lanzar InterrruptedException (a veces aún así, especialmente si el sleep es pequeño)
        waitFor();

        Logging.log("WaitForNext (nextList)" + this);
        for (Action a : next)
            a.waitForNext();

        Logging.log("WaitForNext (End) " + this);
        return ActionValues.ok.intValue();
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