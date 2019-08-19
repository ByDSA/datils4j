package es.danisales.tasks;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static es.danisales.time.Sleep.sleep;
import static org.junit.Assert.*;

public class ActionListTest {
    final static long runSleep = 100;

    @Test
    public void runConcurrentTaskInConcurrentList() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = ActionList.of(Action.Mode.CONCURRENT);
        Action action1 = Action.of(Action.Mode.CONCURRENT, (Action self) -> {
            for (int i = 0; i < 100; i++) {
                sleep(2);
                ai.incrementAndGet();
            }
        });
        action1.setName("action1");
        al.setName("al");
        al.add(action1);
        assertFalse(al.isRunning());
        assertFalse(action1.isRunning());
        al.run();
        assertTrue(al.isRunning());
        sleep(runSleep);
        assertTrue(al.isRunning());
        sleep(runSleep * 3);
        assertEquals(100, ai.get());
        assertTrue(action1.isDone());
        assertTrue(al.isDone());
    }
    @Test
    public void runConcurrentTaskInSequentialList() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = ActionList.of(Action.Mode.SEQUENTIAL);
        Action action1 = Action.of(Action.Mode.CONCURRENT, (Action self) -> {
            for (int i = 0; i < 200; i++) {
                sleep(2);
                ai.incrementAndGet();
            }
        });
        al.add(action1);
        assertFalse(al.isRunning());
        assertFalse(action1.isRunning());
        al.run();
        assertFalse(al.isRunning());
        assertEquals(200, ai.get());
    }

    @Test
    public void runSequentialTasksInSequentialList() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = ActionList.of(Action.Mode.SEQUENTIAL);
        for (int i = 0; i < 20; i++) {
            Action action1 = Action.of(Action.Mode.SEQUENTIAL, (Action self) -> {
                sleep(2);
                ai.incrementAndGet();
            });
            al.add(action1);
        }
        al.run();
        assertNotEquals(0, ai.get());
        assertEquals(20, ai.get());
    }

    @Test(timeout=1000)
    public void runConcurrentTasksInSequentialList() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = ActionList.of(Action.Mode.SEQUENTIAL);
        for (int i = 0; i < 20; i++) {
            Action action1 = Action.of(Action.Mode.CONCURRENT, (Action self) -> {
                sleep(200);
                ai.incrementAndGet();
            });
            al.add(action1);
        }
        al.run();
        assertEquals(20, ai.get());
    }

    @Test(timeout=1000)
    public void runConcurrentTasksInConcurrentList() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = ActionList.of(Action.Mode.CONCURRENT);
        for (int i = 0; i < 20; i++) {
            Action action1 = Action.of(Action.Mode.CONCURRENT, (Action self) -> {
                sleep(2);
                ai.incrementAndGet();
            });
            action1.setName("action-" + i);
            al.add(action1);
        }
        al.run();
        assertNotEquals(20, ai.get());
        sleep(100);
        assertNotEquals("No llama a las subacciones", 0, ai.get());
        assertEquals(20, ai.get());
    }
    @Test(timeout=2000)
    public void runSequentialTasksInConcurrentList() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = ActionList.of(Action.Mode.CONCURRENT);
        for (int i = 0; i < 20; i++) {
            Action action1 = Action.of(Action.Mode.SEQUENTIAL, (Action self) -> {
                sleep(20);
                ai.incrementAndGet();
            });
            al.add(action1);
        }
        al.run();
        assertNotEquals(20, ai.get());
        sleep(20*20-100);
        assertNotEquals(20, ai.get());
        sleep(400);
        assertEquals(20, ai.get());
    }

    @Test
    public void runNoTask() {
        ActionList al = ActionList.of(Action.Mode.CONCURRENT);
        al.run();
        sleep(runSleep);
        assertTrue(al.isSuccessful());
        assertTrue(al.isDone());
        assertFalse(al.isRunning());
    }

    @Test
    public void sequential() {
        ActionList sam = ActionList.of(Action.Mode.SEQUENTIAL);
        AtomicInteger atomicInteger = new AtomicInteger( 0 );
        for (int i = 0; i < ActionTest.N; i++)
            sam.add(new ActionWaitForTests.Action2(atomicInteger));

        for (int i = 0; i < ActionTest.N; i++)
            assertFalse(sam.get(i).isDone());

        for (int i = 0; i < ActionTest.N; i++)
            assertFalse(sam.get(i).isRunning());

        sam.run();

        assertEquals(ActionTest.N, atomicInteger.get());

        for(int i = 0; i < ActionTest.N; i++)
            assertTrue("" + i, sam.get(i).isDone());

        for(int i = 0; i <ActionTest. N; i++)
            assertFalse(sam.get(i).isRunning());
    }

    @Test
    public void runSameClonedTaskSequentialy() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = ActionList.of(Action.Mode.SEQUENTIAL);
        al.setName("Action List");
        Action action1 = Action.of(Action.Mode.SEQUENTIAL, (Action self) -> {
            sleep(2);
            ai.incrementAndGet();
        });
        for (int i = 0; i < 20; i++) {
            Action a = Action.of(action1);
            a.setName("Action " + i);
            al.add(a);
        }
        al.run();
        assertTrue(al.isDone());
        assertFalse(al.isRunning());
        assertEquals(20, ai.get());
    }

    @Test(expected= ActionList.AddedException.class)
    public void runSameTaskException() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = ActionList.of(Action.Mode.SEQUENTIAL);
        Action action1 = Action.of(Action.Mode.SEQUENTIAL, (Action self) -> {
            sleep(2);
            ai.incrementAndGet();
        });
        for (int i = 0; i < 20; i++)
            al.add(action1);
        al.run();
    }

    @Test
    public void runSameClonedTaskConcurrently() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = ActionList.of(Action.Mode.SEQUENTIAL);
        Action action1 = Action.of(Action.Mode.CONCURRENT, (Action self) -> {
            sleep(2);
            ai.incrementAndGet();
        });
        action1.setName("Action Base");
        for (int i = 0; i < 20; i++) {
            Action a = Action.of(action1);
            a.setName(action1.getName()+ " (copy " + i + ")");
            al.add(a);
        }
        assertEquals(20, al.size());
        al.run();
        assertTrue(al.isDone());
        assertFalse(al.isRunning());
        assertEquals(20, ai.get());
    }

    @Test
    public void size() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = ActionList.of(Action.Mode.SEQUENTIAL);
        Action action1 = Action.of(Action.Mode.CONCURRENT, (Action self) -> {
        });
        assertTrue(al.isEmpty());
        assertEquals(0, al.size());
        al.add(action1);
        assertFalse(al.isEmpty());
        assertEquals(1, al.size());
    }
}
