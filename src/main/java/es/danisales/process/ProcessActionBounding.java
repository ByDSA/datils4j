package es.danisales.process;

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
    public boolean addNotFoundListener(Consumer<IOException> consumer) {
        return processAction.addNotFoundListener(consumer);
    }

    @Override
    public boolean addBeforeListener(Runnable runnable) {
        return processAction.addBeforeListener(runnable);
    }

    @Override
    public boolean addErrorLineListener(Consumer<String> consumer) {
        return processAction.addErrorLineListener(consumer);
    }

    @Override
    public boolean addOutLineListener(Consumer<String> consumer) {
        return processAction.addOutLineListener(consumer);
    }

    @Override
    public boolean addErrorListener(Consumer<Integer> consumer) {
        return processAction.addErrorListener(consumer);
    }

    @Override
    public boolean addOnNoArgumentsListener(Consumer<NoArgumentsException> consumer) {
        return processAction.addOnNoArgumentsListener(consumer);
    }

    @Override
    public boolean removeNotFoundListener(Consumer<IOException> consumer) {
        return processAction.removeNotFoundListener(consumer);
    }

    @Override
    public boolean removeBeforeListener(Runnable runnable) {
        return processAction.removeBeforeListener(runnable);
    }

    @Override
    public boolean removeErrorLineListener(Consumer<String> consumer) {
        return processAction.removeErrorLineListener(consumer);
    }

    @Override
    public boolean removeOutLineListener(Consumer<String> consumer) {
        return processAction.removeOutLineListener(consumer);
    }

    @Override
    public boolean removeErrorListener(Consumer<Integer> consumer) {
        return processAction.removeErrorListener(consumer);
    }

    @Override
    public boolean removeOnNoArgumentsListener(Consumer<NoArgumentsException> consumer) {
        return processAction.removeOnNoArgumentsListener(consumer);
    }

    @Override
    public void clearNotFoundListeners() {
        processAction.clearNotFoundListeners();
    }

    @Override
    public void clearBeforeListeners() {
        processAction.clearBeforeListeners();
    }

    @Override
    public void clearErrorLineListeners() {
        processAction.clearErrorLineListeners();
    }

    @Override
    public void clearOutLineListener() {
        processAction.clearOutLineListener();
    }

    @Override
    public void clearErrorListeners() {
        processAction.clearErrorListeners();
    }

    @Override
    public void clearOnNoArgumentsListeners() {
        processAction.clearOnNoArgumentsListeners();
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
    public void setFilenameAndParams(String file, String... params) {
        processAction.setFilenameAndParams(file, params);
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
