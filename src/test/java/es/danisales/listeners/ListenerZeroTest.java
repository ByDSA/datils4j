package es.danisales.listeners;

import es.danisales.tasks.Action;
import es.danisales.time.Sleep;
import org.junit.Test;

import java.util.ConcurrentModificationException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ListenerZeroTest {
    private static final int TEST_VALUE_SEQUENTIAL_EXPECTED = 3;
    private static final int TEST_VALUE_CONCURRENT_EXPECTED = 4;
    private static final int TIME_SLEEP = 100;

    private AtomicInteger listenerAddTestValues(ListenerListZero listener, int times) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < times; i++)
            listener.add(() -> {
                atomicInteger.getAndIncrement();
                Sleep.sleep(TIME_SLEEP);
            });

        return atomicInteger;
    }

    private void listenerAddTestValuesConcurrentModification(ListenerListZero listener, int times) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < times; i++)
            listener.add(() -> {
                atomicInteger.getAndIncrement();
                listener.add(atomicInteger::getAndIncrement);
                Sleep.sleep(TIME_SLEEP);
            });
    }

    @Test
    public void newInstanceSequential() {
        ListenerListZero listenerZero = ListenerListZero.newInstanceSequential();

        AtomicInteger atomicInteger = listenerAddTestValues(listenerZero, TEST_VALUE_SEQUENTIAL_EXPECTED);

        listenerZero.call();

        assertEquals(TEST_VALUE_SEQUENTIAL_EXPECTED, atomicInteger.get());
    }

    @Test(expected = ConcurrentModificationException.class)
    public void newInstanceSequentialConcurrentModification() {
        ListenerListZero listenerZero = ListenerListZero.newInstanceSequential();

        listenerAddTestValuesConcurrentModification(listenerZero, TEST_VALUE_SEQUENTIAL_EXPECTED);

        listenerZero.call();
    }

    @Test
    public void newInstanceSequentialThreadSafe() {
        ListenerListZero l0 = ListenerListZero.newInstanceSequentialThreadSafe();

        AtomicInteger atomicInteger = listenerAddTestValues(l0, TEST_VALUE_SEQUENTIAL_EXPECTED);

        l0.call();

        assertEquals(TEST_VALUE_SEQUENTIAL_EXPECTED, atomicInteger.get());
    }

    @Test(expected = ConcurrentModificationException.class)
    public void newInstanceSequentialThreadSafeConcurrentModificationException() {
        ListenerListZero l0 = ListenerListZero.newInstanceSequentialThreadSafe();

        listenerAddTestValuesConcurrentModification(l0, TEST_VALUE_SEQUENTIAL_EXPECTED);

        l0.call();
    }

    @Test(timeout = TIME_SLEEP * (TEST_VALUE_CONCURRENT_EXPECTED))
    public void newInstanceConcurrent() {
        ListenerListZero listenerZero = ListenerListZero.newInstanceConcurrent();

        AtomicInteger atomicInteger = listenerAddTestValues(listenerZero, TEST_VALUE_CONCURRENT_EXPECTED);

        listenerZero.call();

        assertEquals(TEST_VALUE_CONCURRENT_EXPECTED, atomicInteger.get());
    }

    @Test(timeout = TIME_SLEEP * (TEST_VALUE_CONCURRENT_EXPECTED))
    public void newInstanceConcurrentConcurrentModification() {
        ListenerListZero listenerZero = ListenerListZero.newInstanceConcurrent();

        listenerAddTestValuesConcurrentModification(listenerZero, TEST_VALUE_CONCURRENT_EXPECTED);

        listenerZero.call();
    }

    @Test(timeout = TIME_SLEEP * (TEST_VALUE_CONCURRENT_EXPECTED))
    public void newInstanceConcurrentThreadSafe() {
        ListenerListZero l0 = ListenerListZero.newInstanceConcurrentThreadSafe();

        AtomicInteger atomicInteger = listenerAddTestValues(l0, TEST_VALUE_CONCURRENT_EXPECTED);

        l0.call();

        assertEquals(TEST_VALUE_CONCURRENT_EXPECTED, atomicInteger.get());
    }

    @Test(timeout = TIME_SLEEP * (TEST_VALUE_CONCURRENT_EXPECTED))
    public void newInstanceConcurrentThreadSafeConcurrentModification() {
        ListenerListZero l0 = ListenerListZero.newInstanceConcurrentThreadSafe();

        listenerAddTestValuesConcurrentModification(l0, TEST_VALUE_CONCURRENT_EXPECTED);

        l0.call();
    }

    @Test
    public void getMode() {
        ListenerListZero l0Sequential = ListenerListZero.newInstanceSequential();
        assertEquals(Action.Mode.SEQUENTIAL, l0Sequential.getMode());

        ListenerListZero l0SequentialThreadSafe = ListenerListZero.newInstanceSequentialThreadSafe();
        assertEquals(Action.Mode.SEQUENTIAL, l0SequentialThreadSafe.getMode());

        ListenerListZero l0Concurrent = ListenerListZero.newInstanceConcurrent();
        assertEquals(Action.Mode.CONCURRENT, l0Concurrent.getMode());

        ListenerListZero l0ConcurrentThreadSafe = ListenerListZero.newInstanceConcurrentThreadSafe();
        assertEquals(Action.Mode.CONCURRENT, l0ConcurrentThreadSafe.getMode());
    }
}