package cn.com.mz.app.finance.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * CommonUtils 单元测试
 *
 * @author mz
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("通用工具类测试")
class CommonUtilsTest {

    // ==================== listToSet 测试 ====================

    @Test
    @DisplayName("测试列表转集合：正常列表")
    void testListToSetWithNormalList() {
        List<String> list = Arrays.asList("a", "b", "c", "a", "b");
        Set<String> set = CommonUtils.listToSet(list);

        assertNotNull(set);
        assertEquals(3, set.size());
        assertTrue(set.contains("a"));
        assertTrue(set.contains("b"));
        assertTrue(set.contains("c"));
    }

    @Test
    @DisplayName("测试列表转集合：空列表")
    void testListToSetWithEmptyList() {
        List<String> list = Collections.emptyList();
        Set<String> set = CommonUtils.listToSet(list);

        assertNotNull(set);
        assertTrue(set.isEmpty());
    }

    @Test
    @DisplayName("测试列表转集合：null输入")
    void testListToSetWithNull() {
        Set<String> set = CommonUtils.listToSet(null);

        assertNotNull(set);
        assertTrue(set.isEmpty());
    }

    @Test
    @DisplayName("测试列表转集合：包含null元素")
    void testListToSetWithNullElements() {
        List<String> list = Arrays.asList("a", null, "b", null, "c");
        Set<String> set = CommonUtils.listToSet(list);

        assertNotNull(set);
        assertEquals(3, set.size());
        assertTrue(set.contains("a"));
        assertTrue(set.contains("b"));
        assertTrue(set.contains("c"));
        assertFalse(set.contains(null));
    }

    @Test
    @DisplayName("测试列表转集合：全是null元素")
    void testListToSetWithAllNullElements() {
        List<String> list = Arrays.asList(null, null, null);
        Set<String> set = CommonUtils.listToSet(list);

        assertNotNull(set);
        assertTrue(set.isEmpty());
    }

    @Test
    @DisplayName("测试列表转集合：整数列表")
    void testListToSetWithIntegers() {
        List<Integer> list = Arrays.asList(1, 2, 3, 2, 1);
        Set<Integer> set = CommonUtils.listToSet(list);

        assertEquals(3, set.size());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
    }

    @Test
    @DisplayName("测试列表转集合：对象列表")
    void testListToSetWithObjects() {
        class TestObject {
            private final int value;

            TestObject(int value) {
                this.value = value;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                TestObject that = (TestObject) o;
                return value == that.value;
            }

            @Override
            public int hashCode() {
                return Objects.hash(value);
            }
        }

        List<TestObject> list = Arrays.asList(
                new TestObject(1),
                new TestObject(2),
                new TestObject(1)
        );

        Set<TestObject> set = CommonUtils.listToSet(list);

        assertEquals(2, set.size());
    }

    // ==================== listToList 测试 ====================

    @Test
    @DisplayName("测试列表转换：字符串转长度")
    void testListToListStringToLength() {
        List<String> list = Arrays.asList("a", "bb", "ccc");
        List<Integer> result = CommonUtils.listToList(list, String::length);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(Arrays.asList(1, 2, 3), result);
    }

    @Test
    @DisplayName("测试列表转换：整数转字符串")
    void testListToListIntegerToString() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        List<String> result = CommonUtils.listToList(list, String::valueOf);

        assertEquals(Arrays.asList("1", "2", "3"), result);
    }

    @Test
    @DisplayName("测试列表转换：null列表输入")
    void testListToListWithNullList() {
        List<String> result = CommonUtils.listToList(null, String::toUpperCase);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试列表转换：null函数输入")
    void testListToListWithNullFunction() {
        List<String> list = Arrays.asList("a", "b", "c");
        List<String> result = CommonUtils.listToList(list, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试列表转换：包含null元素")
    void testListToListWithNullElements() {
        List<String> list = Arrays.asList("a", null, "b", null, "c");
        List<String> result = CommonUtils.listToList(list, s -> s == null ? null : s.toUpperCase());

        assertEquals(Arrays.asList("A", "B", "C"), result);
    }

    @Test
    @DisplayName("测试列表转换：函数返回null")
    void testListToListWithFunctionReturningNull() {
        List<String> list = Arrays.asList("a", "b", "c");
        List<String> result = CommonUtils.listToList(list, s -> s.equals("b") ? null : s.toUpperCase());

        assertEquals(Arrays.asList("A", "C"), result);
    }

    @Test
    @DisplayName("测试列表转换：空列表")
    void testListToListWithEmptyList() {
        List<String> list = Collections.emptyList();
        List<String> result = CommonUtils.listToList(list, String::toUpperCase);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试列表转换：对象属性提取")
    void testListToListExtractProperty() {
        class Person {
            private final String name;

            Person(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }
        }

        List<Person> people = Arrays.asList(
                new Person("Alice"),
                new Person("Bob"),
                new Person("Charlie")
        );

        List<String> names = CommonUtils.listToList(people, Person::getName);

        assertEquals(Arrays.asList("Alice", "Bob", "Charlie"), names);
    }

    // ==================== listToMap 测试 ====================

    @Test
    @DisplayName("测试列表转Map：字符串作为key")
    void testListToMapWithStrings() {
        class Person {
            private final String id;
            private final String name;

            Person(String id, String name) {
                this.id = id;
                this.name = name;
            }

            public String getId() {
                return id;
            }

            public String getName() {
                return name;
            }
        }

        List<Person> people = Arrays.asList(
                new Person("1", "Alice"),
                new Person("2", "Bob"),
                new Person("3", "Charlie")
        );

        Map<String, Person> map = CommonUtils.listToMap(people, Person::getId);

        assertNotNull(map);
        assertEquals(3, map.size());
        assertEquals("Alice", map.get("1").getName());
        assertEquals("Bob", map.get("2").getName());
        assertEquals("Charlie", map.get("3").getName());
    }

    @Test
    @DisplayName("测试列表转Map：null列表输入")
    void testListToMapWithNullList() {
        Map<String, String> map = CommonUtils.listToMap(null, s -> s);

        assertNotNull(map);
        assertTrue(map.isEmpty());
    }

    @Test
    @DisplayName("测试列表转Map：null函数输入")
    void testListToMapWithNullFunction() {
        List<String> list = Arrays.asList("a", "b", "c");
        Map<String, String> map = CommonUtils.listToMap(list, null);

        assertNotNull(map);
        assertTrue(map.isEmpty());
    }

    @Test
    @DisplayName("测试列表转Map：包含null元素")
    void testListToMapWithNullElements() {
        class Person {
            private final String id;
            private final String name;

            Person(String id, String name) {
                this.id = id;
                this.name = name;
            }

            public String getId() {
                return id;
            }
        }

        List<Person> people = Arrays.asList(
                new Person("1", "Alice"),
                null,
                new Person("2", "Bob")
        );

        Map<String, Person> map = CommonUtils.listToMap(people, Person::getId);

        assertEquals(2, map.size());
        assertEquals("Alice", map.get("1").name);
        assertEquals("Bob", map.get("2").name);
    }

    @Test
    @DisplayName("测试列表转Map：函数返回null")
    void testListToMapWithFunctionReturningNull() {
        class Person {
            private final String id;
            private final String name;

            Person(String id, String name) {
                this.id = id;
                this.name = name;
            }

            public String getId() {
                return id;
            }
        }

        List<Person> people = Arrays.asList(
                new Person("1", "Alice"),
                new Person(null, "Bob"),
                new Person("2", "Charlie")
        );

        Map<String, Person> map = CommonUtils.listToMap(people, Person::getId);

        assertEquals(2, map.size());
        assertEquals("Alice", map.get("1").name);
        assertEquals("Charlie", map.get("2").name);
    }

    @Test
    @DisplayName("测试列表转Map：空列表")
    void testListToMapWithEmptyList() {
        List<String> list = Collections.emptyList();
        Map<String, String> map = CommonUtils.listToMap(list, s -> s);

        assertNotNull(map);
        assertTrue(map.isEmpty());
    }

    @Test
    @DisplayName("测试列表转Map：重复key（后者覆盖前者）")
    void testListToMapWithDuplicateKeys() {
        List<String> list = Arrays.asList("a", "b", "a");
        Map<String, String> map = CommonUtils.listToMap(list, s -> s);

        assertEquals(2, map.size());
        // 最后的"a"应该覆盖前面的
        assertEquals("a", map.get("a"));
        assertEquals("b", map.get("b"));
    }

    // ==================== 组合测试 ====================

    @Test
    @DisplayName("测试三个方法的组合使用")
    void testCombinationOfMethods() {
        // 原始数据
        List<String> names = Arrays.asList("alice", "bob", "charlie", "alice", "bob");

        // 1. 转换为大写
        List<String> upperNames = CommonUtils.listToList(names, String::toUpperCase);
        assertEquals(Arrays.asList("ALICE", "BOB", "CHARLIE", "ALICE", "BOB"), upperNames);

        // 2. 转为集合去重
        Set<String> uniqueNames = CommonUtils.listToSet(upperNames);
        assertEquals(3, uniqueNames.size());

        // 3. 转为Map（name -> length）
        Map<String, Integer> nameLengthMap = CommonUtils.listToMap(
                new ArrayList<>(uniqueNames),
                String::valueOf
        );
        // 这里只是演示，实际Map的value还是String
        assertEquals(3, nameLengthMap.size());
    }

    @Test
    @DisplayName("测试大数据量处理")
    void testLargeDataProcessing() {
        // 创建10000个元素的列表
        List<Integer> largeList = IntStream.range(0, 10000)
                .boxed()
                .collect(Collectors.toList());

        // 测试listToSet
        Set<Integer> set = CommonUtils.listToSet(largeList);
        assertEquals(10000, set.size());

        // 测试listToList
        List<String> stringList = CommonUtils.listToList(largeList, String::valueOf);
        assertEquals(10000, stringList.size());

        // 测试listToMap
        Map<Integer, Integer> map = CommonUtils.listToMap(largeList, i -> i);
        assertEquals(10000, map.size());
    }

    @Test
    @DisplayName("测试常量定义")
    void testConstants() {
        assertEquals("theordinaryCrmMao", CommonUtils.DM_LAND_APP_CODE);
        assertEquals("theordinaryCrm", CommonUtils.DM_APP_CODE);
        assertEquals("theordinaryCrmMao-Tmall", CommonUtils.DM_LAND_APP_ID);
        assertEquals("theordinaryCrm-Tmall", CommonUtils.DM_APP_ID);
    }

    @Test
    @DisplayName("测试链式转换")
    void testChainedConversion() {
        class Person {
            private final String name;
            private final int age;

            Person(String name, int age) {
                this.name = name;
                this.age = age;
            }

            public String getName() {
                return name;
            }

            public int getAge() {
                return age;
            }
        }

        List<Person> people = Arrays.asList(
                new Person("Alice", 25),
                new Person("Bob", 30),
                new Person("Charlie", 35)
        );

        // 提取名字
        List<String> names = CommonUtils.listToList(people, Person::getName);
        assertEquals(Arrays.asList("Alice", "Bob", "Charlie"), names);

        // 名字转大写
        List<String> upperNames = CommonUtils.listToList(names, String::toUpperCase);
        assertEquals(Arrays.asList("ALICE", "BOB", "CHARLIE"), upperNames);

        // 转为集合
        Set<String> nameSet = CommonUtils.listToSet(upperNames);
        assertEquals(3, nameSet.size());
    }

    @Test
    @DisplayName("测试类型转换：字符串转数字")
    void testStringToNumberConversion() {
        List<String> numbers = Arrays.asList("1", "2", "3", "4", "5");

        List<Integer> ints = CommonUtils.listToList(numbers, Integer::parseInt);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), ints);

        List<Long> longs = CommonUtils.listToList(numbers, Long::parseLong);
        assertEquals(Arrays.asList(1L, 2L, 3L, 4L, 5L), longs);
    }

    @Test
    @DisplayName("测试过滤null和空字符串")
    void testFilterNullAndEmptyStrings() {
        List<String> list = Arrays.asList("a", "", "b", null, "c", "", null);

        // 过滤null和空字符串
        List<String> filtered = CommonUtils.listToList(
                list,
                s -> (s != null && !s.isEmpty()) ? s : null
        );

        assertEquals(Arrays.asList("a", "b", "c"), filtered);
    }
}
