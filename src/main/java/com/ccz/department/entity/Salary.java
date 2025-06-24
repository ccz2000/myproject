package com.ccz.department.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author : Chenchunze
 * @description : 薪资实体类
 * @createDate : 2025/6/11
 */
@Data
@TableName("salary")
public class Salary {
    
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 员工ID，关联employee表
     */
    private Long employeeId;

    /**
     * 部门ID，冗余字段
     */
    private Long departmentId;

    /**
     * 基本工资
     */
    private BigDecimal baseSalary;

    /**
     * 奖金
     */
    private BigDecimal bonus;

    /**
     * 加班费
     */
    private BigDecimal overtimePay;

    /**
     * 社保扣除
     */
    private BigDecimal socialSecurity;

    /**
     * 个税扣除
     */
    private BigDecimal tax;

    /**
     * 实发工资
     */
    private BigDecimal netSalary;

    /**
     * 工资月份
     */
    private LocalDate salaryMonth;

    /**
     * 发放状态：0-未发放，1-已发放
     */
    private Integer paymentStatus;

    /**
     * 发放时间
     */
    private LocalDateTime paymentDate;

    /**
     * 备注
     */
    private String remark;

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
} 