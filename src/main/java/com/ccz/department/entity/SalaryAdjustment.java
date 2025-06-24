package com.ccz.department.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author : Chenchunze
 * @description : 薪资调整记录实体类
 * @createDate : 2025/6/11
 */
@Data
@TableName("salary_adjustment")
public class SalaryAdjustment {
    
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 部门ID，冗余字段
     */
    private Long departmentId;

    /**
     * 调整类型：1-升薪，2-降薪
     */
    private Integer adjustmentType;

    /**
     * 调整前基本工资
     */
    private BigDecimal beforeSalary;

    /**
     * 调整后基本工资
     */
    private BigDecimal afterSalary;

    /**
     * 调整原因
     */
    private String adjustmentReason;

    /**
     * 生效日期
     */
    private LocalDate effectiveDate;

    /**
     * 审批人ID
     */
    private Long approverId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 额外字段，用于 JOIN 查询结果
    /**
     * 员工姓名（JOIN 查询结果）
     */
    @TableField(exist = false)
    private String employeeName;

    /**
     * 部门名称（JOIN 查询结果）
     */
    @TableField(exist = false)
    private String departmentName;

    /**
     * 职位（JOIN 查询结果）
     */
    @TableField(exist = false)
    private String position;

    /**
     * 审批人姓名（JOIN 查询结果）
     */
    @TableField(exist = false)
    private String approverName;
} 