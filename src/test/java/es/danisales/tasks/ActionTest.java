package es.danisales.tasks;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static es.danisales.time.Sleep.sleep;
import static org.junit.Assert.*;

public class ActionTest {
	public static class Action2 extends Action {
		final AtomicInteger atomicInteger;
		public Action2(AtomicInteger ai) {
			super(Action.Mode.CONCURRENT);
			atomicInteger = ai;
		}
		@Override
		protected void innerRun() {
			atomicInteger.incrementAndGet();
		}
	}

	public static int N = 10000;

	public void recursive(Action2 a, int n, int levels) {
		if (levels == 0)
			return;
		for (int i = 0; i < n; i++) {
			Action2 b;
			if (levels == 1)
				b = new Action2(a.atomicInteger) {
					@Override
					protected void innerRun() {
						atomicInteger.incrementAndGet();
					}
				};
			else
				b = new Action2(a.atomicInteger) {
					@Override
					protected void innerRun() {
					}
				};
			a.addNext( b );
			recursive(b, n, levels-1);
		}
	}

	@Test
	public void cloneTest() {
		final AtomicInteger atomicInteger = new AtomicInteger( 0 );
		Action a = new Action(Action.Mode.CONCURRENT) {
			@Override
			protected void innerRun() {
				atomicInteger.incrementAndGet();
				sleep(100);
			}
		};
		a.run();
		Action a2 = a.newCopy();
		try {
			a.join();
		} catch (InterruptedException e) { }
		Action a3 = a.newCopy();
		assertTrue(a != a2);
		assertNotEquals(a, a2);
		assertEquals(a.isConcurrent(), a2.isConcurrent());
		assertFalse(a2.isDone());
		assertFalse(a3.isDone());
		assertFalse(a2.isRunning());
		assertNotEquals(a._lock, a2._lock);

		a2.run();
		try {
			a2.join();
        } catch (InterruptedException ignored) {
        }
		assertNotEquals(a, a2);
	}

	@Test
	public void nextSimple() {
		AtomicInteger ai = new AtomicInteger(0);
		Action first = new Action(Action.Mode.SEQUENTIAL) {
			@Override
			protected void innerRun() {
				ai.incrementAndGet();
			}
		};
		Action root = first;
		for (int i = 0; i < 10; i++) {
			Action second = first.newCopy();
			first.addNext(second);
			first = second;
		}

		root.run();
		root.joinNext();
		assertEquals(11, ai.get());
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

		Action2 a = new Action2(atomicInteger) {
			@Override
			protected void innerRun() {
			}
		};
		recursive(a, 10, 4);
		a.run();
		a.joinNext();

		assertEquals(N, atomicInteger.get());
	}
}
