-- 移除 user_profile 表中的 github 字段
-- 执行前请确认表已存在且包含 github 字段

-- 检查表是否存在 github 字段（可选查询）
-- SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_SCHEMA = 'dept_management' AND TABLE_NAME = 'user_profile' AND COLUMN_NAME = 'github';

-- 移除 github 字段
ALTER TABLE user_profile DROP COLUMN IF EXISTS github;

-- 验证字段已移除（可选查询）
-- DESCRIBE user_profile; 