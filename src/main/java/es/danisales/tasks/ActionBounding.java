package es.danisales.tasks;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Consumer;

public abstract class ActionBounding implements Action {
    final Action action;

    @SuppressWarnings("WeakerAccess")
    protected ActionBounding(Action a) {
        action = a;
    }

    @Override
    public void addAfter(@NonNull Runnable r) {
        action.addAfter(r);
    }

    @Override
    public void addOnInterrupt(@NonNull Runnable a) {
        action.addOnInterrupt(a);
    }

    @Override
    public boolean isRunning() {
        return action.isRunning();
    }

    @Override
    public boolean isDone() {
        return action.isDone();
    }

    @Override
    public boolean isReady() {
        return action.isReady();
    }

    @Override
    public boolean isSuccessful() {
        return action.isSuccessful();
    }

    @Override
    public void interrupt() {
        action.interrupt();
    }

    @Override
    public Mode getMode() {
        return action.getMode();
    }

    @Override
    public void addNext(@NonNull Action a) {
        action.addNext(a);
    }

    @Override
    public void addPrevious(@NonNull Action a) {
        action.addPrevious(a);
    }

    @Override
    public int waitFor() {
        return action.waitFor();
    }

    @Override
    public int waitForNext() {
        return action.waitForNext();
    }

    @Override
    public String getName() {
        return action.getName();
    }

    @Override
    public void setName(String s) {
        action.setName(s);
    }

    @Override
    public boolean hasPrevious(@NonNull Action a) {
        return action.hasPrevious(a);
    }

    @Override
    public boolean hasNext(@NonNull Action a) {
        return action.hasNext(a);
    }

    @Override
    public Object getContext() {
        return action.getContext();
    }

    @Override
    public void run(@NonNull Object context) {
        action.run(context);
    }

    @Override
    @NonNull
    public Consumer<? extends Action> getFunc() {
        return action.getFunc();
    }

    @Override
    public void run() {
        action.run();
    }
}
