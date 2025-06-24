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

 Date: 11/06/2025 11:07:21
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for departments
-- ----------------------------
DROP TABLE IF EXISTS `departments`;
CREATE TABLE `departments`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `parent_id` bigint NULL DEFAULT NULL,
  `manager_id` bigint NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_department_name`(`name` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_manager_id`(`manager_id` ASC) USING BTREE,
  CONSTRAINT `fk_department_manager` FOREIGN KEY (`manager_id`) REFERENCES `employees` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_department_parent` FOREIGN KEY (`parent_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of departments
-- ----------------------------
INSERT INTO `departments` VALUES (1, '总公司', '企业总部', NULL, NULL, '2025-05-29 15:31:58', '2025-05-29 15:31:58');
INSERT INTO `departments` VALUES (2, '技术部', '负责产品研发和技术创新', 1, 1, '2025-05-29 15:31:58', '2025-05-29 15:32:04');
INSERT INTO `departments` VALUES (3, '市场部', '市场推广和品牌建设', 1, 2, '2025-05-29 15:31:58', '2025-05-29 15:32:04');
INSERT INTO `departments` VALUES (4, '前端开发组', 'Web前端开发团队', 2, 3, '2025-05-29 15:31:58', '2025-05-29 15:32:04');
INSERT INTO `departments` VALUES (5, '后端开发组', '服务器端开发团队', 2, 4, '2025-05-29 15:31:58', '2025-05-29 15:32:05');
INSERT INTO `departments` VALUES (6, '数字营销组', '线上营销和推广', 3, NULL, '2025-05-29 15:31:58', '2025-05-29 15:31:58');
INSERT INTO `departments` VALUES (7, '品牌管理组', '品牌建设和公关', 3, NULL, '2025-05-29 15:31:58', '2025-05-29 15:31:58');

SET FOREIGN_KEY_CHECKS = 1;
