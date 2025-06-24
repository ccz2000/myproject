package com.ccz.department.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author : Chenchunze
 * @description : 薪资调整记录数据传输对象
 * @createDate : 2025/6/11
 */
@Data
public class SalaryAdjustmentDTO {
    
    private Long id;

    @NotNull(message = "员工ID不能为空")
    private Long employeeId;

    private Long departmentId;

    // 员工信息（用于展示）
    private String employeeName;
    private String departmentName;
    private String position;

    @NotNull(message = "调整类型不能为空")
    private Integer adjustmentType;

    @NotNull(message = "调整前工资不能为空")
    @DecimalMin(value = "0.0", message = "调整前工资不能小于0")
    private BigDecimal beforeSalary;

    @NotNull(message = "调整后工资不能为空")
    @DecimalMin(value = "0.0", message = "调整后工资不能小于0")
    private BigDecimal afterSalary;

    @NotBlank(message = "调整原因不能为空")
    private String adjustmentReason;

    @NotNull(message = "生效日期不能为空")
    private LocalDate effectiveDate;

    @NotNull(message = "审批人不能为空")
    private Long approverId;

    // 审批人信息（用于展示）
    private String approverName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
} 