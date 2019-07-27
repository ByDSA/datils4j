package es.danisales.tasks;

import es.danisales.log.string.Logging;
import es.danisales.rules.RuleList;

import java.util.*;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

// todo: override hashCode()
class ActionAdapter<A extends Action> implements Action {
    static final Action pointless = Action.of(Mode.SEQUENTIAL, (Action a) -> {
    });
    // Non-duplicated
    final private Set<Action> next = new HashSet<>();
    private final Set<Action> previous = new HashSet<>();
    private final List<Runnable> afterListeners = new ArrayList<>();
    private final List<Runnable> interruptionListeners = new ArrayList<>();
    private final Object statusLock = new Object();
    boolean redoOnFail;
    RuleList readyRules, successRules;

    private final Mode mode;
    private final Consumer<A> innerRun;
    private Thread thread;
    private Object context;
    private String name;
    private A caller;
    private Thread checkThread;
    ActionStatus status = ActionStatus.NONE;
    private boolean once = false;

    ActionAdapter(Builder<A> builder) {
        checkNotNull(builder.mode);
        checkNotNull(builder.function);

        mode = builder.mode;
        innerRun = builder.function;

        readyRules = builder.readyRules;
        successRules = builder.successRules;

        if (builder.caller != null)
            caller = builder.caller;
        else
            caller = (A) this;

        redoOnFail = builder.redoOnFail;
    }

    // todo: para comparar next y prev en equal
    private static boolean equalList(List<Action> l1, List<Action> l2, Action o1, Action o2) {
        if (l1.size() != l2.size())
            return false;
        else
            for (int i = 0; i < l1.size(); i++) {
                if (l1.get(i) != o1 || l2.get(i) != o2) {
                    if (l1.get(i) == o1 || l2.get(i) == o2) {
                        return false;
                    } else if (!l1.get(i).equals(l2.get(i))) {
                        return false;
                    }
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
                && getMode() == casted.getMode()
                && (caller == this || Objects.equals(caller, casted.caller))
                && Objects.equals(getContext(), casted.getContext())
                && Objects.equals(getName(), casted.getName())
                && Objects.equals(this.status, casted.status)
                ;
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
            return status == ActionStatus.INITALIZING || status == ActionStatus.WAITING || status == ActionStatus.EXECUTING;
        }
    }

    @Override
    public final boolean isDone() {
        synchronized (statusLock) {
            return status == ActionStatus.DONE;
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
            if (!isRunning())
                return;

            Logging.log("Interrumpida acción " + this);
            status = ActionStatus.ABORTING;
            if (thread != null && thread.isAlive())
                thread.interrupt();
            synchronized (interruptionListeners) {
                for (Runnable r : interruptionListeners)
                    r.run();
            }
            status = ActionStatus.INTERRUPTED;
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

            if (status != ActionStatus.NONE && status != ActionStatus.DONE || isSuccessful() && once)
                return;

            checkNotNull(caller);

            status = ActionStatus.INITALIZING;
        }

        if (isSequential()) {
            // Todo: no para la ejecución de Sequential con Interrupt
            thread = Thread.currentThread();
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

    private void doDependencies() {
        if (previous.size() > 0) {
            Logging.log("Waiting for previous of " + this + "...");
            ActionList prevActionList = ActionList.of(Mode.CONCURRENT, previous);
            prevActionList.setName("previous of " + this);
            prevActionList.runAndWaitFor();
        }
    }

    private void waitForConditions() {
        synchronized (statusLock) {
            status = ActionStatus.WAITING;
        }
        while (!caller.isReady()) {
            try {
                if (checkThread == null) {
                    checkThread = Thread.currentThread();
                    Logging.log("Waiting for " + this + " check...");
                }
                long checkReadyEvery = 100;
                Thread.sleep(checkReadyEvery);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void execute() {
        synchronized (statusLock) {
            status = ActionStatus.EXECUTING;
        }
        Logging.log("Executing " + this + "...");

        innerRun.accept(caller);
    }

    private void doAction() {
        try {
            do {
                // Previous dependencies
                doDependencies();

                // Wait for conditions
                waitForConditions();

                // Running
                execute();
            } while (!isSuccessful() && redoOnFail);

            if (redoOnFail || isSuccessful())
                synchronized (this) {
                    status = ActionStatus.DONE;
                }
            else
                synchronized (this) {
                    status = ActionStatus.INTERRUPTED;
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
                    if (isDone()) {
                        Logging.log("Don't WaitingFor " + this + ". It's already done!");
                        return ActionValues.OK.intValue();
                    } else if (status == ActionStatus.INTERRUPTED) {
                        Logging.log("Don't WaitingFor " + this + ". Interrupted!");
                        return ActionValues.ABORT.intValue();
                    }
                    Logging.log("WaitingFor " + this);
                    wait();
                }
                break;
            } catch (InterruptedException ignored) {
            }
        } while (true);

        Logging.log("WaitingFor (End)" + this);

        return ActionValues.OK.intValue();
    }

    public int waitForNext() {
        Logging.log("WaitForNext " + this);
        // Ni idea de por qué a veces falla (en test 'tree'). Si se pone un sleep/println antes del waitFor, no suele lanzar InterrruptedException (a veces aún así, especialmente si el sleep es pequeño)
        waitFor();

        Logging.log("WaitForNext (nextList)" + this);
        for (Action a : next)
            a.waitForNext();

        Logging.log("WaitForNext (End) " + this);
        return ActionValues.OK.intValue();
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
        boolean redoOnFail = false;

        Builder<A> setCaller(A caller) {
            this.caller = caller;

            return self();
        }

        Builder<A> redoOnFail() {
            redoOnFail = true;

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