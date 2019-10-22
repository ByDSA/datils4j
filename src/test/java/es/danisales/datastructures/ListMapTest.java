package es.danisales.datastructures;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

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

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Test(expected = IndexOutOfBoundsException.class)
        public void getIndex() {
            listMap.get(0);
        }

        @Test(expected = IndexOutOfBoundsException.class)
        public void removeIndex() {
            listMap.remove(0);
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
            listMap.put("1+1", 2);
        }

        @Test
        public void initTest() {
            assertEquals(5, listMap.size());
            assertEquals(5, listMap.mapKeyPosition.size());

            assertEquals((Integer) 2, listMap.getByKey("2x1"));
            assertEquals((Integer) 4, listMap.getByKey("2x2"));
            assertEquals((Integer) 6, listMap.getByKey("2x3"));
            assertEquals((Integer) 8, listMap.getByKey("2x4"));
            assertEquals((Integer) 2, listMap.getByKey("1+1"));

            assertEquals((Integer) 0, listMap.mapKeyPosition.get("2x1"));
            assertEquals((Integer) 1, listMap.mapKeyPosition.get("2x2"));
            assertEquals((Integer) 2, listMap.mapKeyPosition.get("2x3"));
            assertEquals((Integer) 3, listMap.mapKeyPosition.get("2x4"));
            assertEquals((Integer) 4, listMap.mapKeyPosition.get("1+1"));

            assertEquals(new KeyValuePair<>("2x1", 2), listMap.get(0));
            assertEquals(new KeyValuePair<>("2x2", 4), listMap.get(1));
            assertEquals(new KeyValuePair<>("2x3", 6), listMap.get(2));
            assertEquals(new KeyValuePair<>("2x4", 8), listMap.get(3));
            assertEquals(new KeyValuePair<>("1+1", 2), listMap.get(4));
        }

        @Test
        public void removeIndexSize() {
            listMap.remove(0);
            assertEquals(4, listMap.size());
            assertEquals(4, listMap.mapKeyPosition.size());
        }

        @Test
        public void removeIndexOldEntry() {
            Map.Entry oldEntry = listMap.remove(2);
            assertEquals("2x3", oldEntry.getKey());
            assertEquals(6, oldEntry.getValue());
        }

        @Test(expected = IndexOutOfBoundsException.class)
        public void removeIndexOutIndexLowerBound() {
            listMap.remove(-1);
        }

        @Test(expected = IndexOutOfBoundsException.class)
        public void removeIndexOutIndexUpperBound() {
            listMap.remove(5);
        }

        @Test
        public void removeIndexEntryDisplacement() {
            listMap.remove(1);
            assertEquals("2x3", listMap.get(1).getKey());
            assertEquals((Integer) 6, listMap.get(1).getValue());
        }

        @Test
        public void removeByKeySize() {
            listMap.removeByKey("2x2");
            assertEquals(4, listMap.mapKeyPosition.size());
            assertEquals(4, listMap.size());
        }

        @Test
        public void removeByKeyOldValue() {
            Integer oldValue = listMap.removeByKey("2x2");
            assertEquals((Integer) 4, oldValue);
        }

        @SuppressWarnings("SuspiciousMethodCalls")
        @Test
        public void removeObjectTypeMismatch() {
            listMap.remove(123456.789);
            assertEquals(5, listMap.size());
        }

        @Test
        public void removeByKeyNotFound() {
            assertNull(listMap.removeByKey("3x3"));
        }

        @Test
        public void removeByKeyNull() {
            assertNull(listMap.removeByKey(null));
        }

        @Test
        public void removeObjectGetByKey() {
            listMap.removeByKey("2x2");
            assertNull(listMap.getByKey("2x2"));
        }

        @Test
        public void getIndex() {
            Map.Entry entry = listMap.get(0);
            assertEquals("2x1", entry.getKey());
            assertEquals(2, entry.getValue());
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Test(expected = IndexOutOfBoundsException.class)
        public void getIndexOutLowerBound() {
            listMap.get(-1);
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Test(expected = IndexOutOfBoundsException.class)
        public void getIndexOutUpperBound() {
            listMap.get(5);
        }

        @Test
        public void keySet() {
            Set<String> s = listMap.keySet();
            int i = 0;
            String[] keys = new String[]{"2x1", "2x2", "2x3", "2x4", "1+1"};
            for (String key : s) {
                assertEquals(keys[i++], key);
            }
        }

        @Test
        public void valueSet() {
            Collection<Integer> s = listMap.values();
            int i = 0;
            Integer[] values = new Integer[]{2, 4, 6, 8, 2};
            for (Integer value : s) {
                assertEquals(values[i++], value);
            }
        }

        @Test
        public void isEmpty() {
            assertFalse(listMap.isEmpty());
            assertFalse(listMap.mapKeyPosition.isEmpty());
        }

        @Test
        public void clear() {
            listMap.clear();
            assertTrue(listMap.isEmpty());
            assertTrue(listMap.mapKeyPosition.isEmpty());
        }

        @Test
        public void putNewValue() {
            listMap.put("2x5", 10);
            assertEquals(6, listMap.size());
            assertEquals(6, listMap.mapKeyPosition.size());
            assertEquals((Integer) 10, listMap.getByKey("2x5"));
            assertEquals((Integer) 10, listMap.get(5).getValue());
        }

        @Test
        public void putChangeValue() {
            listMap.put("2x4", 10);
            assertEquals(5, listMap.mapKeyPosition.size());
            assertEquals(5, listMap.size());
            assertEquals((Integer) 10, listMap.getByKey("2x4"));
            assertEquals((Integer) 10, listMap.get(3).getValue());
        }

        @Test
        public void containsKey1() {
            assertTrue(listMap.containsKey("2x1"));
            listMap.removeByKey("2x1");
            assertFalse(listMap.containsKey("2x1"));
        }

        @Test
        public void containsKey2() {
            assertTrue(listMap.containsKey("2x1"));
            listMap.remove(0);
            assertFalse(listMap.containsKey("2x1"));
        }

        @Test
        public void containsValue1() {
            assertTrue(listMap.containsValue(4));
            listMap.removeByKey("2x2");
            assertFalse(listMap.containsValue(4));
        }

        @Test
        public void containsValue2() {
            assertTrue(listMap.containsValue(4));
            listMap.remove(1);
            assertNotEquals((Integer) 4, listMap.get(1).getValue());
            assertFalse(listMap.containsValue(4));
        }

        @Test
        public void containsValue3() {
            assertTrue(listMap.containsValue(2));
            listMap.removeByKey("2x1");
            assertTrue(listMap.containsValue(2));
        }

        @Test
        public void containsValue4() {
            assertTrue(listMap.containsValue(2));
            listMap.removeByKey("2x1");
            assertTrue(listMap.containsValue(2));
            listMap.removeByKey("1+1");
            assertFalse(listMap.containsValue(2));
        }

        @Test
        public void indexOf() {
            assertEquals(0, listMap.indexOfKey("2x1"));
        }

        @Test
        public void indexOfNotFound() {
            assertEquals(-1, listMap.indexOfKey("2x10"));
        }

        @Test
        public void lastIndexOfValue() {
            assertEquals(4, listMap.lastIndexOfValue(2));
        }

        @Test
        public void lastIndexOfValueNotFound() {
            assertEquals(-1, listMap.lastIndexOfValue(22));
        }

        @SuppressWarnings("SuspiciousMethodCalls")
        @Test(expected = IllegalStateException.class)
        public void lastIndexOfMismatchType() {
            assertEquals(-1, listMap.lastIndexOf(2.2));
        }

        @Test
        public void lastIndexOf() {
            assertEquals(4, listMap.lastIndexOf(new KeyValuePair<>("1+1", 2)));
        }

        @Test
        public void lastIndexOfNotFound() {
            assertEquals(-1, listMap.lastIndexOf(new KeyValuePair<>("1+2", 3)));
        }

        @Test
        public void setValue() {
            listMap.setValue(0, 45);
            assertEquals("2x1", listMap.get(0).getKey());
            assertEquals((Integer) 45, listMap.get(0).getValue());
            assertEquals((Integer) 45, listMap.getByKey("2x1"));
        }

        @Test
        public void setValueOldValue() {
            Integer oldValue = listMap.setValue(0, 45);
            assertEquals((Integer) 2, oldValue);
        }

        @Test
        public void addExistingEntry() {
            listMap.add(new KeyValuePair<>("2x1", 2));
            assertEquals(5, listMap.size());
            assertEquals(5, listMap.mapKeyPosition.size());
        }
    }

    public static class Iterator {
        public static class First {
            ListMap<String, Integer> listMap;
            ListIterator<Map.Entry<String, Integer>> listMapIterator;

            @Before
            public void init() {
                ListMapTest.SampleStatus ss = new ListMapTest.SampleStatus();
                ss.init();
                listMap = ss.listMap;
                listMapIterator = listMap.iterator();
            }

            @Test
            public void hasNext() {
                assertTrue(listMapIterator.hasNext());
            }

            @Test
            public void hasPrevious() {
                assertFalse(listMapIterator.hasPrevious());
            }

            @Test
            public void nextIndex() {
                assertEquals(0, listMapIterator.nextIndex());
            }

            @Test
            public void previousIndex() {
                assertEquals(-1, listMapIterator.previousIndex());
            }

            @Test
            public void next() {
                assertEquals(new KeyValuePair<>("2x1", 2), listMapIterator.next());
            }

            @Test(expected = NoSuchElementException.class)
            public void previous() {
                listMapIterator.previous();
            }

            @Test(expected = IllegalStateException.class)
            public void remove() {
                listMapIterator.remove();
            }

            @Test
            public void add() {
                listMapIterator.add(new KeyValuePair<>("2+2", 4));
                assertEquals(new KeyValuePair<>("2+2", 4), listMap.get(0));
            }

            @Test(expected = IllegalStateException.class)
            public void set() {
                listMapIterator.set(new KeyValuePair<>("2", 2));
            }
        }

        public static class AfterNext {
            ListMap<String, Integer> listMap;
            ListIterator<Map.Entry<String, Integer>> listMapIterator;

            @Before
            public void init() {
                ListMapTest.SampleStatus ss = new ListMapTest.SampleStatus();
                ss.init();
                listMap = ss.listMap;
                listMapIterator = listMap.iterator();
                listMapIterator.next();
            }

            @Test
            public void hasNext() {
                assertTrue(listMapIterator.hasNext());
            }

            @Test
            public void hasPrevious() {
                assertTrue(listMapIterator.hasPrevious());
            }

            @Test
            public void nextIndex() {
                assertEquals(1, listMapIterator.nextIndex());
            }

            @Test
            public void previousIndex() {
                assertEquals(0, listMapIterator.previousIndex());
            }

            @Test
            public void next() {
                assertEquals(listMap.get(1), listMapIterator.next());
            }

            @Test
            public void previous() {
                assertEquals(listMap.get(0), listMapIterator.previous());
            }

            @Test
            public void remove() {
                listMapIterator.remove();
                assertEquals(4, listMap.size());
                assertEquals(new KeyValuePair<>("2x2", 4), listMap.get(0));
            }

            @Test
            public void add() {
                listMapIterator.add(new KeyValuePair<>("2+2", 4));
                assertEquals(6, listMap.size());
                assertEquals(new KeyValuePair<>("2+2", 4), listMap.get(1));
            }

            @Test
            public void set() {
                listMapIterator.set(new KeyValuePair<>("2", 2));
                assertEquals(5, listMap.size());
                assertEquals(new KeyValuePair<>("2", 2), listMap.get(0));
            }
        }

        public static class Last {
            private ListMap<String, Integer> listMap;
            private ListIterator<Map.Entry<String, Integer>> listMapIterator;

            @Before
            public void init() {
                ListMapTest.SampleStatus ss = new ListMapTest.SampleStatus();
                ss.init();
                listMap = ss.listMap;
                listMapIterator = listMap.iterator();
                while (listMapIterator.hasNext())
                    listMapIterator.next();
            }

            @Test
            public void hasNext() {
                assertFalse(listMapIterator.hasNext());
            }

            @Test
            public void hasPrevious() {
                assertTrue(listMapIterator.hasPrevious());
            }

            @Test
            public void nextIndex() {
                assertEquals(5, listMapIterator.nextIndex());
            }

            @Test
            public void previousIndex() {
                assertEquals(4, listMapIterator.previousIndex());
            }

            @Test(expected = NoSuchElementException.class)
            public void next() {
                listMapIterator.next();
            }

            @Test
            public void previous() {
                assertEquals(new KeyValuePair<>("1+1", 2), listMapIterator.previous());
            }

            @Test
            public void remove() {
                listMapIterator.remove();
                assertEquals(4, listMap.size());
                assertEquals(new KeyValuePair<>("2x4", 8), listMap.get(listMap.size() - 1));
            }

            @Test
            public void add() {
                listMapIterator.add(new KeyValuePair<>("2+2", 4));
                assertEquals(6, listMap.size());
                assertEquals(new KeyValuePair<>("2+2", 4), listMap.get(listMap.size() - 1));
            }

            @Test
            public void set() {
                listMapIterator.set(new KeyValuePair<>("2", 2));
                assertEquals(5, listMap.size());
                assertEquals(new KeyValuePair<>("2", 2), listMap.get(listMap.size() - 1));
            }
        }
    }
}