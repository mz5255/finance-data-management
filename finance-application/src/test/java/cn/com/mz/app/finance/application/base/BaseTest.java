package cn.com.mz.app.finance.application.base;

import cn.com.mz.app.finance.application.FinanceApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = FinanceApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class BaseTest {

    @Before
    public void before(){
        System.err.println("测试累开始启动");
    }
    @After
    public void after(){
        System.err.println("测试累结束");
    }
}
