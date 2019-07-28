package es.danisales.tasks;

import es.danisales.rules.Rule;
import es.danisales.rules.RuleList;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ActionBuilder<T extends ActionBuilder<T, A>, A extends Action> {
    Action.Mode mode;
    Consumer<A> function;
    RuleList readyRules = RuleList.of(true),
            successRules = RuleList.of(false);
    A caller;
    boolean redoOnFail = false;

    @SuppressWarnings("WeakerAccess")
    public T setCaller(A caller) {
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

    public T setRun(@NonNull Consumer<A> f) {
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

    protected abstract T self();
}
