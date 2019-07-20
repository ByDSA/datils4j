package es.danisales.tasks;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ToDoActionListTest {
    @Test(timeout = 2000)
    public void testAutoRun() {
        ToDoActionList td = new ToDoActionList();
        td.setName("ToDoList");
        assertFalse(td.isRunning());

        AtomicInteger ai = new AtomicInteger(0);
        Action a = Action.of(Action.Mode.SEQUENTIAL, (Action ac) -> ai.incrementAndGet());
        a.setName("IncrementorAction");
        td.run();
        assertTrue(td.isRunning());
        td.join();
        assertEquals(0, ai.get());
        td.add(a);
        td.join();
        assertEquals(1, ai.get());
        assertEquals(0, td.size());
    }
}
