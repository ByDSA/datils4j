package es.danisales.tasks;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static es.danisales.time.Sleep.sleep;
import static org.junit.Assert.*;

public class ActionListTest {
    final static long runSleep = 200;

    @Test
    public void runConcurrentTaskInConcurrentList() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = new ActionList(Action.Mode.CONCURRENT);
        Action action1 = new Action(Action.Mode.CONCURRENT) {
            @Override
            protected void innerRun() {
                for (int i = 0; i < 200; i++) {
                    sleep(2);
                    ai.incrementAndGet();
                }
            }
        };
        al.add(action1);
        assertFalse(al.isRunning());
        assertFalse(action1.isRunning());
        al.run();
        assertTrue(al.isRunning());
        sleep(runSleep);
        assertTrue(action1.isRunning());
        assertNotEquals(0, ai.get());
    }
    @Test
    public void runConcurrentTaskInSequentialList() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = new ActionList(Action.Mode.SEQUENTIAL);
        Action action1 = new Action(Action.Mode.CONCURRENT) {
            @Override
            protected void innerRun() {
                for (int i = 0; i < 200; i++) {
                    sleep(2);
                    ai.incrementAndGet();
                }
            }
        };
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

        ActionList al = new ActionList(Action.Mode.SEQUENTIAL);
        for (int i = 0; i < 20; i++) {
            Action action1 = new Action(Action.Mode.SEQUENTIAL) {
                @Override
                protected void innerRun() {
                    sleep(2);
                    ai.incrementAndGet();
                }
            };
            al.add(action1);
        }
        al.run();
        assertNotEquals(0, ai.get());
        assertEquals(20, ai.get());
    }

    @Test(timeout=1000)
    public void runConcurrentTasksInSequentialList() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = new ActionList(Action.Mode.SEQUENTIAL);
        for (int i = 0; i < 20; i++) {
            Action action1 = new Action(Action.Mode.CONCURRENT) {
                @Override
                protected void innerRun() {
                    sleep(200);
                    ai.incrementAndGet();
                }
            };
            al.add(action1);
        }
        al.run();
        assertEquals(20, ai.get());
    }

    @Test(timeout=1000)
    public void runConcurrentTasksInConcurrentList() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = new ActionList(Action.Mode.CONCURRENT);
        for (int i = 0; i < 20; i++) {
            Action action1 = new Action(Action.Mode.CONCURRENT) {
                @Override
                protected void innerRun() {
                    sleep(20);
                    ai.incrementAndGet();
                }
            };
            al.add(action1);
        }
        al.run();
        assertNotEquals(20, ai.get());
        sleep(100);
        assertEquals(20, ai.get());
    }
    @Test(timeout=2000)
    public void runSequentialTasksInConcurrentList() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = new ActionList(Action.Mode.CONCURRENT);
        for (int i = 0; i < 20; i++) {
            Action action1 = new Action(Action.Mode.SEQUENTIAL) {
                @Override
                protected void innerRun() {
                    sleep(20);
                    ai.incrementAndGet();
                }
            };
            al.add(action1);
        }
        al.run();
        assertNotEquals(20, ai.get());
        sleep(20*20-100);
        assertNotEquals(20, ai.get());
        sleep(200);
        assertEquals(20, ai.get());
    }

    @Test
    public void runNoTask() {
        ActionList al = new ActionList(Action.Mode.CONCURRENT);
        al.run();
        sleep(runSleep);
        assertTrue(al.isDone());
        assertFalse(al.isRunning());
    }

    @Test
    public void runSameClonedTaskSequentialy() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = new ActionList(Action.Mode.SEQUENTIAL);
        Action action1 = new Action(Action.Mode.SEQUENTIAL) {
            @Override
            protected void innerRun() {
                sleep(2);
                ai.incrementAndGet();
            }
        };
        for (int i = 0; i < 20; i++)
            al.add(action1.getCopy());
        al.run();
        assertTrue(al.isDone());
        assertFalse(al.isRunning());
        assertEquals(20, ai.get());
    }

    @Test(expected= ActionList.AddedException.class)
    public void runSameTaskException() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = new ActionList(Action.Mode.SEQUENTIAL);
        Action action1 = new Action(Action.Mode.SEQUENTIAL) {
            @Override
            protected void innerRun() {
                sleep(2);
                ai.incrementAndGet();
            }
        };
        for (int i = 0; i < 20; i++)
            al.add(action1);
        al.run();
    }

    @Test
    public void runSameClonedTaskConcurrently() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = new ActionList(Action.Mode.SEQUENTIAL);
        Action action1 = new Action(Action.Mode.CONCURRENT) {
            @Override
            protected void innerRun() {
                sleep(2);
                ai.incrementAndGet();
            }
        };
        for (int i = 0; i < 20; i++)
            al.add(action1.getCopy());
        assertEquals(20, al.size());
        al.run();
        assertTrue(al.isDone());
        assertFalse(al.isRunning());
        assertEquals(20, ai.get());
    }

    @Test
    public void size() {
        AtomicInteger ai = new AtomicInteger(0);

        ActionList al = new ActionList(Action.Mode.SEQUENTIAL);
        Action action1 = new Action(Action.Mode.CONCURRENT) {
            @Override
            protected void innerRun() {
            }
        };
        assertTrue(al.isEmpty());
        assertEquals(0, al.size());
        al.add(action1);
        assertFalse(al.isEmpty());
        assertEquals(1, al.size());
    }
}
