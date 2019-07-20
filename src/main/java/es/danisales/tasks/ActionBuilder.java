package es.danisales.tasks;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ActionBuilder<T extends ActionBuilder<T, A>, A extends Action> {
    Action.Mode mode;
    Consumer<A> function;
    Supplier<Boolean> checkFunction;

    public T setMode(Action.Mode mode) {
        this.mode = mode;
        return self();
    }

    public T setRun(Consumer<A> f) {
        function = f;

        return self();
    }

    public T setCheckFunction(Supplier<Boolean> f) {
        checkFunction = f;

        return self();
    }

    public abstract Action build();

    protected abstract T self();
}
