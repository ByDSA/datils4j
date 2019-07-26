package es.danisales.tasks;

public enum ActionValues {
    ABORT(-1), OK(0);

    private final int value;

    ActionValues(int v) {
        value = v;
    }

    public int intValue() {
        return value;
    }
}
