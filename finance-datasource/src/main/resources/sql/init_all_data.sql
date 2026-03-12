-- 财务数据管理系统 - 完整初始化脚本
-- 执行此脚本将初始化所有必要的菜单、角色、用户和权限数据
--
-- 登录信息：
-- 手机号: 13130608426
-- 密码: 123456
-- ============================================================

-- ==================== 第一部分：菜单数据 ====================

-- 系统管理目录
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, 'system', NULL, '', 1, 0, 'M', '0', '0', '', 'Setting', 'admin', NOW(), '', NOW(), '系统管理目录');

-- 用户管理菜单及按钮
INSERT INTO `sys_menu` VALUES (2, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'User', 'admin', NOW(), '', NOW(), '用户管理菜单');
INSERT INTO `sys_menu` VALUES (100, '用户查询', 2, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (101, '用户新增', 2, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (102, '用户修改', 2, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (103, '用户删除', 2, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (104, '用户重置密码', 2, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (105, '用户导入导出', 2, 6, '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', NOW(), '', NOW(), '');

-- 角色管理菜单及按钮
INSERT INTO `sys_menu` VALUES (3, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'UserFilled', 'admin', NOW(), '', NOW(), '角色管理菜单');
INSERT INTO `sys_menu` VALUES (1001, '角色查询', 3, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1002, '角色新增', 3, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1003, '角色修改', 3, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1004, '角色删除', 3, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1005, '分配菜单权限', 3, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:role:assignMenu', '#', 'admin', NOW(), '', NOW(), '');

-- 菜单管理菜单及按钮
INSERT INTO `sys_menu` VALUES (4, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'Menu', 'admin', NOW(), '', NOW(), '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (1006, '菜单查询', 4, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1007, '菜单新增', 4, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1008, '菜单修改', 4, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1009, '菜单删除', 4, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', NOW(), '', NOW(), '');

-- 日志管理目录及子菜单
INSERT INTO `sys_menu` VALUES (10, '日志管理', 1, 9, 'log', '', '', 1, 0, 'M', '0', '0', '', 'Document', 'admin', NOW(), '', NOW(), '日志管理目录');
INSERT INTO `sys_menu` VALUES (1031, '操作日志', 10, 1, 'operlog', 'system/operlog/index', '', 1, 0, 'C', '0', '0', 'system:operlog:list', 'Form', 'admin', NOW(), '', NOW(), '操作日志菜单');
INSERT INTO `sys_menu` VALUES (1032, '登录日志', 10, 2, 'logininfor', 'system/logininfor/index', '', 1, 0, 'C', '0', '0', 'system:logininfor:list', 'Timer', 'admin', NOW(), '', NOW(), '登录日志菜单');

-- 监控管理目录
INSERT INTO `sys_menu` VALUES (11, '监控管理', 0, 2, 'monitor', NULL, '', 1, 0, 'M', '0', '0', '', 'Monitor', 'admin', NOW(), '', NOW(), '监控管理目录');

-- 在线用户菜单
INSERT INTO `sys_menu` VALUES (12, '在线用户', 11, 1, 'online', 'monitor/online/index', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'UserFilled', 'admin', NOW(), '', NOW(), '在线用户菜单');

-- 定时任务菜单
INSERT INTO `sys_menu` VALUES (13, '定时任务', 11, 2, 'job', 'monitor/job/index', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'Calendar', 'admin', NOW(), '', NOW(), '定时任务菜单');

-- 数据监控菜单
INSERT INTO `sys_menu` VALUES (14, '数据监控', 11, 3, 'druid', 'monitor/druid/index', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'Histogram', 'admin', NOW(), '', NOW(), '数据监控菜单');

-- 服务监控菜单
INSERT INTO `sys_menu` VALUES (15, '服务监控', 11, 4, 'server', 'monitor/server/index', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'Server', 'admin', NOW(), '', NOW(), '服务监控菜单');

-- 财务管理目录
INSERT INTO `sys_menu` VALUES (20, '财务管理', 0, 3, 'finance', NULL, '', 1, 0, 'M', '0', '0', '', 'Money', 'admin', NOW(), '', NOW(), '财务管理目录');

-- 财务数据菜单
INSERT INTO `sys_menu` VALUES (21, '财务数据', 20, 1, 'data', 'finance/data/index', '', 1, 0, 'C', '0', '0', 'finance:data:list', 'DataLine', 'admin', NOW(), '', NOW(), '财务数据菜单');

-- 数据分析菜单
INSERT INTO `sys_menu` VALUES (22, '数据分析', 20, 2, 'analysis', 'finance/analysis/index', '', 1, 0, 'C', '0', '0', 'finance:analysis:list', 'TrendCharts', 'admin', NOW(), '', NOW(), '数据分析菜单');

-- 报表管理菜单
INSERT INTO `sys_menu` VALUES (23, '报表管理', 20, 3, 'report', 'finance/report/index', '', 1, 0, 'C', '0', '0', 'finance:report:list', 'Document', 'admin', NOW(), '', NOW(), '报表管理菜单');

-- ==================== 第二部分：角色数据 ====================
INSERT INTO `sys_role` VALUES
(1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', NOW(), '', NOW(), '超级管理员拥有所有权限'),
(2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', NOW(), '', NOW(), '普通角色拥有部分权限'),
(3, '财务人员', 'finance', 3, '5', 1, 1, '0', '0', 'admin', NOW(), '', NOW(), '财务人员角色');

-- ==================== 第三部分：角色菜单关联数据 ====================
-- 超级管理员拥有所有菜单权限
INSERT INTO `sys_role_menu` (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu;

-- 普通角色拥有基础菜单权限
INSERT INTO `sys_role_menu` VALUES
(2, 1), (2, 10), (2, 1031), (2, 1032),
(2, 20), (2, 21), (2, 1040), (2, 1045);

-- 财务人员拥有财务管理模块权限
INSERT INTO `sys_role_menu` VALUES
(3, 20), (3, 21), (3, 22), (3, 23),
(3, 1040), (3, 1041), (3, 1042), (3, 1043), (3, 1044), (3, 1045),
(3, 1046);

-- ==================== 第四部分：用户数据 ====================
-- 创建测试用户 (手机号: 13130608426, 密码: 123456)
INSERT INTO `Users` (
    id,
    nick_name,
    password_hash,
    salt,
    state,
    telephone,
    user_role,
    certification,
    create_time,
    update_time
) VALUES (
    1,
    '超级管理员',
    '98d5cc01d21e66f1fc01159315c81265',
    '5255',
    'AUTH',
    '13130608426',
    'ADMIN',
    0,
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    nick_name = '超级管理员',
    password_hash = '98d5cc01d21e66f1fc01159315c81265',
    salt = '5255',
    state = 'AUTH',
    user_role = 'ADMIN';

-- 关联用户到超级管理员角色
INSERT INTO `sys_user_role` (user_id, role_id)
VALUES (1, 1)
ON DUPLICATE KEY UPDATE role_id = 1;

-- ==================== 验证查询 ====================
-- 执行完成后，可以运行以下查询验证数据是否正确插入：

-- 查看用户信息
-- SELECT u.id, u.nick_name, u.telephone, u.user_role FROM Users u WHERE u.telephone = '13130608426';

-- 查看用户角色关联
-- SELECT ur.user_id, ur.role_id, r.role_name, r.role_key
-- FROM sys_user_role ur
-- LEFT JOIN sys_role r ON ur.role_id = r.role_id
-- WHERE ur.user_id = 1;

-- 查看角色菜单数量
-- SELECT COUNT(*) as menu_count FROM sys_role_menu WHERE role_id = 1;

-- 查看顶级菜单
-- SELECT * FROM sys_menu WHERE parent_id = 0;
