package com.ccz.department.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccz.department.entity.Employee;
import com.ccz.department.dto.EmployeeDTO;
import java.util.List;

public interface EmployeeService extends IService<Employee> {
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);
    
    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO);
    
    void deleteEmployee(Long id);
    
    EmployeeDTO getEmployee(Long id);
    
    List<EmployeeDTO> getAllEmployees();
    
    List<EmployeeDTO> getEmployeesByDepartment(Long departmentId);
    
    void transferEmployee(Long employeeId, Long newDepartmentId);
} 