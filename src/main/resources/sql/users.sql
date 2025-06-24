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

 Date: 16/06/2025 15:56:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户序号',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '电子邮箱',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '用户状态(0:未激活 1:已激活)',
  `created_at` timestamp NOT NULL,
  `updated_at` timestamp NOT NULL,
  `permission` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户权限等级(1：管理员等级 2：普通等级 3:无登录权限)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uniq_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '853d629c2da5aa22441329adfc809bfd', '1814328094@qq.com', 1, '2025-06-05 15:37:41', '2025-06-05 15:37:41', '');
INSERT INTO `users` VALUES (2, 'ccz', 'ae76febf08b9bfed34112a9260235c1f', '19833782348@163.com', 1, '2025-06-05 16:22:19', '2025-06-05 16:23:34', '');
INSERT INTO `users` VALUES (3, 'stq', '05f6a6e7f491dd1ab1a63f3aba8b09eb', '772279506@qq.com', 1, '2025-06-05 17:06:43', '2025-06-05 17:07:05', '');
INSERT INTO `users` VALUES (4, 'czxczxczx', '2a48402649fb5663de31ba1969bb0550', 'cczxczxc@qweqwe.com', 0, '2025-06-06 11:23:56', '2025-06-06 11:23:56', '');
INSERT INTO `users` VALUES (5, '145', '9b39e51a05b93b1f45a66a25a724bf89', '1453627845@qq.com', 1, '2025-06-12 21:50:52', '2025-06-12 21:51:19', '');

SET FOREIGN_KEY_CHECKS = 1;
