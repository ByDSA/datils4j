package es.danisales.tasks;

public class RunningException extends RuntimeException {
    public RunningException() {
        super("The action is already running");
    }
}
