package com.ccz.department.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class EmployeeDTO {
    private Long id;

    @NotBlank(message = "员工姓名不能为空")
    private String name;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "职位不能为空")
    private String position;

    @NotNull(message = "部门ID不能为空")
    private Long departmentId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
} 