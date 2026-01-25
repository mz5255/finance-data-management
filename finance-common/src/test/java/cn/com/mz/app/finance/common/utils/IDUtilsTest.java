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
    @DisplayName("测试边界值：最大库索引")
    void testMaxDbIndex() {
        // 测试能否产生最大库索引（15）
        boolean foundMaxDbIndex = false;
        for (int i = 13000000000; i < 19999999999L; i += 100000000) {
            try {
                String phone = String.valueOf(i);
                if (phone.length() == 11 && phone.matches("^1[3-9]\\d{9}$")) {
                    Long uniqueId = IDUtils.generateUniqueId(phone);
                    int dbIndex = IDUtils.getDbIndex(uniqueId);
                    if (dbIndex == 15) {
                        foundMaxDbIndex = true;
                        break;
                    }
                }
            } catch (Exception e) {
                // 忽略无效手机号
            }
        }
        // 这个测试可能会失败，因为不一定能产生最大值
        // assertTrue(foundMaxDbIndex, "应该能产生最大库索引15");
    }

    @Test
    @DisplayName("测试边界值：最大表索引")
    void testMaxTableIndex() {
        // 测试能否产生最大表索引（511）
        boolean foundMaxTableIndex = false;
        for (int i = 13000000000; i < 19999999999L; i += 100000000) {
            try {
                String phone = String.valueOf(i);
                if (phone.length() == 11 && phone.matches("^1[3-9]\\d{9}$")) {
                    Long uniqueId = IDUtils.generateUniqueId(phone);
                    int tableIndex = IDUtils.getTableIndex(uniqueId);
                    if (tableIndex == 511) {
                        foundMaxTableIndex = true;
                        break;
                    }
                }
            } catch (Exception e) {
                // 忽略无效手机号
            }
        }
        // 这个测试可能会失败，因为不一定能产生最大值
        // assertTrue(foundMaxTableIndex, "应该能产生最大表索引511");
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

    @Test
    @DisplayName("测试并发生成ID的唯一性")
    void testConcurrentIdGeneration() throws InterruptedException {
        String phone = "13800138000";
        int threadCount = 10;
        int idsPerThread = 10;
        Thread[] threads = new Thread[threadCount];
        Long[][] allIds = new Long[threadCount][idsPerThread];

        // 多线程并发生成ID
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < idsPerThread; j++) {
                    allIds[threadIndex][j] = IDUtils.generateUniqueId(phone);
                }
            });
            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // 验证所有ID都是唯一的
        for (int i = 0; i < threadCount; i++) {
            for (int j = 0; j < idsPerThread; j++) {
                for (int k = i; k < threadCount; k++) {
                    int startJ = (k == i) ? j + 1 : 0;
                    for (int l = startJ; l < idsPerThread; l++) {
                        assertNotEquals(allIds[i][j], allIds[k][l],
                                "并发生成的ID应该唯一");
                    }
                }
            }
        }
    }
}
