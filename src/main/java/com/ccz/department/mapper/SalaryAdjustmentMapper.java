package com.ccz.department.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccz.department.entity.SalaryAdjustment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * @author : Chenchunze
 * @description : 薪资调整记录数据访问层
 * @createDate : 2025/6/11
 */
@Mapper
public interface SalaryAdjustmentMapper extends BaseMapper<SalaryAdjustment> {

    /**
     * 根据员工ID查询薪资调整记录
     */
    @Select("SELECT sa.*, e.name as employeeName, d.name as departmentName, e.position, " +
            "a.name as approverName " +
            "FROM salary_adjustment sa " +
            "LEFT JOIN employees e ON sa.employee_id = e.id " +
            "LEFT JOIN departments d ON sa.department_id = d.id " +
            "LEFT JOIN employees a ON sa.approver_id = a.id " +
            "WHERE sa.employee_id = #{employeeId} " +
            "ORDER BY sa.effective_date DESC")
    List<SalaryAdjustment> selectByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * 根据部门ID查询薪资调整记录（包含子部门）
     */
    @Select("SELECT sa.*, e.name as employeeName, d.name as departmentName, e.position, " +
            "a.name as approverName " +
            "FROM salary_adjustment sa " +
            "LEFT JOIN employees e ON sa.employee_id = e.id " +
            "LEFT JOIN departments d ON sa.department_id = d.id " +
            "LEFT JOIN employees a ON sa.approver_id = a.id " +
            "WHERE (sa.department_id = #{departmentId} OR sa.department_id IN (" +
            "  SELECT id FROM departments WHERE parent_id = #{departmentId} " +
            "  UNION " +
            "  SELECT id FROM departments WHERE parent_id IN (SELECT id FROM departments WHERE parent_id = #{departmentId})" +
            ")) " +
            "ORDER BY sa.effective_date DESC")
    List<SalaryAdjustment> selectByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 查询某个日期范围内的薪资调整记录
     */
    @Select("SELECT sa.*, e.name as employeeName, d.name as departmentName, e.position, " +
            "a.name as approverName " +
            "FROM salary_adjustment sa " +
            "LEFT JOIN employees e ON sa.employee_id = e.id " +
            "LEFT JOIN departments d ON sa.department_id = d.id " +
            "LEFT JOIN employees a ON sa.approver_id = a.id " +
            "WHERE sa.effective_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY sa.effective_date DESC")
    List<SalaryAdjustment> selectByDateRange(@Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);

    /**
     * 查询所有薪资调整记录
     */
    @Select("SELECT sa.*, e.name as employeeName, d.name as departmentName, e.position, " +
            "a.name as approverName " +
            "FROM salary_adjustment sa " +
            "LEFT JOIN employees e ON sa.employee_id = e.id " +
            "LEFT JOIN departments d ON sa.department_id = d.id " +
            "LEFT JOIN employees a ON sa.approver_id = a.id " +
            "ORDER BY sa.effective_date DESC")
    List<SalaryAdjustment> selectAll();
} 