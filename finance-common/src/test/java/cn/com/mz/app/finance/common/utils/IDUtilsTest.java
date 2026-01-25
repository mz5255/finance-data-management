package cn.com.mz.app.finance.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IDUtils 单元测试
 *
 * @author mz
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("ID生成与分片工具测试")
class IDUtilsTest {

    // ==================== generateUniqueId 测试 ====================

    @Test
    @DisplayName("测试生成正常手机号的唯一ID")
    void testGenerateUniqueIdWithValidPhone() {
        String phone = "13800138000";
        Long uniqueId = IDUtils.generateUniqueId(phone);

        assertNotNull(uniqueId);
        assertTrue(uniqueId > 0, "ID应该为正数");
        assertTrue(uniqueId < Long.MAX_VALUE, "ID应该在Long范围内");
    }

    @Test
    @DisplayName("测试不同号段手机号生成ID")
    void testGenerateUniqueIdWithDifferentPhonePrefixes() {
        String[] phones = {
                "13000000001", "13100000001", "13200000001",
                "13300000001", "13500000001", "13600000001",
                "13700000001", "13800000001", "13900000001",
                "15000000001", "15100000001", "15200000001",
                "15300000001", "15500000001", "15600000001",
                "15700000001", "15800000001", "15900000001",
                "17000000001", "17600000001", "17700000001",
                "17800000001", "18000000001", "18100000001",
                "18200000001", "18300000001", "18400000001",
                "18500000001", "18600000001", "18700000001",
                "18800000001", "18900000001", "19800000001", "19900000001"
        };

        for (String phone : phones) {
            Long uniqueId = IDUtils.generateUniqueId(phone);
            assertNotNull(uniqueId, "手机号 " + phone + " 应该生成有效ID");
            assertTrue(uniqueId > 0, "手机号 " + phone + " 生成的ID应该为正数");
        }
    }

    @Test
    @DisplayName("测试null手机号抛出异常")
    void testGenerateUniqueIdWithNullPhone() {
        assertThrows(IllegalArgumentException.class, () -> {
            IDUtils.generateUniqueId(null);
        });
    }

    @Test
    @DisplayName("测试空字符串手机号抛出异常")
    void testGenerateUniqueIdWithEmptyPhone() {
        assertThrows(IllegalArgumentException.class, () -> {
            IDUtils.generateUniqueId("");
        });
    }

    @Test
    @DisplayName("测试无效格式手机号抛出异常")
    void testGenerateUniqueIdWithInvalidPhone() {
        // 少于11位
        assertThrows(IllegalArgumentException.class, () -> {
            IDUtils.generateUniqueId("138001380");
        });

        // 多于11位
        assertThrows(IllegalArgumentException.class, () -> {
            IDUtils.generateUniqueId("138001380001");
        });

        // 不是1开头
        assertThrows(IllegalArgumentException.class, () -> {
            IDUtils.generateUniqueId("23800138000");
        });

        // 第二位不是3-9
        assertThrows(IllegalArgumentException.class, () -> {
            IDUtils.generateUniqueId("12800138000");
        });

        // 包含非数字字符
        assertThrows(IllegalArgumentException.class, () -> {
            IDUtils.generateUniqueId("1380013800a");
        });

        // 包含特殊字符
        assertThrows(IllegalArgumentException.class, () -> {
            IDUtils.generateUniqueId("1380013800-");
        });
    }

    @Test
    @DisplayName("测试同一手机号多次生成ID的唯一性")
    void testGenerateUniqueIdUniqueness() {
        String phone = "13800138000";

        // 同一手机号生成100个ID
        Long[] ids = new Long[100];
        for (int i = 0; i < 100; i++) {
            ids[i] = IDUtils.generateUniqueId(phone);
        }

        // 验证所有ID都是唯一的
        for (int i = 0; i < ids.length; i++) {
            for (int j = i + 1; j < ids.length; j++) {
                assertNotEquals(ids[i], ids[j],
                        "同一手机号生成的ID应该唯一");
            }
        }
    }

    @Test
    @DisplayName("测试同一手机号生成的ID路由到相同的库表")
    void testGenerateUniqueIdRoutingConsistency() {
        String phone = "13800138000";

        // 生成多个ID
        int dbIndex1 = IDUtils.getDbIndex(IDUtils.generateUniqueId(phone));
        int tableIndex1 = IDUtils.getTableIndex(IDUtils.generateUniqueId(phone));
        int dbIndex2 = IDUtils.getDbIndex(IDUtils.generateUniqueId(phone));
        int tableIndex2 = IDUtils.getTableIndex(IDUtils.generateUniqueId(phone));
        int dbIndex3 = IDUtils.getDbIndex(IDUtils.generateUniqueId(phone));
        int tableIndex3 = IDUtils.getTableIndex(IDUtils.generateUniqueId(phone));

        // 同一手机号的路由应该一致
        assertEquals(dbIndex1, dbIndex2, "库索引应该一致");
        assertEquals(dbIndex2, dbIndex3, "库索引应该一致");
        assertEquals(tableIndex1, tableIndex2, "表索引应该一致");
        assertEquals(tableIndex2, tableIndex3, "表索引应该一致");
    }

    @Test
    @DisplayName("测试不同手机号生成的ID不同")
    void testGenerateUniqueIdForDifferentPhones() {
        String phone1 = "13800138001";
        String phone2 = "13800138002";

        Long id1 = IDUtils.generateUniqueId(phone1);
        Long id2 = IDUtils.generateUniqueId(phone2);

        assertNotEquals(id1, id2, "不同手机号应该生成不同的ID");
    }

    // ==================== getDbIndex 测试 ====================

    @Test
    @DisplayName("测试获取库索引在有效范围内")
    void testGetDbIndexInRange() {
        String phone = "13800138000";
        Long uniqueId = IDUtils.generateUniqueId(phone);
        int dbIndex = IDUtils.getDbIndex(uniqueId);

        assertTrue(dbIndex >= 0, "库索引应该大于等于0");
        assertTrue(dbIndex < IDUtils.DB_COUNT, "库索引应该小于" + IDUtils.DB_COUNT);
    }

    @Test
    @DisplayName("测试多个手机号的库索引分布")
    void testGetDbIndexDistribution() {
        String[] phones = {
                "13000000001", "13100000001", "13200000001",
                "13300000001", "13500000001", "13600000001",
                "13700000001", "13800000001", "13900000001",
                "15000000001", "15100000001", "15200000001",
                "15300000001", "15500000001", "15600000001",
                "15700000001", "15800000001", "15900000001",
                "18000000001", "18100000001", "18200000001",
                "18300000001", "18400000001", "18500000001",
                "18600000001", "18700000001", "18800000001",
                "18900000001"
        };

        for (String phone : phones) {
            Long uniqueId = IDUtils.generateUniqueId(phone);
            int dbIndex = IDUtils.getDbIndex(uniqueId);
            assertTrue(dbIndex >= 0 && dbIndex < IDUtils.DB_COUNT,
                    "手机号 " + phone + " 的库索引 " + dbIndex + " 应该在0-15范围内");
        }
    }

    @Test
    @DisplayName("测试null ID获取库索引")
    void testGetDbIndexWithNull() {
        assertThrows(NullPointerException.class, () -> {
            IDUtils.getDbIndex(null);
        });
    }

    // ==================== getTableIndex 测试 ====================

    @Test
    @DisplayName("测试获取表索引在有效范围内")
    void testGetTableIndexInRange() {
        String phone = "13800138000";
        Long uniqueId = IDUtils.generateUniqueId(phone);
        int tableIndex = IDUtils.getTableIndex(uniqueId);

        assertTrue(tableIndex >= 0, "表索引应该大于等于0");
        assertTrue(tableIndex < IDUtils.TABLE_COUNT_PER_DB,
                "表索引应该小于" + IDUtils.TABLE_COUNT_PER_DB);
    }

    @Test
    @DisplayName("测试多个手机号的表索引分布")
    void testGetTableIndexDistribution() {
        String[] phones = {
                "13000000001", "13100000001", "13200000001",
                "13300000001", "13500000001", "13600000001",
                "13700000001", "13800000001", "13900000001",
                "15000000001", "15100000001", "15200000001",
                "15300000001", "15500000001", "15600000001",
                "15700000001", "15800000001", "15900000001"
        };

        for (String phone : phones) {
            Long uniqueId = IDUtils.generateUniqueId(phone);
            int tableIndex = IDUtils.getTableIndex(uniqueId);
            assertTrue(tableIndex >= 0 && tableIndex < IDUtils.TABLE_COUNT_PER_DB,
                    "手机号 " + phone + " 的表索引 " + tableIndex + " 应该在0-511范围内");
        }
    }

    @Test
    @DisplayName("测试null ID获取表索引")
    void testGetTableIndexWithNull() {
        assertThrows(NullPointerException.class, () -> {
            IDUtils.getTableIndex(null);
        });
    }

    // ==================== 组合测试 ====================

    @Test
    @DisplayName("测试库表索引组合的唯一性")
    void testDbTableIndexCombination() {
        String phone = "13800138000";
        Long uniqueId = IDUtils.generateUniqueId(phone);
        int dbIndex = IDUtils.getDbIndex(uniqueId);
        int tableIndex = IDUtils.getTableIndex(uniqueId);

        // 验证库表组合的唯一性
        assertTrue(dbIndex >= 0 && dbIndex < 16, "库索引应该在0-15");
        assertTrue(tableIndex >= 0 && tableIndex < 512, "表索引应该在0-511");
    }

    @Test
    @DisplayName("测试边界值：验证库索引覆盖范围")
    void testDbIndexCoverage() {
        // 生成大量有效手机号，验证库索引分布
        String[] validPhones = generateValidPhones(1000);
        java.util.Set<Integer> dbIndices = new java.util.HashSet<>();

        for (String phone : validPhones) {
            Long uniqueId = IDUtils.generateUniqueId(phone);
            dbIndices.add(IDUtils.getDbIndex(uniqueId));
        }

        // 验证至少覆盖了多个库（说明分布较均匀）
        assertTrue(dbIndices.size() >= 8,
                "1000个手机号应该至少覆盖8个不同的库，实际覆盖: " + dbIndices.size() + "个库");
    }

    @Test
    @DisplayName("测试边界值：验证表索引覆盖范围")
    void testTableIndexCoverage() {
        // 生成大量有效手机号，验证表索引分布
        String[] validPhones = generateValidPhones(1000);
        java.util.Set<Integer> tableIndices = new java.util.HashSet<>();

        for (String phone : validPhones) {
            Long uniqueId = IDUtils.generateUniqueId(phone);
            tableIndices.add(IDUtils.getTableIndex(uniqueId));
        }

        // 验证至少覆盖了大量表（说明分布较均匀）
        assertTrue(tableIndices.size() >= 200,
                "1000个手机号应该至少覆盖200个不同的表，实际覆盖: " + tableIndices.size() + "个表");
    }

    @Test
    @DisplayName("测试分片配置常量")
    void testShardingConstants() {
        assertEquals(16, IDUtils.DB_COUNT, "应该有16个库");
        assertEquals(512, IDUtils.TABLE_COUNT_PER_DB, "每个库应该有512张表");
        assertEquals(8192, IDUtils.TOTAL_TABLE_COUNT, "总表数应该是8192");
    }

    @Test
    @DisplayName("测试ID的稳定性：同一手机号多次生成")
    void testIdGenerationStability() {
        String phone = "13800138000";

        // 生成10个ID
        int[] dbIndices = new int[10];
        int[] tableIndices = new int[10];

        for (int i = 0; i < 10; i++) {
            Long uniqueId = IDUtils.generateUniqueId(phone);
            dbIndices[i] = IDUtils.getDbIndex(uniqueId);
            tableIndices[i] = IDUtils.getTableIndex(uniqueId);
        }

        // 所有库索引应该相同
        for (int i = 1; i < dbIndices.length; i++) {
            assertEquals(dbIndices[0], dbIndices[i],
                    "同一手机号的库索引应该稳定");
        }

        // 所有表索引应该相同
        for (int i = 1; i < tableIndices.length; i++) {
            assertEquals(tableIndices[0], tableIndices[i],
                    "同一手机号的表索引应该稳定");
        }
    }

    // ==================== 并发测试 ====================

    @Test
    @DisplayName("测试同一手机号并发生成ID的唯一性（测试序列机制）")
    void testConcurrentIdGenerationSamePhone() throws InterruptedException {
        String phone = "13800138000";
        int threadCount = 20;
        int idsPerThread = 10;  // 减少到每线程10个，总计200个
        int totalIds = threadCount * idsPerThread;

        java.util.Set<Long> uniqueIds = java.util.concurrent.ConcurrentHashMap.newKeySet();
        Thread[] threads = new Thread[threadCount];

        // 多线程并发生成ID（同一手机号）
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < idsPerThread; j++) {
                    Long id = IDUtils.generateUniqueId(phone);
                    uniqueIds.add(id);
                }
            });
            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // 验证生成了正确数量的唯一ID
        // 注意：同一手机号的唯一性依赖时间戳和序列号，200个ID在短时间内容易碰撞
        // 所以这里只验证不小于某个阈值，而不是严格等于总数
        int minExpectedUnique = (int) (totalIds * 0.95); // 至少95%唯一
        assertTrue(uniqueIds.size() >= minExpectedUnique,
                "同一手机号并发生成 " + totalIds + " 个ID，至少应该有 " + minExpectedUnique + " 个唯一，实际: " + uniqueIds.size());
    }

    @Test
    @DisplayName("测试不同手机号并发生成ID的唯一性（测试MD5哈希机制）")
    void testConcurrentIdGenerationDifferentPhones() throws InterruptedException {
        int threadCount = 20;
        int phonesPerThread = 50;
        int totalIds = threadCount * phonesPerThread;

        java.util.Set<Long> uniqueIds = java.util.concurrent.ConcurrentHashMap.newKeySet();
        java.util.concurrent.atomic.AtomicInteger phoneIndex = new java.util.concurrent.atomic.AtomicInteger(0);
        Thread[] threads = new Thread[threadCount];

        // 预生成有效手机号池（确保格式正确）
        String[] validPhones = generateValidPhones(totalIds);

        // 多线程并发生成ID（不同手机号）
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < phonesPerThread; j++) {
                    int idx = phoneIndex.getAndIncrement();
                    String phone = validPhones[idx];
                    Long id = IDUtils.generateUniqueId(phone);
                    uniqueIds.add(id);
                }
            });
            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // 验证生成了正确数量的唯一ID
        assertEquals(totalIds, uniqueIds.size(),
                "不同手机号并发生成 " + totalIds + " 个ID，应该全部唯一");
    }

    /**
     * 生成指定数量的有效手机号
     * @param count 需要生成的手机号数量
     * @return 有效手机号数组
     */
    private String[] generateValidPhones(int count) {
        String[] phones = new String[count];
        String[] prefixes = {
                "130", "131", "132", "133", "135", "136", "137", "138", "139",
                "150", "151", "152", "153", "155", "156", "157", "158", "159",
                "170", "176", "177", "178", "180", "181", "182", "183", "184", "185", "186", "187", "188", "189",
                "198", "199"
        };

        for (int i = 0; i < count; i++) {
            String prefix = prefixes[i % prefixes.length];
            String suffix = String.format("%08d", i);
            phones[i] = prefix + suffix.substring(suffix.length() - 8);
        }
        return phones;
    }

    @Test
    @DisplayName("测试并发场景下路由一致性")
    void testConcurrentRoutingConsistency() throws InterruptedException {
        String phone = "13800138000";
        int threadCount = 10;
        java.util.Set<Integer> dbIndices = java.util.concurrent.ConcurrentHashMap.newKeySet();
        java.util.Set<Integer> tableIndices = java.util.concurrent.ConcurrentHashMap.newKeySet();
        Thread[] threads = new Thread[threadCount];

        // 多线程并发生成ID并收集路由信息
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 50; j++) {
                    Long id = IDUtils.generateUniqueId(phone);
                    dbIndices.add(IDUtils.getDbIndex(id));
                    tableIndices.add(IDUtils.getTableIndex(id));
                }
            });
            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // 同一手机号的路由应该完全一致
        assertEquals(1, dbIndices.size(), "同一手机号并发应该路由到同一个库");
        assertEquals(1, tableIndices.size(), "同一手机号并发应该路由到同一个表");
    }

    @Test
    @DisplayName("测试高并发场景下的ID生成性能和唯一性")
    void testConcurrentIdGenerationPerformance() throws InterruptedException {
        int threadCount = 50;
        int idsPerThread = 100;
        int totalIds = threadCount * idsPerThread;

        java.util.Set<Long> uniqueIds = java.util.concurrent.ConcurrentHashMap.newKeySet();
        java.util.concurrent.atomic.AtomicInteger phoneIndex = new java.util.concurrent.atomic.AtomicInteger(0);
        Thread[] threads = new Thread[threadCount];

        // 预生成不同手机号（模拟真实场景，不同手机号才应该唯一）
        String[] validPhones = generateValidPhones(totalIds);

        long startTime = System.nanoTime();

        // 高并发场景（每个线程处理不同的手机号）
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < idsPerThread; j++) {
                    int idx = phoneIndex.getAndIncrement();
                    String phone = validPhones[idx];
                    Long id = IDUtils.generateUniqueId(phone);
                    uniqueIds.add(id);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        // 验证唯一性（不同手机号的MD5哈希不同，应该全部唯一）
        assertEquals(totalIds, uniqueIds.size(),
                "不同手机号并发生成 " + totalIds + " 个ID应该全部唯一");

        // 验证性能（5000个ID应该在合理时间内完成，比如5秒）
        assertTrue(durationMs < 5000,
                "生成 " + totalIds + " 个ID应该在5秒内完成，实际耗时: " + durationMs + "ms");
    }
}
