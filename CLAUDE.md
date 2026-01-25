# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

财务数据管理系统，基于 Spring Boot 3.2 + Java 21 的多模块 Maven 项目。

### 技术栈
- **Java 21** + **Maven** 多模块构建
- **Spring Boot 3.2.2** (Spring Security, Web, Thymeleaf)
- **MyBatis Plus 3.5.5** + MySQL 8.0
- **Druid** 数据库连接池
- **Redis** + **Redisson** 分布式缓存/锁
- **JetCache 2.7.5** 多级缓存
- **Sa-Token 1.37.0** 权限认证
- **SpringDoc OpenAPI 2.1.0** (Swagger UI)
- **Lombok** + **Hutool** + **Guava** 工具库
- **MapStruct 1.5.5** 对象映射

## 常用命令

```bash
# 编译项目
mvn clean compile

# 运行应用 (在 finance-application 目录下)
cd finance-application && mvn spring-boot:run

# 打包
mvn clean package

# 跳过测试打包
mvn clean package -DskipTests
```

## 项目架构

### 模块结构

```
finance-data-management/
├── finance-application/    # 启动模块 (主入口)
├── finance-common/         # 公共模块 (DTO、工具类、常量)
├── finance-datasource/     # 数据源模块 (Entity、Mapper、Service)
├── finance-module/         # 业务模块
│   ├── module-auth/        # 认证授权 (Security配置、登录注册、权限切面)
│   ├── module-user/        # 用户模块
│   ├── module-system/      # 系统模块 (健康检查等)
│   └── module-finance/     # 财务核心业务
└── finance-starter/        # 自定义 Starter
    ├── cache-starter/      # 缓存 Starter (Redis、Caffeine、JetCache)
    ├── sa-token-starter/   # Sa-Token 权限 Starter
    └── sms-starter/        # 短信服务 Starter
```

### 核心架构要点

1. **启动类位置**: `finance-application/src/main/java/cn/com/mz/app/finance/application/FinanceApplication.java`
   - `@ComponentScan(basePackages = "cn.com.mz.app.finance")`
   - `@MapperScan("cn.com.mz.app.finance.datasource.mysql.mapper")`

2. **配置文件导入**: `application.yml` 通过 `spring.config.import` 导入各模块配置
   - `datasource.yml` - 数据源配置
   - `base.yml` - Redis 基础配置
   - `cache.yml` - 缓存配置
   - `auth.yml` - Sa-Token 配置

3. **统一响应格式**: `BaseResult<T>` (code, message, data, traceId)
   - 成功: `code = 200`
   - 使用静态工厂方法构造: `BaseResult.success(data)`, `BaseResult.error(code, msg)`

4. **认证授权**:
   - Spring Security + Sa-Token 双重认证
   - `SecurityConfig` 配置默认允许 `/swagger-ui/**`, `/api/*/auth/login`, `/api/*/auth/register`
   - 自定义权限注解: `@RequiresPermissions`, `@RequiresRoles`
   - `PermissionAspect` 处理权限检查

5. **数据层**:
   - MyBatis Plus: Mapper 位置 `cn.com.mz.app.finance.datasource.mysql.mapper`
   - Entity 位置 `cn.com.mz.app.finance.datasource.mysql.entity`
   - MapStruct Convertor 用于 Entity <-> DTO 转换
   - `AesEncryptTypeHandler` 字段级加密

6. **缓存与分布式锁**:
   - `@DistributeLock` 注解实现分布式锁 (基于 Redisson)
   - JetCache 二级缓存 (本地 Caffeine + Redis)
   - `RedisUtils` 封装 Redis 操作

## 重要约定

1. **包命名**: `cn.com.mz.app.finance.{module}`
2. **Mapper XML**: `classpath:/mappers/**/*.xml`
3. **API 路径**: `/api/{version}/{业务路径}` (如 `/api/v1/auth/login`)
4. **日志追踪**: 使用 SkyWalking APM Toolkit，TID 通过 `BaseResult.traceId` 返回
5. **敏感数据**: 使用 `sensitive-logback` 脱敏

## 开发注意事项

1. 添加新的业务模块时，需要:
   - 在 `finance-module` 下创建新模块
   - 在 `finance-application/pom.xml` 中添加依赖
   - 确保包路径在 `@ComponentScan` 覆盖范围内

2. 数据库变更时:
   - Entity 使用 `@TableName` 映射表名
   - 继承 `BaseEntity` 获取通用字段
   - 使用 MapStruct Convertor 转换 DTO

3. 新增 API 时:
   - 需要在 `SecurityConfig` 中配置白名单或认证规则
   - 使用 `@RequiresPermissions/@RequiresRoles` 进行权限控制
   - 返回 `BaseResult<T>` 统一格式
