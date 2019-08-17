package es.danisales.listeners;

import es.danisales.tasks.Action;

import java.util.List;

public interface Listener<T> extends List<T> {
    void call();

    Action.Mode getMode();
}
