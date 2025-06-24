-- 修复 project_members 表结构
-- 添加缺失的时间戳字段

USE department_db;

-- 添加 created_at 和 updated_at 字段
ALTER TABLE project_members 
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- 为现有数据设置默认时间戳
UPDATE project_members 
SET created_at = NOW(), updated_at = NOW() 
WHERE created_at IS NULL OR updated_at IS NULL; 