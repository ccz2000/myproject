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

 Date: 11/06/2025 11:07:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for employees
-- ----------------------------
DROP TABLE IF EXISTS `employees`;
CREATE TABLE `employees`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `position` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `department_id` bigint NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_employee_email`(`email` ASC) USING BTREE,
  INDEX `idx_department_id`(`department_id` ASC) USING BTREE,
  CONSTRAINT `fk_employee_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employees
-- ----------------------------
INSERT INTO `employees` VALUES (1, '张三', 'zhangsan@company.com', 'CTO', 2, '2025-05-29 15:32:01', '2025-05-29 15:32:01');
INSERT INTO `employees` VALUES (2, '李四', 'lisi@company.com', '市场总监', 3, '2025-05-29 15:32:01', '2025-05-29 15:32:01');
INSERT INTO `employees` VALUES (3, '王五', 'wangwu@company.com', '高级前端工程师', 4, '2025-05-29 15:32:01', '2025-05-29 15:32:01');
INSERT INTO `employees` VALUES (4, '赵六', 'zhaoliu@company.com', '后端架构师', 5, '2025-05-29 15:32:01', '2025-05-29 15:32:01');
INSERT INTO `employees` VALUES (5, '钱七', 'qianqi@company.com', 'SEO专家', 6, '2025-05-29 15:32:01', '2025-05-29 15:32:01');
INSERT INTO `employees` VALUES (6, '孙八', 'sunba@company.com', '品牌经理', 7, '2025-05-29 15:32:01', '2025-05-29 15:32:01');
INSERT INTO `employees` VALUES (7, '周九', 'zhoujiu@company.com', '前端开发工程师', 4, '2025-05-29 15:32:01', '2025-05-29 15:32:01');
INSERT INTO `employees` VALUES (8, '吴十', 'wushi@company.com', 'Java开发工程师', 5, '2025-05-29 15:32:01', '2025-05-29 15:32:01');
INSERT INTO `employees` VALUES (10, '于戈', 'yugeshidashabi@dashabi.com', '啥比', 2, '2025-05-31 16:05:45', '2025-05-31 16:05:45');

SET FOREIGN_KEY_CHECKS = 1;
