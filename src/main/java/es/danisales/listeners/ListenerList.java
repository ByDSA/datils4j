package es.danisales.listeners;

import es.danisales.tasks.Action;

import java.util.List;

public interface ListenerList<T> extends List<T> {
    void call();

    Action.Mode getMode();
}
