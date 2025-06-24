package com.ccz.department.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author : Chenchunze
 * @description :
 * @createDate : 2025/5/30 10:13
 */
@Data
public class ProjectsDTO {

    private Long id;

    @NotNull(message = "项目名称不能为空")
    private String name;

    private String description;

    @NotNull(message = "项目状态不能为空")
    private String status;

    @NotNull(message = "项目开始时间不能为空")
    private LocalDate startDate;

    @NotNull(message = "项目结束时间不能为空")
    private LocalDate endDate;

    private BigDecimal budget;

    @NotNull(message = "项目部门不能为空")
    private Long departmentId;

    @NotNull(message = "项目负责人不能为空")
    private Long leaderId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 额外的展示字段
    private String departmentName;
    private String leaderName;
}
