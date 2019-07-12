package es.danisales.tasks;

import es.danisales.log.string.Logging;
import es.danisales.rules.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static es.danisales.time.Sleep.sleep;

public abstract class Action implements Runnable, Rule, Cloneable {
    // Non-duplicated
    private AtomicBoolean done;
    private AtomicBoolean running;
    protected Object _lock;
    private Thread thread;
    private final Object threadLock = new Object();
    private ActionList next;
    private List<Action> previous;
    private final List<Runnable> afterActions = new ArrayList<>();
    private final List<Runnable> interruptionListeners = new ArrayList<>();
    @SuppressWarnings("WeakerAccess") protected Object context;
    private AtomicBoolean ending;

    // Duplicated
    private long checkingTime = 100;
    private String name;
    private final Mode mode;

    public enum Mode {
        CONCURRENT, SEQUENTIAL
    }

    public Action(Mode m) {
        mode = m;
        initialize();
    }

    private void initialize() {
        ending = new AtomicBoolean(false);
        done = new AtomicBoolean(false);
        running = new AtomicBoolean(false);
        _lock = new Object();
        next = null;
        previous = new ArrayList<>();
        context = null;
    }

    @SuppressWarnings("WeakerAccess")
    public Action newCopy() {
        try {
            Action a = (Action)clone();
            a.initialize();
            return a;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
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
        synchronized (afterActions) {
            afterActions.add(r);
        }
    }

    @SuppressWarnings("unused")
    public final void addInterruptionListener(Runnable a) {
        synchronized (interruptionListeners) {
            interruptionListeners.add(a);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public final boolean isRunning() {
        return running.get();
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isEnding() {
        return ending.get();
    }

    @SuppressWarnings("WeakerAccess")
    public final boolean isDone() {
        return done.get();
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

    public boolean equals(Object o) {
        if (o != null && o.getClass().equals(this.getClass()) && o instanceof Action) {
            Action a = (Action)o;
            return a == this;
        }

        return false;
    }

    public boolean isConcurrent() {
        return mode == Mode.CONCURRENT;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isSequential() {
        return !isConcurrent();
    }

    protected abstract void innerRun();

    @SuppressWarnings("WeakerAccess")
    public synchronized final Action addNext(Action a) {
        if (next == null) {
            next = new ActionList(Mode.CONCURRENT);
        }
        if (!next.contains( a ))
            next.add(a);

        if (!a.previous.contains( this ))
            a.addPrevious( this );

        return this;
    }

    @SuppressWarnings("WeakerAccess")
    public synchronized final Action addPrevious(Action a) {
        if (!previous.contains( a ))
            previous.add(a);

        if (a.next == null) {
            a.next = new ActionList(Mode.CONCURRENT);
        }
        if (!a.next.contains( this ))
            a.addNext( this );

        return this;
    }

    public synchronized final void run() {
        if (isRunning())
            throw new IllegalStateException();

        if (ending.get())
            return;

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
                assert thread.isAlive();
            }
        }
    }

    @SuppressWarnings("unused")
    public Action runAndJoin() throws InterruptedException {
        run();
        return join();
    }

    @SuppressWarnings("unused")
    public Action runAndJoinNext() {
        run();
        return joinNext();
    }

    private void doAction() {
        try {
            // Wait for previous
            for (Action a : previous) {
                try {
                    a.join();
                } catch (InterruptedException ignored) {
                }
            }

            // Wait for conditions
            while (!check()) {
                try {
                    Thread.sleep(checkingTime);
                } catch (InterruptedException ignored) {
                }
            }

            innerRun();

            running.set(false);
            done.set(true);

            // End Actions
            if (next != null) {
                next.setName("next of " + this);
                next.run();
            }
            synchronized (afterActions) {
                for (Runnable r : afterActions)
                    r.run();
            }
        } catch (Exception e) {
            interrupt();
            throw e;
        }
    }

    public synchronized boolean check() {
        return true;
    }

    @Deprecated
    public synchronized final Object getContext() {
        return context;
    }

    public Action join() throws InterruptedException {
        if (!isRunning() || isDone() || isEnding())
            return null;

        if (isConcurrent())
            thread.join();
        else
            while (!isDone() && isRunning() && !isEnding()) {
                Thread.sleep( checkingTime );
            }

        return this;
    }

    public Action joinNext() {
        boolean error = false;
        do { // Ni idea de por qué a veces falla (en test 'tree'). Si se pone un sleep/println antes del join, no suele lanzar InterrruptedException (a veces aún así, especialmente si el sleep es pequeño)
            try {
                if (error)
                    sleep(20);
                join();
                error = false;
            } catch (InterruptedException e) {
                error = true;
            }
        } while (error);


        if (next != null)
            next.joinNext();

        return this;
    }

    @SuppressWarnings("WeakerAccess")
    public Action setName(String s) {
        name = s;

        return this;
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name == null ? super.toString() : name;
    }
}