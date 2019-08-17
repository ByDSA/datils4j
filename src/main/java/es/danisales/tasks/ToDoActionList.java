package es.danisales.tasks;

import es.danisales.log.string.Logging;

public class ToDoActionList extends ActionList {
    private ActionValues returnCode = ActionValues.ABORT;

    @SuppressWarnings("WeakerAccess")
    public ToDoActionList() {
        super(Mode.CONCURRENT);

        actionAdapter.redoOnFail = true;
        actionAdapter.readyRules.add(() -> !isEmpty());
        actionAdapter.successRules.add(() -> false);
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

        a.addAfterListener(() -> {
            Logging.log("End remove ToDoActionList " + a);
            remove(a);
            if (isEmpty()) {
                returnCode = ActionValues.OK;
                synchronized (this) {
                    notifyAll();
                }
            }
        });

        a.addOnInterruptListener(() -> {
            remove(a);
            interrupt();
            returnCode = ActionValues.ABORT;
            synchronized (this) {
                notifyAll();
            }
        });


        synchronized (this) {
            super.add(a);

            actionAdapter.forceCheck();

            return true;
        }
    }
}
