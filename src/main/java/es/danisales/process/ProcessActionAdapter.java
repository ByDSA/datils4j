package es.danisales.process;

import es.danisales.arrays.ArrayUtils;
import es.danisales.log.string.Logging;
import es.danisales.tasks.Action;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
    private final List<Consumer<IOException>> notFoundListeners = new ArrayList<>();
    private final List<Runnable> beforeListeners = new ArrayList<>();
    private final List<Consumer<String>> errorLineListeners = new ArrayList<>();
    private final List<Consumer<String>> outLineListeners = new ArrayList<>();
    private final List<Consumer<Integer>> errorListeners = new ArrayList<>();
    private final List<Consumer<NoArgumentsException>> onNoArgumentsListeners = new ArrayList<>();
    private String[] paramsWithName;
    private Thread normalMessagesThread;
    private AtomicInteger resultCode = new AtomicInteger();
    private Thread errorMessagesThread;
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

    @Override
    public boolean addNotFoundListener(@NonNull Consumer<IOException> consumer) {
        synchronized (notFoundListeners) {
            return notFoundListeners.add(consumer);
        }
    }

    @Override
    public boolean addBeforeListener(@NonNull Runnable runnable) {
        synchronized (beforeListeners) {
            return beforeListeners.add(runnable);
        }
    }

    @Override
    public boolean addErrorLineListener(@NonNull Consumer<String> consumer) {
        synchronized (errorLineListeners) {
            return errorLineListeners.add(consumer);
        }
    }

    @Override
    public boolean addOutLineListener(@NonNull Consumer<String> consumer) {
        synchronized (outLineListeners) {
            return outLineListeners.add(consumer);
        }
    }

    @Override
    public boolean addErrorListener(@NonNull Consumer<Integer> consumer) {
        synchronized (errorListeners) {
            return errorListeners.add(consumer);
        }
    }

    @Override
    public boolean addOnNoArgumentsListener(@NonNull Consumer<ProcessAction.NoArgumentsException> consumer) {
        synchronized (onNoArgumentsListeners) {
            return onNoArgumentsListeners.add(consumer);
        }
    }

    @Override
    public boolean removeNotFoundListener(@NonNull Consumer<IOException> consumer) {
        synchronized (notFoundListeners) {
            return notFoundListeners.remove(consumer);
        }
    }

    @Override
    public boolean removeBeforeListener(@NonNull Runnable runnable) {
        synchronized (beforeListeners) {
            return beforeListeners.remove(runnable);
        }
    }

    @Override
    public boolean removeErrorLineListener(@NonNull Consumer<String> consumer) {
        synchronized (errorLineListeners) {
            return errorLineListeners.remove(consumer);
        }
    }

    @Override
    public boolean removeOutLineListener(@NonNull Consumer<String> consumer) {
        synchronized (outLineListeners) {
            return outLineListeners.remove(consumer);
        }
    }

    @Override
    public void clearNotFoundListeners() {
        synchronized (notFoundListeners) {
            notFoundListeners.clear();
        }
    }

    @Override
    public void clearBeforeListeners() {
        synchronized (beforeListeners) {
            beforeListeners.clear();
        }
    }

    @Override
    public void clearErrorLineListeners() {
        synchronized (errorLineListeners) {
            errorLineListeners.clear();
        }
    }

    @Override
    public void clearOutLineListener() {
        synchronized (outLineListeners) {
            outLineListeners.clear();
        }
    }

    @Override
    public void clearErrorListeners() {
        synchronized (errorListeners) {
            errorListeners.clear();
        }
    }

    @Override
    public void clearOnNoArgumentsListeners() {
        synchronized (onNoArgumentsListeners) {
            onNoArgumentsListeners.clear();
        }
    }

    @Override
    public boolean removeErrorListener(@NonNull Consumer<Integer> consumer) {
        synchronized (errorListeners) {
            return errorListeners.remove(consumer);
        }
    }

    @Override
    public boolean removeOnNoArgumentsListener(@NonNull Consumer<NoArgumentsException> consumer) {
        synchronized (onNoArgumentsListeners) {
            return onNoArgumentsListeners.remove(consumer);
        }
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
            for (Consumer<NoArgumentsException> c : onNoArgumentsListeners)
                c.accept(e);
        }
    }

    private void callNotFoundListenersSequentially(IOException e) {
        synchronized (notFoundListeners) {
            for (Consumer<IOException> c : notFoundListeners)
                c.accept(e);
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
    public void addAfter(@NonNull Runnable r) {
        actionAdapter.addAfter(r);
    }

    @Override
    public void addOnInterrupt(@NonNull Runnable a) {
        actionAdapter.addOnInterrupt(a);
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
