package es.danisales.tasks;

import es.danisales.log.string.Logging;

public class ToDoActionList extends ActionList {
    public ToDoActionList() {
        super(Mode.CONCURRENT);
    }
    @Override
    public boolean add(Action a) {
        boolean ret = super.add(a);

        a.addAfter(() -> {
            Logging.log("End remove ToDoActionList " + a);
            remove(a);
        });
        a.addAtInterruptActions(() -> {
            Logging.log("Interrupt " + a);
            interrupt();
        });

        if (!isRunning())
            run();

        return ret;
    }

}
