-- 验证码表
DROP TABLE IF EXISTS `captcha_verification`;
CREATE TABLE `captcha_verification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `session_id` varchar(100) NOT NULL COMMENT '会话ID',
  `captcha_code` varchar(10) NOT NULL COMMENT '验证码',
  `captcha_image` text COMMENT '验证码图片base64',
  `status` int DEFAULT 0 COMMENT '状态：0未使用 1已使用 2已过期',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='验证码表';

-- 扩展 email_verification 表，添加登录验证码功能
ALTER TABLE `email_verification` 
MODIFY COLUMN `purpose` varchar(50) NOT NULL COMMENT '验证目的：REGISTER注册、RESET_PASSWORD重置密码、LOGIN_VERIFY登录验证'; 