-- 菜单管理系统初始化数据
-- 包含系统管理、监控管理、财务管理等模块的完整菜单数据

-- 清空现有数据（可选，用于重新初始化）
-- DELETE FROM sys_role_menu WHERE role_id IN (1, 2, 3);
-- DELETE FROM sys_menu WHERE menu_id > 0;
-- DELETE FROM sys_role WHERE role_id IN (1, 2, 3);

-- ==================== 系统管理模块 ====================
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

-- 部门管理菜单及按钮
INSERT INTO `sys_menu` VALUES (5, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'OfficeBuilding', 'admin', NOW(), '', NOW(), '部门管理菜单');
INSERT INTO `sys_menu` VALUES (1010, '部门查询', 5, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1011, '部门新增', 5, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1012, '部门修改', 5, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1013, '部门删除', 5, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', NOW(), '', NOW(), '');

-- 岗位管理菜单及按钮
INSERT INTO `sys_menu` VALUES (6, '岗位管理', 1, 5, 'post', 'system/post/index', '', 1, 0, 'C', '0', '0', 'system:post:list', 'Postcard', 'admin', NOW(), '', NOW(), '岗位管理菜单');
INSERT INTO `sys_menu` VALUES (1014, '岗位查询', 6, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1015, '岗位新增', 6, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1016, '岗位修改', 6, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1017, '岗位删除', 6, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', NOW(), '', NOW(), '');

-- 字典管理菜单及按钮
INSERT INTO `sys_menu` VALUES (7, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'Notebook', 'admin', NOW(), '', NOW(), '字典管理菜单');
INSERT INTO `sys_menu` VALUES (1018, '字典查询', 7, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1019, '字典新增', 7, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1020, '字典修改', 7, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1021, '字典删除', 7, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', NOW(), '', NOW(), '');

-- 参数设置菜单及按钮
INSERT INTO `sys_menu` VALUES (8, '参数设置', 1, 7, 'config', 'system/config/index', '', 1, 0, 'C', '0', '0', 'system:config:list', 'Edit', 'admin', NOW(), '', NOW(), '参数设置菜单');
INSERT INTO `sys_menu` VALUES (1022, '参数查询', 8, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1023, '参数新增', 8, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1024, '参数修改', 8, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1025, '参数删除', 8, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1026, '参数刷新缓存', 8, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:config:refresh', '#', 'admin', NOW(), '', NOW(), '');

-- 通知公告菜单及按钮
INSERT INTO `sys_menu` VALUES (9, '通知公告', 1, 8, 'notice', 'system/notice/index', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'Bell', 'admin', NOW(), '', NOW(), '通知公告菜单');
INSERT INTO `sys_menu` VALUES (1027, '公告查询', 9, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1028, '公告新增', 9, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1029, '公告修改', 9, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1030, '公告删除', 9, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', NOW(), '', NOW(), '');

-- 日志管理目录及子菜单
INSERT INTO `sys_menu` VALUES (10, '日志管理', 1, 9, 'log', '', '', 1, 0, 'M', '0', '0', '', 'Document', 'admin', NOW(), '', NOW(), '日志管理目录');
INSERT INTO `sys_menu` VALUES (1031, '操作日志', 10, 1, 'operlog', 'system/operlog/index', '', 1, 0, 'C', '0', '0', 'system:operlog:list', 'Form', 'admin', NOW(), '', NOW(), '操作日志菜单');
INSERT INTO `sys_menu` VALUES (1032, '登录日志', 10, 2, 'logininfor', 'system/logininfor/index', '', 1, 0, 'C', '0', '0', 'system:logininfor:list', 'Timer', 'admin', NOW(), '', NOW(), '登录日志菜单');

-- ==================== 监控管理模块 ====================
-- 监控管理目录
INSERT INTO `sys_menu` VALUES (11, '监控管理', 0, 2, 'monitor', NULL, '', 1, 0, 'M', '0', '0', '', 'Monitor', 'admin', NOW(), '', NOW(), '监控管理目录');

-- 在线用户菜单及按钮
INSERT INTO `sys_menu` VALUES (12, '在线用户', 11, 1, 'online', 'monitor/online/index', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'UserFilled', 'admin', NOW(), '', NOW(), '在线用户菜单');
INSERT INTO `sys_menu` VALUES (1033, '用户查询', 12, 1, '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1034, '用户强退', 12, 2, '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', NOW(), '', NOW(), '');

-- 定时任务菜单及按钮
INSERT INTO `sys_menu` VALUES (13, '定时任务', 11, 2, 'job', 'monitor/job/index', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'Calendar', 'admin', NOW(), '', NOW(), '定时任务菜单');
INSERT INTO `sys_menu` VALUES (1035, '任务查询', 13, 1, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1036, '任务新增', 13, 2, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1037, '任务修改', 13, 3, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1038, '任务删除', 13, 4, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1039, '任务执行', 13, 5, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:execute', '#', 'admin', NOW(), '', NOW(), '');

-- 数据监控菜单
INSERT INTO `sys_menu` VALUES (14, '数据监控', 11, 3, 'druid', 'monitor/druid/index', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'Histogram', 'admin', NOW(), '', NOW(), '数据监控菜单');

-- 服务监控菜单
INSERT INTO `sys_menu` VALUES (15, '服务监控', 11, 4, 'server', 'monitor/server/index', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'Server', 'admin', NOW(), '', NOW(), '服务监控菜单');

-- ==================== 财务管理模块 ====================
-- 财务管理目录
INSERT INTO `sys_menu` VALUES (20, '财务管理', 0, 3, 'finance', NULL, '', 1, 0, 'M', '0', '0', '', 'Money', 'admin', NOW(), '', NOW(), '财务管理目录');

-- 财务数据菜单及按钮
INSERT INTO `sys_menu` VALUES (21, '财务数据', 20, 1, 'data', 'finance/data/index', '', 1, 0, 'C', '0', '0', 'finance:data:list', 'DataLine', 'admin', NOW(), '', NOW(), '财务数据菜单');
INSERT INTO `sys_menu` VALUES (1040, '数据查询', 21, 1, '', '', '', 1, 0, 'F', '0', '0', 'finance:data:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1041, '数据新增', 21, 2, '', '', '', 1, 0, 'F', '0', '0', 'finance:data:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1042, '数据修改', 21, 3, '', '', '', 1, 0, 'F', '0', '0', 'finance:data:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1043, '数据删除', 21, 4, '', '', '', 1, 0, 'F', '0', '0', 'finance:data:remove', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1044, '数据导入', 21, 5, '', '', '', 1, 0, 'F', '0', '0', 'finance:data:import', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1045, '数据导出', 21, 6, '', '', '', 1, 0, 'F', '0', '0', 'finance:data:export', '#', 'admin', NOW(), '', NOW(), '');

-- 数据分析菜单及按钮
INSERT INTO `sys_menu` VALUES (22, '数据分析', 20, 2, 'analysis', 'finance/analysis/index', '', 1, 0, 'C', '0', '0', 'finance:analysis:list', 'TrendCharts', 'admin', NOW(), '', NOW(), '数据分析菜单');
INSERT INTO `sys_menu` VALUES (1046, '分析查询', 22, 1, '', '', '', 1, 0, 'F', '0', '0', 'finance:analysis:query', '#', 'admin', NOW(), '', NOW(), '');

-- 报表管理菜单及按钮
INSERT INTO `sys_menu` VALUES (23, '报表管理', 20, 3, 'report', 'finance/report/index', '', 1, 0, 'C', '0', '0', 'finance:report:list', 'Document', 'admin', NOW(), '', NOW(), '报表管理菜单');
INSERT INTO `sys_menu` VALUES (1047, '报表查询', 23, 1, '', '', '', 1, 0, 'F', '0', '0', 'finance:report:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1048, '报表新增', 23, 2, '', '', '', 1, 0, 'F', '0', '0', 'finance:report:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1049, '报表修改', 23, 3, '', '', '', 1, 0, 'F', '0', '0', 'finance:report:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO `sys_menu` VALUES (1050, '报表删除', 23, 4, '', '', '', 1, 0, 'F', '0', '0', 'finance:report:remove', '#', 'admin', NOW(), '', NOW(), '');

-- ==================== 角色数据 ====================
INSERT INTO `sys_role` VALUES
(1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', NOW(), '', NOW(), '超级管理员拥有所有权限'),
(2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', NOW(), '', NOW(), '普通角色拥有部分权限'),
(3, '财务人员', 'finance', 3, '5', 1, 1, '0', '0', 'admin', NOW(), '', NOW(), '财务人员角色');

-- ==================== 角色菜单关联数据 ====================
-- 超级管理员拥有所有菜单权限
INSERT INTO `sys_role_menu` (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu;

-- 普通角色拥有基础菜单权限（系统管理主页、财务管理基础功能）
INSERT INTO `sys_role_menu` VALUES
(2, 1), (2, 10), (2, 1031), (2, 1032),  -- 日志管理
(2, 20), (2, 21), (2, 1040), (2, 1045);  -- 财务管理（只读权限）

-- 财务人员拥有财务管理模块权限
INSERT INTO `sys_role_menu` VALUES
(3, 20), (3, 21), (3, 22), (3, 23),  -- 财务管理目录
(3, 1040), (3, 1041), (3, 1042), (3, 1043), (3, 1044), (3, 1045),  -- 财务数据权限
(3, 1046);  -- 数据分析权限
