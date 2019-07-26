package es.danisales.tasks;

import es.danisales.log.string.Logging;

public class ToDoActionList extends ActionList {
    public ToDoActionList() {
        super(Mode.CONCURRENT);

        addNext(this);

        actionAdapter.readyRules.add(() -> !isEnding() && size() > 0);
        actionAdapter.successRules.add(() -> size() == 0);
    }

    @Override
    public int waitFor() {
        while (!isEmpty()) {
            try {
                // todo: interrupir el thread al terminar las acciones
                Thread.sleep(getCheckingTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return ActionValues.ok.intValue();
    }

    @Override
    public boolean add(final Action a) {
        Logging.log(this + " Adding action " + a);

        a.addAfter(() -> {
            Logging.log("End remove ToDoActionList " + a);
            remove(a);
        });

        a.addOnInterrupt(() -> {
            remove(a);
            interrupt();
        });


        synchronized (this) {
            boolean ret = super.add(a);

            actionAdapter.forceCheck();

            return ret;
        }
    }
}
