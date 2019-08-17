package es.danisales.process;

import es.danisales.arrays.ArrayUtils;
import es.danisales.listeners.ListenerListOne;
import es.danisales.listeners.ListenerListZero;
import es.danisales.log.string.Logging;
import es.danisales.tasks.Action;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.join;

public class ProcessActionAdapter implements ProcessAction {
    private static final Map<String[], ProcessActionAdapter> registeredProcessAction = new ConcurrentHashMap<>();

    /**
     * Listeners
     */
    private final ListenerListOne<IOException> notFoundListeners = ListenerListOne.newInstanceSequentialThreadSafe();
    private final ListenerListZero beforeListeners = ListenerListZero.newInstanceSequentialThreadSafe();
    private final ListenerListOne<String> errorLineListeners = ListenerListOne.newInstanceSequentialThreadSafe();
    private final ListenerListOne<String> outLineListeners = ListenerListOne.newInstanceSequentialThreadSafe();
    private final ListenerListOne<Integer> errorListeners = ListenerListOne.newInstanceSequentialThreadSafe();
    private final ListenerListOne<NoArgumentsException> onNoArgumentsListeners = ListenerListOne.newInstanceSequentialThreadSafe();

    private String[] paramsWithName;
    private Thread normalMessagesThread;
    private Thread errorMessagesThread;
    private AtomicInteger resultCode = new AtomicInteger();

    private final Action actionAdapter = Action.of(Mode.CONCURRENT, this::innerRun, this);

    protected ProcessActionAdapter() {
    }

    @SuppressWarnings("SameParameterValue")
    protected static @NonNull <T extends ProcessActionAdapter> T of(@NonNull Class<T> c, @NonNull String fname, String... params) {
        return of(c, fname, Arrays.asList(params));
    }

    protected static @NonNull <T extends ProcessActionAdapter> T of(@NonNull Class<T> c, @NonNull String... fnameAndparams) {
        T ret = getFromRegister(fnameAndparams);

        if (ret == null)
            ret = newInstance(c, fnameAndparams);

        return ret;
    }

    private static @NonNull <T extends ProcessActionAdapter> T newInstance(@NonNull Class<T> c, String... fnameAndparams) {
        try {
            T ret = c.newInstance();
            ret.setFilenameAndParamsAndRegister(fnameAndparams[0], getParams(fnameAndparams));

            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void registerInstance(ProcessActionAdapter p) {
        registeredProcessAction.put(p.paramsWithName, p);
    }

    private static @Nullable <T extends ProcessActionAdapter> T getFromRegister(String... fnameAndparams) {
        T ret;
        try {
            //noinspection unchecked
            ret = (T) registeredProcessAction.get(fnameAndparams);
        } catch (ClassCastException e) {
            ret = null;
        }

        return ret;
    }

    private static List<String> getParams(@NonNull String[] fnameAndParams) {
        return Arrays.asList(Arrays.copyOfRange(fnameAndParams, 1, fnameAndParams.length - 1));
    }

    protected static @NonNull <T extends ProcessActionAdapter> T of(@NonNull Class<T> c, @NonNull String fname, @NonNull List<String> params) {
        String[] fnameAndParams = fnameAndParamsToStringArray(fname, params);
        return of(c, fnameAndParams);
    }

    private static String[] fnameAndParamsToStringArray(@NonNull String fname, String... params) {
        String[] paramsWithName = new String[params.length + 1];
        paramsWithName[0] = fname;
        System.arraycopy(params, 0, paramsWithName, 1, params.length);
        return paramsWithName;
    }

    private static String[] fnameAndParamsToStringArray(@NonNull String fname, @NonNull List<String> paramsList) {
        String[] paramsString = ArrayUtils.fromList(paramsList);
        return fnameAndParamsToStringArray(fname, paramsString);
    }

    void setFilenameAndParamsAndRegister(@NonNull String fname, @NonNull List<String> params) {
        setFilenameAndParamsAndRegister(fname, params.toArray(new String[0]));
    }

    private void setFilenameAndParamsAndRegister(@NonNull String fname, String... params) {
        if (paramsWithName != null) {
            registeredProcessAction.remove(paramsWithName);
        }

        paramsWithName = fnameAndParamsToStringArray(fname, params);
        registerInstance(this);
    }

    private void innerRun(@NonNull ProcessActionAdapter self) {
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
