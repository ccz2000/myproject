package com.ccz.department.controller;

import com.ccz.department.annotation.RequirePermission;
import com.ccz.department.common.Result;
import com.ccz.department.dto.DepartmentDTO;
import com.ccz.department.service.DepartmentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @PostMapping
    @RequirePermission(manageDepartments = true)
    public Result<DepartmentDTO> createDepartment(@Validated @RequestBody DepartmentDTO departmentDTO) {
        return Result.success(departmentService.createDepartment(departmentDTO));
    }

    @PutMapping("/{id}")
    @RequirePermission(manageDepartments = true)
    public Result<DepartmentDTO> updateDepartment(@PathVariable Long id, @Validated @RequestBody DepartmentDTO departmentDTO) {
        return Result.success(departmentService.updateDepartment(id, departmentDTO));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(manageDepartments = true)
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DepartmentDTO> getDepartment(@PathVariable Long id) {
        return Result.success(departmentService.getDepartment(id));
    }

    @GetMapping
    public Result<List<DepartmentDTO>> getAllDepartments() {
        return Result.success(departmentService.getAllDepartments());
    }

    @GetMapping("/sub/{parentId}")
    public Result<List<DepartmentDTO>> getSubDepartments(@PathVariable Long parentId) {
        return Result.success(departmentService.getSubDepartments(parentId));
    }

    @PutMapping("/{departmentId}/manager/{managerId}")
    public Result<Void> assignManager(@PathVariable Long departmentId, @PathVariable Long managerId) {
        departmentService.assignManager(departmentId, managerId);
        return Result.success();
    }
} 