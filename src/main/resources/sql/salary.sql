SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for salary
-- ----------------------------
DROP TABLE IF EXISTS `salary`;
CREATE TABLE `salary` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` bigint NOT NULL COMMENT '员工ID，关联employee表',
  `department_id` bigint NOT NULL COMMENT '部门ID，冗余字段',
  `base_salary` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '基本工资',
  `bonus` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '奖金',
  `overtime_pay` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '加班费',
  `social_security` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '社保扣除',
  `tax` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '个税扣除',
  `net_salary` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '实发工资',
  `salary_month` date NOT NULL COMMENT '工资月份',
  `payment_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '发放状态：0-未发放，1-已发放',
  `payment_date` datetime DEFAULT NULL COMMENT '发放时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_employee_id` (`employee_id`) USING BTREE,
  KEY `idx_department_id` (`department_id`) USING BTREE,
  KEY `idx_salary_month` (`salary_month`) USING BTREE,
  KEY `idx_payment_status` (`payment_status`) USING BTREE,
  CONSTRAINT `fk_salary_employee_id` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_salary_department_id` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='薪资表' ROW_FORMAT=Dynamic;

-- ----------------------------
-- Table structure for salary_adjustment
-- ----------------------------
DROP TABLE IF EXISTS `salary_adjustment`;
CREATE TABLE `salary_adjustment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` bigint NOT NULL COMMENT '员工ID',
  `department_id` bigint NOT NULL COMMENT '部门ID，冗余字段',
  `adjustment_type` tinyint(1) NOT NULL COMMENT '调整类型：1-升薪，2-降薪',
  `before_salary` decimal(12,2) NOT NULL COMMENT '调整前基本工资',
  `after_salary` decimal(12,2) NOT NULL COMMENT '调整后基本工资',
  `adjustment_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调整原因',
  `effective_date` date NOT NULL COMMENT '生效日期',
  `approver_id` bigint NOT NULL COMMENT '审批人ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_employee_id` (`employee_id`) USING BTREE,
  KEY `idx_department_id` (`department_id`) USING BTREE,
  KEY `idx_effective_date` (`effective_date`) USING BTREE,
  CONSTRAINT `fk_adjustment_employee_id` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_adjustment_department_id` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_adjustment_approver_id` FOREIGN KEY (`approver_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='薪资调整记录表' ROW_FORMAT=Dynamic;

SET FOREIGN_KEY_CHECKS = 1; 