package com.ccz.department.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccz.department.entity.Salary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * @author : Chenchunze
 * @description : 薪资数据访问层
 * @createDate : 2025/6/11
 */
@Mapper
public interface SalaryMapper extends BaseMapper<Salary> {

    /**
     * 根据员工ID和月份查询薪资记录
     */
    @Select("SELECT s.*, e.name as employeeName, d.name as departmentName, e.position " +
            "FROM salary s " +
            "LEFT JOIN employees e ON s.employee_id = e.id " +
            "LEFT JOIN departments d ON s.department_id = d.id " +
            "WHERE s.employee_id = #{employeeId} AND s.salary_month = #{month}")
    Salary selectByEmployeeAndMonth(@Param("employeeId") Long employeeId, @Param("month") LocalDate month);

    /**
     * 根据部门ID和月份查询薪资记录列表（包含子部门）
     */
    @Select("<script>" +
            "SELECT s.*, e.name as employeeName, d.name as departmentName, e.position " +
            "FROM salary s " +
            "LEFT JOIN employees e ON s.employee_id = e.id " +
            "LEFT JOIN departments d ON s.department_id = d.id " +
            "WHERE (s.department_id = #{departmentId} OR s.department_id IN (" +
            "  SELECT id FROM departments WHERE parent_id = #{departmentId} " +
            "  UNION " +
            "  SELECT id FROM departments WHERE parent_id IN (SELECT id FROM departments WHERE parent_id = #{departmentId})" +
            ")) " +
            "<if test='month != null'> AND s.salary_month = #{month}</if> " +
            "ORDER BY s.salary_month DESC" +
            "</script>")
    List<Salary> selectByDepartmentAndMonth(@Param("departmentId") Long departmentId, @Param("month") LocalDate month);

    /**
     * 根据月份查询所有薪资记录
     */
    @Select("<script>" +
            "SELECT s.*, e.name as employeeName, d.name as departmentName, e.position " +
            "FROM salary s " +
            "LEFT JOIN employees e ON s.employee_id = e.id " +
            "LEFT JOIN departments d ON s.department_id = d.id " +
            "<where>" +
            "<if test='month != null'> AND s.salary_month = #{month}</if> " +
            "</where>" +
            "ORDER BY s.salary_month DESC" +
            "</script>")
    List<Salary> selectByMonth(@Param("month") LocalDate month);
} 