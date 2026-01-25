package cn.com.mz.app.finance.application.base;

import cn.com.mz.app.finance.application.FinanceApplication;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 基础测试类
 * 提供测试前后的通用操作
 *
 * @author mz
 */
@SpringBootTest(classes = FinanceApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BaseTest {

    @BeforeAll
    public static void beforeAll() {
        System.out.println("========== 测试类开始启动 ==========");
    }

    @AfterAll
    public static void afterAll() {
        System.out.println("========== 测试类结束 ==========");
    }

    @BeforeEach
    public void beforeEach() {
        System.out.println("----- 测试方法开始 -----");
    }

    @AfterEach
    public void afterEach() {
        System.out.println("----- 测试方法结束 -----");
    }
}
