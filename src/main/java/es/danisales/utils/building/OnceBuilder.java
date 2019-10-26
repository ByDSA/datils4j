package es.danisales.utils.building;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkState;

public abstract class OnceBuilder<SELF extends OnceBuilder<SELF, INSTANCE>, INSTANCE> extends Builder<SELF, INSTANCE> {
    protected INSTANCE instance = null;
    private List<Consumer<INSTANCE>> onBuildFunctions = new ArrayList<>();

    public final INSTANCE getInstance() {
        return Objects.requireNonNull(instance, "Instance not built");
    }

    protected abstract @NonNull INSTANCE buildOnce();

    public final @NonNull INSTANCE build() {
        checkNotInstantiated();
        instance = buildOnce();

        for (Consumer<INSTANCE> f : onBuildFunctions)
            f.accept(instance);

        return instance;
    }

    public SELF onBuild(Consumer<INSTANCE> f) {
        onBuildFunctions.add(f);

        return self();
    }

    protected final void checkNotInstantiated() {
        checkState(instance == null);
    }
}
