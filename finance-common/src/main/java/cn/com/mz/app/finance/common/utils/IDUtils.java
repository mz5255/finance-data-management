package cn.com.mz.app.finance.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 马震
 * @version 1.0
 * @date 2026/1/19 13:53
 */

public class IDUtils {

    // 生成用户唯一 ID
    public static long generateUserId(String phoneNumber) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(phoneNumber.getBytes());
            return bytesToLong(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // 将字节数组转换为长整型（long）
    private static long bytesToLong(byte[] bytes) {
        long result = 0;
        for (int i = 0; i < Math.min(bytes.length, Long.BYTES); i++) {
            result <<= 8; // 左移8位
            result |= (bytes[i] & 0xFF); // 与上0xFF以确保为正数
        }
        return result;
    }

    // 获取数据库和表索引
    public static int[] getDatabaseAndTable(long userId) {
        // 确保 userId 为正数
        userId = Math.abs(userId);
        int databaseIndex = (int) (userId % 16); // 选择数据库
        int tableIndex = (int) ((userId / 16) % 512); // 选择表
        return new int[]{databaseIndex, tableIndex};
    }

    public static void main(String[] args) {
        String phoneNumber = "13800138000"; // 输入手机号
        long userId = generateUserId(phoneNumber);
        int[] dbAndTable = getDatabaseAndTable(userId);

        System.out.println("用户手机号: " + phoneNumber);
        System.out.println("生成的用户唯一 ID: " + userId);
        System.out.println("数据库索引: " + dbAndTable[0]);
        System.out.println("表索引: " + dbAndTable[1]);
    }
}
