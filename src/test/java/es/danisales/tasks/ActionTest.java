package es.danisales.tasks;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static es.danisales.time.Sleep.sleep;
import static org.junit.Assert.*;

public class ActionTest {
	public static int N = 1000;

	@Test
	public void checkingTimeValue() {
		// Getter & setter testing
		Action a = Action.of(Action.Mode.CONCURRENT, (Action self) -> {
			sleep(100);
		});
		long iniCheckTime = a.getCheckingTime();
		final long newValue = 500;
		a.setCheckingTime(newValue);
		assertEquals(newValue, a.getCheckingTime());

		// Same value on new instance
		Action a2 = Action.of(Action.Mode.CONCURRENT, (Action self) -> {
			sleep(100);
		});
		assertEquals(iniCheckTime, a2.getCheckingTime());
	}

	@Test
	public void afterActions() {
		AtomicInteger ai = new AtomicInteger(0);
		Action a = Action.of(Action.Mode.CONCURRENT, (Action self) -> {
			sleep(100);
		});
		Action a2 = Action.of(Action.Mode.CONCURRENT, (Action self) -> {
			sleep(50);
			ai.incrementAndGet();
		});
		a.addAfter(a2);
		assertEquals(0, ai.get());
		a.run();
		assertEquals(0, ai.get());
		a.waitFor();
		assertEquals(0, ai.get());
		a2.waitFor();
		assertEquals(1, ai.get());
	}

	@Test
	public void cloneTest() {
		Action a = Action.of(Action.Mode.CONCURRENT, (Action self) -> {
			sleep(100);
		});
		a.run();
		Action a2 = Action.of(a);
		assertNotEquals(a, a2);
		a.waitFor();
		assertTrue(a.isDone());
		assertFalse(a2.isDone());
		Action a3 = Action.of(a);
		assertTrue(a != a2);
		assertNotEquals(a, a2);
		assertFalse(a2.isDone());
		assertFalse(a3.isDone());
		assertFalse(a2.isRunning());

		a2.run();
		a2.waitFor();
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
}
