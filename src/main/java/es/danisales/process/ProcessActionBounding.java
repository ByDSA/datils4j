package es.danisales.process;

import es.danisales.listeners.Listener0;
import es.danisales.listeners.Listener1;
import es.danisales.tasks.Action;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class ProcessActionBounding implements ProcessAction {
    private final ProcessAction processAction;

    protected ProcessActionBounding(ProcessAction processAction) {
        this.processAction = processAction;
    }

    @Override
    public Listener1<IOException> notFoundListeners() {
        return processAction.notFoundListeners();
    }

    @Override
    public Listener0 beforeListeners() {
        return processAction.beforeListeners();
    }

    @Override
    public Listener1<String> errorLineListeners() {
        return processAction.errorLineListeners();
    }

    @Override
    public Listener1<String> outLineListeners() {
        return processAction.outLineListeners();
    }

    @Override
    public Listener1<Integer> errorListeners() {
        return processAction.errorListeners();
    }

    @Override
    public Listener1<NoArgumentsException> onNoArgumentsListeners() {
        return processAction.onNoArgumentsListeners();
    }

    @Override
    public int getResultCode() {
        return processAction.getResultCode();
    }

    @Override
    public String getFileName() {
        return processAction.getFileName();
    }

    @Override
    public void addAfter(@NonNull Runnable r) {
        processAction.addAfter(r);
    }

    @Override
    public void addOnInterrupt(@NonNull Runnable a) {
        processAction.addOnInterrupt(a);
    }

    @Override
    public boolean isRunning() {
        return processAction.isRunning();
    }

    @Override
    public boolean isDone() {
        return processAction.isDone();
    }

    @Override
    public boolean isReady() {
        return processAction.isReady();
    }

    @Override
    public boolean isSuccessful() {
        return processAction.isSuccessful();
    }

    @Override
    public boolean isLaunched() {
        return processAction.isLaunched();
    }

    @Override
    public void interrupt() {
        processAction.interrupt();
    }

    @Override
    public Mode getMode() {
        return processAction.getMode();
    }

    @Override
    public void addNext(@NonNull Action a) {
        processAction.addNext(a);
    }

    @Override
    public void addPrevious(@NonNull Action a) {
        processAction.addPrevious(a);
    }

    @Override
    public int waitFor() {
        return processAction.waitFor();
    }

    @Override
    public int waitForNext() {
        return processAction.waitForNext();
    }

    @Override
    public String getName() {
        return processAction.getName();
    }

    @Override
    public void setName(String s) {
        processAction.setName(s);
    }

    @Override
    public boolean hasPrevious(@NonNull Action a) {
        return processAction.hasPrevious(a);
    }

    @Override
    public boolean hasNext(@NonNull Action a) {
        return processAction.hasNext(a);
    }

    @Override
    public Object getContext() {
        return processAction.getContext();
    }

    @Override
    public void run(@NonNull Object context) {
        processAction.run(context);
    }

    @Override
    @NonNull
    public Consumer<? extends Action> getFunc() {
        return processAction.getFunc();
    }

    @Override
    public void run() {
        processAction.run();
    }
}
