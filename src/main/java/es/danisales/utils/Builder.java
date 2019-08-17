package es.danisales.utils;

public abstract class Builder<SELF extends Builder<SELF, INSTANCE>, INSTANCE> {
    public abstract INSTANCE build();

    protected abstract SELF self();
}
