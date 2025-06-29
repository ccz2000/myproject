-- 更新用户权限脚本 - 更严格的权限控制
-- 执行前请备份数据库

-- 1. 设置管理员权限（假设用户名为admin的用户为管理员）
UPDATE users 
SET permission = '1' 
WHERE username = 'admin' OR id = 1;

-- 2. 设置其他已激活用户为普通用户权限
UPDATE users 
SET permission = '2' 
WHERE permission IS NULL 
   OR permission = '' 
   OR permission NOT IN ('1', '2', '3')
   AND status = 1;

-- 3. 设置未激活用户为普通用户权限（但仍需激活才能登录）
UPDATE users 
SET permission = '2' 
WHERE permission IS NULL 
   OR permission = '' 
   OR permission NOT IN ('1', '2', '3')
   AND status = 0;

-- 4. 如果需要禁止某些用户登录，可以手动设置
-- UPDATE users SET permission = '3' WHERE username IN ('禁用用户名1', '禁用用户名2');

-- 5. 查看更新结果
SELECT 
    id,
    username,
    email,
    status,
    permission,
    CASE 
        WHEN permission = '1' THEN '管理员'
        WHEN permission = '2' THEN '普通用户'
        WHEN permission = '3' THEN '禁止登录'
        ELSE '未知权限'
    END as permission_text,
    created_at
FROM users 
ORDER BY id;

-- 6. 统计各权限级别用户数量
SELECT 
    permission,
    CASE 
        WHEN permission = '1' THEN '管理员'
        WHEN permission = '2' THEN '普通用户'
        WHEN permission = '3' THEN '禁止登录'
        ELSE '未知权限'
    END as permission_text,
    COUNT(*) as user_count
FROM users 
GROUP BY permission
ORDER BY permission;
