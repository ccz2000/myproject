package com.ccz.department.controller;

import com.ccz.department.annotation.RequirePermission;
import com.ccz.department.common.Result;
import com.ccz.department.dto.SalaryDTO;
import com.ccz.department.service.SalaryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * @author : Chenchunze
 * @description : 薪资控制器
 * @createDate : 2025/6/11
 */
@RestController
@RequestMapping("/salaries")
@CrossOrigin(origins = "*")
public class SalaryController {

    @Resource
    private SalaryService salaryService;

    /**
     * 创建薪资记录
     */
    @PostMapping
    @RequirePermission(viewSalary = true)
    public Result<SalaryDTO> createSalary(@Validated @RequestBody SalaryDTO salaryDTO) {
        SalaryDTO result = salaryService.createSalary(salaryDTO);
        Result<SalaryDTO> response = Result.success(result);
        response.setMessage("薪资记录创建成功");
        return response;
    }

    /**
     * 更新薪资记录
     */
    @PutMapping("/{id}")
    @RequirePermission(viewSalary = true)
    public Result<SalaryDTO> updateSalary(@PathVariable Long id, @Validated @RequestBody SalaryDTO salaryDTO) {
        SalaryDTO result = salaryService.updateSalary(id, salaryDTO);
        Result<SalaryDTO> response = Result.success(result);
        response.setMessage("薪资记录更新成功");
        return response;
    }

    /**
     * 删除薪资记录
     */
    @DeleteMapping("/{id}")
    @RequirePermission(viewSalary = true)
    public Result<Void> deleteSalary(@PathVariable Long id) {
        salaryService.deleteSalary(id);
        Result<Void> response = Result.success();
        response.setMessage("薪资记录删除成功");
        return response;
    }

    /**
     * 获取单个薪资记录
     */
    @GetMapping("/{id}")
    @RequirePermission(viewSalary = true)
    public Result<SalaryDTO> getSalary(@PathVariable Long id) {
        SalaryDTO salary = salaryService.getSalaryById(id);
        return Result.success(salary);
    }

    /**
     * 根据员工ID和月份获取薪资记录
     */
    @GetMapping("/employee/{employeeId}")
    @RequirePermission(viewSalary = true)
    public Result<SalaryDTO> getSalaryByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate month) {
        SalaryDTO salary = salaryService.getSalaryByEmployeeAndMonth(employeeId, month);
        return Result.success(salary);
    }

    /**
     * 根据部门ID和月份获取薪资记录列表
     */
    @GetMapping("/department/{departmentId}")
    @RequirePermission(viewSalary = true)
    public Result<List<SalaryDTO>> getSalariesByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate month) {
        List<SalaryDTO> salaries = salaryService.getSalariesByDepartmentAndMonth(departmentId, month);
        return Result.success(salaries);
    }

    /**
     * 根据月份获取所有薪资记录
     */
    @GetMapping("/month")
    @RequirePermission(viewSalary = true)
    public Result<List<SalaryDTO>> getSalariesByMonth(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate month) {
        List<SalaryDTO> salaries = salaryService.getSalariesByMonth(month);
        return Result.success(salaries);
    }

    /**
     * 发放薪资
     */
    @PostMapping("/{id}/pay")
    @RequirePermission(viewSalary = true)
    public Result<Void> paySalary(@PathVariable Long id) {
        salaryService.paySalary(id);
        Result<Void> response = Result.success();
        response.setMessage("薪资发放成功");
        return response;
    }

    /**
     * 批量发放薪资
     */
    @PostMapping("/batch-pay")
    @RequirePermission(viewSalary = true)
    public Result<Void> batchPaySalaries(@RequestBody List<Long> ids) {
        salaryService.batchPaySalaries(ids);
        Result<Void> response = Result.success();
        response.setMessage("批量薪资发放成功");
        return response;
    }
} 