package es.danisales.listeners;

import es.danisales.tasks.Action;

public interface Listener {
    void call();

    Action.Mode getMode();
}
