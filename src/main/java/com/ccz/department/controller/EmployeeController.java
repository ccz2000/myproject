package com.ccz.department.controller;

import com.ccz.department.annotation.RequirePermission;
import com.ccz.department.common.Result;
import com.ccz.department.dto.EmployeeDTO;
import com.ccz.department.service.EmployeeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;

    @PostMapping
    @RequirePermission(manageEmployees = true)
    public Result<EmployeeDTO> createEmployee(@Validated @RequestBody EmployeeDTO employeeDTO) {
        return Result.success(employeeService.createEmployee(employeeDTO));
    }

    @PutMapping("/{id}")
    @RequirePermission(manageEmployees = true)
    public Result<EmployeeDTO> updateEmployee(@PathVariable Long id, @Validated @RequestBody EmployeeDTO employeeDTO) {
        return Result.success(employeeService.updateEmployee(id, employeeDTO));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(manageEmployees = true)
    public Result<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<EmployeeDTO> getEmployee(@PathVariable Long id) {
        return Result.success(employeeService.getEmployee(id));
    }

    @GetMapping
    public Result<List<EmployeeDTO>> getAllEmployees() {
        return Result.success(employeeService.getAllEmployees());
    }

    @GetMapping("/department/{departmentId}")
    public Result<List<EmployeeDTO>> getEmployeesByDepartment(@PathVariable Long departmentId) {
        return Result.success(employeeService.getEmployeesByDepartment(departmentId));
    }

    @PutMapping("/{employeeId}/transfer/{departmentId}")
    @RequirePermission(manageEmployees = true)
    public Result<Void> transferEmployee(@PathVariable Long employeeId, @PathVariable Long departmentId) {
        employeeService.transferEmployee(employeeId, departmentId);
        return Result.success();
    }
} 