package cn.com.mz.app.finance.module.webapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/module-system/healthy")
@Tag(name = "健康检查", description = "系统健康检查相关接口")
public class HealthyApi {

    @Operation(summary = "健康检查", description = "检查系统运行状态")
    @GetMapping("/healthy")
    public String healthy() {
        return "success";
    }
}
