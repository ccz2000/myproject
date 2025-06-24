package com.ccz.department.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class DepartmentDTO {
    private Long id;

    @NotBlank(message = "部门名称不能为空")
    private String name;

    private String description;

    private Long parentId;

    private Long managerId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
} 