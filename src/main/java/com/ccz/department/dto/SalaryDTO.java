package com.ccz.department.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author : Chenchunze
 * @description : 薪资数据传输对象
 * @createDate : 2025/6/11
 */
@Data
public class SalaryDTO {
    
    private Long id;

    @NotNull(message = "员工ID不能为空")
    private Long employeeId;

    private Long departmentId;

    // 员工信息（用于展示）
    private String employeeName;
    private String departmentName;
    private String position;

    @NotNull(message = "基本工资不能为空")
    @DecimalMin(value = "0.0", message = "基本工资不能小于0")
    private BigDecimal baseSalary;

    @DecimalMin(value = "0.0", message = "奖金不能小于0")
    private BigDecimal bonus;

    @DecimalMin(value = "0.0", message = "加班费不能小于0")
    private BigDecimal overtimePay;

    @DecimalMin(value = "0.0", message = "社保扣除不能小于0")
    private BigDecimal socialSecurity;

    @DecimalMin(value = "0.0", message = "个税扣除不能小于0")
    private BigDecimal tax;

    private BigDecimal netSalary;

    @NotNull(message = "工资月份不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate salaryMonth;

    private Integer paymentStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDate;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
} 