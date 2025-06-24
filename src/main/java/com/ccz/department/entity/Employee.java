package com.ccz.department.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("employees")
public class Employee {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String email;

    private String position;

    @TableField("department_id")
    private Long departmentId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
} 