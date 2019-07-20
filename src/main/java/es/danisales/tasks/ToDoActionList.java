package es.danisales.tasks;

import es.danisales.log.string.Logging;

public class ToDoActionList extends ActionList {
    public ToDoActionList() {
        super(Mode.CONCURRENT);

        addNext(this);
    }

    @Override
    public void join() {
        while (!isEmpty()) {
            try {
                // todo: interrupir el thread al terminar las acciones
                Thread.sleep(getCheckingTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean add(Action a) {
        Logging.log(this + " Adding action " + a);
        boolean ret = super.add(a);

        a.addAfter(() -> {
            Logging.log("End remove ToDoActionList " + a);
            remove(a);
        });
        a.addInterruptedListener(() -> {
            remove(a);
            interrupt();
        });

        actionAdapter.forceCheck();

        return ret;
    }

    @Override
    public boolean check() {
        return !isEnding() && size() > 0;
    }
}
