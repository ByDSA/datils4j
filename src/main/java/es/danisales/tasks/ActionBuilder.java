package es.danisales.tasks;

import es.danisales.rules.ListOfRules;
import es.danisales.rules.Rule;

import java.util.function.Consumer;

public abstract class ActionBuilder<T extends ActionBuilder<T, A>, A extends Action> {
    Action.Mode mode;
    Consumer<A> function;
    ListOfRules readyRules, successRules;

    public T setMode(Action.Mode mode) {
        this.mode = mode;
        return self();
    }

    public T setRun(Consumer<A> f) {
        function = f;

        return self();
    }

    public T addReadyRule(Rule r) {
        if (readyRules == null)
            readyRules = ListOfRules.of(true);
        readyRules.add(r);

        return self();
    }

    public T addTestRule(Rule r) {
        if (successRules == null)
            successRules = ListOfRules.of(false);
        successRules.add(r);

        return self();
    }

    public abstract Action build();

    protected abstract T self();
}
