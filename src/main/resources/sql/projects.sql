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

 Date: 11/06/2025 11:07:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for projects
-- ----------------------------
DROP TABLE IF EXISTS `projects`;
CREATE TABLE `projects`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '项目描述',
  `status` enum('进行中','已完成','延期','暂停') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '进行中',
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `budget` decimal(15, 2) NULL DEFAULT NULL COMMENT '项目预算',
  `department_id` bigint NOT NULL COMMENT '负责部门',
  `leader_id` bigint NOT NULL COMMENT '项目负责人',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_project_name`(`name` ASC) USING BTREE,
  INDEX `idx_department`(`department_id` ASC) USING BTREE,
  INDEX `idx_leader`(`leader_id` ASC) USING BTREE,
  CONSTRAINT `fk_project_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_project_leader` FOREIGN KEY (`leader_id`) REFERENCES `employees` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of projects
-- ----------------------------
INSERT INTO `projects` VALUES (1, '官网重构项目', '企业官网改版与性能优化', '进行中', '2024-03-01', '2024-06-30', 250000.00, 2, 1, '2025-05-30 09:53:22', '2025-05-30 09:53:22');
INSERT INTO `projects` VALUES (2, '618营销活动', '电商平台促销推广方案', '进行中', '2024-05-01', '2024-06-20', 1500000.00, 3, 2, '2025-05-30 09:53:22', '2025-05-30 09:53:22');
INSERT INTO `projects` VALUES (3, 'ERP系统升级', '供应链管理系统升级改造', '暂停', '2024-01-15', '2024-12-31', 800000.00, 2, 4, '2025-05-30 09:53:22', '2025-05-30 09:53:22');
INSERT INTO `projects` VALUES (4, '255项目', '255项目', '延期', '2025-04-30', '2025-05-07', 5000.00, 3, 4, '2025-05-30 17:09:16', '2025-05-30 17:09:16');
INSERT INTO `projects` VALUES (5, '1111111111', '1111111111', '进行中', '2025-05-30', '2025-09-10', 600000.00, 2, 10, '2025-05-30 09:53:22', '2025-05-30 09:53:22');

SET FOREIGN_KEY_CHECKS = 1;
