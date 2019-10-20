package es.danisales.utils.datastructures;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

@SuppressWarnings("unused")
public class ListMapTest {
    public static class InitialStatus {
        private ListMap<String, Integer> listMap;

        @Before
        public void init() {
            listMap = new ListMap<>();
        }

        @Test
        public void size() {
            assertEquals(0, listMap.size());
        }

        @Test
        public void isEmpty() {
            assertTrue(listMap.isEmpty());
        }

        @Test
        public void keySet() {
            Set s = listMap.keySet();
            assertTrue(s.isEmpty());
        }

        @Test
        public void values() {
            Collection s = listMap.values();
            assertTrue(s.isEmpty());
        }

        @Test
        public void entrySet() {
            Set s = listMap.entrySet();
            assertTrue(s.isEmpty());
        }

        @Test(expected = IndexOutOfBoundsException.class)
        public void getIndex() {
            listMap.getIndex(0);
        }

        @Test(expected = IndexOutOfBoundsException.class)
        public void removeIndex() {
            listMap.removeIndex(0);
        }
    }

    public static class SampleStatus {
        private ListMap<String, Integer> listMap;

        @Before
        public void init() {
            listMap = new ListMap<>();
            listMap.put("2x1", 2);
            listMap.put("2x2", 4);
            listMap.put("2x3", 6);
            listMap.put("2x4", 8);
        }

        @Test
        public void removeIndexSize() {
            assertEquals(4, listMap.size());
            listMap.removeIndex(0);
            assertEquals(3, listMap.size());
        }

        @Test
        public void removeIndexOldEntry() {
            Map.Entry oldEntry = listMap.removeIndex(2);
            assertEquals("2x3", oldEntry.getKey());
            assertEquals(6, oldEntry.getValue());
        }

        @Test(expected = IndexOutOfBoundsException.class)
        public void removeIndexOutIndexLowerBound() {
            listMap.removeIndex(-1);
        }

        @Test(expected = IndexOutOfBoundsException.class)
        public void removeIndexOutIndexUpperBound() {
            listMap.removeIndex(4);
        }

        @Test
        public void removeIndexEntryDisplacement() {
            assertEquals("2x2", listMap.getIndex(1).getKey());
            assertEquals((Integer) 4, listMap.getIndex(1).getValue());
            listMap.removeIndex(1);
            assertEquals("2x3", listMap.getIndex(1).getKey());
            assertEquals((Integer) 6, listMap.getIndex(1).getValue());
        }

        @Test
        public void removeObjectSize() {
            assertEquals(4, listMap.size());
            assertEquals(4, listMap.list.size());
            assertEquals(4, listMap.map.size());
            listMap.remove("2x2");
            assertEquals(3, listMap.map.size());
            assertEquals(3, listMap.list.size());
            assertEquals(3, listMap.size());
        }

        @Test
        public void removeObjectOldValue() {
            Integer oldValue = listMap.remove("2x2");
            assertEquals((Integer) 4, oldValue);
        }

        @SuppressWarnings("SuspiciousMethodCalls")
        @Test
        public void removeObjectTypeMismatch() {
            assertEquals(4, listMap.size());
            listMap.remove(123456.789);
            assertEquals(4, listMap.size());
        }

        @Test
        public void removeObjectNotFound() {
            assertNull(listMap.remove("3x3"));
        }

        @Test
        public void removeObjectNull() {
            assertNull(listMap.remove(null));
        }

        @Test
        public void removeObjectGetByKey() {
            assertEquals((Integer) 4, listMap.get("2x2"));
            listMap.remove("2x2");
            assertNull(listMap.get("2x2"));
        }

        @Test
        public void getIndex() {
            Map.Entry entry = listMap.getIndex(0);
            assertEquals("2x1", entry.getKey());
            assertEquals(2, entry.getValue());
        }

        @Test(expected = IndexOutOfBoundsException.class)
        public void getIndexOutLowerBound() {
            listMap.getIndex(-1);
        }

        @Test(expected = IndexOutOfBoundsException.class)
        public void getIndexOutUpperBound() {
            listMap.getIndex(4);
        }

        @Test
        public void keySet() {
            Set<String> s = listMap.keySet();
            int i = 0;
            String[] keys = new String[]{"2x1", "2x2", "2x3", "2x4"};
            for (String key : s) {
                assertEquals(keys[i++], key);
            }
        }

        @Test
        public void valueSet() {
            Collection<Integer> s = listMap.values();
            int i = 0;
            Integer[] values = new Integer[]{2, 4, 6, 8};
            for (Integer value : s) {
                assertEquals(values[i++], value);
            }
        }

        @Test
        public void isEmpty() {
            assertFalse(listMap.isEmpty());
            assertFalse(listMap.list.isEmpty());
            assertFalse(listMap.map.isEmpty());
        }

        @Test
        public void clear() {
            listMap.clear();
            assertTrue(listMap.isEmpty());
            assertTrue(listMap.list.isEmpty());
            assertTrue(listMap.map.isEmpty());
        }

        @Test
        public void putNewValue() {
            listMap.put("2x5", 10);
            assertEquals(5, listMap.size());
            assertEquals(5, listMap.list.size());
            assertEquals(5, listMap.map.size());
            assertEquals((Integer) 10, listMap.get("2x5"));
            assertEquals((Integer) 10, listMap.getIndex(4).getValue());
        }

        @Test
        public void putChangeValue() {
            listMap.put("2x4", 10);
            assertEquals(4, listMap.size());
            assertEquals(4, listMap.list.size());
            assertEquals(4, listMap.map.size());
            assertEquals((Integer) 10, listMap.get("2x4"));
            assertEquals((Integer) 10, listMap.getIndex(3).getValue());
        }
    }
}