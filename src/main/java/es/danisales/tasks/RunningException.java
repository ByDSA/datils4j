package es.danisales.tasks;

@SuppressWarnings("WeakerAccess")
public class RunningException extends RuntimeException {
    public RunningException() {
        super("The action is already running");
    }
}
