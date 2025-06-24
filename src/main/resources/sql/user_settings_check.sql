-- 用户设置表检查和创建脚本
-- 用于验证和确保 user_settings 表正确创建

-- 1. 检查表是否存在
SELECT 
    TABLE_NAME,
    TABLE_COMMENT,
    ENGINE,
    TABLE_COLLATION
FROM 
    INFORMATION_SCHEMA.TABLES 
WHERE 
    TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'user_settings';

-- 2. 检查表结构
DESCRIBE user_settings;

-- 3. 检查字段详细信息
SELECT 
    COLUMN_NAME as '字段名',
    DATA_TYPE as '数据类型',
    IS_NULLABLE as '允许NULL',
    COLUMN_DEFAULT as '默认值',
    COLUMN_COMMENT as '注释'
FROM 
    INFORMATION_SCHEMA.COLUMNS 
WHERE 
    TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'user_settings'
ORDER BY 
    ORDINAL_POSITION;

-- 4. 检查索引
SHOW INDEX FROM user_settings;

-- 5. 检查外键约束
SELECT 
    CONSTRAINT_NAME as '约束名',
    COLUMN_NAME as '字段',
    REFERENCED_TABLE_NAME as '引用表',
    REFERENCED_COLUMN_NAME as '引用字段'
FROM 
    INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE 
    TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'user_settings'
    AND REFERENCED_TABLE_NAME IS NOT NULL;

-- 6. 如果表不存在，则创建（安全版本）
CREATE TABLE IF NOT EXISTS `user_settings` (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` int UNSIGNED NOT NULL COMMENT '用户ID，关联users表',
    `theme` varchar(20) DEFAULT 'light' COMMENT '主题设置：light-浅色，dark-深色',
    `language` varchar(10) DEFAULT 'zh-CN' COMMENT '语言设置',
    `timezone` varchar(50) DEFAULT 'Asia/Shanghai' COMMENT '时区设置',
    `email_notification` tinyint(1) DEFAULT 1 COMMENT '邮件通知：0-关闭，1-开启',
    `sms_notification` tinyint(1) DEFAULT 1 COMMENT '短信通知：0-关闭，1-开启',
    `privacy_level` tinyint(1) DEFAULT 1 COMMENT '隐私级别：0-完全公开，1-部分公开，2-完全私密',
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户设置表' ROW_FORMAT=Dynamic;

-- 7. 验证默认值设置
SELECT 
    'user_settings' as table_name,
    COUNT(*) as record_count
FROM user_settings;

-- 8. 测试插入默认设置（如果用户ID=1存在）
-- 注意：这只是示例，实际使用时请确保用户ID存在
-- INSERT INTO user_settings (user_id) VALUES (1) 
-- ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- 9. 查看示例数据（如果存在）
SELECT * FROM user_settings LIMIT 5;

-- 验证查询和完整性检查完成提示
SELECT 'user_settings 表检查完成' as message; 