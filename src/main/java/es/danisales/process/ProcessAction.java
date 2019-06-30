package es.danisales.process;

import es.danisales.log.string.Logging;
import es.danisales.strings.StringUtils;
import es.danisales.tasks.Action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ProcessAction extends Action {
    @SuppressWarnings("WeakerAccess")
    String[] paramsWithName;

    @SuppressWarnings("unused")
    public ProcessAction(String fname, List<String> params) {
        super(Mode.CONCURRENT);
        String[] paramsArray = params.toArray(new String[0]);
        constructor(fname, paramsArray);
    }

    @SuppressWarnings("unused")
    public ProcessAction(String fname, String... params) {
        super(Mode.CONCURRENT);

        constructor(fname, params);
    }

    public ProcessAction() {
        super(Mode.CONCURRENT);
    }

    protected void constructor(String fname, String... params) {
        paramsWithName = new String[ params.length +1 ];
        paramsWithName[0] = fname;
        System.arraycopy(params, 0, paramsWithName, 1, params.length);
    }

    @Override
    protected void innerRun() {
        try {
            if (paramsWithName == null || paramsWithName.length == 0)
                throw new NoArgumentsException();
            else
                for (String str : paramsWithName)
                    if (str == null)
                        throw new NoArgumentsException();

            Logging.log("Executing " + paramsWithName[0] + " " + StringUtils.join(" ", paramsWithName, 1));
            final Process p = Runtime.getRuntime().exec(paramsWithName);

            Thread normalOutputThread = startNormalOutputListener(p);
            startErrorOutputListener(p);

            int result = p.waitFor();
            normalOutputThread.join();
            if (result != 0) {
                onError(result);
            }
        } catch (IOException e) {
            onNotFound();
        } catch (InterruptedException e) {
            onInterrupted();
        } catch(NoArgumentsException e) {
            onNoArguments(paramsWithName);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void onNoArguments(String[] paramsWithName) {
        System.err.println("no valid arguments");
        if (paramsWithName != null)
        for (String str : paramsWithName)
            System.err.println(str);
    }

    @SuppressWarnings("WeakerAccess")
    public void onInterrupted() {
        System.err.println("procccess interrupted");
    }

    @SuppressWarnings("WeakerAccess")
    public void onNotFound() {
        System.err.println("procccess not found");
    }

    private Thread startNormalOutputListener(Process p) {
        Thread normalOutputThread = new Thread(() -> {
            try {
                String line;
                BufferedReader input =
                        new BufferedReader
                                (new InputStreamReader(p.getInputStream()));

                while ((line = input.readLine()) != null)
                    onOutLine(line);


                input.close();
            } catch(Exception ignored) {}
        });

        normalOutputThread.start();

        return normalOutputThread;
    }

    private void startErrorOutputListener(Process p) {
        Thread errorOutputThread = new Thread(() -> {
            try {
                String line;
                BufferedReader input =
                        new BufferedReader
                                (new InputStreamReader(p.getErrorStream()));

                while ((line = input.readLine()) != null)
                    onErrLine(line);


                input.close();
            } catch(Exception ignored) {}
        });

        errorOutputThread.start();
    }

    @SuppressWarnings("WeakerAccess")
    public void onError(int code) {
        Logging.error("Process failed with status: " + code);
    }

    @SuppressWarnings("WeakerAccess")
    public void onOutLine(String line) {
        Logging.log(line);
    }

    @SuppressWarnings("WeakerAccess")
    public void onErrLine(String line) {
        Logging.error(line);
    }

    @SuppressWarnings("WeakerAccess")
    public static class NoArgumentsException extends RuntimeException {
        public NoArgumentsException() {
            super("No arguments");
        }
    }
}
