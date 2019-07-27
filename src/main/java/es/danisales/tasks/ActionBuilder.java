package es.danisales.tasks;

import es.danisales.rules.Rule;
import es.danisales.rules.RuleList;

import java.util.function.Consumer;

public abstract class ActionBuilder<T extends ActionBuilder<T, A>, A extends Action> {
    Action.Mode mode;
    Consumer<A> function;
    RuleList readyRules = RuleList.of(true),
            successRules = RuleList.of(false);

    public T setMode(Action.Mode mode) {
        this.mode = mode;
        return self();
    }

    public T setRun(Consumer<A> f) {
        function = f;

        return self();
    }

    public T addReadyRule(Rule r) {
        readyRules.add(r);

        return self();
    }

    public T addSuccessRule(Rule r) {
        successRules.add(r);

        return self();
    }

    public abstract Action build();

    protected abstract T self();
}
