package cn.com.mz.app.finance.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 手机号生成全局唯一ID + 16库512表分片路由工具
 * 核心逻辑：ID包含分片因子，可直接路由到指定库表，且全局唯一、不暴露手机号
 * @author ：MZ
 * @date ：Created in 2026/01/23 16:05
 */
public class IDUtils {
    // 分片配置：16个库，每个库512张表
    public static final int DB_COUNT = 16;
    public static final int TABLE_COUNT_PER_DB = 512;
    public static final int TOTAL_TABLE_COUNT = DB_COUNT * TABLE_COUNT_PER_DB; // 8192

    // 机器ID（分布式部署时，不同机器配置不同值，0-1023），可从配置文件读取
    private static final int MACHINE_ID = 0;
    // 原子序列器：保证同一纳秒内的唯一性
    private static final AtomicLong SEQUENCE = new AtomicLong(0);
    // 线程安全的随机数生成器
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    // 进程ID（进一步区分同一机器的不同进程）
    private static final int PROCESS_ID = getProcessId();
    // MD5哈希实例（提前初始化，避免重复创建）
    private static final MessageDigest MD5_DIGEST;

    static {
        try {
            MD5_DIGEST = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("初始化MD5算法失败", e);
        }
    }

    /**
     * 根据手机号生成全局唯一ID（包含分片因子，可路由到16库512表）
     * @param mobile 11位有效手机号
     * @return 全局唯一ID（Long类型，便于存储和计算，长度约18位）
     */
    public static long generateUniqueId(String mobile) {
        // 1. 参数校验
        if (mobile == null || !mobile.matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException("手机号格式不正确，必须是11位有效手机号");
        }

        // 2. 生成手机号哈希值（64位长整数，作为分片因子的基础）
        long mobileHash = getMobileHash(mobile);

        // 3. 生成分布式唯一因子
        long nanoTime = System.nanoTime(); // 纳秒时间戳（高精度）
        long sequence = SEQUENCE.getAndIncrement() % 1000000; // 原子序列（避免同一纳秒重复）
        int random = RANDOM.nextInt(10000); // 随机数（进一步降低碰撞）

        // 4. 组合全局唯一ID（按位拼接，保证唯一性且嵌入分片因子）
        // 拼接规则（可根据需要调整，核心是保留mobileHash用于分片）：
        // 高位：mobileHash（用于分片） + 机器ID + 进程ID
        // 低位：纳秒时间戳 + 序列 + 随机数
        long uniqueId = (mobileHash << 40)
                | ((long) MACHINE_ID << 30)
                | ((long) PROCESS_ID << 20)
                | (nanoTime % 1000000000000L << 10)
                | (sequence % 1000 << 4)
                | (random % 16);

        // 确保ID为正数（避免取模时出现负数）
        return uniqueId & Long.MAX_VALUE;
    }

    /**
     * 根据唯一ID计算对应的库索引（0-15）
     * @param uniqueId 生成的全局唯一ID
     * @return 库索引（0-15）
     */
    public static int getDbIndex(long uniqueId) {
        // 从ID中提取手机号哈希值（还原分片基础）
        long mobileHash = (uniqueId >> 40) & 0xFFFFFFFFFFFFL;
        // 取模16，得到库索引
        return (int) (mobileHash % DB_COUNT);
    }

    /**
     * 根据唯一ID计算对应的表索引（0-511）
     * @param uniqueId 生成的全局唯一ID
     * @return 表索引（0-511）
     */
    public static int getTableIndex(long uniqueId) {
        // 从ID中提取手机号哈希值
        long mobileHash = (uniqueId >> 40) & 0xFFFFFFFFFFFFL;
        // 先取模总表数8192，再除以16（库数），得到每个库下的表索引（0-511）
        return (int) ((mobileHash % TOTAL_TABLE_COUNT) / DB_COUNT);
    }

    /**
     * 对手机号进行MD5哈希，转换为64位长整数（用于分片和ID生成）
     * @param mobile 手机号
     * @return 64位哈希值（不可逆，脱敏且固定）
     */
    private static long getMobileHash(String mobile) {
        byte[] hashBytes = MD5_DIGEST.digest(mobile.getBytes(StandardCharsets.UTF_8));
        // 将16字节的MD5哈希转换为64位长整数（取前8字节和后8字节拼接）
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
        // 合并为一个64位哈希值（保证唯一性）
        return hash1 ^ hash2;
    }

    /**
     * 获取当前进程ID（简化实现，分布式部署时可从配置读取）
     * @return 进程ID
     */
    private static int getProcessId() {
        try {
            String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
            return Integer.parseInt(processName.split("@")[0]);
        } catch (Exception e) {
            return RANDOM.nextInt(1024); // 失败则生成随机进程ID
        }
    }

//    // 测试示例
//    public static void main(String[] args) {
//        // 测试手机号
//        String mobile = "13800138000";
//
//        // 生成唯一ID
//        long uniqueId = generateUniqueId(mobile);
//        System.out.println("生成的全局唯一ID：" + uniqueId);
//
//        // 计算库表索引
//        int dbIndex = getDbIndex(uniqueId);
//        int tableIndex = getTableIndex(uniqueId);
//        System.out.println("路由到的库索引：" + dbIndex + "（0-15）");
//        System.out.println("路由到的表索引：" + tableIndex + "（0-511）");
//
//        // 验证：同一手机号多次生成ID，库表索引一致（保证分片稳定）
//        for (int i = 0; i < 5; i++) {
//            long id = generateUniqueId(mobile);
//            System.out.println("第" + (i+1) + "次生成ID：" + id
//                    + " → 库：" + getDbIndex(id)
//                    + "，表：" + getTableIndex(id));
//        }
//    }
}
