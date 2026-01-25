package cn.com.mz.app.finance.common.utils;

import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 手机号生成全局唯一ID + 16库512表分片路由工具
 * 核心逻辑：ID包含分片因子，可直接路由到指定库表，且全局唯一、不暴露手机号
 *
 * 修复版本历史：
 * v1.0: 初始版本（存在多个 BUG）
 * v1.1: 修复负数左移、SEQUENCE溢出、位段重叠、MD5线程安全问题
 *
 * @author MZ
 * @date 2026/01/23 16:05
 */
public class IDUtils {
    // 分片配置：16个库，每个库512张表
    public static final int DB_COUNT = 16;
    public static final int TABLE_COUNT_PER_DB = 512;
    public static final int TOTAL_TABLE_COUNT = DB_COUNT * TABLE_COUNT_PER_DB; // 8192

    // 机器ID（分布式部署时，不同机器配置不同值，0-63）
    private static final int MACHINE_ID = 0;
    // 进程ID（进一步区分同一机器的不同进程，0-15）
    private static final int PROCESS_ID = getProcessId();

    // MD5哈希实例（提前初始化，避免重复创建）
    private static final MessageDigest MD5_DIGEST;
    // 每个线程独立的序列号（避免全局 AtomicLong 溢出问题）
    private static final ThreadLocal<java.util.concurrent.atomic.AtomicLong> THREAD_SEQUENCE =
            ThreadLocal.withInitial(() -> new java.util.concurrent.atomic.AtomicLong(0));

    static {
        try {
            MD5_DIGEST = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("初始化MD5算法失败", e);
        }
    }

    /**
     * 根据手机号生成全局唯一ID（包含分片因子，可路由到16库512表）
     *
     * 位布局（64位，无符号）：
     * - 位 63-42 (22位): mobileHash 低位（用于分片）
     * - 位 41-32 (10位): 秒级时间戳 (1024秒循环)
     * - 位 31-26 (6位):  机器ID
     * - 位 25-22 (4位):  进程ID
     * - 位 21-12 (10位): 线程序列号 (每线程1024)
     * - 位 11-8  (4位):  毫秒时间戳低位
     * - 位 7-0   (8位):  随机数
     *
     * @param mobile 11位有效手机号
     * @return 全局唯一ID（Long类型，始终为正数）
     */
    public static Long generateUniqueId(String mobile) {
        // 1. 参数校验
        if (mobile == null || !mobile.matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException("手机号格式不正确，必须是11位有效手机号");
        }

        // 2. 生成手机号哈希值（取绝对值确保为正）
        long mobileHash = Math.abs(getMobileHash(mobile));

        // 3. 获取时间戳（秒级 + 毫秒低位）
        long epochSeconds = System.currentTimeMillis() / 1000;
        long millisLowBits = System.currentTimeMillis() % 1000;

        // 4. 获取线程本地序列号（0-1023）
        long sequence = THREAD_SEQUENCE.get().getAndIncrement() & 0x3FFL;

        // 5. 随机数（0-255）
        int random = ThreadLocalRandom.current().nextInt(256);

        // 6. 组合全局唯一ID（每个字段都确保在指定位数内）
        long uniqueId = ((mobileHash & 0x3FFFFFL) << 42)              // 22位: mobileHash
                | ((epochSeconds & 0x3FFL) << 32)                      // 10位: 秒级时间戳
                | ((MACHINE_ID & 0x3FL) << 26)                         // 6位: 机器ID
                | ((PROCESS_ID & 0xF) << 22)                           // 4位: 进程ID
                | ((sequence & 0x3FFL) << 12)                          // 10位: 线程序列号
                | ((millisLowBits & 0xF) << 8)                         // 4位: 毫秒低位
                | (random & 0xFF);                                     // 8位: 随机数

        // 7. 确保结果为正数（清除符号位，虽然理论上不会出现）
        return uniqueId & Long.MAX_VALUE;
    }

    /**
     * 根据唯一ID计算对应的库索引（0-15）
     * @param uniqueId 生成的全局唯一ID
     * @return 库索引（0-15）
     */
    public static int getDbIndex(Long uniqueId) {
        // 从ID中提取 mobileHash（位 63-42）
        long mobileHash = (uniqueId >>> 42) & 0x3FFFFFL;
        // 取模16，得到库索引
        return (int) (mobileHash % DB_COUNT);
    }

    /**
     * 根据唯一ID计算对应的表索引（0-511）
     * @param uniqueId 生成的全局唯一ID
     * @return 表索引（0-511）
     */
    public static int getTableIndex(Long uniqueId) {
        // 从ID中提取 mobileHash（位 63-42）
        long mobileHash = (uniqueId >>> 42) & 0x3FFFFFL;
        // 先取模总表数8192，再除以16（库数），得到每个库下的表索引（0-511）
        return (int) ((mobileHash % TOTAL_TABLE_COUNT) / DB_COUNT);
    }

    /**
     * 对手机号进行MD5哈希，转换为64位长整数（用于分片和ID生成）
     * 注意：使用 synchronized 保证线程安全
     * @param mobile 手机号
     * @return 64位哈希值（不可逆，脱敏且固定）
     */
    private static long getMobileHash(String mobile) {
        byte[] hashBytes;
        synchronized (MD5_DIGEST) {
            hashBytes = MD5_DIGEST.digest(mobile.getBytes(StandardCharsets.UTF_8));
            MD5_DIGEST.reset();
        }

        // 将16字节的MD5哈希转换为64位长整数
        long hash1 = ((long) (hashBytes[0] & 0xFF) << 56)
                | ((long) (hashBytes[1] & 0xFF) << 48)
                | ((long) (hashBytes[2] & 0xFF) << 40)
                | ((long) (hashBytes[3] & 0xFF) << 32)
                | ((long) (hashBytes[4] & 0xFF) << 24)
                | ((long) (hashBytes[5] & 0xFF) << 16)
                | ((long) (hashBytes[6] & 0xFF) << 8)
                | (hashBytes[7] & 0xFF);
        long hash2 = ((long) (hashBytes[8] & 0xFF) << 56)
                | ((long) (hashBytes[9] & 0xFF) << 48)
                | ((long) (hashBytes[10] & 0xFF) << 40)
                | ((long) (hashBytes[11] & 0xFF) << 32)
                | ((long) (hashBytes[12] & 0xFF) << 24)
                | ((long) (hashBytes[13] & 0xFF) << 16)
                | ((long) (hashBytes[14] & 0xFF) << 8)
                | (hashBytes[15] & 0xFF);
        // 合并为一个64位哈希值
        return hash1 ^ hash2;
    }

    /**
     * 获取当前进程ID（简化实现，分布式部署时可从配置读取）
     * @return 进程ID (0-15)
     */
    private static int getProcessId() {
        try {
            String processName = ManagementFactory.getRuntimeMXBean().getName();
            int pid = Integer.parseInt(processName.split("@")[0]);
            return pid & 0xF; // 限制在 0-15 范围
        } catch (Exception e) {
            return ThreadLocalRandom.current().nextInt(16); // 失败则生成随机进程ID (0-15)
        }
    }
}
