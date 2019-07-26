package es.danisales.tasks;

public enum ActionValues {
    abortError(-1), ok(0);

    private final int value;

    ActionValues(int v) {
        value = v;
    }

    public int intValue() {
        return value;
    }
}
