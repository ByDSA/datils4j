package es.danisales.process;

import es.danisales.log.string.Logging;
import es.danisales.strings.StringUtils;
import es.danisales.tasks.Action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ProcessAction implements Action {
    @SuppressWarnings("WeakerAccess")
    protected String[] paramsWithName;
    private AtomicInteger resultCode = new AtomicInteger();
    private Thread normalOutputThread;
    private Thread errorOutputThread;

    private final List<Consumer<IOException>> notFoundListeners = new ArrayList<>();
    private final List<Runnable> beforeListeners = new ArrayList<>();
    private final List<Consumer<String>> errorLineListeners = new ArrayList<>();
    private final List<Consumer<String>> outLineListeners = new ArrayList<>();
    private final List<Consumer<Integer>> errorListeners = new ArrayList<>();
    private final List<Consumer<NoArgumentsException>> onNoArgumentsListeners = new ArrayList<>();

    private final Action actionAdapter = Action.of(Mode.CONCURRENT, this::innerRun);

    private ProcessAction() {
    }

    public static ProcessAction of(String fname, List<String> params) {
        ProcessAction ret = new ProcessAction();

        ret.setFilenameAndParams(fname, params);

        return ret;
    }

    public static ProcessAction of(String fname, String... params) {
        ProcessAction ret = new ProcessAction();

        ret.setFilenameAndParams(fname, params);

        return ret;
    }

    @SuppressWarnings("unused")
    public boolean addNotFoundListener(Consumer<IOException> consumer) {
        synchronized (notFoundListeners) {
            return notFoundListeners.add(consumer);
        }
    }

    @SuppressWarnings("unused")
    public boolean addBeforeListener(Runnable runnable) {
        synchronized (beforeListeners) {
            return beforeListeners.add(runnable);
        }
    }

    @SuppressWarnings("unused")
    public boolean addErrorLineListener(Consumer<String> consumer) {
        synchronized (errorLineListeners) {
            return errorLineListeners.add(consumer);
        }
    }

    @SuppressWarnings("unused")
    public boolean addOutLineListener(Consumer<String> consumer) {
        synchronized (outLineListeners) {
            return outLineListeners.add(consumer);
        }
    }

    @SuppressWarnings("unused")
    public boolean addErrorListener(Consumer<Integer> consumer) {
        synchronized (errorListeners) {
            return errorListeners.add(consumer);
        }
    }

    @SuppressWarnings("unused")
    public boolean addOnNoArgumentsListener(Consumer<NoArgumentsException> consumer) {
        synchronized (onNoArgumentsListeners) {
            return onNoArgumentsListeners.add(consumer);
        }
    }

    @SuppressWarnings("unused")
    public boolean removeNotFoundListener(Consumer<IOException> consumer) {
        synchronized (notFoundListeners) {
            return notFoundListeners.remove(consumer);
        }
    }

    @SuppressWarnings("unused")
    public boolean removeBeforeListener(Runnable runnable) {
        synchronized (beforeListeners) {
            return beforeListeners.remove(runnable);
        }
    }

    @SuppressWarnings("unused")
    public boolean removeErrorLineListener(Consumer<String> consumer) {
        synchronized (errorLineListeners) {
            return errorLineListeners.remove(consumer);
        }
    }

    @SuppressWarnings("unused")
    public boolean removeOutLineListener(Consumer<String> consumer) {
        synchronized (outLineListeners) {
            return outLineListeners.remove(consumer);
        }
    }

    @SuppressWarnings("unused")
    public boolean removeErrorListener(Consumer<Integer> consumer) {
        synchronized (errorListeners) {
            return errorListeners.remove(consumer);
        }
    }

    @SuppressWarnings("unused")
    public boolean removeOnNoArgumentsListener(Consumer<NoArgumentsException> consumer) {
        synchronized (onNoArgumentsListeners) {
            return onNoArgumentsListeners.remove(consumer);
        }
    }

    @SuppressWarnings("unused")
    public void clearNotFoundListeners() {
        synchronized (notFoundListeners) {
            notFoundListeners.clear();
        }
    }

    @SuppressWarnings("unused")
    public void clearBeforeListeners() {
        synchronized (beforeListeners) {
            beforeListeners.clear();
        }
    }

    @SuppressWarnings("unused")
    public void clearErrorLineListeners() {
        synchronized (errorLineListeners) {
            errorLineListeners.clear();
        }
    }

    @SuppressWarnings("unused")
    public void clearOutLineListener() {
        synchronized (outLineListeners) {
            outLineListeners.clear();
        }
    }

    @SuppressWarnings("unused")
    public void clearErrorListeners() {
        synchronized (errorListeners) {
            errorListeners.clear();
        }
    }

    @SuppressWarnings("unused")
    public void clearOnNoArgumentsListeners() {
        synchronized (onNoArgumentsListeners) {
            onNoArgumentsListeners.clear();
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected void setFilenameAndParams(String fname, List<String> params) {
        setFilenameAndParams(fname, params.toArray(new String[0]));
    }

    private void setFilenameAndParams(String fname, String... params) {
        paramsWithName = new String[ params.length +1 ];
        paramsWithName[0] = fname;
        System.arraycopy(params, 0, paramsWithName, 1, params.length);
    }

    private void innerRun(ProcessAction self) {
        synchronized (self.beforeListeners) {
            for (Runnable r : self.beforeListeners)
                r.run();
        }
        try {
            if (self.paramsWithName == null || self.paramsWithName.length == 0)
                throw new NoArgumentsException();
            else
                for (String str : self.paramsWithName)
                    if (str == null)
                        throw new NoArgumentsException();

            Logging.log("Executing " + self.paramsWithName[0] + " " + StringUtils.join(" ", self.paramsWithName, 1));
            final Process p = Runtime.getRuntime().exec(self.paramsWithName);

            self.normalOutputThread = self.startNormalOutputListener(p);
            self.startErrorOutputListener(p);

            self.resultCode.set(p.waitFor());
            self.normalOutputThread.join();
            if (self.resultCode.get() != 0) {
                synchronized (self.errorListeners) {
                    for (Consumer<Integer> c : self.errorListeners)
                        c.accept(self.resultCode.get());
                }
            }
        } catch (IOException e) {
            synchronized (self.notFoundListeners) {
                for (Consumer<IOException> c : self.notFoundListeners)
                    c.accept(e);
            }
        } catch (InterruptedException e) {
            self.interrupt();
        } catch(NoArgumentsException e) {
            synchronized (self.onNoArgumentsListeners) {
                for (Consumer<NoArgumentsException> c : self.onNoArgumentsListeners)
                    c.accept(e);
            }
        }
    }

    @Override
    public long getCheckingTime() {
        return actionAdapter.getCheckingTime();
    }

    @Override
    public void setCheckingTime(long checkingTime) {
        actionAdapter.setCheckingTime(checkingTime);
    }

    @Override
    public void addAfter(Runnable r) {
        actionAdapter.addAfter(r);
    }

    @Override
    public void addOnInterrupt(Runnable a) {
        actionAdapter.addOnInterrupt(a);
    }

    @Override
    public boolean isRunning() {
        return actionAdapter.isRunning();
    }

    @Override
    public boolean isIddle() {
        return actionAdapter.isIddle();
    }

    @Override
    public boolean isEnding() {
        return actionAdapter.isEnding();
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
    public synchronized void interrupt() {
        if (normalOutputThread != null)
            normalOutputThread.interrupt();
        if (errorOutputThread != null)
            errorOutputThread.interrupt();
        actionAdapter.interrupt();
    }

    @Override
    public Mode getMode() {
        return actionAdapter.getMode();
    }

    @Override
    public void addNext(Action a) {
        actionAdapter.addNext(a);
    }

    @Override
    public void addPrevious(Action a) {
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
    public boolean hasPrevious(Action a) {
        return actionAdapter.hasPrevious(a);
    }

    @Override
    public boolean hasNext(Action a) {
        return actionAdapter.hasNext(a);
    }

    @Override
    public Object getContext() {
        return actionAdapter.getContext();
    }

    @Override
    public void run(Object context) {
        actionAdapter.run(context);
    }

    @Override
    public Consumer<? extends Action> getFunc() {
        return actionAdapter.getFunc();
    }

    private Thread startNormalOutputListener(Process p) {
        assert normalOutputThread == null;
        normalOutputThread = new Thread(() -> {
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
            } catch(Exception ignored) {}
        });

        normalOutputThread.start();

        return normalOutputThread;
    }

    private void startErrorOutputListener(Process p) {
        if (errorOutputThread != null)
            errorOutputThread.interrupt();
        errorOutputThread = new Thread(() -> {
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
            } catch(Exception ignored) {}
        });

        errorOutputThread.start();
    }

    @SuppressWarnings("WeakerAccess")
    public int getResultCode() {
        return resultCode.get();
    }

    @SuppressWarnings("unused")
    public String getFileName() {
        return paramsWithName == null || paramsWithName.length == 0 ? null : paramsWithName[0];
    }

    @Override
    public void run() {
        actionAdapter.run();
    }


    @SuppressWarnings("WeakerAccess")
    public static class NoArgumentsException extends RuntimeException {
        public NoArgumentsException() {
            super("No arguments");
        }
    }
}
