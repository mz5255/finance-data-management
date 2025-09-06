package cn.com.mz.app.finance.module.webapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/module-system/healthy")
public class HealthyApi {

    /**
     * 健康检查
     * @return
     */
    @GetMapping("/healthy")
    public String healthy() {
        return "success";
    }
}
