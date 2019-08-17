package es.danisales.listeners;

import es.danisales.tasks.Action;
import es.danisales.time.Sleep;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ListenerZeroTest {
    private static final int TEST_VALUE_SEQUENTIAL_EXPECTED = 3;
    private static final int TEST_VALUE_CONCURRENT_EXPECTED = 4;
    private static final int TIME_SLEEP = 200;

    private AtomicInteger listenerAddTestValues(ListenerZero listener, int times) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < times; i++)
            listener.add(() -> {
                atomicInteger.getAndIncrement();
                Sleep.sleep(TIME_SLEEP);
            });

        return atomicInteger;
    }

    private AtomicInteger listenerAddTestValuesThreadSafe(ListenerZero listener, int times) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < times; i++)
            listener.add(() -> {
                atomicInteger.getAndIncrement();
                listener.add(atomicInteger::getAndIncrement);
                Sleep.sleep(TIME_SLEEP);
            });

        return atomicInteger;
    }

    @Test
    public void newInstanceSequential() {
        ListenerZero listenerZero = ListenerZero.newInstanceSequential();

        AtomicInteger atomicInteger = listenerAddTestValues(listenerZero, TEST_VALUE_SEQUENTIAL_EXPECTED);

        listenerZero.call();

        assertEquals(TEST_VALUE_SEQUENTIAL_EXPECTED, atomicInteger.get());
    }

    @Test
    public void newInstanceSequentialThreadSafe() {
        ListenerZero l0 = ListenerZero.newInstanceSequentialThreadSafe();

        AtomicInteger atomicInteger = listenerAddTestValuesThreadSafe(l0, TEST_VALUE_SEQUENTIAL_EXPECTED);

        l0.call();

        assertEquals(TEST_VALUE_SEQUENTIAL_EXPECTED, atomicInteger.get());
    }

    @Test(timeout = TIME_SLEEP * (TEST_VALUE_CONCURRENT_EXPECTED - 1))
    public void newInstanceConcurrent() {
        ListenerZero listenerZero = ListenerZero.newInstanceConcurrent();

        AtomicInteger atomicInteger = listenerAddTestValues(listenerZero, TEST_VALUE_CONCURRENT_EXPECTED);

        listenerZero.call();

        assertEquals(TEST_VALUE_CONCURRENT_EXPECTED, atomicInteger.get());
    }

    @Test(timeout = TIME_SLEEP * (TEST_VALUE_CONCURRENT_EXPECTED - 1))
    public void newInstanceConcurrentThreadSafe() {
        ListenerZero l0 = ListenerZero.newInstanceConcurrentThreadSafe();

        AtomicInteger atomicInteger = listenerAddTestValuesThreadSafe(l0, TEST_VALUE_CONCURRENT_EXPECTED);

        l0.call();

        assertEquals(TEST_VALUE_CONCURRENT_EXPECTED, atomicInteger.get());
    }

    @Test
    public void getMode() {
        ListenerZero l0Sequential = ListenerZero.newInstanceSequential();
        assertEquals(Action.Mode.SEQUENTIAL, l0Sequential.getMode());

        ListenerZero l0SequentialThreadSafe = ListenerZero.newInstanceSequentialThreadSafe();
        assertEquals(Action.Mode.SEQUENTIAL, l0SequentialThreadSafe.getMode());

        ListenerZero l0Concurrent = ListenerZero.newInstanceConcurrent();
        assertEquals(Action.Mode.CONCURRENT, l0Concurrent.getMode());

        ListenerZero l0ConcurrentThreadSafe = ListenerZero.newInstanceConcurrentThreadSafe();
        assertEquals(Action.Mode.CONCURRENT, l0ConcurrentThreadSafe.getMode());
    }
}