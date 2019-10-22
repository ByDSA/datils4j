package es.danisales.utils.building;

import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class Builder<SELF extends Builder<SELF, INSTANCE>, INSTANCE> {
    public abstract @NonNull INSTANCE build();

    protected abstract @NonNull SELF self();
}
