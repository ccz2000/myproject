/*
 Navicat Premium Dump SQL

 Source Server         : 43.133.231.68
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : 43.133.231.68:3323
 Source Schema         : department_db

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 11/06/2025 11:07:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for email_verification
-- ----------------------------
DROP TABLE IF EXISTS `email_verification`;
CREATE TABLE `email_verification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱地址',
  `verification_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '验证码',
  `purpose` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '验证目的：REGISTER注册、RESET_PASSWORD重置密码',
  `status` int NULL DEFAULT 0 COMMENT '状态：0未使用 1已使用 2已过期',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_code`(`verification_code` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮箱验证表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_verification
-- ----------------------------
INSERT INTO `email_verification` VALUES (1, '19833782348@163.com', '294514', 'REGISTER', 1, 2, '2025-06-05 16:52:19', '2025-06-05 16:22:19', '2025-06-05 16:23:34');
INSERT INTO `email_verification` VALUES (2, '772279506@qq.com', '023642', 'REGISTER', 1, 3, '2025-06-05 17:36:43', '2025-06-05 17:06:43', '2025-06-05 17:07:05');
INSERT INTO `email_verification` VALUES (3, 'cczxczxc@qweqwe.com', '891997', 'REGISTER', 0, 4, '2025-06-06 11:53:56', '2025-06-06 11:23:56', '2025-06-06 11:23:56');

SET FOREIGN_KEY_CHECKS = 1;
