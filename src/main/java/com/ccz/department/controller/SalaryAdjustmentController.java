package com.ccz.department.controller;

import com.ccz.department.annotation.RequirePermission;
import com.ccz.department.common.Result;
import com.ccz.department.dto.SalaryAdjustmentDTO;
import com.ccz.department.service.SalaryAdjustmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * @author : Chenchunze
 * @description : 薪资调整控制器
 * @createDate : 2025/6/11
 */
@RestController
@RequestMapping("/salary-adjustments")
@CrossOrigin(origins = "*")
public class SalaryAdjustmentController {

    @Resource
    private SalaryAdjustmentService salaryAdjustmentService;

    /**
     * 创建薪资调整记录
     */
    @PostMapping
    @RequirePermission(viewSalary = true)
    public Result<SalaryAdjustmentDTO> createAdjustment(@Validated @RequestBody SalaryAdjustmentDTO adjustmentDTO) {
        SalaryAdjustmentDTO result = salaryAdjustmentService.createAdjustment(adjustmentDTO);
        return Result.success(result);
    }

    /**
     * 更新薪资调整记录
     */
    @PutMapping("/{id}")
    @RequirePermission(viewSalary = true)
    public Result<SalaryAdjustmentDTO> updateAdjustment(
            @PathVariable Long id, 
            @Validated @RequestBody SalaryAdjustmentDTO adjustmentDTO) {
        SalaryAdjustmentDTO result = salaryAdjustmentService.updateAdjustment(id, adjustmentDTO);
        return Result.success(result);
    }

    /**
     * 删除薪资调整记录
     */
    @DeleteMapping("/{id}")
    @RequirePermission(viewSalary = true)
    public Result<Void> deleteAdjustment(@PathVariable Long id) {
        salaryAdjustmentService.deleteAdjustment(id);
        return Result.success();
    }

    /**
     * 获取单个薪资调整记录
     */
    @GetMapping("/{id}")
    @RequirePermission(viewSalary = true)
    public Result<SalaryAdjustmentDTO> getAdjustment(@PathVariable Long id) {
        SalaryAdjustmentDTO adjustment = salaryAdjustmentService.getAdjustmentById(id);
        return Result.success(adjustment);
    }

    /**
     * 获取员工的薪资调整记录
     */
    @GetMapping("/employee/{employeeId}")
    @RequirePermission(viewSalary = true)
    public Result<List<SalaryAdjustmentDTO>> getAdjustmentsByEmployee(@PathVariable Long employeeId) {
        List<SalaryAdjustmentDTO> adjustments = salaryAdjustmentService.getAdjustmentsByEmployeeId(employeeId);
        return Result.success(adjustments);
    }

    /**
     * 获取部门的薪资调整记录
     */
    @GetMapping("/department/{departmentId}")
    @RequirePermission(viewSalary = true)
    public Result<List<SalaryAdjustmentDTO>> getAdjustmentsByDepartment(@PathVariable Long departmentId) {
        List<SalaryAdjustmentDTO> adjustments = salaryAdjustmentService.getAdjustmentsByDepartmentId(departmentId);
        return Result.success(adjustments);
    }

    /**
     * 获取日期范围内的薪资调整记录
     */
    @GetMapping("/date-range")
    @RequirePermission(viewSalary = true)
    public Result<List<SalaryAdjustmentDTO>> getAdjustmentsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<SalaryAdjustmentDTO> adjustments = salaryAdjustmentService.getAdjustmentsByDateRange(startDate, endDate);
        return Result.success(adjustments);
    }

    /**
     * 获取所有薪资调整记录
     */
    @GetMapping
    @RequirePermission(viewSalary = true)
    public Result<List<SalaryAdjustmentDTO>> getAllAdjustments() {
        List<SalaryAdjustmentDTO> adjustments = salaryAdjustmentService.getAllAdjustments();
        return Result.success(adjustments);
    }
} 