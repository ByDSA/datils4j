package es.danisales.process;

import es.danisales.log.string.Logging;
import es.danisales.strings.StringUtils;
import es.danisales.tasks.Action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ProcessAction extends Action {
    String[] paramsWithName;

    public ProcessAction(String fname, List<String> params) {
        super(Mode.CONCURRENT);
        String[] paramsArray = params.toArray(new String[params.size()]);
        constructor(fname, paramsArray);
    }

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
        for (int i = 1; i <= params.length; i++) {
            paramsWithName[i] = params[i-1];
        }
    }

    @Override
    protected void innerRun() {
        int result = 1;
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

            result = p.waitFor();
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

    public void onNoArguments(String[] paramsWithName) {
        System.err.println("no valid arguments");
        if (paramsWithName != null)
        for (String str : paramsWithName)
            System.err.println(str);
    }

    public void onInterrupted() {
        System.err.println("procccess interrupted");
    }

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
                    Logging.log(line);


                input.close();
            } catch(Exception e) {}
        });

        normalOutputThread.start();

        return normalOutputThread;
    }

    private Thread startErrorOutputListener(Process p) {
        Thread errorOutputThread = new Thread(() -> {
            try {
                String line;
                BufferedReader input =
                        new BufferedReader
                                (new InputStreamReader(p.getErrorStream()));

                while ((line = input.readLine()) != null)
                    Logging.error(line);


                input.close();
            } catch(Exception e) {}
        });

        errorOutputThread.start();

        return errorOutputThread;
    }

    public void onError(int code) {
        Logging.error("Process failed with status: " + code);
    }

    public class NoArgumentsException extends RuntimeException {
        public NoArgumentsException() {
            super("No arguments");
        }
    }
}
