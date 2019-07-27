package es.danisales.tasks;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ActionWaitForTests {
    @Test(timeout = 1000)
    public void waitFor() {
        Action first = Action.of(Action.Mode.CONCURRENT, (Action a) -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        });
        first.setName("Action");
        first.run();
        assertTrue(!first.isDone());
        first.waitFor();
        assertTrue(first.isDone());
    }

    @Test
    public void waitForNextWithoutNext() {
        Action first = Action.of(Action.Mode.CONCURRENT, (Action a) -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        });
        first.run();
        assertTrue(!first.isDone());
        first.waitForNext();

        assertTrue(first.isDone());
    }

    @Test(timeout = 2000)
    public void waitNext10() {
        final int N = 10;
        Action[] actions = new Action[N + 1];
        Action root = Action.createPointless();
        actions[0] = root;
        root.setName("root");
        for (int i = 0; i < N; i++) {
            Action first = actions[i];
            Action second = Action.of(first);
            second.setName("Action " + i);
            first.addNext(second);
            actions[i + 1] = second;
        }

        root.run();
        int r2 = root.waitForNext();
        assertEquals(ActionValues.OK.intValue(), r2);
        for (Action a : actions)
            assertTrue(a.getName(), a.isDone());
    }

    @Test(timeout = 2000)
    public void waitNext() {
        Action first = Action.of(Action.Mode.CONCURRENT, (Action self) -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        first.setName("first");
        Action second = Action.of(first);
        second.setName("second");
        first.addNext(second);
        first.run();
        first.waitForNext();
        assertTrue(first.getName(), first.isDone());
        assertTrue(second.getName(), second.isDone());
    }

    @Test(timeout = 2000)
    public void waitNextBeforeRunning() {
        Action first = Action.of(Action.Mode.CONCURRENT, (Action self) -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        first.setName("first");
        Action second = Action.of(first);
        second.setName("second");
        first.addNext(second);
        first.run();
        assertFalse(first.getName(), first.isDone());
        second.waitForNext();
        assertTrue(second.getName(), second.isDone());
    }

    @Test(timeout = 4000)
    public void waitNextTimes() throws InterruptedException {
        Action first = Action.of(Action.Mode.CONCURRENT, (Action self) -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        first.setName("first");
        Action second = Action.of(first);
        second.setName("second");
        first.addNext(second);

        assertFalse(first.getName(), first.isDone());
        assertFalse(second.getName(), second.isDone());

        first.run();
        assertFalse(first.getName(), first.isDone());
        assertFalse(second.getName(), second.isDone());
        assertTrue(first.getName(), first.isRunning());

        first.waitForNext();
        assertTrue(first.getName(), first.isDone());
        assertTrue(second.getName(), second.isDone());
        assertFalse(first.getName(), first.isRunning());
        assertFalse(second.getName(), second.isRunning());

        first.run();
        assertFalse(first.getName(), first.isDone());
        assertTrue(second.getName(), second.isDone());
        assertTrue(first.getName(), first.isRunning());

        first.waitFor();
        Thread.sleep(100);
        assertFalse(second.getName(), second.isDone());

        first.waitForNext();
        assertTrue(first.getName(), first.isDone());
        assertTrue(second.getName(), second.isDone());
        assertFalse(first.getName(), first.isRunning());
        assertFalse(second.getName(), second.isRunning());
    }

    @Test
    public void simpleNextWaitConcurrent() throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger(0);

        Action a = Action.of(Action.Mode.CONCURRENT, (Action self) -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            atomicInteger.incrementAndGet();
        });
        a.setName("a");
        Action b = Action.of(a);
        b.setName("b");
        a.addNext(b);
        a.run();
        Thread.sleep(500);
        b.run();
        a.waitFor();
        assertEquals(1, atomicInteger.get());
        b.waitFor();
        assertEquals(2, atomicInteger.get());
        a.waitForNext();
        assertEquals(2, atomicInteger.get());
    }

    private void recursive(final Action a, final AtomicInteger ai, final int n, final int levels) {
        if (levels == 0)
            return;
        for (int i = 0; i < n; i++) {
            Action b;
            if (levels == 1) {
                b = new Action2(ai);
                b.setName("Leaf " + levels + " " + n);
            } else {
                b = Action.createPointless();
                b.setName("Branch " + levels + " " + n);
            }
            a.addNext(b);
            recursive(b, ai, n, levels - 1);
        }
    }

    // todo
    /*
    @Test
    public void treeNext() {
        AtomicInteger atomicInteger = new AtomicInteger( 0 );

        Action2 a = new Action2(atomicInteger);
        a.setName("base");
        recursive(a, atomicInteger, 3, 4);
        a.run();
        a.waitForNext();

        assertEquals(ActionTest.N + 1, atomicInteger.get());
    }
*/
    public static class Action2 extends ActionAdapter<Action2> {
        static int N = 0;
        final AtomicInteger atomicInteger;

        public Action2(AtomicInteger ai) {
            super(new ActionAdapter.Builder<Action2>()
                    .setMode(Action.Mode.CONCURRENT)
                    .setRun((Action2 self) -> self.atomicInteger.incrementAndGet()));
            atomicInteger = ai;
            successRules.add(() -> isRunning() || isDone());
            N++;
            setName("Action2(n=" + N + ")");
        }

        public Action2(Action2 a) {
            this(a.atomicInteger);
        }

        public boolean equals(Object o) {
            if (!(o instanceof Action2))
                return false;

            Action2 casted = (Action2) o;

            return super.equals(o)
                    && atomicInteger.get() == casted.atomicInteger.get();
        }
    }
}
