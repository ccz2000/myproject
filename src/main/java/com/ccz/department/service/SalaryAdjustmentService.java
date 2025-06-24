package com.ccz.department.service;

import com.ccz.department.dto.SalaryAdjustmentDTO;
import java.time.LocalDate;
import java.util.List;

/**
 * @author : Chenchunze
 * @description : 薪资调整服务接口
 * @createDate : 2025/6/11
 */
public interface SalaryAdjustmentService {

    /**
     * 创建薪资调整记录
     */
    SalaryAdjustmentDTO createAdjustment(SalaryAdjustmentDTO adjustmentDTO);

    /**
     * 更新薪资调整记录
     */
    SalaryAdjustmentDTO updateAdjustment(Long id, SalaryAdjustmentDTO adjustmentDTO);

    /**
     * 删除薪资调整记录
     */
    void deleteAdjustment(Long id);

    /**
     * 获取单个薪资调整记录
     */
    SalaryAdjustmentDTO getAdjustmentById(Long id);

    /**
     * 获取员工的薪资调整记录
     */
    List<SalaryAdjustmentDTO> getAdjustmentsByEmployeeId(Long employeeId);

    /**
     * 获取部门的薪资调整记录
     */
    List<SalaryAdjustmentDTO> getAdjustmentsByDepartmentId(Long departmentId);

    /**
     * 获取日期范围内的薪资调整记录
     */
    List<SalaryAdjustmentDTO> getAdjustmentsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 获取所有薪资调整记录
     */
    List<SalaryAdjustmentDTO> getAllAdjustments();
} 