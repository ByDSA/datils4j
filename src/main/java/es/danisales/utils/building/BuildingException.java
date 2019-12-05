package es.danisales.utils.building;

import org.checkerframework.checker.nullness.qual.NonNull;

public class BuildingException extends Exception {
    private final Exception exception;

    public BuildingException(@NonNull Exception exception) {
        super(exception.getMessage());

        this.exception = exception;
    }

    public @NonNull Exception getInnerException() {
        return exception;
    }
}
