package es.danisales.tasks;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class ActionTest {
	class Action2 extends Action {
		AtomicInteger atomicInteger;
		public Action2(AtomicInteger ai) {
			atomicInteger = ai;
		}
		@Override
		protected void innerRun() {
			atomicInteger.incrementAndGet();
		}
	}
	
	int N = 10000;

	@Test
	public void sequential() {
		SequentialActionManager sam = new SequentialActionManager();
		AtomicInteger atomicInteger = new AtomicInteger( 0 );
		for (int i = 0; i < N; i++)
			sam.add( new Action2(atomicInteger) );

		for (int i = 0; i < N; i++)
			assertEquals(false, sam.get(i).isDone());

		for (int i = 0; i < N; i++)
			assertEquals(false, sam.get(i).isApplying());

		sam.run();

		try {
			sam.joinAll();
		} catch ( InterruptedException e ) {
			e.printStackTrace();
		}

		assertEquals(N, atomicInteger.get());

		for(int i = 0; i < N; i++)
			assertEquals(true, sam.get(i).isDone());

		for(int i = 0; i < N; i++)
			assertEquals(false, sam.get(i).isApplying());
	}

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
			Thread t = new Thread() {
				@Override
				public void run() {
					atomicInteger.incrementAndGet();
				}
			};
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
			Thread t = new Thread() {
				@Override
				public void run() {
					atomicInteger.incrementAndGet();
				}
			};
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

		try {
			a.joinAll();
		} catch ( InterruptedException e ) {
			e.printStackTrace();
		}

		assertEquals(N, atomicInteger.get());
	}
}
