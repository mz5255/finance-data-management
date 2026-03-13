-- 修复财务管理菜单数据
-- 注意：这里假设财务管理目录的 menu_id=5，如果不是请修改下面的 parent_id

-- 1. 先删除可能存在的旧数据（避免重复）
DELETE
FROM sys_role_menu
WHERE menu_id IN (21, 22, 23, 1040, 1041, 1042, 1043, 1044, 1045, 1046, 1047, 1048, 1049, 1050);
DELETE
FROM sys_menu
WHERE menu_id IN (21, 22, 23, 1040, 1041, 1042, 1043, 1044, 1045, 1046, 1047, 1048, 1049, 1050);

-- 2. 插入财务数据菜单及按钮权限
INSERT INTO `sys_menu`
VALUES (21, '财务数据', 5, 1, 'data', 'finance/data/index', '', 1, 0, 'C', '0', '0', 'finance:data:list', 'DataLine',
        'admin', NOW(), '', NOW(), '财务数据菜单'),
       (1040, '数据查询', 21, 1, '', '', '', 1, 0, 'F', '0', '0', 'finance:data:query', '#', 'admin', NOW(), '', NOW(),
        ''),
       (1041, '数据新增', 21, 2, '', '', '', 1, 0, 'F', '0', '0', 'finance:data:add', '#', 'admin', NOW(), '', NOW(),
        ''),
       (1042, '数据修改', 21, 3, '', '', '', 1, 0, 'F', '0', '0', 'finance:data:edit', '#', 'admin', NOW(), '', NOW(),
        ''),
       (1043, '数据删除', 21, 4, '', '', '', 1, 0, 'F', '0', '0', 'finance:data:remove', '#', 'admin', NOW(), '', NOW(),
        ''),
       (1044, '数据导入', 21, 5, '', '', '', 1, 0, 'F', '0', '0', 'finance:data:import', '#', 'admin', NOW(), '', NOW(),
        ''),
       (1045, '数据导出', 21, 6, '', '', '', 1, 0, 'F', '0', '0', 'finance:data:export', '#', 'admin', NOW(), '', NOW(),
        '');

-- 3. 插入数据分析菜单及按钮权限
INSERT INTO `sys_menu`
VALUES (22, '数据分析', 5, 2, 'analysis', 'finance/analysis/index', '', 1, 0, 'C', '0', '0', 'finance:analysis:list',
        'TrendCharts', 'admin', NOW(), '', NOW(), '数据分析菜单'),
       (1046, '分析查询', 22, 1, '', '', '', 1, 0, 'F', '0', '0', 'finance:analysis:query', '#', 'admin', NOW(), '',
        NOW(), '');

-- 4. 插入报表管理菜单及按钮权限
INSERT INTO `sys_menu`
VALUES (23, '报表管理', 5, 3, 'report', 'finance/report/index', '', 1, 0, 'C', '0', '0', 'finance:report:list',
        'Document', 'admin', NOW(), '', NOW(), '报表管理菜单'),
       (1047, '报表查询', 23, 1, '', '', '', 1, 0, 'F', '0', '0', 'finance:report:query', '#', 'admin', NOW(), '',
        NOW(), ''),
       (1048, '报表新增', 23, 2, '', '', '', 1, 0, 'F', '0', '0', 'finance:report:add', '#', 'admin', NOW(), '', NOW(),
        ''),
       (1049, '报表修改', 23, 3, '', '', '', 1, 0, 'F', '0', '0', 'finance:report:edit', '#', 'admin', NOW(), '', NOW(),
        ''),
       (1050, '报表删除', 23, 4, '', '', '', 1, 0, 'F', '0', '0', 'finance:report:remove', '#', 'admin', NOW(), '',
        NOW(), '');

-- 5. 为超级管理员(role_id=1)分配所有财务管理菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id
FROM sys_menu
WHERE menu_id IN (21, 22, 23, 1040, 1041, 1042, 1043, 1044, 1045, 1046, 1047, 1048, 1049, 1050);

-- 6. 为财务人员(role_id=3)分配财务管理菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 3, menu_id
FROM sys_menu
WHERE menu_id IN (21, 22, 23, 1040, 1041, 1042, 1043, 1044, 1045, 1046, 1047, 1048, 1049, 1050);

-- 7. 为普通角色(role_id=2)分配财务只读权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 2, menu_id
FROM sys_menu
WHERE menu_id IN (21, 1040, 1045);
