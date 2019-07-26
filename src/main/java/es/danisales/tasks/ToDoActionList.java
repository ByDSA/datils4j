package es.danisales.tasks;

import es.danisales.log.string.Logging;

public class ToDoActionList extends ActionList {
    private ActionValues returnCode = ActionValues.abortError;

    public ToDoActionList() {
        super(Mode.CONCURRENT);

        addNext(this);

        actionAdapter.readyRules.add(() -> !isEnding() && size() > 0);
        actionAdapter.successRules.add(this::isEmpty);
    }

    @Override
    public int waitFor() {
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }

        return returnCode.intValue();
    }

    @Override
    public boolean add(final Action a) {
        Logging.log(this + " Adding action " + a);

        a.addAfter(() -> {
            Logging.log("End remove ToDoActionList " + a);
            remove(a);
            if (isSuccessful()) {
                returnCode = ActionValues.ok;
                synchronized (this) {
                    notifyAll();
                }
            }
        });

        a.addOnInterrupt(() -> {
            remove(a);
            interrupt();
            if (isSuccessful()) {
                returnCode = ActionValues.abortError;
                synchronized (this) {
                    notifyAll();
                }
            }
        });


        synchronized (this) {
            boolean ret = super.add(a);

            actionAdapter.forceCheck();

            return ret;
        }
    }
}
