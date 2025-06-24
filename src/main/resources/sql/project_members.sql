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

 Date: 11/06/2025 11:07:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for project_members
-- ----------------------------
DROP TABLE IF EXISTS `project_members`;
CREATE TABLE `project_members`  (
  `project_id` bigint NOT NULL,
  `employee_id` bigint NOT NULL,
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目角色',
  `joined_date` date NOT NULL,
  PRIMARY KEY (`project_id`, `employee_id`) USING BTREE,
  INDEX `fk_pm_employee`(`employee_id` ASC) USING BTREE,
  CONSTRAINT `fk_pm_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_pm_project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of project_members
-- ----------------------------
INSERT INTO `project_members` VALUES (1, 3, '前端开发', '2024-03-01');
INSERT INTO `project_members` VALUES (1, 4, '后端架构', '2024-03-01');
INSERT INTO `project_members` VALUES (1, 7, '前端开发', '2024-03-05');
INSERT INTO `project_members` VALUES (2, 5, '推广执行', '2024-05-01');
INSERT INTO `project_members` VALUES (2, 6, '品牌包装', '2024-05-01');
INSERT INTO `project_members` VALUES (3, 4, '系统架构', '2024-01-15');
INSERT INTO `project_members` VALUES (3, 8, '开发工程师', '2024-01-15');

SET FOREIGN_KEY_CHECKS = 1;
