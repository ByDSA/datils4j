package es.danisales.tasks;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static es.danisales.time.Sleep.sleep;
import static org.junit.Assert.*;

public class ToDoActionListTest {
    private Action createTaskIncrementor(Action.Mode mode, AtomicInteger ai) {
        Action a = Action.of(mode, (Action ac) -> ai.incrementAndGet());
        a.setName("IncrementorAction");
        return a;
    }

    private ToDoActionList createTodoList() {
        ToDoActionList td = new ToDoActionList();
        td.setName("ToDoList");
        return td;
    }

    @Test
    public void initialState() {
        ToDoActionList td = createTodoList();

        assertFalse(td.isRunning());
        assertFalse(td.isReady());
        assertEquals(0, td.size());
    }

    @Test
    public void runWithoutActionsState() {
        ToDoActionList td = createTodoList();
        td.run();
        sleep(50);
        assertFalse(td.isReady());
        assertFalse(td.isDone());
        assertTrue(td.isRunning());
        assertEquals(0, td.size());
    }

    @Test
    public void autorun() {
        ToDoActionList td = createTodoList();
        AtomicInteger ai = new AtomicInteger(0);

        Action a = createTaskIncrementor(Action.Mode.SEQUENTIAL, ai);

        td.run();
        td.add(a);
        td.waitFor();
        assertEquals(1, ai.get());
        assertEquals(0, td.size());
        assertTrue(td.isRunning());
        assertFalse(td.isDone());
    }

    // todo: move to ActionList
    @Test(timeout = 20000, expected = ActionList.AddedException.class)
    public void addSameActionTwice() {
        ToDoActionList td = createTodoList();
        AtomicInteger ai = new AtomicInteger(0);

        Action a = createTaskIncrementor(Action.Mode.SEQUENTIAL, ai);
        td.add(a);
        td.add(a);

        td.run();
        td.waitFor();
    }

    @Test(timeout = 20000)
    public void addBeforeRun() {
        ToDoActionList td = createTodoList();
        AtomicInteger ai = new AtomicInteger(0);

        Action a = createTaskIncrementor(Action.Mode.SEQUENTIAL, ai);
        td.add(a);
        td.run();
        td.waitFor();

        assertEquals(1, ai.get());
        assertEquals(0, td.size());
        assertTrue(td.isRunning());
        assertFalse(td.isDone());
    }
}
