package es.danisales.utils;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkState;

public abstract class OnceBuilder<SELF extends OnceBuilder<SELF, INSTANCE>, INSTANCE> extends Builder<SELF, INSTANCE> {
    protected INSTANCE instance = null;

    public final INSTANCE getInstance() {
        return Objects.requireNonNull(instance, "Instance not built");
    }

    protected abstract INSTANCE buildOnce();

    public final INSTANCE build() {
        checkNotInstantiated();
        instance = buildOnce();
        return instance;
    }

    protected final void checkNotInstantiated() {
        checkState(instance == null);
    }
}
