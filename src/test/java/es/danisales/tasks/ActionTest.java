package es.danisales.tasks;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static es.danisales.time.Sleep.sleep;
import static org.junit.Assert.*;

public class ActionTest {
	public static int N = 10000;

	@Test
	public void cloneTest() {
		Action a = Action.of(Action.Mode.CONCURRENT, (Action self) -> {
			sleep(100);
		});
		a.run();
		Action a2 = Action.of(a);
		assertNotEquals(a, a2);
		try {
			a.join();
			assertTrue(a.isDone());
		} catch (InterruptedException ignored) {
		}
		assertFalse(a2.isDone());
		Action a3 = Action.of(a);
		assertTrue(a != a2);
		assertNotEquals(a, a2);
		assertFalse(a2.isDone());
		assertFalse(a3.isDone());
		assertFalse(a2.isRunning());

		a2.run();
		try {
			a2.join();
        } catch (InterruptedException ignored) {
        }
		assertEquals(a, a2);
	}

	@Test
	public void pointless() {
		Action p = Action.createPointless();
		Action p2 = Action.createPointless();
		assertEquals(p, p2);
		assertFalse(p == p2);
	}

	@Test(expected = NullPointerException.class)
	public void modeNull() {
		Action.of(null, (Action) -> {
		});
	}

	@Test(expected = NullPointerException.class)
	public void builderModeNull() {
		new ActionAdapter.Builder<>()
				.setMode(null)
				.setRun((Action) -> {
				})
				.build();
	}

	@Test(expected = NullPointerException.class)
	public void builderRunNull() {
		new ActionAdapter.Builder<>()
				.setMode(Action.Mode.SEQUENTIAL)
				.setRun(null)
				.build();
	}

	@Test(expected = NullPointerException.class)
	public void runNull() {
		Action.of(Action.Mode.SEQUENTIAL, null);
	}

	@Test
	public void join() {
		Action first = Action.of(Action.Mode.SEQUENTIAL, (Action a) -> {
		});
		first.run();
		try {
			first.join();
		} catch (InterruptedException ignored) {
		}

		assertTrue(first.isDone());
	}

	@Test
	public void joinNextWithoutNext() {
		Action first = Action.of(Action.Mode.SEQUENTIAL, (Action a) -> {
		});
		first.run();
		first.joinNext();

		assertTrue(first.isDone());
	}

	@Test
	public void joinNext() {
		Action first = Action.of(Action.Mode.SEQUENTIAL, (Action a) -> {
		});
		Action root = first;
		root.setName("root");
		for (int i = 0; i < 10; i++) {
			Action second = Action.of(first);
			second.setName("Action " + i);
			first.addNext(second);
			first = second;
		}

		root.run();
		root.joinNext();
		while (((ActionAdapter) root).next.size() > 0) {
			assertTrue(root.getName(), root.isDone());
			root = (ActionAdapter) ((ActionAdapter) root).next.get(0);
		}
	}

	@Test
	public void nextSimple() {
		AtomicInteger ai = new AtomicInteger(0);
		Action first = Action.of(Action.Mode.SEQUENTIAL, (Action a) -> ai.incrementAndGet());
		Action root = first;
		root.setName("root");
		for (int i = 0; i < 10; i++) {
			Action second = Action.of(first);
			second.setName("Action " + i);
			first.addNext(second);
			first = second;
		}

		root.run();
		root.joinNext();
		assertEquals(11, ai.get());
	}

	public void recursive(Action a, AtomicInteger ai, int n, int levels) {
		if (levels == 0)
			return;
		for (int i = 0; i < n; i++) {
			Action b;
			if (levels == 1)
				b = new Action2(ai);
			else
				b = Action.createPointless();
			a.addNext(b);
			recursive(b, ai, n, levels - 1);
		}
	}

	@Test
	public void incr() {
		AtomicInteger atomicInteger = new AtomicInteger( 0 );
		for(int i = 0; i < N; i++)
			atomicInteger.incrementAndGet();

		assertEquals(N, atomicInteger.get());
	}

	@Test
	public void incrThreadSeq() {
		final AtomicInteger atomicInteger = new AtomicInteger( 0 );
		for(int i = 0; i < N; i++) {
			Thread t = new Thread(() -> atomicInteger.incrementAndGet());
			t.start();
			try {
				t.join();
			} catch ( InterruptedException e ) {
				e.printStackTrace();
			}
		}

		assertEquals(N, atomicInteger.get());
	}

	@Test
	public void incrThread() {
		final AtomicInteger atomicInteger = new AtomicInteger( 0 );
		Thread last = null;
		for(int i = 0; i < N; i++) {
			Thread t = new Thread(() -> atomicInteger.incrementAndGet());
			t.start();
			if (i == N-1)
				last = t;
		}

		try {
			last.join();
		} catch ( InterruptedException e ) {
			e.printStackTrace();
		}

		assertEquals(N, atomicInteger.get());
	}

	@Test
	public void tree() {
		AtomicInteger atomicInteger = new AtomicInteger( 0 );

		Action2 a = new Action2(atomicInteger);
		a.setName("base");
		recursive(a, atomicInteger, 10, 4);
		a.run();
		a.joinNext();

		assertEquals(N + 1, atomicInteger.get());
	}

	public static class Action2 extends ActionAdapter<Action2> {
		final AtomicInteger atomicInteger;
		static int N = 0;

		public Action2(AtomicInteger ai) {
			super(new ActionAdapter.Builder<Action2>()
					.setMode(Action.Mode.CONCURRENT)
					.setRun((Action2 self) -> self.atomicInteger.incrementAndGet()));
			atomicInteger = ai;
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
