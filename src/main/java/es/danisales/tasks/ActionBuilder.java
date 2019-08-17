package es.danisales.tasks;

import es.danisales.rules.Rule;
import es.danisales.rules.RuleList;
import es.danisales.utils.OnceBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ActionBuilder<T extends ActionBuilder<T, INSTANCE, CALLER>, INSTANCE extends Action, CALLER extends Action> extends OnceBuilder<T, INSTANCE> {
    Action.Mode mode;
    RuleList readyRules = RuleList.fromDefaultValue(true),
            successRules = RuleList.fromDefaultValue(false);
    Consumer<CALLER> function;
    boolean redoOnFail = false;
    CALLER caller;

    @SuppressWarnings("WeakerAccess")
    public T setCaller(CALLER caller) {
        checkNotInstantiated();
        checkNotNull(caller);
        this.caller = caller;

        return self();
    }

    public T redoOnFail() {
        checkNotInstantiated();
        redoOnFail = true;

        return self();
    }

    public T setMode(Action.Mode mode) {
        checkNotInstantiated();
        checkNotNull(mode);
        this.mode = mode;
        return self();
    }

    public T setRun(@NonNull Consumer<CALLER> f) {
        checkNotInstantiated();
        checkNotNull(f);
        function = f;

        return self();
    }

    public T addReadyRule(@NonNull Rule r) {
        checkNotInstantiated();
        checkNotNull(r);
        readyRules.add(r);

        return self();
    }

    @SuppressWarnings("WeakerAccess")
    public T addSuccessRule(@NonNull Rule r) {
        checkNotInstantiated();
        checkNotNull(r);
        successRules.add(r);

        return self();
    }

    protected abstract T self();
}
