package es.danisales.tasks;

import es.danisales.rules.Rule;
import es.danisales.rules.RuleList;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ActionBuilder<T extends ActionBuilder<T, INSTANCE, CALLER>, INSTANCE extends Action, CALLER extends Action> {
    Action.Mode mode;
    protected INSTANCE instance = null;
    RuleList readyRules = RuleList.of(true),
            successRules = RuleList.of(false);
    Consumer<CALLER> function;
    boolean redoOnFail = false;
    CALLER caller;

    @SuppressWarnings("WeakerAccess")
    public T setCaller(CALLER caller) {
        this.caller = caller;

        return self();
    }

    public T redoOnFail() {
        redoOnFail = true;

        return self();
    }

    public T setMode(Action.Mode mode) {
        checkNotNull(mode);
        this.mode = mode;
        return self();
    }

    public T setRun(@NonNull Consumer<CALLER> f) {
        checkNotNull(f);
        function = f;

        return self();
    }

    public T addReadyRule(@NonNull Rule r) {
        checkNotNull(r);
        readyRules.add(r);

        return self();
    }

    @SuppressWarnings("WeakerAccess")
    public T addSuccessRule(@NonNull Rule r) {
        checkNotNull(r);
        successRules.add(r);

        return self();
    }

    public abstract Action build();

    protected INSTANCE getInstance() {
        return Objects.requireNonNull(instance, "Instance not built");
    }

    protected abstract T self();
}
