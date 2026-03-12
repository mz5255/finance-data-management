-- 用户初始化数据
-- 手机号: 13130608426
-- 密码: 123456 (更方便记忆)
-- salt: 5255
-- 实际存储: MD5('123456' + '5255') = MD5('1234565255')

-- 1. 创建测试用户
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

-- 2. 关联用户到超级管理员角色
INSERT INTO `sys_user_role` (user_id, role_id)
VALUES (1, 1)
ON DUPLICATE KEY UPDATE role_id = 1;

-- 3. 确保超级管理员角色拥有所有菜单权限
-- 先删除现有的角色菜单关联
DELETE FROM sys_role_menu WHERE role_id = 1;

-- 为超级管理员分配所有菜单
INSERT INTO `sys_role_menu` (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu;

-- ==================== 验证查询 ====================
-- 查看用户信息
-- SELECT u.id, u.nick_name, u.telephone, u.user_role, u.salt FROM Users u WHERE u.telephone = '13130608426';

-- 查看用户角色关联
-- SELECT ur.user_id, ur.role_id, r.role_name, r.role_key
-- FROM sys_user_role ur
-- LEFT JOIN sys_role r ON ur.role_id = r.role_id
-- WHERE ur.user_id = 1;

-- 查看角色菜单数量
-- SELECT COUNT(*) as menu_count FROM sys_role_menu WHERE role_id = 1;

-- ==================== 使用说明 ====================
-- 登录信息：
-- 手机号: 13130608426
-- 密码: 123456
-- 说明: 系统使用 MD5(password + salt) 方式验证
--       这里使用 salt='5255', password='123456'
--       MD5('1234565255') = '98d5cc01d21e66f1fc01159315c81265'
