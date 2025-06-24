package com.ccz.department.service;

import com.ccz.department.dto.SalaryDTO;
import java.time.LocalDate;
import java.util.List;

/**
 * @author : Chenchunze
 * @description : 薪资服务接口
 * @createDate : 2025/6/11
 */
public interface SalaryService {

    /**
     * 创建薪资记录
     */
    SalaryDTO createSalary(SalaryDTO salaryDTO);

    /**
     * 更新薪资记录
     */
    SalaryDTO updateSalary(Long id, SalaryDTO salaryDTO);

    /**
     * 删除薪资记录
     */
    void deleteSalary(Long id);

    /**
     * 获取单个薪资记录
     */
    SalaryDTO getSalaryById(Long id);

    /**
     * 根据员工ID和月份获取薪资记录
     */
    SalaryDTO getSalaryByEmployeeAndMonth(Long employeeId, LocalDate month);

    /**
     * 根据部门ID和月份获取薪资记录列表
     */
    List<SalaryDTO> getSalariesByDepartmentAndMonth(Long departmentId, LocalDate month);

    /**
     * 根据月份获取所有薪资记录
     */
    List<SalaryDTO> getSalariesByMonth(LocalDate month);

    /**
     * 发放薪资
     */
    void paySalary(Long id);

    /**
     * 批量发放薪资
     */
    void batchPaySalaries(List<Long> ids);

    /**
     * 计算实发工资
     */
    void calculateNetSalary(SalaryDTO salaryDTO);
} 