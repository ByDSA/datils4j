package es.danisales.tasks;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static es.danisales.time.Sleep.sleep;
import static org.junit.Assert.*;

public class ToDoActionListTest {
    @Test(timeout = 20000)
    public void testAutoRun() {
        ToDoActionList td = new ToDoActionList();
        td.setName("ToDoList");
        assertFalse(td.isRunning());
        AtomicInteger ai = new AtomicInteger(0);
        Action a = Action.of(Action.Mode.SEQUENTIAL, (Action ac) -> ai.incrementAndGet());
        a.setName("IncrementorAction");
        assertFalse(td.isIddle());
        assertFalse(td.isReady());
        assertEquals(0, td.size());
        td.run();
        sleep(50);
        assertFalse(td.isReady());
        assertTrue(td.isRunning());
        assertTrue(td.isIddle());
        assertEquals(0, ai.get());
        td.add(a);
        td.waitFor();
        assertEquals(1, ai.get());
        assertEquals(0, td.size());
    }
}
