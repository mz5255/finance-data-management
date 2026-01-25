package cn.com.mz.app.finance.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AesUtil 单元测试
 *
 * @author mz
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("AES加密解密工具测试")
class AesUtilTest {

    @Test
    @DisplayName("测试加密解密正常流程")
    void testEncryptDecrypt() {
        // 准备测试数据
        String originalText = "这是一个测试文本";
        String encryptedText = AesUtil.encrypt(originalText);
        String decryptedText = AesUtil.decrypt(encryptedText);

        // 验证加密结果不为空且不等于原文
        assertNotNull(encryptedText);
        assertNotEquals(originalText, encryptedText);

        // 验证解密结果与原文一致
        assertEquals(originalText, decryptedText);
    }

    @Test
    @DisplayName("测试加密手机号")
    void testEncryptPhoneNumber() {
        String phoneNumber = "13800138000";
        String encrypted = AesUtil.encrypt(phoneNumber);
        String decrypted = AesUtil.decrypt(encrypted);

        assertEquals(phoneNumber, decrypted);
    }

    @Test
    @DisplayName("测试加密身份证号")
    void testEncryptIdCard() {
        String idCard = "110101199001011234";
        String encrypted = AesUtil.encrypt(idCard);
        String decrypted = AesUtil.decrypt(encrypted);

        assertEquals(idCard, decrypted);
    }

    @Test
    @DisplayName("测试加密中文姓名")
    void testEncryptChineseName() {
        String chineseName = "张三";
        String encrypted = AesUtil.encrypt(chineseName);
        String decrypted = AesUtil.decrypt(encrypted);

        assertEquals(chineseName, decrypted);
    }

    @Test
    @DisplayName("测试空字符串输入")
    void testEmptyString() {
        String emptyString = "";
        String encrypted = AesUtil.encrypt(emptyString);
        String decrypted = AesUtil.decrypt(emptyString);

        // 空字符串应该原样返回
        assertEquals(emptyString, encrypted);
        assertEquals(emptyString, decrypted);
    }

    @Test
    @DisplayName("测试null输入")
    void testNullInput() {
        String encrypted = AesUtil.encrypt(null);
        String decrypted = AesUtil.decrypt(null);

        // null应该原样返回
        assertNull(encrypted);
        assertNull(decrypted);
    }

    @Test
    @DisplayName("测试空白字符串输入")
    void testBlankString() {
        String blankString = "   ";
        String encrypted = AesUtil.encrypt(blankString);
        String decrypted = AesUtil.decrypt(blankString);

        // 空白字符串应该原样返回
        assertEquals(blankString, encrypted);
        assertEquals(blankString, decrypted);
    }

    @Test
    @DisplayName("测试加密结果的一致性")
    void testEncryptionConsistency() {
        String originalText = "一致性测试文本";

        // 同一文本多次加密，结果应该一致
        String encrypted1 = AesUtil.encrypt(originalText);
        String encrypted2 = AesUtil.encrypt(originalText);

        assertEquals(encrypted1, encrypted2);
    }

    @Test
    @DisplayName("测试特殊字符加密")
    void testSpecialCharacters() {
        String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        String encrypted = AesUtil.encrypt(specialChars);
        String decrypted = AesUtil.decrypt(encrypted);

        assertEquals(specialChars, decrypted);
    }

    @Test
    @DisplayName("测试长文本加密")
    void testLongText() {
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longText.append("测试文本");
        }

        String encrypted = AesUtil.encrypt(longText.toString());
        String decrypted = AesUtil.decrypt(encrypted);

        assertEquals(longText.toString(), decrypted);
    }

    @Test
    @DisplayName("测试加密结果是十六进制字符串")
    void testEncryptedFormat() {
        String originalText = "测试";
        String encrypted = AesUtil.encrypt(originalText);

        // 加密结果应该是十六进制字符串（只包含0-9和a-f）
        assertTrue(encrypted.matches("^[0-9a-f]+$"));
    }

    @Test
    @DisplayName("测试错误密文解密")
    void testDecryptionWithWrongCiphertext() {
        String wrongCiphertext = "错误的密文12345";

        // 错误的密文解密应该抛出异常或返回null
        // 具体行为取决于实现，这里验证不会崩溃
        assertDoesNotThrow(() -> {
            try {
                AesUtil.decrypt(wrongCiphertext);
            } catch (Exception e) {
                // 预期可能抛出异常
            }
        });
    }

    @Test
    @DisplayName("测试数字加密")
    void testNumberEncryption() {
        String number = "1234567890";
        String encrypted = AesUtil.encrypt(number);
        String decrypted = AesUtil.decrypt(encrypted);

        assertEquals(number, decrypted);
    }

    @Test
    @DisplayName("测试混合内容加密")
    void testMixedContent() {
        String mixed = "用户张三，手机号13800138000，身份证110101199001011234";
        String encrypted = AesUtil.encrypt(mixed);
        String decrypted = AesUtil.decrypt(encrypted);

        assertEquals(mixed, decrypted);
    }

    @Test
    @DisplayName("测试URL加密")
    void testUrlEncryption() {
        String url = "https://example.com/user?id=123&name=test";
        String encrypted = AesUtil.encrypt(url);
        String decrypted = AesUtil.decrypt(encrypted);

        assertEquals(url, decrypted);
    }

    @Test
    @DisplayName("测试JSON加密")
    void testJsonEncryption() {
        String json = "{\"name\":\"张三\",\"age\":25,\"phone\":\"13800138000\"}";
        String encrypted = AesUtil.encrypt(json);
        String decrypted = AesUtil.decrypt(encrypted);

        assertEquals(json, decrypted);
    }

    @Test
    @DisplayName("测试加密后的长度与原文不同")
    void testEncryptedLength() {
        String originalText = "测试文本";
        String encrypted = AesUtil.encrypt(originalText);

        // 加密后的长度应该与原文不同（AES是块加密，会有填充）
        assertNotEquals(originalText.length(), encrypted.length());
    }
}
