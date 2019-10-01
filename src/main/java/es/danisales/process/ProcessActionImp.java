package es.danisales.process;

import es.danisales.arrays.ArrayUtils;
import es.danisales.listeners.ListenerListOne;
import es.danisales.listeners.ListenerListZero;
import es.danisales.log.string.Logging;
import es.danisales.tasks.Action;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.join;

public class ProcessActionImp implements ProcessAction {
    /**
     * Listeners
     */
    private final ListenerListOne<IOException> notFoundListeners = ListenerListOne.newInstanceSequentialThreadSafe();
    private final ListenerListZero beforeListeners = ListenerListZero.newInstanceSequentialThreadSafe();
    private final ListenerListOne<String> errorLineListeners = ListenerListOne.newInstanceSequentialThreadSafe();
    private final ListenerListOne<String> outLineListeners = ListenerListOne.newInstanceSequentialThreadSafe();
    private final ListenerListOne<Integer> errorListeners = ListenerListOne.newInstanceSequentialThreadSafe();
    private final ListenerListOne<NoArgumentsException> onNoArgumentsListeners = ListenerListOne.newInstanceSequentialThreadSafe();

    final String[] paramsWithName;
    private Thread normalMessagesThread;
    private Thread errorMessagesThread;
    private AtomicInteger resultCode = new AtomicInteger();

    private final Action actionAdapter = Action.of(Mode.CONCURRENT, this::innerRun, this);

    protected ProcessActionImp(ProcessActionBuilder builder) {
        paramsWithName = ArrayUtils.fromList(builder.args);
        ProcessActionBuilder.registerInstance(this);
    }

    private void innerRun(@NonNull ProcessActionImp self) {
        checkNotNull(self.paramsWithName);

        self.runBeforeListenersSequentially();

        try {
            self.checkValidArguments();

            Process p = self.startProcess(); // throws IOException
            self.waitForProcess(p); // throws InterruptedException

            if (self.isErrorCodeReturned())
                self.callErrorListenersSequentially();
        } catch (IOException e) {
            self.callNotFoundListenersSequentially(e);
        } catch (InterruptedException e) {
            self.interrupt();
        } catch (NoArgumentsException e) {
            self.callNoArgumentListenersSequentially(e);
        }
    }

    private void waitForProcess(Process p) throws InterruptedException {
        resultCode.set(p.waitFor());
        normalMessagesThread.join();
    }

    private Process startProcess() throws IOException {
        Logging.log("Executing " + join(" ", paramsWithName));
        final Process p = Runtime.getRuntime().exec(paramsWithName);

        initAndStartNormalMessagesOutput(p);
        initAndStartErrorMessagesOutput(p);

        return p;
    }

    private void callNoArgumentListenersSequentially(NoArgumentsException e) {
        synchronized (onNoArgumentsListeners) {
            onNoArgumentsListeners.call(e);
        }
    }

    private void callNotFoundListenersSequentially(IOException e) {
        synchronized (notFoundListeners) {
            notFoundListeners.call(e);
        }
    }

    private void callErrorListenersSequentially() {
        synchronized (errorListeners) {
            for (Consumer<Integer> c : errorListeners)
                c.accept(getResultCode());
        }
    }

    private boolean isErrorCodeReturned() {
        return getResultCode() != 0;
    }

    private void runBeforeListenersSequentially() {
        synchronized (beforeListeners) {
            for (Runnable r : beforeListeners)
                r.run();
        }
    }

    private void checkValidArguments() {
        if (paramsWithName == null || paramsWithName.length == 0)
            throw new NoArgumentsException();
        else
            for (String str : paramsWithName)
                if (str == null)
                    throw new NoArgumentsException();
    }

    @Override
    public void addAfterListener(@NonNull Runnable r) {
        actionAdapter.addAfterListener(r);
    }

    @Override
    public void addOnInterruptListener(@NonNull Runnable a) {
        actionAdapter.addOnInterruptListener(a);
    }

    @Override
    public boolean isRunning() {
        return actionAdapter.isRunning();
    }

    @Override
    public boolean isDone() {
        return actionAdapter.isDone();
    }

    @Override
    public boolean isReady() {
        return actionAdapter.isReady();
    }

    @Override
    public boolean isSuccessful() {
        return actionAdapter.isSuccessful();
    }

    @Override
    public boolean isLaunched() {
        return actionAdapter.isLaunched();
    }

    @Override
    public synchronized void interrupt() {
        if (normalMessagesThread != null)
            normalMessagesThread.interrupt();
        if (errorMessagesThread != null)
            errorMessagesThread.interrupt();
        actionAdapter.interrupt();
    }

    @Override
    public Mode getMode() {
        return actionAdapter.getMode();
    }

    @Override
    public void addNext(@NonNull Action a) {
        actionAdapter.addNext(a);
    }

    @Override
    public void addPrevious(@NonNull Action a) {
        actionAdapter.addPrevious(a);
    }

    @Override
    public int waitFor() {
        actionAdapter.waitFor();

        return getResultCode();
    }

    @Override
    public int waitForNext() {
        return actionAdapter.waitForNext();
    }

    @Override
    public String getName() {
        return actionAdapter.getName();
    }

    @Override
    public void setName(String s) {
        actionAdapter.setName(s);
    }

    @Override
    public boolean hasPrevious(@NonNull Action a) {
        return actionAdapter.hasPrevious(a);
    }

    @Override
    public boolean hasNext(@NonNull Action a) {
        return actionAdapter.hasNext(a);
    }

    @Override
    public Object getContext() {
        return actionAdapter.getContext();
    }

    @Override
    public void run(@NonNull Object context) {
        actionAdapter.run(context);
    }

    @Override
    @NonNull
    public Consumer<? extends Action> getFunc() {
        return actionAdapter.getFunc();
    }

    private void initAndStartNormalMessagesOutput(Process p) {
        assert normalMessagesThread == null;
        normalMessagesThread = new Thread(() -> {
            try {
                String line;
                BufferedReader input =
                        new BufferedReader
                                (new InputStreamReader(p.getInputStream()));

                while ((line = input.readLine()) != null)
                    synchronized (outLineListeners) {
                        for (Consumer<String> c : outLineListeners)
                            c.accept(line);
                    }


                input.close();
            } catch (Exception ignored) {
            }
        });

        normalMessagesThread.start();
    }

    private void initAndStartErrorMessagesOutput(Process p) {
        if (errorMessagesThread != null)
            errorMessagesThread.interrupt();
        errorMessagesThread = new Thread(() -> {
            try {
                String line;
                BufferedReader input =
                        new BufferedReader
                                (new InputStreamReader(p.getErrorStream()));

                while ((line = input.readLine()) != null)
                    synchronized (errorLineListeners) {
                        for (Consumer<String> c : errorLineListeners)
                            c.accept(line);
                    }


                input.close();
            } catch (Exception ignored) {
            }
        });

        errorMessagesThread.start();
    }

    @Override
    public ListenerListOne<IOException> notFoundListeners() {
        return notFoundListeners;
    }

    @Override
    public ListenerListZero beforeListeners() {
        return beforeListeners;
    }

    @Override
    public ListenerListOne<String> errorLineListeners() {
        return errorLineListeners;
    }

    @Override
    public ListenerListOne<String> outLineListeners() {
        return outLineListeners;
    }

    @Override
    public ListenerListOne<Integer> errorListeners() {
        return errorListeners;
    }

    @Override
    public ListenerListOne<NoArgumentsException> onNoArgumentsListeners() {
        return onNoArgumentsListeners;
    }

    @Override
    public int getResultCode() {
        return resultCode.get();
    }

    @Override
    public String getFileName() {
        return paramsWithName == null || paramsWithName.length == 0 ? null : paramsWithName[0];
    }

    @Override
    public void run() {
        actionAdapter.run();
    }
}
