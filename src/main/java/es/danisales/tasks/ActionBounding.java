package es.danisales.tasks;

public interface ActionBounding<CALLER extends Action> extends Action {
    void setCaller(CALLER caller);
}